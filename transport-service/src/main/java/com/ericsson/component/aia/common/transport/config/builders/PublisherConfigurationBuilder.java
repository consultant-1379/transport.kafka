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

import com.ericsson.component.aia.common.transport.config.KafkaPublisherConfiguration;
import com.ericsson.component.aia.common.transport.config.KafkaPublisherConfiguration.KafkaPublisherConfigurationBuilder;
import com.ericsson.component.aia.common.transport.service.MessageServiceTypes;

/**
 * Builder class for publishers.
 */
public class PublisherConfigurationBuilder {

    /**
     * Managed Subscriber
     */
    private boolean managed;

    /**
     * Message Type
     */
    private MessageServiceTypes type;

    /**
     * A method to create KAFKA consumer configuration builder
     * @param <K>
     *            the type of keys maintained by this publisher.
     * @param <V>
     *            the value type holds by publisher.
     * @param props
     *            connection properties and application properties.
     * @return instance of {@link KafkaPublisherConfigurationBuilder}
     */
    public static <K, V> KafkaPublisherConfigurationBuilder<K, V> createkafkaPublisherBuilder(final Properties props) {
        final KafkaPublisherConfigurationBuilder<K, V> kafkaPublisherConfigurationBuilder = new
                KafkaPublisherConfiguration.KafkaPublisherConfigurationBuilder<>(
                props);
        // kafkaPublisherConfigurationBuilder.addKafkaMessageType();
        return kafkaPublisherConfigurationBuilder;
    }

    /**
     * A method to create ZeroMQ consumer configuration builder
     * @param <V>
     *            the value type holds by publisher.
     * @return instance of {@link KafkaPublisherConfigurationBuilder}
     */
    public static <V> Object createZMQPublisherBuilder() {
        throw new UnsupportedOperationException("No ZMQ provider, not supported yet!!!");
    }

    /**
     * A mechanism to chose the technology type
     * @return {@link PublisherConfigurationBuilder}
     */
    public PublisherConfigurationBuilder addAMQType() {
        checkState(type != null, "Messaging type already defined");
        type = MessageServiceTypes.AMQ;
        return this;
    }

    /**
     * A mechanism to chose the technology type
     * @return {@link PublisherConfigurationBuilder}
     */
    public PublisherConfigurationBuilder addKafkaType() {
        checkState(type != null, "Messaging type already defined");
        type = MessageServiceTypes.KAFKA;
        return this;
    }

    /**
     * A mechanism to chose the technology type
     * @return {@link PublisherConfigurationBuilder}
     */
    public PublisherConfigurationBuilder addZMQType() {
        checkState(type != null, "Messaging type already defined");
        type = MessageServiceTypes.ZMQ;
        return this;
    }

    /**
     * access method for the property flag managed
     * @return the managed
     */
    public boolean isManaged() {
        return managed;
    }

    /**
     * A mechanism to chose the technology type
     * @return {@link PublisherConfigurationBuilder}
     */
    public PublisherConfigurationBuilder setManaged() {
        checkState(type != null, "Messaging type already defined");
        managed = true;
        return this;
    }

}
