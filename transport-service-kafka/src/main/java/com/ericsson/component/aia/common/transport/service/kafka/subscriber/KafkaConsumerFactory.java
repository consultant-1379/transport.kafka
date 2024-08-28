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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.transport.config.SubscriberConfiguration;
import com.ericsson.component.aia.common.transport.service.Subscriber;
import com.ericsson.component.aia.common.transport.service.kafka.KafkaSubscriber;

/**
 * The <code>KafkaConsumerFactory</code> simplifies the creation of consumer.
 * @param <K>
 *            key type.
 * @param <V>
 *            Value type
 */
public final class KafkaConsumerFactory<K, V> {
    private static final  Logger LOG = LoggerFactory.getLogger(KafkaConsumerFactory.class);

    /**
     * Avoid creation of instance of factory class as class contains all static methods.
     */
    private KafkaConsumerFactory() {
        super();
    }

    /**
     * A method to create Kafka managed or unmanaged implementation
     * @param <K>
     *            key
     * @param <V>
     *            value
     * @param config
     *            is {@link SubscriberConfiguration} from the client
     * @return instance of {@link Subscriber}
     */
    public static <K, V> Subscriber<K, V> create(final SubscriberConfiguration<V> config) {
        final Subscriber<K, V> subscriber = config.isManaged() ? new ManagedkaSubscriberProxy<K, V>() : new KafkaSubscriberImpl<K, V>();
        subscriber.init(config);
        if (LOG.isInfoEnabled()) {
            LOG.info("Kafka Consumer created with managed= " + config.isManaged() + " and Config " + config);
        }
        return subscriber;
    }

    /**
     * A method to create an extended kafka subscriber interface.
     * @param <K>
     *            key
     * @param <V>
     *            value
     * @param config
     *            is {@link SubscriberConfiguration} from the client
     * @return instance of {@link Subscriber}
     */
    public static <K, V> KafkaSubscriber<K, V> createExtended(final SubscriberConfiguration<V> config) {
        final KafkaSubscriber<K, V> kafkaSubscriberImpl = new KafkaSubscriberImpl<>();
        kafkaSubscriberImpl.init(config);
        if (LOG.isInfoEnabled()) {
            LOG.info("Extended Kafka Consumer created with Config " + config);
        }
        return kafkaSubscriberImpl;
    }
}
