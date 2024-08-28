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

package com.ericsson.component.aia.common.transport.kafka.partition;

import static org.junit.Assert.*;

import java.io.File;
import java.net.ServerSocket;
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
 * Test class to validate AllGoSinglePartition functionality.
 */
public class KafkaCustomPartionerTest {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaCustomPartionerTest.class);
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
    private static String kafkaPort = "9092";
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
    private static Path tempDirectory;
    /**
     * default zookeeper port which is randomized using findFreePort.
     */
    private static int zookeeperPort = 2181;

    /**
     * Initialize prerequisite associated with test.
     *
     * @throws Exception
     *             if required things unable to setup.
     */
    @BeforeClass
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public static void init() throws Exception {
        zookeeperPort = findFreePort();
        kafkaPort = String.valueOf(findFreePort());
        tempDirectory = Files.createTempDirectory("_" + KafkaCustomPartionerTest.class.getCanonicalName());
        zkTestServer = new TestingServer(zookeeperPort, new File(tempDirectory.toFile().getAbsolutePath() + "/tmp_zookeeper"));
        final Properties props = new Properties();
        props.put("broker.id", "0");
        props.put("offsets.topic.replication.factor", "1");
        props.put("listeners",  "PLAINTEXT://" + KAFKA_HOST + ":" + kafkaPort);
        props.put("log.dir", tempDirectory.toFile().getAbsolutePath() + "/tmp_kafka_dir");
        props.put("zookeeper.connect", zkTestServer.getConnectString());
        props.put("replica.socket.timeout.ms", "1500");
        final KafkaConfig config = new KafkaConfig(props);
        Option<String> option = Option.empty();
        kafkaServer = new KafkaServer(config, Time.SYSTEM, option, false);
        kafkaServer.startup();
        kafkaTopic = "MyTopic" + ThreadLocalRandom.current().nextInt(1000000, 2000000);
    }

    /**
     * Test will check for the AllGoSinglePartition functionality.
     */
    @Test
    public void testKafkaAllGoSinglePartitionTest() {
        final PublisherConfiguration config = new PublisherConfiguration(publisherConfig(), MessageServiceTypes.KAFKA);
        final Publisher<String, String> publisher = KafkaFactory.createKafkaPublisher(config);
        final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConfig());
        final TopicPartition partition = new TopicPartition(kafkaTopic, 0);
        consumer.assign(Arrays.asList(partition));
        ConsumerRecords<String, String> records = consumer.poll(2000);
        while (records.count() == 0) {
            records = consumer.poll(1000);
            LOG.info("waiting for records");
            publisher.sendMessage(kafkaTopic, Integer.toString(1), "Published Message --> TEST MSG 1");
            publisher.sendMessage(kafkaTopic, Integer.toString(2), "Published Message --> TEST MSG 2");
            publisher.sendMessage(kafkaTopic, Integer.toString(3), "Published Message --> TEST MSG 3");
        }
        final List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
        final long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
        consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
        //partitionRecords.stream().forEach(rec -> LOG.info(rec.value()));
        consumer.close();
        publisher.flush();
        publisher.close();

        assertEquals(3, partitionRecords.size());
        assertEquals("Published Message --> TEST MSG 1", partitionRecords.get(0).value());
        assertEquals("Published Message --> TEST MSG 2", partitionRecords.get(1).value());
        assertEquals("Published Message --> TEST MSG 3", partitionRecords.get(2).value());

    }

    /**
     * @return consumer specific configuration.
     */
    private static Properties consumerConfig() {
        final Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_HOST + ":" + kafkaPort);
        props.put("group.id", kafkaTopic);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("zookeeper.connect", zkTestServer.getConnectString());
        return props;
    }

    /**
     * @return publisher configuration.
     */
    private Properties publisherConfig() {
        final Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_HOST + ":" + kafkaPort);
        props.put("acks", "0");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("partitioner.class", AllGoSinglePartition.class.getName());
        return props;
    }

    /**
     * Tear down all services as cleanup.
     *
     * @throws Exception
     *             if tear down unsuccessful.
     */
    @AfterClass
    public static void tearDown() throws Exception {
        kafkaServer.shutdown();
        kafkaServer.awaitShutdown();
        zkTestServer.stop();
        FileDeleteStrategy.FORCE.deleteQuietly(tempDirectory.toFile());
    }

    /**
     * Find available port number.
     *
     * @return available port number else -1
     */
    public static int findFreePort() {
        int port = -1;
        try {
            final ServerSocket socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            port = socket.getLocalPort();
            socket.close();
        } catch (final Exception e) {
        }
        return port;
    }

}
