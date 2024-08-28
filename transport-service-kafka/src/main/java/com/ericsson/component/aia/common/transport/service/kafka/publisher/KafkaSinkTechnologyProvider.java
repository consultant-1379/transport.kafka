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

import java.util.Properties;

import com.ericsson.aia.common.datarouting.api.StreamingSink;
import com.ericsson.aia.common.datarouting.api.SinkTechnologyProvider;
import com.ericsson.aia.common.datarouting.api.builder.PartitionBuilder;
import com.ericsson.component.aia.common.transport.config.KafkaPublisherConfiguration.KafkaPublisherConfigurationBuilder;
import com.ericsson.component.aia.common.transport.config.builders.PublisherConfigurationBuilder;
import com.ericsson.component.aia.common.transport.service.kafka.KafkaFactory;
import com.ericsson.component.aia.common.transport.service.kafka.partitioning.KafkaPartitionBuilder;
import com.ericsson.component.aia.common.transport.service.util.KafkaConstants;

/**
 *
 * This class is responsible for publishing AVRO records to KAFKA.
 *
 * @param <V>
 *            The type of the record being sent.
 */
public class KafkaSinkTechnologyProvider<V> implements SinkTechnologyProvider<V> {
    private static final String PROTOCOL = "kafka";

    private KafkaPartitionBuilder<V> partitionBuilder;

    @Override
    public StreamingSink<?, V> getSink(final Properties sinkProperties) {

        partitionBuilder.addPartitionerToProperties(sinkProperties);

        final KafkaPublisherConfigurationBuilder<String, V> publisherConfigurationBuilder = PublisherConfigurationBuilder
                .<String, V>createkafkaPublisherBuilder(sinkProperties).addKeySerializer(sinkProperties.getProperty(KafkaConstants.KEY_SERIALIZER))
                .addValueSerializer(sinkProperties.getProperty(KafkaConstants.VALUE_SERIALIZER));

        if (sinkProperties.getProperty(KafkaConstants.OUTPUT_THREAD_POOL_SIZE_PARAM) != null) {
            publisherConfigurationBuilder.addProcessors(Integer.parseInt(sinkProperties.getProperty(KafkaConstants.OUTPUT_THREAD_POOL_SIZE_PARAM)));
        }

        return KafkaFactory.<String, V>createKafkaPublisher(publisherConfigurationBuilder.build());
    }

    @Override
    public String getProviderName() {
        return PROTOCOL;
    }

    @Override
    public PartitionBuilder<V> getPartitionBuilder() {
        partitionBuilder = new KafkaPartitionBuilder<>();
        return partitionBuilder;
    }

}
