/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.common.transport.service.kafka.integration;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.curator.test.TestingServer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Time;
import org.junit.*;

import com.ericsson.aia.common.datarouting.api.DataSink;
import com.ericsson.aia.common.datarouting.api.builder.DataRouterBuilder;
import com.ericsson.aia.common.datarouting.api.builder.OutputDataRouterBuilder;
import com.ericsson.component.aia.common.avro.GenericRecordWrapper;
import com.ericsson.component.aia.model.registry.client.SchemaRegistryClient;
import com.ericsson.component.aia.model.registry.impl.RegisteredSchema;
import com.ericsson.component.aia.model.registry.impl.SchemaRegistryClientFactory;
import com.google.common.base.Stopwatch;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import scala.Option;

/**
 * Test to validate Kafka Publisher and Subscriber with String or plain text messages.
 */
public class DataRoutingKafkaAvroIOTest {

    private static final long MAX_WAIT = 30;
    /**
     * Zookeeper server.
     */
    private static TestingServer zkTestServer;
    /**
     * Kafka server.
     */
    private static KafkaServer kafkaServer;
    /**
     * default kafka port which is randomized using findFreePort.
     */
    private static String kafkaPortNumber = "9092";
    /**
     * Default kafka host.
     */
    private static final String KAFKA_HOST = "localhost";

    private static String kafkaTopic1 = "MyTopic1";
    private static String kafkaTopic2 = "MyTopic2";

    /**
     * Temporary directory to hold intermediate kafka server and zookeeper data.
     */
    private static Path tmpDir;
    /**
     * default zookeeper port which is randomized using findFreePort.
     */
    private static int zkPort = 2181;

    /**
     * The schema for records.
     */
    private static Schema schema;
    private static long id;

    /**
     * Initialize prerequisite associated with test.
     *
     * @throws Exception
     *             if required things unable to setup.
     */
    @BeforeClass
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public static void init() throws Exception {
        zkPort = TestUtility.findFreePort();
        kafkaPortNumber = String.valueOf(TestUtility.findFreePort());
        tmpDir = Files.createTempDirectory("__" + DataRoutingKafkaAvroIOTest.class.getCanonicalName());
        zkTestServer = new TestingServer(zkPort, new File(tmpDir.toFile().getAbsolutePath() + "/tmp_zookeeper"));
        final Properties props = new Properties();
        props.put("broker.id", "0");
        props.put("offsets.topic.replication.factor", "1");
        props.put("num.partitions", "2");
        props.put("listeners",  "PLAINTEXT://" + KAFKA_HOST + ":" + kafkaPortNumber);
        props.put("log.dir", tmpDir.toFile().getAbsolutePath() + "/tmp_kafka_dir");
        props.put("zookeeper.connect", zkTestServer.getConnectString());
        props.put("replica.socket.timeout.ms", "1500");
        final KafkaConfig config = new KafkaConfig(props);

        Option<String> option = Option.empty();
        kafkaServer = new KafkaServer(config, Time.SYSTEM, option, false);
        kafkaServer.startup();
        kafkaTopic1 = "MyTopic" + ThreadLocalRandom.current().nextInt(1000000, 2000000);
        kafkaTopic2 = "MyTopic" + ThreadLocalRandom.current().nextInt(1000000, 2000000);

        final Properties schemaRegistryClientProperties = new Properties();
        schemaRegistryClientProperties.put("schemaRegistry.address", "src/test/resources/avro/");
        schemaRegistryClientProperties.put("schemaRegistry.cacheMaximumSize", "50");

        final SchemaRegistryClient SCHEMA_REGISTRY_CLIENT = SchemaRegistryClientFactory
                .newSchemaRegistryClientInstance(schemaRegistryClientProperties);

        final RegisteredSchema registeredSchema = SCHEMA_REGISTRY_CLIENT.lookup("CELL.cell");
        schema = registeredSchema.getSchema();
        id = registeredSchema.getSchemaId();

        System.setProperty("schemaRegistry.address", "src/test/resources/avro/");
    }

