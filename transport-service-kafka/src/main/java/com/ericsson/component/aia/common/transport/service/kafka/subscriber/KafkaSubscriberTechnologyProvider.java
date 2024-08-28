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

import java.util.Properties;

import com.ericsson.aia.common.datarouting.api.SourceTechnologyProvider;
import com.ericsson.aia.common.datarouting.api.StreamingSource;
import com.ericsson.component.aia.common.transport.config.KafkaSubscriberConfiguration;
import com.ericsson.component.aia.common.transport.config.KafkaSubscriberConfiguration.KafkaSubScriberConfigurationBuilder;
import com.ericsson.component.aia.common.transport.config.builders.SubscriberConfigurationBuilder;
import com.ericsson.component.aia.common.transport.service.kafka.KafkaFactory;
import com.ericsson.component.aia.common.transport.service.util.KafkaConstants;

/**
 *
 * Provider for KAFKA subscribers
 *
 * @param <K>
 *            The type of the record key.
 *
 * @param <V>
 *            The record being consumed.
 */
public class KafkaSubscriberTechnologyProvider<K, V> implements SourceTechnologyProvider<K, V> {

    private static final String PROTOCOL = "kafka";

    @Override
    public String getProviderName() {
        return PROTOCOL;
    }

    @Override
    public StreamingSource<K, V> getSource(final String topicName, final Properties sourceProperties) {

        final KafkaSubScriberConfigurationBuilder<String, V> subscriberConfigurationBuilder = SubscriberConfigurationBuilder
                .<String, V>createkafkaConsumerBuilder(sourceProperties);

        subscriberConfigurationBuilder.addTopic(topicName)
                .addKeyDeserializer(sourceProperties.getProperty(KafkaConstants.KAFKA_KEY_DESERIALIZER_CLASS))
                .addValueDeserializer(sourceProperties.getProperty(KafkaConstants.KAFKA_VALUE_DE_SERIALIZER_CLASS));

        if (sourceProperties.getProperty(KafkaConstants.INPUT_THREAD_POOL_SIZE_PARAM) != null) {
            subscriberConfigurationBuilder.addProcessors(Integer.parseInt(sourceProperties.getProperty(KafkaConstants.INPUT_THREAD_POOL_SIZE_PARAM)));
        }

        final KafkaSubscriberConfiguration<V> configuration = subscriberConfigurationBuilder.enableManaged().build();

        return KafkaFactory.<K, V>createKafkaSubscriber(configuration);
    }

}
