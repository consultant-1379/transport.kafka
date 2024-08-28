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
package com.ericsson.component.aia.common.transport.service;

import com.ericsson.aia.common.datarouting.api.StreamingSink;
import com.ericsson.component.aia.common.transport.config.PublisherConfiguration;
import com.ericsson.component.aia.common.transport.exception.PublisherConfigurationException;

/**
 * The <code>Publisher</code> interface is a common interface for all messaging service implementations such <b>kafka</b>,<b>ZeroMQ</b>, <b>RabitMQ
 * </b>.
 *
 * @param <K>
 *            the type of keys maintained by this publisher.
 * @param <V>
 *            the value type holds by publisher.
 */
public interface Publisher<K, V> extends StreamingSink<K, V> {

    /**
     * Close this producer
     */
    @Override
    void close();

    /**
     * Flush any accumulated records from the producer. Blocks until all sends are complete.
     */
    @Override
    void flush();

    /**
     * Get the current configuration of this producer.
     *
     * @return instance of {@link PublisherConfiguration}
     */
    PublisherConfiguration getConfig();

    /**
     * AbstractPublisher initialization method
     *
     * @param config
     *            is the configuration parameters for the {@link AbstractPublisher}
     * @return an instance of this class
     * @throws PublisherConfigurationException
     *             if provided configuration is not satisfying minimum requirement.
     */
    Publisher<K, V> init(PublisherConfiguration config) throws PublisherConfigurationException;

    /**
     * @return true if connected else false.
     */
    boolean isconnected();

    /**
     * Send the given record asynchronously and return a future which will eventually contain the response information.
     *
     * @param topic
     *            The topic the record will be appended to
     * @param pKey
     *            The partition to which the record should be sent
     * @param key
     *            The key that will be included in the record
     * @param value
     *            The record contents
     */
    void sendMessage(String topic, Integer pKey, K key, V value);

    /**
     * Send the given record asynchronously and return a future which will eventually contain the response information.
     *
     * @param topic
     *            The topic the record will be appended to
     * @param key
     *            The key that will be included in the record
     * @param value
     *            The record contents
     */
    void sendMessage(String topic, K key, V value);

    /**
     * Send the given record asynchronously and return a future which will eventually contain the response information.
     *
     * @param topic
     *            The topic the record will be appended to
     * @param value
     *            The record contents
     */
    void sendMessage(String topic, V value);

}