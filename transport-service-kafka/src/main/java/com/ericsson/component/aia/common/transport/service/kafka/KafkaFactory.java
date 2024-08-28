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
package com.ericsson.component.aia.common.transport.service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.transport.config.PublisherConfiguration;
import com.ericsson.component.aia.common.transport.config.SubscriberConfiguration;
import com.ericsson.component.aia.common.transport.exception.PublisherConfigurationException;
import com.ericsson.component.aia.common.transport.exception.SubscriberConfigurationException;
import com.ericsson.component.aia.common.transport.service.Publisher;
import com.ericsson.component.aia.common.transport.service.Subscriber;
import com.ericsson.component.aia.common.transport.service.kafka.publisher.KafkaPublisher;
import com.ericsson.component.aia.common.transport.service.kafka.subscriber.KafkaConsumerFactory;

/**
 * This factory provides ways to create the Kafka Consumer and publisher
 */
public final class KafkaFactory {

    private static final  Logger LOG = LoggerFactory.getLogger(KafkaFactory.class);

    /**
     * private constructor to avoid creation of instance of this class.
     */
    private KafkaFactory() {
    }

    /**
     * A method to create an extended kafka consumer
     * @param <K>
     *            type key
     * @param <V>
     *            type value
     * @param config
     *            of type {@link SubscriberConfiguration}
     * @return instance of {@link Subscriber}
     * @throws SubscriberConfigurationException
     *             if failed to configure a kafka subscriber
     */
    public static <K, V> KafkaSubscriber<K, V> createExtendedKafkaSubscriber(final SubscriberConfiguration<V> config)
            throws SubscriberConfigurationException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Creating Extended Kafka Subscriber with config " + config);
        }
        return KafkaConsumerFactory.createExtended(config);
    }

    /**
     * A factory method to create the kafka publisher.
     * @param <K>
     *            is the key.
     * @param <V>
     *            is the value
     * @param config
     *            is the basic configuration required to establish connection to kafka
     * @return instance of AbstractPublisher &lt;K,V&gt;
     * @throws PublisherConfigurationException
     *             if the system failed to configure or initialize.
     */
    public static <K, V> Publisher<K, V> createKafkaPublisher(final PublisherConfiguration config) throws PublisherConfigurationException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Creating Kafka publisher with config " + config);
        }
        return new KafkaPublisher<K, V>().init(config);
    }

    /**
     * A method to create a kafka consumer
     * @param <K>
     *            type key
     * @param <V>
     *            type value
     * @param config
     *            of type {@link SubscriberConfiguration}
     * @return instance of {@link Subscriber}
     * @throws SubscriberConfigurationException
     *             if failed to configure a kafka subscriber
     */
    public static <K, V> Subscriber<K, V> createKafkaSubscriber(final SubscriberConfiguration<V> config) throws SubscriberConfigurationException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Creating Kafka Subscriber with config " + config);
        }
        return KafkaConsumerFactory.create(config);
    }
}
