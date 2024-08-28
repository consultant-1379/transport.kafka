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

import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

/**
 * A <code>AllGoSinglePartition</code>class for data partitioning and it moves data to single partition. This is only for test purpose
 */

public final class AllGoSinglePartition implements Partitioner {
    /**
     * Configure this class with the given key-value pairs
     */
    @Override
    public void configure(final Map<String, ?> configs) {
    }

    /**
     * Compute the partition for the given record.
     *
     * @param topic
     *            The topic name
     * @param key
     *            The key to partition on (or null if no key)
     * @param keyBytes
     *            The serialized key to partition on( or null if no key)
     * @param value
     *            The value to partition on or null
     * @param valueBytes
     *            The serialized value to partition on or null
     * @param cluster
     *            The current cluster metadata
     */
    @Override
    public int partition(final String topic, final Object key, final byte[] keyBytes, final Object value, final byte[] valueBytes,
                         final Cluster cluster) {
        return 0;
    }

    /**
     * This is called when partitioner is closed.
     */
    @Override
    public void close() {
    }
}