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

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.curator.test.TestingServer;
import org.apache.kafka.common.utils.Time;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.transport.config.KafkaSubscriberConfiguration;
import com.ericsson.component.aia.common.transport.config.PublisherConfiguration;
import com.ericsson.component.aia.common.transport.config.builders.SubscriberConfigurationBuilder;
import com.ericsson.component.aia.common.transport.service.*;
import com.ericsson.component.aia.common.transport.service.kafka.KafkaFactory;
import com.ericsson.component.aia.common.transport.service.kafka.KafkaSubscriber;
import com.google.common.base.Stopwatch;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import scala.Option;

/**
 * A test class to validate managed and extended subscriber.
 */
public class KafkaStringSubscriberTest {
    private static final long MAX_WAIT = 30;
    private static final Logger LOG = LoggerFactory.getLogger(KafkaStringSubscriberTest.class);
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
        zookeeperPort = TestUtility.findFreePort();
        kafkaPort = String.valueOf(TestUtility.findFreePort());
        tempDirectory = Files.createTempDirectory("_" + KafkaStringSubscriberTest.class.getCanonicalName());
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
     * Test to validate managed kafka subscriber.
     */
    @Test
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public void testManagedKafkaSubscriber() {

        final List<String> sentMsg = new LinkedList<>();
        sentMsg.add("publisher Message --> TEST MSG 1");
        sentMsg.add("publisher Message --> TEST MSG 2");
        sentMsg.add("publisher Message --> TEST MSG 3");

        final PublisherConfiguration config = new PublisherConfiguration(publisherConfig(), MessageServiceTypes.KAFKA);
        if (LOG.isInfoEnabled()) {
            LOG.info("Publisher configuration " + config);
        }
        final Publisher<String, String> publisher = KafkaFactory.createKafkaPublisher(config);
        final ManagedSubscriber kafkaSubscriber = new ManagedSubscriber();
        final Thread subscriberThread = new Thread(kafkaSubscriber);
        subscriberThread.start();
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (LOG.isInfoEnabled()) {
                    LOG.info("publisher thread start and waiting for publisher to up");
                }
                TestUtility.delay(10000);
                publisher.sendMessage(kafkaTopic, Integer.toString(1), sentMsg.get(0));

                if (LOG.isInfoEnabled()) {
                    LOG.info(" Published Message [" + sentMsg.get(0) + "]");
                }
                publisher.sendMessage(kafkaTopic, Integer.toString(2), sentMsg.get(1));

                if (LOG.isInfoEnabled()) {
                    LOG.info(" Published Message [" + sentMsg.get(1) + "]");
                }

                publisher.sendMessage(kafkaTopic, Integer.toString(3), sentMsg.get(2));

                if (LOG.isInfoEnabled()) {
                    LOG.info(" Published Message [" + sentMsg.get(2) + "]");
                }

                publisher.flush();

                if (LOG.isInfoEnabled()) {
                    LOG.info("Flushing done successfully..");
                }
            }
        }).start();

        final Stopwatch st = Stopwatch.createStarted();
        while (kafkaSubscriber.getMessages().size() < sentMsg.size()) {
            LOG.info(" Waiting to receive message......");
            TestUtility.delay(1000);
            if (st.elapsed(TimeUnit.SECONDS) > MAX_WAIT) {
                break;
            }
        }

        LOG.info(" Message received suceessfully.");
        subscriberThread.interrupt();
        publisher.close();

        final List<String> receivedMsg = kafkaSubscriber.getMessages();

        assertEquals(sentMsg.size(), receivedMsg.size());
        assertEquals(sentMsg.get(0), receivedMsg.get(0));
        assertEquals(sentMsg.get(1), receivedMsg.get(1));
        assertEquals(sentMsg.get(2), receivedMsg.get(2));
    }

    /**
     * Test to validate extended kafka subscriber.
     */
    @Test
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public void testExtendedKafkaSubscriber() {

        final List<String> sentMessages = new LinkedList<>();
        sentMessages.add("Published Message --> TEST MSG 1");
        sentMessages.add("Published Message --> TEST MSG 2");
        sentMessages.add("Published Message --> TEST MSG 3");

        final PublisherConfiguration config = new PublisherConfiguration(publisherConfig(), MessageServiceTypes.KAFKA);
        final Publisher<String, String> publisher = KafkaFactory.createKafkaPublisher(config);
        final ExtendedSubscriber kafkaSubscriber = new ExtendedSubscriber();
        final Thread subscriberThread = new Thread(kafkaSubscriber);
        subscriberThread.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Publisher thread start and waiting for subscriber to up");
                }
                TestUtility.delay(10000);
                publisher.sendMessage(kafkaTopic, Integer.toString(1), sentMessages.get(0));

                if (LOG.isInfoEnabled()) {
                    LOG.info(" Published Message [" + sentMessages.get(0) + "]");
                }

                publisher.sendMessage(kafkaTopic, Integer.toString(2), sentMessages.get(1));

                if (LOG.isInfoEnabled()) {
                    LOG.info(" Published Message [" + sentMessages.get(1) + "]");
                }

                publisher.sendMessage(kafkaTopic, Integer.toString(3), sentMessages.get(2));

                if (LOG.isInfoEnabled()) {
                    LOG.info(" Published Message [" + sentMessages.get(2) + "]");
                }

                publisher.flush();

                if (LOG.isInfoEnabled()) {
                    LOG.info("Flushing done successfully");
                }

            }
        }).start();

        final Stopwatch st = Stopwatch.createStarted();
        while (kafkaSubscriber.getMessages().size() < sentMessages.size()) {
            LOG.info(" Waiting to receive message......");
            TestUtility.delay(1000);
            if (st.elapsed(TimeUnit.SECONDS) > MAX_WAIT) {
                break;
            }
        }

        LOG.info(" Message received suceessfully.");
        subscriberThread.interrupt();
        publisher.close();
        final List<String> receivedMsg = kafkaSubscriber.getMessages();
        assertEquals(sentMessages.size(), receivedMsg.size());
        assertEquals(sentMessages.get(0), receivedMsg.get(0));
        assertEquals(sentMessages.get(1), receivedMsg.get(1));
        assertEquals(sentMessages.get(2), receivedMsg.get(2));

    }

    /**
     * Test class for Managed Kafka Subscriber.
     */
    public static class ManagedSubscriber implements Runnable {

        List<String> messages = new LinkedList<>();
        Subscriber<String, String> subscriber;

        /**
         * default constructor.
         */
        public ManagedSubscriber() {
            final Properties subscriberConfig = subscriberConfig();
            final KafkaSubscriberConfiguration<String> conf = SubscriberConfigurationBuilder
                    .<String, String> createkafkaConsumerBuilder(subscriberConfig)
                    .addValueDeserializer(subscriberConfig.getProperty("value.deserializer"))
                    .addKeyDeserializer(subscriberConfig.getProperty("key.deserializer")).addTopic(kafkaTopic).addProcessors(1).enableManaged()
                    .addListener(new GenericEventListener<String>() {
                        @Override
                        public void onEvent(final Collection<String> microBatch) {
                            for (final String entry : microBatch) {
                                if (LOG.isInfoEnabled()) {
                                    LOG.info("Message Received [" + entry + "]");
                                }
                                messages.add(entry);
                            }
                        }
                    }).build();
            subscriber = KafkaFactory.createKafkaSubscriber(conf);
        }

        public List<String> getMessages() {
            return messages;
        }

        @Override
        public void run() {
            if (LOG.isInfoEnabled()) {
                LOG.info("Subscriber Started......");
            }
            try {
                subscriber.start();
            } finally {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Closing subscriber Thread.");
                }
                subscriber.close();
            }
        }

    }

    /**
     * Test class for Extended Kafka Subscriber.
     */
    public static class ExtendedSubscriber implements Runnable {

        List<String> messages = new LinkedList<>();
        KafkaSubscriber<Object, String> subscriber;

        /**
         * Default constructor.
         */
        public ExtendedSubscriber() {
            final Properties subscriberConfig = subscriberConfig();
            final KafkaSubscriberConfiguration<String> conf = SubscriberConfigurationBuilder
                    .<String, String> createkafkaConsumerBuilder(subscriberConfig)
                    .addValueDeserializer(subscriberConfig.getProperty("value.deserializer"))
                    .addKeyDeserializer(subscriberConfig.getProperty("key.deserializer")).addTopic(kafkaTopic).addProcessors(1).build();
            subscriber = KafkaFactory.createExtendedKafkaSubscriber(conf);
            subscriber.subscribe(Arrays.asList(kafkaTopic));
        }

        public List<String> getMessages() {
            return messages;
        }

        @Override
        public void run() {
            if (LOG.isInfoEnabled()) {
                LOG.info("Extended subscriber Started......");
            }
            try {

                Collection<String> collectStream = subscriber.collectStream();
                while (collectStream.isEmpty()) {
                    collectStream = subscriber.collectStream();
                }
                messages.addAll(collectStream);
            } finally {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Closing Extended subscriber Thread.");
                }
                subscriber.close();
            }
        }

    }

    /**
     * @return consumer specific configuration.
     */
    private static Properties subscriberConfig() {
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
        props.put("acks", "1");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }

    /**
     * Tear down all services as cleanup.
     *
     * @throws Exception
     *             if unable to tear down successfully.
     */
    @AfterClass
    public static void tearDown() throws Exception {
        kafkaServer.shutdown();
        kafkaServer.awaitShutdown();
        zkTestServer.stop();
        FileDeleteStrategy.FORCE.deleteQuietly(tempDirectory.toFile());
    }
}
