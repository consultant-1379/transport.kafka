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
package com.ericsson.component.aia.common.transport.service.kafka.publisher;

import static org.junit.Assert.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Future;

import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.producer.internals.FutureRecordMetadata;
import org.apache.kafka.clients.producer.internals.ProduceRequestResult;
import org.apache.kafka.common.*;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.utils.Time;
import org.junit.*;

import com.ericsson.component.aia.common.transport.config.PublisherConfiguration;
import com.ericsson.component.aia.common.transport.exception.PublisherConfigurationException;
import com.ericsson.component.aia.common.transport.service.MessageServiceTypes;

/**
 * Test functionality of KafkaPublisher.
 */
public class KafkaPublisherTest {
    private static TestProducer<String, String> pub = new TestProducer();

    static final String TOPIC = "test";

    /**
     * Initialize prerequisite associated with test. throws Exception if required things unable to setup.
     */
    @BeforeClass
    public static void init() {
        final Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 1);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("URI", "kafka:/");
        pub.init(new PublisherConfiguration(props, MessageServiceTypes.KAFKA));
    }

    /**
     * Perform cleanup activity.
     */
    @AfterClass
    public static void close() {
        pub.close();
    }

    /**
     * Test {@link KafkaPublisher#getConfig()} methods.
     *
     * @throws PublisherConfigurationException
     *             if configuration object is null.
     */
    @Test
    public void testInitialized() throws PublisherConfigurationException {
        assertNotNull(pub.getConfig());
    }

    /**
     * Test {@link KafkaPublisher#sendMessage(String, Object, Object)} methods.
     */
    @Test
    public void testSendMessageStringKV() {
        pub.sendMessage(TOPIC, "Key", "Message");
        assertEquals(1, pub.getRecords().size());
        pub.flush();

    }

    /**
     * Test {@link KafkaPublisher#sendMessage(String, Integer, Object, Object)} methods.
     */
    @Test
    public void testSendMessageStringPKV() {

        pub.sendMessage(TOPIC, 1, "Key", "Message");
        assertEquals(1, pub.getRecords().size());
        pub.flush();

    }

    /**
     * Test {@link KafkaPublisher#sendMessage(String, Object)} methods.
     */
    @Test
    public void testSendMessageStringV() {
        pub.sendMessage(TOPIC, "Message");
        assertEquals(1, pub.getRecords().size());
        pub.flush();
        pub.close();
    }

}

/**
 * Test stub for KafkaPublisher.
 *
 * @param <K>
 *            Key type.
 * @param <V>
 *            value type.
 */
class TestProducer<K, V> extends KafkaPublisher<K, V> {
    private MockPublisher<K, V> mockPublisher;

    /**
     * create instance of kafka producer
     *
     * @param config
     *            instance of {@link Producer}
     */
    @Override
    protected void createProducer(final PublisherConfiguration config) {
        mockPublisher = new MockPublisher<K, V>();
        producer = mockPublisher;
    }

    public List<ProducerRecord<K, V>> getRecords() {
        return mockPublisher.getRecords();
    }

}

/**
 * Mock class for producer.
 *
 * @param <K>
 *            key type.
 * @param <V>
 *            Value type.
 */
class MockPublisher<K, V> implements Producer<K, V> {
    List<ProducerRecord<K, V>> records = new ArrayList<ProducerRecord<K, V>>();

    public List<ProducerRecord<K, V>> getRecords() {
        return records;
    }

    @Override
    public Future<RecordMetadata> send(final ProducerRecord<K, V> record) {
        final TopicPartition topicPartition = new TopicPartition(KafkaPublisherTest.TOPIC, 0);
        final ProduceRequestResult result = new ProduceRequestResult(topicPartition);
        final FutureRecordMetadata future = new FutureRecordMetadata(result, 0, 0, 0, 0, Time.SYSTEM);
        records.add(record);
        return future;
    }

    @Override
    public Future<RecordMetadata> send(final ProducerRecord<K, V> record, final Callback callback) {
        final TopicPartition topicPartition = new TopicPartition(KafkaPublisherTest.TOPIC, 0);
        final ProduceRequestResult result = new ProduceRequestResult(topicPartition);
        final FutureRecordMetadata future = new FutureRecordMetadata(result, 0, 0, 0, 0,  Time.SYSTEM);
        records.add(record);
        return future;
    }

    @Override
    public void flush() {
        records.clear();
    }

    @Override
    public List<PartitionInfo> partitionsFor(final String topic) {
        final List<PartitionInfo> info = new ArrayList<>();
        final Node node = new Node(0, "localhost", 9092);
        final PartitionInfo partitionInfo = new PartitionInfo(topic, 0, node, new Node[] { node }, new Node[] { node });
        info.add(partitionInfo);
        return info;
    }

    @Override
    public Map<MetricName, ? extends Metric> metrics() {
        return new HashMap<>();
    }

    @Override
    public void close() {
    }

    @Override
    public void close(final Duration timeout) {
    }

    @Override
    public void abortTransaction() {
    }

    @Override
    public void commitTransaction() {
    }

    @Override
    public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> map, ConsumerGroupMetadata consumerGroupMetadata) throws ProducerFencedException {

    }

    @Override
    public void beginTransaction() {
    }

    @Override
    public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> map, String s) throws ProducerFencedException {
    }

    @Override
    public void initTransactions() {
    }
}