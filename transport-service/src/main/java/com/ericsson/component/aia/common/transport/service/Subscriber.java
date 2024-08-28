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

import java.util.Collection;
import java.util.List;

import com.ericsson.aia.common.datarouting.api.StreamingSource;
import com.ericsson.component.aia.common.transport.config.SubscriberConfiguration;

/**
 * The <code>Subscriber</code> interface is a common interface for all messaging service implementations such <b>kafka</b>,<b>ZeroMQ</b>, <b>RabitMQ
 * </b>.
 *
 * @param <K>
 *            the type of keys maintained by this Subscriber
 * @param <V>
 *            the value type holds by subscriber.
 */
public interface Subscriber<K, V> extends StreamingSource<K, V> {

    /**
     * Close the consumer and perform cleanup
     */
    @Override
    void close();

    /**
     * Fetch data for the topics or partitions specified using one of the subscribe/assign APIs.
     *
     * @return stream of messages collected duirng poll methods.
     */
    @Override
    Collection<V> collectStream();

    /**
     * This method will initialized the Subscriber based on config.
     *
     * @param configuration
     *            subscriber configuration.
     */
    void init(SubscriberConfiguration<V> configuration);

    /**
     * This method initiate the Subscriber service.
     */
    @Override
    void start();

    /**
     * This method used to subscribe list of interested topics.
     *
     * @param topics
     *            list of topics for which subscriber is interested.
     */
    @Override
    void subscribe(List<String> topics);

}
