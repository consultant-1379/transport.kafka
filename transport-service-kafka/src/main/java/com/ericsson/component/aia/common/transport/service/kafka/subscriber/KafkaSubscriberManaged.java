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

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.transport.config.SubscriberConfiguration;

/**
 * A managed Kafka consumer processor, it can publish data all received data to registered listener.
 * @param <K>
 *            Key type.
 * @param <V>
 *            Value Type.
 */
final class KafkaSubscriberManaged<K, V> extends KafkaSubscriberImpl<K, V> implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaSubscriberManaged.class);
    private final BlockingQueue<Collection<V>> queue;
    private boolean close;

    /**
     * global data queue
     * @param queue
     *            queue that needs to be used by the consumer to consume the events.
     */
    KafkaSubscriberManaged(final BlockingQueue<Collection<V>> queue) {
        this.queue = queue;
    }

    @Override
    public void init(final SubscriberConfiguration<V> configuration) {
        super.init(configuration);
        if (LOG.isInfoEnabled()) {
            LOG.info("Managed Kafka Subscriber initialized");
        }

    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (!close) {
            final ConsumerRecords<K, V> result = getConsumer().poll(getTimeOut());
            if (!result.isEmpty()) {
                final Collection<V> output = trasform(result);
                queue.add(output);
            }
        }

    }

    @Override
    public void close() {
        super.close();
        close = true;
    }

}
