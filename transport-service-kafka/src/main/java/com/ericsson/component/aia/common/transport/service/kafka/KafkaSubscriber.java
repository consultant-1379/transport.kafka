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
package com.ericsson.component.aia.common.transport.service.kafka;

import java.util.*;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import com.ericsson.component.aia.common.transport.service.Subscriber;

/**
 * The <code>KafkaSubscriber</code> provides kafka specific subscriber {@link Subscriber}}
 * @param <K>
 *            key Type.
 * @param <V>
 *            Value Type.
 */
public interface KafkaSubscriber<K, V> extends Subscriber<K, V> {

    /**
     * Get the set of partitions currently assigned to this consumer. If subscription happened by directly assigning partitions using
     * KafkaConsumer#assign(List) then this will simply return the same partitions that were assigned. If topic subscription was used, then
     * this will give the set of topic partitions currently assigned to the consumer (which may be none if the assignment hasn't happened yet, or the
     * partitions are in the process of getting reassigned).
     * @return The set of partitions currently assigned to this consumer
     */
    Collection<TopicPartitionMetaData> getPartions();

    /**
     * Subscribe to the given list of topics to get dynamically assigned partitions. <b>Topic subscriptions are not incremental. This list will
     * replace the current assignment (if there is one).</b> It is not possible to combine topic subscription with group management with manual
     * partition assignment through KafkaConsumer#assign(List).
     * @param topics list of topics needs to subscribe.
     */
    void subscribe(List<String> topics);

    /**
     * Subscribe to all topics matching specified pattern to get dynamically assigned partitions. The pattern matching will be done periodically
     * against topics existing at the time of check.
     * @param pattern
     *            Pattern to subscribe to
     */
    void subscribe(Pattern pattern);

    /**
     * Subscribe to the given topic to get dynamically assigned partitions.
     * @param topic name of topic needs to subscribe.
     */
    void subscribe(String topic);

    /**
     * Unsubscribe from topics currently subscribed with {@link #subscribe(List)}. This also clears any partitions directly assigned through
     * KafkaConsumer#assign(List).
     */
    void unsubscribe();

    /**
     * Get the current subscription. Will return the same topics used in the most recent call to
     * KafkaConsumer#subscribe(List, ConsumerRebalanceListener), or an empty set if no such call has been made.
     * @return The set of topics currently subscribed to
     */
    Set<String> subscription();

    /**
     * Fetch data for the topics or partitions specified using one of the subscribe/assign APIs. It is an error to not have subscribed to any topics
     * or partitions before polling for data.
     * @param timeout
     *            The time, in milliseconds, spent waiting in poll if data is not available. If 0, returns immediately with any records that are
     *            available now. Must not be negative.
     * @return map of topic to records since the last fetch for the subscribed list of topics and partitions
     */
    ConsumerRecords<K, V> poll(long timeout);

    /**
     * KafkaConsumer#pause(TopicPartition...)
     * @param partitions pause TopicPartitions.
     */
    void pause(TopicPartition... partitions);

    /**
     * KafkaConsumer#resume(TopicPartition...)
     * @param partitions resume partitions which were paused using {@link #pause(TopicPartition...)}
     */
    void resume(TopicPartition... partitions);

    /**
     * @see KafkaConsumer#close()
     */
    void close();

    /**
     * @see KafkaConsumer#wakeup()
     */
    void wakeup();

    /**
     * @see #getConsumer()
     * @return consumer.
     */
    KafkaConsumer<K, V> getConsumer();

}
