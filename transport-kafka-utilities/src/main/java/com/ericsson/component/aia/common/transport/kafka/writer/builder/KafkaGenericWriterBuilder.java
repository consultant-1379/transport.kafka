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
package com.ericsson.component.aia.common.transport.kafka.writer.builder;

import static com.google.common.base.Preconditions.*;

import java.io.Serializable;
import java.util.*;

import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.transport.config.ConfigurationIdentity;
import com.ericsson.component.aia.common.transport.config.PublisherConfiguration;
import com.ericsson.component.aia.common.transport.kafka.writer.api.KafkaKeyGenerator;
import com.ericsson.component.aia.common.transport.kafka.writer.impl.*;
import com.ericsson.component.aia.common.transport.service.MessageServiceTypes;
import com.ericsson.component.aia.common.transport.service.Publisher;
import com.ericsson.component.aia.common.transport.service.kafka.KafkaFactory;

/**
 * The Class KafkaGenericWriterBuilder is used to create an instance of KafkaGenericWriter used to write records to KAFKA.
 *
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 */
public class KafkaGenericWriterBuilder<K, V> implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaGenericWriterBuilder.class);

    /** The topic. */
    private String topic;

    /** The value serializer. */
    private String valueSerializer;

    /** The key serializer. */
    private String keySerializer;

    /** The brokers. */
    private String brokers;

    /** The generic kafka properties. */
    private Map<String, String> kafkaProperties = Collections.emptyMap();

    /** The key generator. */
    private KafkaKeyGenerator<K, V> keyGenerator;

    /**
     * Builds the KAFKA writer using the provided attributes.
     *
     * @return the kafka writer
     */
    public KafkaGenericWriter<K, V> build() {
        LOGGER.trace("Trying to build {} based on configuration", KafkaGenericWriter.class.getName());
        checkRequired();

        KafkaGenericWriter<K, V> instance = null;

        if (keyGenerator != null) {
            instance = new KeyedKafkaGenericWriter<K, V>(keyGenerator);
        } else {
            instance = new NoKeyKafkaGenericWriter<K, V>();
        }

        final Properties kafkaProperty = getKafkaProperty();

        final Publisher<String, GenericRecord> createKafkaPublisher = KafkaFactory
                .createKafkaPublisher(new PublisherConfiguration(kafkaProperty, MessageServiceTypes.KAFKA));

        instance.setPublisher(createKafkaPublisher);
        instance.setTopic(topic);
        instance.setProperties(kafkaProperty);

        LOGGER.info("{} configured with Topic {} , Borkers {} ,", KafkaGenericWriter.class.getName(), topic, brokers);

        return instance;
    }

    /**
     * Gets the kafka property.
     *
     * @return the kafka property
     */
    private Properties getKafkaProperty() {
        final Properties kafkaprop = new Properties();
        kafkaprop.putAll(kafkaProperties);
        kafkaprop.put(ConfigurationIdentity.BOOTSTRAP_SERVERS_CONFIG, brokers);
        kafkaprop.put(ConfigurationIdentity.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        kafkaprop.put(ConfigurationIdentity.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        return kafkaprop;
    }

    /**
     * With brokers.
     *
     * @param brokers
     *            the brokers
     * @return the builder
     */
    public KafkaGenericWriterBuilder<K, V> withBrokers(final String brokers) {
        checkArgument(brokers != null && !brokers.isEmpty(), "Borker's name can not be null or empty.");
        this.brokers = brokers;
        return this;
    }

    /**
     * With key serializer.
     *
     * @param keySerializer
     *            the keySerializer to set
     * @return the builder
     */
    public KafkaGenericWriterBuilder<K, V> withKeySerializer(final String keySerializer) {
        checkArgument(keySerializer != null && !keySerializer.isEmpty(), "Kafka Key Serializer can not be null or empty.");
        this.keySerializer = keySerializer;
        return this;
    }

    /**
     * With key generator which should be used to create a key for each record.
     *
     * @param keyGenerator
     *            the key generator.
     * @return the builder
     */
    public KafkaGenericWriterBuilder<K, V> withKeyGenerator(final KafkaKeyGenerator<K, V> keyGenerator) {
        this.keyGenerator = keyGenerator;
        return this;
    }

    /**
     * With topic.
     *
     * @param topic
     *            the topic to set
     * @return the builder
     */
    public KafkaGenericWriterBuilder<K, V> withTopic(final String topic) {
        checkArgument(topic != null && !topic.isEmpty(), "Topic name can not be null or empty.");
        this.topic = topic.trim();
        return this;
    }

    /**
     * With value serializer.
     *
     * @param valueSerializer
     *            the valueSerializer to set
     * @return the builder
     */
    public KafkaGenericWriterBuilder<K, V> withValueSerializer(final String valueSerializer) {
        checkArgument(valueSerializer != null && !valueSerializer.isEmpty(), "Kafka Value Serializer can not be null or empty.");
        this.valueSerializer = valueSerializer;
        return this;
    }

    /**
     * With topic.
     *
     * @param kafkaProperties
     *            Adds generic kafka properties.
     * @return the builder
     */
    public KafkaGenericWriterBuilder<K, V> withProperties(final Map<String, String> kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
        return this;
    }

    /**
     * Creates an instance of KafkaGenericWriterBuilder.
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @return a new instance of the builder.
     */
    public static <K, V> KafkaGenericWriterBuilder<K, V> create() {
        return new KafkaGenericWriterBuilder<K, V>();
    }

    /**
     * Check required.
     */
    private void checkRequired() {
        LOGGER.trace("checking for required value for {}", this.getClass().getName());
        checkArgument(topic != null && !topic.isEmpty(), "Topic name can not be null or empty.");
        checkArgument(brokers != null && !brokers.isEmpty(), "Borker's name can not be null or empty.");
        checkArgument(keySerializer != null && !keySerializer.isEmpty(), "Kafka Key Serializer can not be null or empty.");
        checkArgument(valueSerializer != null && !valueSerializer.isEmpty(), "Kafka Value Serializer can not be null or empty.");
        LOGGER.trace("Required value validated successfully");
    }

}
