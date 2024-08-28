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
package com.ericsson.component.aia.common.transport.config.builders;

import static com.google.common.base.Preconditions.*;

import java.util.Properties;

import com.ericsson.component.aia.common.transport.config.KafkaSubscriberConfiguration;
import com.ericsson.component.aia.common.transport.config.KafkaSubscriberConfiguration.KafkaSubScriberConfigurationBuilder;
import com.ericsson.component.aia.common.transport.config.ZMQSubscriberConfiguration;
import com.ericsson.component.aia.common.transport.config.ZMQSubscriberConfiguration.ZMQScriberConfigurationBuilder;
import com.ericsson.component.aia.common.transport.service.MessageServiceTypes;

/**
 * Builder class for subscriber.
 */
public class SubscriberConfigurationBuilder {
    /**
     * Managed Subscriber
     */
    private static boolean  managed;
    /**
     * A common connection properties
     */
    private Properties properties;

    /**
     * Message Type
     */
    private MessageServiceTypes type;
    /**
     * A method to create KAFKA consumer configuration builder
     * @param <K>
     *            the type of keys maintained by this subscriber.
     * @param <V>
     *            the value type holds by subscriber.
     * @param properties
     *            connection properties and application properties.
     * @return instance of {@link KafkaSubScriberConfigurationBuilder}
     */
    public static <K, V> KafkaSubScriberConfigurationBuilder<K, V> createkafkaConsumerBuilder(final Properties properties) {
        final KafkaSubScriberConfigurationBuilder<K, V> kafkaSubScriberConfigurationBuilder = new
                KafkaSubscriberConfiguration.KafkaSubScriberConfigurationBuilder<>(
                properties);
        kafkaSubScriberConfigurationBuilder.addKafkaMessageTyep();
        if (managed) {
            kafkaSubScriberConfigurationBuilder.enableManaged();
        }
        return kafkaSubScriberConfigurationBuilder;
    }
    /**
     * A mechanism to chose the technology type
     * @return {@link SubscriberConfigurationBuilder}
     */
    public SubscriberConfigurationBuilder addAMQType() {
        checkState(type != null, "Messaging type already defined");
        type = MessageServiceTypes.AMQ;
        return this;
    }

    /**
     * A mechanism to chose the technology type
     * @return {@link SubscriberConfigurationBuilder}
     */
    public SubscriberConfigurationBuilder addKafkaType() {
        checkState(type != null, "Messaging type already defined");
        type = MessageServiceTypes.KAFKA;
        return this;
    }

    /**
     * A mechanism to chose the technology type
     * @return {@link SubscriberConfigurationBuilder}
     */
    public SubscriberConfigurationBuilder addZMQType() {
        checkState(type != null, "Messaging type already defined");
        type = MessageServiceTypes.ZMQ;
        return this;
    }

    /**
     * A method to create ZeroMQ consumer configuration builder
     * @param <V>
     *            the value type holds by subscriber.
     * @return instance of {@link KafkaSubScriberConfigurationBuilder}
     */
    public <V> ZMQScriberConfigurationBuilder<V> createZMQConsumerBuilder() {
        return new ZMQSubscriberConfiguration.ZMQScriberConfigurationBuilder<>(properties);
    }

    /**
     * A mechanism to chose the technology type
     * @return {@link SubscriberConfigurationBuilder}
     */
    public SubscriberConfigurationBuilder setManaged() {
        checkState(type != null, "Messaging type already defined");
        managed = true;
        return this;
    }

}
