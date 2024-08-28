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

import java.util.*;

import com.ericsson.component.aia.common.transport.exception.SubscriberConfigurationException;
import com.ericsson.component.aia.common.transport.service.GenericEventListener;
import com.ericsson.component.aia.common.transport.service.MessageServiceTypes;

/**
 * The <code>KafkaSubscriberConfiguration</code> provides easy way to build the kafka configuration for the subscriber using builder pattern.
 * @param <V>
 *            Value type
 */
public final class KafkaSubscriberConfiguration<V> extends SubscriberConfiguration<V> {

    /**
     * This class simplifies the creation of kafka subscriber by provided set by step chaining of configuration based on builder design pattern.
     * @param <K>
     *            key type.
     * @param <V>
     *            value type.
     */
    @SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity" })
    public static class KafkaSubScriberConfigurationBuilder<K, V> {

        /**
         * Holds the connection properties.
         */
        private final Properties connectionProperties;
        /**
         * Holds the list of interested topics.
         */
        private final Set<String> topicList = new HashSet<>();
        /**
         * Holds the list of listener interested to subscribe the events.
         */
        private final Set<GenericEventListener<V>> listenerSet = new HashSet<>();
        /**
         * Number of Subscriber processors(threads).
         */
        private int processors = 1;
        /**
         * Kafka Key serialization class.
         */
        private String keyDeserializerClass;
        /**
         * Holds the name of Kafka deserializer.
         */
        private String valueDeserializerClass;
        /**
         * Holds the value associated with managed.
         */
        private boolean managed;

        /**
         * A representation of supported Messaging technology types.
         */
        private MessageServiceTypes messsageAPIType;

        /**
         * Kafka specific consumer configuration builder.
         * @param connProperties
         *            connection property.
         */
        public KafkaSubScriberConfigurationBuilder(final Properties connProperties) {
            this.connectionProperties = connProperties;
        }

        /**
         * Kafka Message Type
         */
        public void addKafkaMessageTyep() {
            this.messsageAPIType = MessageServiceTypes.KAFKA;
        }

        /**
         * Provide fully qualified name of class which is responsible for deserialization of key.
         * @param keyDeserializerClass
         *            full qualified Deserializer class name.
         * @return instance of KafkaSubScriberConfigurationBuilder
         */
        public KafkaSubScriberConfigurationBuilder<K, V> addKeyDeserializer(final String keyDeserializerClass) {
            checkArgument(keyDeserializerClass != null && keyDeserializerClass.trim().length() > 0, "Key Deserializer not cannot be null or empty");
            this.keyDeserializerClass = keyDeserializerClass;
            return this;
        }

        /**
         * Add instance of listener intended to register for topics event.
         * @param listener
         *            list of listener interested to subscribe the events.
         * @return instance of KafkaSubScriberConfigurationBuilder
         */
        public KafkaSubScriberConfigurationBuilder<K, V> addListener(final GenericEventListener<V> listener) {
            checkArgument(listener != null, "GenericEventListener object  cannot be null");
            listenerSet.add(listener);
            return this;

        }

        /**
         * Specify the number of process needs to be created.
         * @param count
         *            value should be grater then 0
         * @return instance of KafkaSubScriberConfigurationBuilder
         */
        public KafkaSubScriberConfigurationBuilder<K, V> addProcessors(final int count) {
            checkArgument(count > 0, "Invalid processor count,It must be greater than zero.");
            processors = count;
            return this;

        }

        /**
         * Add topic for which subscriber needs to subscribe.
         * @param topic
         *            : name of topic.
         * @return instance of KafkaSubScriberConfigurationBuilder
         */
        public KafkaSubScriberConfigurationBuilder<K, V> addTopic(final String topic) {
            checkArgument(topic != null && topic.trim().length() > 0, "Topic name cannot be null");
            topicList.add(topic);
            return this;
        }

        /**
         * Provide fully qualified name of class which is responsible for deserialization of value.
         * @param valueDeserializerClass
         *            full qualified Deserializer class name.
         * @return instance of KafkaSubScriberConfigurationBuilder
         */
        public KafkaSubScriberConfigurationBuilder<K, V> addValueDeserializer(final String valueDeserializerClass) {
            checkArgument(valueDeserializerClass != null && valueDeserializerClass.trim().length() > 0,
                    "Value Deserializer not cannot be null or empty");
            this.valueDeserializerClass = valueDeserializerClass;
            return this;
        }

        /**
         * A method to create the ConsumerConfigurations.
         * @return instance of {@link SubscriberConfiguration}
         */
        public KafkaSubscriberConfiguration<V> build() {
            checkAndAssignRequired();
            final KafkaSubscriberConfiguration<V> consumerConfiguration = new KafkaSubscriberConfiguration<V>(connectionProperties);
            final List<String> arrayList = new ArrayList<>();
            arrayList.addAll(topicList);
            consumerConfiguration.setProcessors(processors);
            consumerConfiguration.setTopics(arrayList);
            consumerConfiguration.setManaged(managed);
            consumerConfiguration.setListeners(listenerSet);
            // consumerConfiguration.eanbleManaged();
            return consumerConfiguration;

        }