    /**
     * Test will check Kafka publisher with string messages.
     */
    @Test
    public void testKafkaProducerWithStringSerializer() {

        final String uri1 = "kafka://" + kafkaTopic1 + "?format=avro";
        final String uri2 = "kafka://" + kafkaTopic2 + "?format=avro";

        final OutputDataRouterBuilder<GenericRecord> outputDataRouterBuilder = DataRouterBuilder.outputRouter();
        outputDataRouterBuilder.addSink(uri1).setProperties(producerConfig()).partitionStrategy().applyFunction("0").filter().acceptRecord()
                .schema("CELL.cell").function("cellId == 1 || cellId == 2 ");

        outputDataRouterBuilder.addSink(uri2).setProperties(producerConfig()).partitionStrategy().applyFunction("0").filter().acceptRecord()
                .regex("cellId", "5.*");

        final DataSink<GenericRecord> sink = outputDataRouterBuilder.build();

        final KafkaConsumer<String, GenericRecord> consumer1 = new KafkaConsumer<>(consumerConfig(kafkaTopic1));
        final TopicPartition partition1 = new TopicPartition(kafkaTopic1, 0);
        consumer1.assign(Arrays.asList(partition1));

        final KafkaConsumer<String, GenericRecord> consumer2 = new KafkaConsumer<>(consumerConfig(kafkaTopic2));
        final TopicPartition partition2 = new TopicPartition(kafkaTopic2, 0);
        consumer2.assign(Arrays.asList(partition2));

        final GenericRecord record1 = genericRecord("1", "1", 7, 272, 7);
        final GenericRecord record2 = genericRecord("1", "2", 7, 272, 7);
        final GenericRecord record3 = genericRecord("1", "3", 7, 272, 7);
        final GenericRecord record4 = genericRecord("1", "54", 7, 272, 7);
        final GenericRecord record5 = genericRecord("1", "55", 8, 272, 7);
        final GenericRecord record6 = genericRecord("1", "2", 8, 272, 7);

        sink.sendMessage(Arrays.asList(record1, record2, record3, record4, record5, record6));
        sink.flush();

        final Collection<GenericRecord> topic1Records = waitToRecieveRecords(consumer1, 3);
        final Collection<GenericRecord> topic2Records = waitToRecieveRecords(consumer2, 2);

        assertEquals(3, topic1Records.size());
        assertEquals(2, topic2Records.size());

    }

    private Collection<GenericRecord> waitToRecieveRecords(final KafkaConsumer<String, GenericRecord> consumer1, final int expectedNumberOfRecords) {
        final Collection<GenericRecord> recievedRecords = new ArrayList<>();
        final Stopwatch st = Stopwatch.createStarted();

        while (recievedRecords.size() < expectedNumberOfRecords) {
            final ConsumerRecords<String, GenericRecord> records = consumer1.poll(1000);

            for (final ConsumerRecord<String, GenericRecord> record : records) {
                recievedRecords.add(record.value());
            }

            if (st.elapsed(TimeUnit.SECONDS) > MAX_WAIT) {
                break;
            }
        }

        return recievedRecords;
    }

    private GenericRecord genericRecord(final String enodebId, final String cellId, final int mnc, final int mcc, final int mncLength) {
        final GenericData.Record avroRecord = new GenericData.Record(schema);
        avroRecord.put("enodebId", enodebId);
        avroRecord.put("cellId", cellId);
        avroRecord.put("mncLength", mncLength);

        avroRecord.put("mnc", mnc);
        avroRecord.put("mcc", mcc);

        return new GenericRecordWrapper(id, avroRecord);

    }

    /**
     * @return consumer specific configuration.
     */
    private static Properties consumerConfig(final String kafkaTopic) {
        final Properties consumerProp = new Properties();
        consumerProp.put("group.id", kafkaTopic);
        consumerProp.put("bootstrap.servers", KAFKA_HOST + ":" + kafkaPortNumber);
        consumerProp.put("enable.auto.commit", "true");
        consumerProp.put("auto.commit.interval.ms", "1000");
        consumerProp.put("auto.offset.reset", "earliest");
        consumerProp.put("auto.create.topics.enable", "true");
        consumerProp.put("session.timeout.ms", "30000");
        consumerProp.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProp.put("value.deserializer", "com.ericsson.component.aia.common.avro.kafka.decoder.KafkaGenericRecordDecoder");
        consumerProp.put("zookeeper.connect", zkTestServer.getConnectString());
        consumerProp.put("input.thread.pool.size", 1);

        return consumerProp;
    }

    /**
     * @return producer configuration.
     */
    private Properties producerConfig() {
        final Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", KAFKA_HOST + ":" + kafkaPortNumber);
        producerProps.put("acks", "0");
        producerProps.put("retries", 0);
        producerProps.put("batch.size", 16384);
        producerProps.put("linger.ms", 1);
        producerProps.put("buffer.memory", 33554432);
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "com.ericsson.component.aia.common.avro.kafka.encoder.KafkaGenericRecordEncoder");

        return producerProps;
    }

    /**
     * Tear down all services as cleanup.
     *
     * @throws Exception
     *             if unable to perform tear down properly.
     */
    @AfterClass
    public static void tearDown() throws Exception {
        kafkaServer.shutdown();
        kafkaServer.awaitShutdown();
        zkTestServer.stop();
        FileDeleteStrategy.FORCE.deleteQuietly(tmpDir.toFile());
    }
}
