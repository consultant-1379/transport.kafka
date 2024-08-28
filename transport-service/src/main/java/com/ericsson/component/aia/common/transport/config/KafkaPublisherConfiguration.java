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
package com.ericsson.component.aia.common.transport.config;

import static com.google.common.base.Preconditions.*;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.transport.exception.PublisherConfigurationException;
import com.ericsson.component.aia.common.transport.service.MessageServiceTypes;

/**
 * The <code>KafkaPublisherConfiguration</code> provides easy way to build the kafka configuration for the publisher using builder pattern.
 * @param <V>
 *            value.
 */
public class KafkaPublisherConfiguration<V> extends PublisherConfiguration {

    private static final  Logger LOG = LoggerFactory.getLogger(KafkaPublisherConfiguration.class);

    /**
     * Builder class to facilitate the creation of configuration.
     * @param <K>
     *            key type.
     * @param <V>
     *            value type.
     */
    public static class KafkaPublisherConfigurationBuilder<K, V> {

        /**
         * Connection properties.
         */
        private final Properties connectionProperties;

        /**
         * Number of Publisher processors(threads)
         */
        private int processors = 1;

        /**
         * Assigned to Kafka String serialization class
         */
        private String keySerializerClass = "org.apache.kafka.common.serialization.StringSerializer";

        /**
         * Assigned to Kafka String serialization class
         */
        private String valueSerializerClass = "org.apache.kafka.common.serialization.StringSerializer";

        /**
         * Kafka specific producer configuration builder.
         * @param connProperties
         *            connection properties.
         */
        public KafkaPublisherConfigurationBuilder(final Properties connProperties) {
            this.connectionProperties = connProperties;
        }

        /**
         * @param keySerializerClass
         *            full qualified serializer class name.
         * @return instance of KafkaPublisherConfigurationBuilder
         */
        public KafkaPublisherConfigurationBuilder<K, V> addKeySerializer(final String keySerializerClass) {
            checkArgument(keySerializerClass != null && keySerializerClass.trim().length() > 0, "Key Serializer not cannot be null or empty");
            this.keySerializerClass = keySerializerClass;
            if (LOG.isInfoEnabled()) {
                LOG.info("keySerializerClass [" + keySerializerClass + "]");
            }
            return this;
        }

        /**
         * @param count
         *            Number of Publisher processors(threads)
         * @return instance of KafkaPublisherConfigurationBuilder
         */
        public KafkaPublisherConfigurationBuilder<K, V> addProcessors(final int count) {
            checkArgument(count > 0, "Invalid processor count, it should be 1 on ore greathan one.");
            processors = count;
            return this;

        }

        /**
         * @param valueSerializerClass
         *            full qualified serializer class name.
         * @return instance of KafkaPublisherConfigurationBuilder
         */
        public KafkaPublisherConfigurationBuilder<K, V> addValueSerializer(final String valueSerializerClass) {
            checkArgument(valueSerializerClass != null && valueSerializerClass.trim().length() > 0, "Value Serializer not cannot be null or empty");
            this.valueSerializerClass = valueSerializerClass;
            if (LOG.isInfoEnabled()) {
                LOG.info("valueSerializerClass [" + valueSerializerClass + "]");
            }
            return this;
        }

        /**
         * A method to create the PublisherConfigurations.
         * @return instance of {@link PublisherConfiguration}
         */
        public KafkaPublisherConfiguration<V> build() {
            checkAndAssignRequired();
            final KafkaPublisherConfiguration<V> publisherConfiguration = new KafkaPublisherConfiguration<V>(connectionProperties,
                    MessageServiceTypes.KAFKA);
            publisherConfiguration.setProcessors(processors);
            return publisherConfiguration;
        }

        /**
         * Check for required properties.
         */
        @SuppressWarnings("PMD.NPathComplexity")
        private void checkAndAssignRequired() {
            if (connectionProperties == null) {
                throw new PublisherConfigurationException("Connection properties cannot be null.");
            }

            // Mandatory properties
            if (!connectionProperties.containsKey(ConfigurationIdentity.BOOTSTRAP_SERVERS_CONFIG)) {
                throw new PublisherConfigurationException("Kafka " + ConfigurationIdentity.BOOTSTRAP_SERVERS_CONFIG + " property is missing. ");
            }

            /**
             * @TODO - Should refactor ConfigurationIdentity to split into 1. MANDATORY PROPERTIES, 2. MESSAGING SERVICE TYPE(kafka/zmq etc.,)
             *       PROPERTIES 3. APPLICATION SPECIFIC properties - processor count / no., of threads etc., 4. Should find invalid properties
             *       supplied 5. Should we check types? Kafka does it anyway.
             */
            if (!connectionProperties.containsKey(ConfigurationIdentity.ACKS_CONFIG)) {
                connectionProperties.put(ConfigurationIdentity.ACKS_CONFIG, "all");
            }
            if (!connectionProperties.containsKey(ConfigurationIdentity.RETRIES_CONFIG)) {
                connectionProperties.put(ConfigurationIdentity.RETRIES_CONFIG, "0");
            }
            if (!connectionProperties.containsKey(ConfigurationIdentity.BATCH_SIZE_CONFIG)) {
                connectionProperties.put(ConfigurationIdentity.BATCH_SIZE_CONFIG, "16384");
            }
            if (!connectionProperties.containsKey(ConfigurationIdentity.LINGER_MS_CONFIG)) {
                connectionProperties.put(ConfigurationIdentity.LINGER_MS_CONFIG, "1");
            }
            if (!connectionProperties.containsKey(ConfigurationIdentity.BUFFER_MEMORY_CONFIG)) {
                connectionProperties.put(ConfigurationIdentity.BUFFER_MEMORY_CONFIG, "33554432");
            }

            connectionProperties.put(ConfigurationIdentity.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
            connectionProperties.put(ConfigurationIdentity.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);

            if (processors <= 0) {
                throw new PublisherConfigurationException("Invalid Processors requested. ");
            }

        }

    } // end of static class

    /**
     * Holds the processorCount. default value 1.
     */
    private int processorCount = 1;

    private KafkaPublisherConfiguration(final Properties props, final MessageServiceTypes type) {
        super(props, type);
    }

    /**
     * @return Number of producer threads.
     */
    public int getProcessors() {
        return processorCount;
    }

    /**
     * Number of producer threads
     * @param processors
     *            number of threads.
     */
    public void setProcessors(final int processors) {
        this.processorCount = processors;
    }

    /**
     * Method returns String represents of current class.
     */
    @Override
    public String toString() {
        return "[{Properties: " + this.getProperties() + "}, processCount:" + this.getProcessors() + "]";
    }
}