        @SuppressWarnings("PMD.NPathComplexity")
        private void checkAndAssignRequired() {
            if (connectionProperties == null) {
                throw new SubscriberConfigurationException("Connection properties cannot be null.");
            }

            if (topicList.isEmpty()) {
                throw new SubscriberConfigurationException("Missing topic names, atleast sigle topic must be provided.");
            }
            if (!connectionProperties.containsKey(ConfigurationIdentity.BOOTSTRAP_SERVERS_CONFIG)) {
                throw new SubscriberConfigurationException("Kafka " + ConfigurationIdentity.BOOTSTRAP_SERVERS_CONFIG + " property is missing. ");
            }
            if (!connectionProperties.containsKey(ConfigurationIdentity.GROUP_ID_CONFIG)) {
                throw new SubscriberConfigurationException("Kafka " + ConfigurationIdentity.GROUP_ID_CONFIG + " property is missing. ");
            }

            if (!connectionProperties.containsKey(ConfigurationIdentity.SESSION_TIMEOUT_MS_CONFIG)) {
                connectionProperties.put(ConfigurationIdentity.SESSION_TIMEOUT_MS_CONFIG, "30000");
            }
            if (!connectionProperties.containsKey(ConfigurationIdentity.ENABLE_AUTO_COMMIT_CONFIG)) {
                connectionProperties.put(ConfigurationIdentity.ENABLE_AUTO_COMMIT_CONFIG, "true");
            }
            if (Boolean.valueOf(((String) connectionProperties.get(ConfigurationIdentity.ENABLE_AUTO_COMMIT_CONFIG)))) {
                if (!connectionProperties.containsKey(ConfigurationIdentity.AUTO_COMMIT_INTERVAL_MS_CONFIG)) {
                    connectionProperties.put(ConfigurationIdentity.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");
                }

            }

            if (listenerSet == null) {
                throw new SubscriberConfigurationException("No subscribers configured. ");
            }
            if (processors <= 0) {
                throw new SubscriberConfigurationException("Invalid Processors requested. ");
            }
            if (keyDeserializerClass != null && keyDeserializerClass.trim().length() < 0) {
                throw new SubscriberConfigurationException("Invalid Key Deserializer class. ");
            }
            if (valueDeserializerClass != null && valueDeserializerClass.trim().length() < 0) {
                throw new SubscriberConfigurationException("Invalid Value Deserializer class. ");
            }
            connectionProperties.put(ConfigurationIdentity.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClass);
            connectionProperties.put(ConfigurationIdentity.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass);

        }

        /**
         * sets Managed processors to run
         * @return instance of this class
         */
        public KafkaSubScriberConfigurationBuilder<K, V> enableManaged() {
            this.managed = true;
            return this;
        }

        public MessageServiceTypes getMesssageAPIType() {
            return messsageAPIType;
        }

    }

    private Set<GenericEventListener<V>> listeners;
    /**
     * Holds number of Consumer Threads.
     */
    private int processorCount = 1;
    /**
     * Holds list of topics.
     */
    private List<String> topicList;

    /**
     * Type of implementation managed or extended.
     */
    @SuppressWarnings("unused")
    private boolean managed;

    private KafkaSubscriberConfiguration(final Properties props) {
        super(props);
    }

    /**
     * Disabled managed Type.
     */
    public void desableMaged() {
        this.managed = false;
    }

    /**
     * Enabled Managed Type.
     */
    public void eanbleManaged() {
        managed = true;

    }

    /**
     * @return list of GenericEventListener.
     */
    public Set<GenericEventListener<V>> getListeners() {
        return listeners;
    }

    /**
     * @return number of subscriber Threads
     */
    public int getProcessors() {
        return processorCount;
    }

    /**
     * @return List of topics subscribed.
     */
    public List<String> getTopicList() {
        return topicList;
    }

    /**
     * @return isManaged or not.
     */
    @Override
    public boolean isManaged() {
        return super.isManaged();
    }

    /**
     * A method add number of subscribers.
     * @param listenerSet
     *            is a set of {@link GenericEventListener} objects.
     */
    public void setListeners(final Set<GenericEventListener<V>> listenerSet) {
        this.listeners = listenerSet;
    }

    /**
     * Number of consumer threads
     * @param processors
     *            number of subscriber threads.
     */
    public void setProcessors(final int processors) {
        this.processorCount = processors;

    }

    /**
     * A method to add a list of topics.
     * @param arrayList
     *            is {@link List} String topic names
     */
    public void setTopics(final List<String> arrayList) {
        this.topicList = arrayList;

    }

}
