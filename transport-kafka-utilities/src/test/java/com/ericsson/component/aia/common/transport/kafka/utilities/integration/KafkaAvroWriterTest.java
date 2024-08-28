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
package com.ericsson.component.aia.common.transport.kafka.utilities.integration;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.curator.test.TestingServer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Time;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.avro.GenericRecordWrapper;
import com.ericsson.component.aia.common.avro.decoder.GenericRecordDecoder;
import com.ericsson.component.aia.common.avro.encoder.GenericRecordEncoder;
import com.ericsson.component.aia.common.transport.kafka.writer.api.KafkaKeyGenerator;
import com.ericsson.component.aia.common.transport.kafka.writer.builder.KafkaGenericWriterBuilder;
import com.ericsson.component.aia.common.transport.kafka.writer.impl.KafkaGenericWriter;
import com.ericsson.component.aia.model.registry.client.SchemaRegistryClient;
import com.ericsson.component.aia.model.registry.exception.SchemaRetrievalException;
import com.ericsson.component.aia.model.registry.impl.RegisteredSchema;
import com.ericsson.component.aia.model.registry.impl.SchemaRegistryClientFactory;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import scala.Option;

/**
 * Test to validate Kafka Avro Writer functionality and validate with kafka Subscriber with GenericRecord messages.
 */
