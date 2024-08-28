/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.common.transport.service.kafka.partitioning;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

/**
 * Round robin partitioner using a simple thread safe AotmicInteger
 */
public class KafkaRoundRobinPartitioner implements Partitioner {

    private static final int MAX_NUMBER_OF_PORTS = 65536;
    final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void configure(final Map<String, ?> configs) {

    }

    @Override
    public int partition(final String topic, final Object key, final byte[] keyBytes, final Object value, final byte[] valueBytes,
            final Cluster cluster) {

        final int partitionId = counter.incrementAndGet() % cluster.partitionCountForTopic(topic);

        if (counter.get() > MAX_NUMBER_OF_PORTS) {
            counter.set(0);
        }
        return partitionId;

    }

    @Override
    public void close() {

    }

}
