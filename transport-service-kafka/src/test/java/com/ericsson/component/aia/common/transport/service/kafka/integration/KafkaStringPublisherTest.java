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

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.curator.test.TestingServer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Time;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.transport.config.PublisherConfiguration;
import com.ericsson.component.aia.common.transport.service.MessageServiceTypes;
import com.ericsson.component.aia.common.transport.service.Publisher;
import com.ericsson.component.aia.common.transport.service.kafka.KafkaFactory;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import scala.Option;

/**
 * Test to validate Kafka Publisher and Subscriber with String or plain text messages.
 */
public class KafkaStringPublisherTest {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaStringPublisherTest.class);
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
        tmpDir = Files.createTempDirectory("__" + KafkaStringPublisherTest.class.getCanonicalName());
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
     */
    @Test
    public void testKafkaProducerWithStringSerializer() {
        final PublisherConfiguration config = new PublisherConfiguration(producerConfig(), MessageServiceTypes.KAFKA);
        final Publisher<String, String> producer = KafkaFactory.createKafkaPublisher(config);
        final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConfig());
        final TopicPartition partition = new TopicPartition(kafkaTopic, 0);
        consumer.assign(Arrays.asList(partition));
        ConsumerRecords<String, String> records = consumer.poll(2000);
        while (records.count() == 0) {
            records = consumer.poll(1000);
            if (LOG.isInfoEnabled()) {
                LOG.info("waiting for records");
            }
            producer.sendMessage(kafkaTopic, Integer.toString(1), "Producer Message --> TEST MSG 1");
            producer.sendMessage(kafkaTopic, Integer.toString(2), "Producer Message --> TEST MSG 2");
            producer.sendMessage(kafkaTopic, Integer.toString(3), "Producer Message --> TEST MSG 3");
        }
        final List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
        final long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
        consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
        //partitionRecords.stream().forEach(rec -> LOG.info(rec.value()));
        consumer.close();
        producer.flush();
        producer.close();

        assertEquals(3, partitionRecords.size());
        assertEquals("Producer Message --> TEST MSG 1", partitionRecords.get(0).value());
        assertEquals("Producer Message --> TEST MSG 2", partitionRecords.get(1).value());
        assertEquals("Producer Message --> TEST MSG 3", partitionRecords.get(2).value());

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
        consumerProp.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProp.put("zookeeper.connect", zkTestServer.getConnectString());
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
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
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
