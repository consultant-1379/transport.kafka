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

import java.util.Collection;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * The <code>SubscriberChangeListener</code> is a callback interface that user can implement to trigger custom actions when the set of partitions
 * assigned to the consumer changes.
 */
public abstract class SubscriberChangeListener implements ConsumerRebalanceListener {
    private static Function<TopicPartition, TopicPartitionMetaData> transformer = new Function<TopicPartition, TopicPartitionMetaData>() {

        @Override
        public TopicPartitionMetaData apply(final TopicPartition input) {
            return new TopicPartitionMetaData(input.partition(), input.topic());
        }
    };

    @Override
    public void onPartitionsRevoked(final Collection<TopicPartition> partitions) {
        if (partitions != null) {
            final Collection<TopicPartitionMetaData> transform = Collections2.transform(partitions, transformer);
            onRevoke(transform);
        }

    }

    @Override
    public void onPartitionsAssigned(final Collection<TopicPartition> partitions) {
        if (partitions != null) {
            final Collection<TopicPartitionMetaData> transform = Collections2.transform(partitions, transformer);
            onAssign(transform);
        }
    }

    /**
     * A callback method the user can implement to provide handling of customized offsets on completion of a successful partition re-assignment. This
     * method will be called after an offset re-assignment completes and before the consumer starts fetching data.
     * @param partitions
     *            list of partitions.
     */
    protected abstract void onAssign(Collection<TopicPartitionMetaData> partitions);

    /**
     * A callback method the user can implement to provide handling of offset commits to a customized store on the start of a rebalance operation.
     * This method will be called before a rebalance operation starts and after the consumer stops fetching data. It is recommended that offsets
     * should be committed in this callback to either Kafka or a custom offset store to prevent duplicate data.
     * @param partitions
     *            list of partitions.
     */
    protected abstract void onRevoke(Collection<TopicPartitionMetaData> partitions);

}
