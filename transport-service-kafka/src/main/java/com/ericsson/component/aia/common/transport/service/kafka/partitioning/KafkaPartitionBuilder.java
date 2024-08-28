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

import java.util.Properties;

import com.ericsson.aia.common.datarouting.api.builder.DataRouterSinkBuilder;
import com.ericsson.aia.common.datarouting.api.builder.PartitionBuilder;
import com.ericsson.component.aia.common.transport.service.util.KafkaConstants;

/**
 * Kafka specific PartitionBuilder
 *
 * @param <V>
 *            The record type used.
 */
public class KafkaPartitionBuilder<V> extends PartitionBuilder<V> {

    private String partitionClass = "";

    /**
     * Partitions the records using round robin
     *
     * @return DataRouterSinkBuilder
     */
    @Override
    public DataRouterSinkBuilder<V> applyRoundRobin() {
        partitionClass = KafkaRoundRobinPartitioner.class.getName();
        return getDataRouterSinkBuilder();
    }

    public String getPartitionClass() {
        return partitionClass;
    }

    /**
     * Adds a partitioner class to the Kafka sink properties.
     *
     * @param sinkProperties
     *            The existing sink properties.
     */
    public void addPartitionerToProperties(final Properties sinkProperties) {
        if (!partitionClass.isEmpty()) {
            sinkProperties.put(KafkaConstants.PARTITIONER_CLASS, partitionClass);
        }

    }

}
