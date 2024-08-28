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

import java.util.Properties;

/**
 * The <code>ZMQSubscriberConfiguration</code> class is specific implementation of ZeroMQ for kafka.representing the kafka configuration for the
 * @param <V>
 *            Value type
 */
public final class ZMQSubscriberConfiguration<V> extends SubscriberConfiguration<V> {

    /**
     * The <code>ZMQScriberConfigurationBuilder</code> helps to build the subscriber configuration.
     * @param <V>
     *            value type.
     */
    public static class ZMQScriberConfigurationBuilder<V> {

        /**
         * Holds instance of connection properties.
         */
        private final Properties connectionProperties;

        /**
         * Kafka specific consumer configuration builder.
         * @param connProperties
         *            connection properties.
         */
        public ZMQScriberConfigurationBuilder(final Properties connProperties) {
            this.connectionProperties = connProperties;
        }

        /**
         * A method to create the ConsumerConfigurations.
         * @return instance of {@link SubscriberConfiguration}
         */
        public ZMQSubscriberConfiguration<V> build() {
            throw new UnsupportedOperationException("No provider");

        }

    }

    //    private Set<GenericEventListener<V>> listeners;
    //
    //    /**
    //     * Number of processor thread.
    //     */
    //    private int processorCount = 1;
    //    /**
    //     * List of topics.
    //     */
    //    private List<String> topicList;
    //    /**
    //     * Holds information related to managed or unmanaged types of implementation.
    //     */
    //    private boolean managed;
    //
    private ZMQSubscriberConfiguration(final Properties props) {
        super(props);
    }
    //
    //    /**
    //     * @return list of GenericEventListener.
    //     */
    //    public Set<GenericEventListener<V>> getListeners() {
    //        return listeners;
    //    }
    //
    //    /**
    //     * @return Number of subscriber threads.
    //     */
    //    public int getProcessorCount() {
    //        return processorCount;
    //    }
    //
    //    /**
    //     * @return List of topics.
    //     */
    //    public List<String> getTopicList() {
    //        return topicList;
    //    }
    //
    //    /**
    //     * A method add number of subscribers.
    //     * @param listenerSet
    //     *            is a set of {@link GenericEventListener} objects.
    //     */
    //    public void setListeners(Set<GenericEventListener<V>> listenerSet) {
    //        this.listeners = listenerSet;
    //    }
    //
    //    public void setProcessorCount(int processorCount) {
    //        this.processorCount = processorCount;
    //    }
    //
    //    /**
    //     * Number of consumer threads
    //     * @param processors
    //     */
    //    public void setProcessors(int processors) {
    //        this.processorCount = processors;
    //
    //    }
    //
    //    /**
    //     * A method to add a list of topics.
    //     * @param arrayList
    //     *            is {@link List} String topic names
    //     */
    //    public void setTopics(List<String> arrayList) {
    //        this.topicList = arrayList;
    //
    //    }

    //    /**
    //     * Enabled Managed Type.
    //     */
    //    public void eanbleManaged() {
    //        managed = true;
    //
    //    }

}
