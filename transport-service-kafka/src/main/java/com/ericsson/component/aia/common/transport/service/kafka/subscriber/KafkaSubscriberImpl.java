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
package com.ericsson.component.aia.common.transport.service.kafka.subscriber;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.*;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.internals.NoOpConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.aia.common.datarouting.api.EventListener;
import com.ericsson.component.aia.common.transport.config.SubscriberConfiguration;
import com.ericsson.component.aia.common.transport.service.kafka.KafkaSubscriber;
import com.ericsson.component.aia.common.transport.service.kafka.TopicPartitionMetaData;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;


/**
 * This class implements the Kafka specific subscriber.
 *
 * @param <K>
 *            Key type.
 * @param <V>
 *            Value Type.
 */
public class KafkaSubscriberImpl<K, V> implements KafkaSubscriber<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaSubscriberImpl.class);
    /**
     * Holds instance of Kafka Consumer.
     */
    private KafkaConsumer<K, V> consumer;
    /**
     * Holds polling timeout.
     */
    private long timeOut;

    @Override
    public void init(final SubscriberConfiguration<V> configuration) {
        validate(configuration, "Consumer Configuration cannot be null.");
        if (LOG.isInfoEnabled()) {
            LOG.info("Kafka Subscriber intialized.");
        }
        final Properties properties = configuration.getProperties();
        consumer = new KafkaConsumer<>(properties);
        timeOut = Long.parseLong(properties.getProperty("application.poll.timeout", "1000"));
    }

    /**
     * Validate configuration
     *
     * @param configuration
     *            subscriber configuration.
     * @param message
     *            raise alert if configuration is null.
     */
    private void validate(final SubscriberConfiguration<V> configuration, final String message) {
        checkArgument(configuration != null, message);
    }

    /**
     * Get the set of partitions currently assigned to this consumer. If subscription happened by directly assigning partitions using
     * KafkaConsumer#assign(List) then this will simply return the same partitions that were assigned. If topic subscription was used, then
     * this will give the set of topic partitions currently assigned to the consumer (which may be none if the assignment hasn't happened yet, or the
     * partitions are in the process of getting reassigned).
     *
     * @return The set of partitions currently assigned to this consumer
     */
    @Override
    public Collection<TopicPartitionMetaData> getPartions() {
        checkState(consumer != null, "Consumer not initialized.");
        // Transforms the return the collection of partitions
        return Collections2.transform(consumer.assignment(), new Function<TopicPartition, TopicPartitionMetaData>() {
            @Override
            public TopicPartitionMetaData apply(final TopicPartition input) {
                return new TopicPartitionMetaData(input.partition(), input.topic());
            }

        });
    }

    /**
     * Subscribe to the given list of topics to get dynamically assigned partitions. <b>Topic subscriptions are not incremental. This list will
     * replace the current assignment (if there is one).</b> It is not possible to combine topic subscription with group management with manual
     * partition assignment through KafkaConsumer#assign(List).
     */
    @Override
    public void subscribe(final List<String> topics) {
        checkArgument((topics != null) && !topics.isEmpty(), "Topic list cannot be empty");
        checkState(consumer != null, "Consumer not initialized.");
        consumer.subscribe(topics);
        if (LOG.isInfoEnabled()) {
            LOG.info("Kafka Subscriber subscribed topics." + topics);
        }
    }

    /**
     * Subscribe to all topics matching specified pattern to get dynamically assigned partitions. The pattern matching will be done periodically
     * against topics existing at the time of check.
     *
     * @param pattern
     *            Pattern to subscribe to
     */
    @Override
    public void subscribe(final Pattern pattern) {
        checkArgument(pattern != null, "Topic search pattern  cannot be empty");
        checkState(consumer != null, "Consumer not initialized.");
        consumer.subscribe(pattern, new NoOpConsumerRebalanceListener());
        if (LOG.isInfoEnabled()) {
            LOG.info("Kafka Subscriber subscribed pattern." + pattern);
        }
    }

    /**
     * Subscribe to the given topic to get dynamically assigned partitions.
     */
    @Override
    public void subscribe(final String topic) {
        checkArgument((topic != null) && (topic.trim().length() > 0), "Invalid topic name(Topic name is null or empty string). ");
        checkState(consumer != null, "Consumer not initialized.");
        consumer.subscribe(Arrays.asList(topic));
        if (LOG.isInfoEnabled()) {
            LOG.info("Kafka Subscriber subscribed single topic " + topic);
        }
    }

    /**
     * Unsubscribe from topics currently subscribed with {@link KafkaSubscriberImpl#subscribe(List)}. This also clears any partitions directly
     * assigned through KafkaConsumer#assign(List).
     */
    @Override
    public void unsubscribe() {
        checkState(consumer != null, "Consumer not initialized.");
        consumer.unsubscribe();
    }

    /**
     * Get the current subscription. Will return the same topics used in the most recent call to
     * KafkaConsumer#subscribe(List, ConsumerRebalanceListener), or an empty set if no such call has been made.
     *
     * @return The set of topics currently subscribed to
     */
    @Override
    public Set<String> subscription() {
        checkState(consumer != null, "Consumer not initialized.");
        return consumer.subscription();
    }

    /**
     * Fetch data for the topics or partitions specified using one of the subscribe/assign APIs. It is an error to not have subscribed to any topics
     * or partitions before polling for data.
     *
     * @param timeout
     *            The time, in milliseconds, spent waiting in poll if data is not available. If 0, returns immediately with any records that are
     *            available now. Must not be negative.
     * @return map of topic to records since the last fetch for the subscribed list of topics and partitions
     */
    @Override
    public ConsumerRecords<K, V> poll(final long timeout) {
        checkState(consumer != null, "Consumer not initialized.");
        return consumer.poll(timeout);
    }

    /**
     * KafkaConsumer#pause(TopicPartition...)
     */
    @Override
    public void pause(final TopicPartition... partitions) {
        checkState(consumer != null, "Consumer not initialized.");
        consumer.pause(Arrays.asList(partitions));
    }

    /**
     * KafkaConsumer#resume(TopicPartition...)
     */
    @Override
    public void resume(final TopicPartition... partitions) {
        checkState(consumer != null, "Consumer not initialized.");
        consumer.resume(Arrays.asList(partitions));
    }

    /**
     * @see KafkaConsumer#close()
     */
    @Override
    public void close() {
        checkState(consumer != null, "Consumer not initialized.");
        consumer.close();
        if (LOG.isInfoEnabled()) {
            LOG.info("Kafka Subscriber closed ");
        }
    }

    /**
     * @see KafkaConsumer#wakeup()
     */
    @Override
    public void wakeup() {
        checkState(consumer != null, "Consumer not initialized.");
        consumer.wakeup();
    }

    /**
     * Start the consumer. The Managed class provides its default implementation. see {@link ManagedkaSubscriberProxy#start()}
     */
    @Override
    public void start() {
    }

    @Override
    public KafkaConsumer<K, V> getConsumer() {
        return consumer;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.component.aia.common.transport.service.Subscriber# registerEventListener(com.ericsson.component.aia.common.transport.service.
     * GenericEventListener)
     */
    @Override
    public void registerEventListener(final EventListener<V> listener) {
        throw new UnsupportedOperationException("please use runnable version. This is just API ");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.component.aia.common.transport.service.Subscriber#removeEventListener(
     * com.ericsson.component.aia.common.transport.service.GenericEventListener)
     */
    @Override
    public void removeEventListener(final EventListener<V> listener) {
        throw new UnsupportedOperationException("please use runnable version. This is just API ");

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.component.aia.common.transport.service.Subscriber#collectStream()
     */
    @Override
    public Collection<V> collectStream() {
        final ConsumerRecords<K, V> poll = poll(timeOut);
        if (poll == null) {
            return new ArrayList<>();
        }
        return trasform(poll);
    }

    /**
     * @return Kafka polling timeout value from configuration.
     */
    public long getTimeOut() {
        return timeOut;
    }

    /**
     * A method to transform the Consumer records to Collection of values of type V
     *
     * @param result
     *            values that needs to be transform.
     * @return Collection of v type.
     */
    protected Collection<V> trasform(final ConsumerRecords<K, V> result) {
        final Iterable<V> transform = Iterables.transform(result, new Function<ConsumerRecord<K, V>, V>() {
            @Override
            public V apply(final ConsumerRecord<K, V> input) {
                return input.value();
            }

        });
        final Collection<V> output = new ArrayList<>();
        // transform.forEach(output::add);
        for (final V value : transform) {
            output.add(value);
        }
        return output;
    }

}