public class KafkaAvroWriterTest {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaAvroWriterTest.class);
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
    /**
     * Default kafka topic.
     */
    private static String kafkaTopic = "MyTopic";
    /**
     * Temporary directory to hold intermediate kafka server and zookeeper data.
     */
    private static Path tmpDir;
    /**
     * default zookeeper port which is randomized using findFreePort.
     */
    private static int zkPort = 2181;
    Calendar cal = GregorianCalendar.getInstance();

    private GenericRecordWrapper generateGenericRecord() throws SchemaRetrievalException {
        final Properties schemaRegistryClientProperties = new Properties();
        schemaRegistryClientProperties.put("schemaRegistry.address", "src/test/resources/avro/");
        schemaRegistryClientProperties.put("schemaRegistry.cacheMaximumSize", "50");
        final SchemaRegistryClient SCHEMA_REGISTRY_CLIENT = SchemaRegistryClientFactory
                .newSchemaRegistryClientInstance(schemaRegistryClientProperties);
        final RegisteredSchema registeredSchema = SCHEMA_REGISTRY_CLIENT.lookup("celltrace.s.ab11.INTERNAL_PROC_UE_CTXT_RELEASE");

        final ByteBuffer buf = ByteBuffer.allocate(48);
        buf.put("1000".getBytes());
        final GenericRecord genericRecord = new GenericData.Record(registeredSchema.getSchema());
        genericRecord.put("_NE", "t1");
        genericRecord.put("_TIMESTAMP", cal.getTimeInMillis());
        genericRecord.put("TIMESTAMP_HOUR", cal.get(Calendar.HOUR));
        genericRecord.put("TIMESTAMP_MINUTE", cal.get(Calendar.MINUTE));
        genericRecord.put("TIMESTAMP_SECOND", cal.get(Calendar.SECOND));
        genericRecord.put("TIMESTAMP_MILLISEC", cal.get(Calendar.MILLISECOND));
        genericRecord.put("SCANNER_ID", 10L);
        genericRecord.put("RBS_MODULE_ID", 400);
        genericRecord.put("GLOBAL_CELL_ID", (long) 1);
        genericRecord.put("ENBS1APID", (long) 1);
        genericRecord.put("MMES1APID", (long) 1);
        genericRecord.put("GUMMEI", buf);
        genericRecord.put("RAC_UE_REF", 10l);
        genericRecord.put("TRIGGERING_NODE", 1);
        genericRecord.put("INTERNAL_RELEASE_CAUSE", ((int) Math.floor(Math.random() * 28)));
        genericRecord.put("S1_RELEASE_CAUSE", ((int) Math.floor(Math.random() * 33)));
        return new GenericRecordWrapper(registeredSchema.getSchemaId(), genericRecord);
    }

    /**
     * Initialize prerequisite associated with test.
     *
     * @throws Exception
     *             if required things unable to setup.
     */
    @BeforeClass
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public static void init() throws Exception {
        System.setProperty("schemaRegistry.address", "src/test/resources/avro/");
        zkPort = TestUtility.findFreePort();
        kafkaPortNumber = String.valueOf(TestUtility.findFreePort());
        tmpDir = Files.createTempDirectory("__" + KafkaAvroWriterTest.class.getCanonicalName());
        zkTestServer = new TestingServer(zkPort, new File(tmpDir.toFile().getAbsolutePath() + "/tmp_zookeeper"));
        final Properties props = new Properties();
        props.put("broker.id", "0");
        props.put("offsets.topic.replication.factor", "1");
        props.put("listeners",  "PLAINTEXT://" + KAFKA_HOST + ":" + kafkaPortNumber);
        props.put("log.dir", tmpDir.toFile().getAbsolutePath() + "/tmp_kafka_dir");
        props.put("zookeeper.connect", zkTestServer.getConnectString());
        props.put("replica.socket.timeout.ms", "1500");
        final KafkaConfig config = new KafkaConfig(props);
        Option<String> option = Option.empty();
        kafkaServer = new KafkaServer(config, Time.SYSTEM, option, false);
        kafkaServer.startup();
        kafkaTopic = "MyTopic" + ThreadLocalRandom.current().nextInt(1000000, 2000000);
    }

    /**
     * Test will check Kafka publisher with string messages.
     *
     * @throws SchemaRetrievalException
     */
    @Test
    public void testKafkaAvroWriterWithGenericRecord() throws SchemaRetrievalException {
        final KafkaGenericWriter<String, GenericRecord> writer = KafkaGenericWriterBuilder.<String, GenericRecord> create()
                .withBrokers(KAFKA_HOST + ":" + kafkaPortNumber).withTopic(kafkaTopic)
                .withKeySerializer("org.apache.kafka.common.serialization.StringSerializer")
                .withValueSerializer("com.ericsson.component.aia.common.avro.kafka.encoder.KafkaGenericRecordEncoder").build();

        final GenericRecordWrapper genericRecord = generateGenericRecord();
        final KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(consumerConfig());
        final TopicPartition partition = new TopicPartition(kafkaTopic, 0);
        consumer.assign(Arrays.asList(partition));
        ConsumerRecords<String, GenericRecord> records = consumer.poll(2000);
        boolean msgpublished = false;
        while (records.count() == 0) {
            records = consumer.poll(1000);
            if (LOG.isInfoEnabled()) {
                LOG.info("waiting for records");
            }
            if (!msgpublished) {
                writer.write(genericRecord);
                writer.flush();
                msgpublished = true;
            }
        }
        writer.close();
        final List<ConsumerRecord<String, GenericRecord>> partitionRecords = records.records(partition);
        final long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
        consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
        consumer.close();

        assertEquals(1, partitionRecords.size());
        final GenericRecordEncoder encoder = new GenericRecordEncoder();
        final GenericRecordDecoder decoder = new GenericRecordDecoder();
        final GenericRecord expacted = decoder.decode(encoder.encode(genericRecord));
        final GenericRecord actual = partitionRecords.get(0).value();
        assertEquals(expacted, actual);

    }

    /**
     * Test will check Kafka publisher with string messages.
     *
     * @throws SchemaRetrievalException
     *             Unable to retrieve schema.
     */
    @Test
    public void testKafkaAvroWriterWithGenericRecordWithKey() throws SchemaRetrievalException {

        final KafkaGenericWriter<String, GenericRecord> writer = KafkaGenericWriterBuilder.<String, GenericRecord> create()
                .withBrokers(KAFKA_HOST + ":" + kafkaPortNumber).withTopic(kafkaTopic)
                .withKeySerializer("org.apache.kafka.common.serialization.StringSerializer")
                .withValueSerializer("com.ericsson.component.aia.common.avro.kafka.encoder.KafkaGenericRecordEncoder")
                .withKeyGenerator(new KafkaKeyGenerator<String, GenericRecord>() {
                    /**                   */
                    private static final long serialVersionUID = 1L;
                    String[] columns = { "GLOBAL_CELL_ID", "ENBS1APID", "MMES1APID" };
                    private final String delimiter = " ";

                    @Override
                    public String getKey(final GenericRecord grecord) {
                        final StringBuilder key = new StringBuilder();
                        final int indexOfLastColumn = columns.length - 1;

                        for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
                            key.append(grecord.get(columns[columnIndex]));
                            if (columnIndex < indexOfLastColumn) {
                                key.append(delimiter);
                            }
                        }

                        return key.toString();
                    }
                }).build();

        final GenericRecordWrapper genericRecord = generateGenericRecord();
        final KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(consumerConfig());
        final TopicPartition partition = new TopicPartition(kafkaTopic, 0);
        consumer.assign(Arrays.asList(partition));
        ConsumerRecords<String, GenericRecord> records = consumer.poll(2000);
        boolean msgpublished = false;
        while (records.count() == 0) {
            records = consumer.poll(1000);
            if (LOG.isInfoEnabled()) {
                LOG.info("waiting for records");
            }
            if (!msgpublished) {
                writer.write(genericRecord);
                writer.flush();
                msgpublished = true;
            }
        }
        writer.close();
        final List<ConsumerRecord<String, GenericRecord>> partitionRecords = records.records(partition);
        final long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
        consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
        consumer.close();

        assertEquals(1, partitionRecords.size());
        final GenericRecordEncoder encoder = new GenericRecordEncoder();
        final GenericRecordDecoder decoder = new GenericRecordDecoder();
        final GenericRecord expacted = decoder.decode(encoder.encode(genericRecord));

        final ConsumerRecord<String, GenericRecord> actualConsumerRecord = partitionRecords.get(0);

        final GenericRecord actual = actualConsumerRecord.value();
        assertEquals(expacted, actual);

        final String actualMessageKey = actualConsumerRecord.key();
        assertEquals("1 1 1", actualMessageKey);
    }

    /**
     * @return consumer specific configuration.
     */
    private static Properties consumerConfig() {
        final Properties consumerProp = new Properties();
        consumerProp.put("bootstrap.servers", KAFKA_HOST + ":" + kafkaPortNumber);
        consumerProp.put("group.id", kafkaTopic);
        consumerProp.put("enable.auto.commit", "true");
        consumerProp.put("auto.commit.interval.ms", "1000");
        consumerProp.put("session.timeout.ms", "30000");
        consumerProp.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProp.put("value.deserializer", "com.ericsson.component.aia.common.avro.kafka.decoder.KafkaGenericRecordDecoder");
        consumerProp.put("zookeeper.connect", zkTestServer.getConnectString());
        return consumerProp;
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
