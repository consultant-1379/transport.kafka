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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.aia.common.datarouting.api.EventListener;
import com.ericsson.component.aia.common.transport.config.KafkaSubscriberConfiguration;
import com.ericsson.component.aia.common.transport.config.SubscriberConfiguration;
import com.ericsson.component.aia.common.transport.service.GenericEventListener;
import com.ericsson.component.aia.common.transport.service.Subscriber;
import com.ericsson.component.aia.common.transport.service.util.ProcessorThreadFactory;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * A proxy implementation for the Subscriber interface. This class manages N number of kafka consumers.
 *
 * @param <K>
 *            Key type.
 * @param <V>
 *            Value Type.
 */
final class ManagedkaSubscriberProxy<K, V> implements Subscriber<K, V> {
    // logger
    private static final Logger LOG = LoggerFactory.getLogger(ManagedkaSubscriberProxy.class);
    // COnsumer threads
    private final List<Subscriber<K, V>> subscribers = new ArrayList<>();
    // data forwader
    private final List<EventListener<V>> listeners = new ArrayList<>();
    // Service executor
    private ExecutorService executor;
    // local thread factory
    private ProcessorThreadFactory threadFactory;
    // global data queue
    private final BlockingQueue<Collection<V>> queue = new LinkedBlockingQueue<>();
    // polling timeout
    private long timeOut;
    // stop flag for update listener
    private boolean stop;

    /**
     * Close all processors
     */
    @Override
    public void close() {
        // subscribers.forEach(subscriber -> subscriber.close());
        for (final Subscriber<K, V> sc : subscribers) {
            sc.close();
        }
        try {
            if (LOG.isInfoEnabled()) {
                LOG.info("attempt to shutdown executor");
            }
            executor.shutdown();
            executor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Processors are interrupted", e);
            }
        } finally {
            if (!executor.isTerminated()) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Stopping all ongoing incompleate  tasks");
                }
            }
            executor.shutdownNow();
            if (LOG.isInfoEnabled()) {
                LOG.info("All process are closed.");
            }
        }

    }

    /**
     * create and initialize the requested number of Consumers
     */
    @Override
    public void init(final SubscriberConfiguration<V> configuration) {
        checkArgument(configuration != null, "Subsciber configuration cannot be null.");
        checkArgument(configuration.getProperties() != null, "Subsciber configuration cannot be null.");
        final KafkaSubscriberConfiguration<V> kafkaSubscriberConfiguration = (KafkaSubscriberConfiguration<V>) configuration;
        final int processorCount = kafkaSubscriberConfiguration.getProcessors();
        timeOut = Long.parseLong(configuration.getProperties().getProperty("application.poll.timeout", "1000"));
        // consigure each processors
        configureProcessors(configuration, kafkaSubscriberConfiguration, processorCount);
        // register listeners
        final Set<GenericEventListener<V>> listenersToBeRegistered = kafkaSubscriberConfiguration.getListeners();
        if (listenersToBeRegistered != null) {
            listeners.addAll(listenersToBeRegistered);
        }
    }

    private void configureProcessors(final SubscriberConfiguration<V> configuration,
            final KafkaSubscriberConfiguration<V> kafkaSubscriberConfiguration, final int processorCount) {
        for (int counter = 0; counter < processorCount; counter++) {
            final Subscriber<K, V> processor = new KafkaSubscriberManaged<>(queue);
            processor.init(configuration);
            processor.subscribe(kafkaSubscriberConfiguration.getTopicList());
            subscribers.add(processor);
            createProcessRunners(processorCount);
        }
    }

    private void createProcessRunners(final int processorCount) {
        threadFactory = new ProcessorThreadFactory("Kafka-consumer-", 1024, 7);
        executor = Executors.newFixedThreadPool(processorCount, threadFactory);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.component.aia.common.transport.service.kafka.Subscriber#start()
     */
    @Override
    public void start() {
        startProcessors();
        while (!stop) {
            try {
                final Collection<V> take = queue.take();
                // listeners.forEach(listener -> listener.onEvent(take));
                for (final EventListener<V> listener : listeners) {
                    listener.onEvent(take);
                }
            } catch (final InterruptedException e) {
                // ignore this exception as this is occured while waiting.
                if (LOG.isErrorEnabled()) {
                    LOG.error("Error processing data from the internal consumer queue. Reason : " + e.getMessage(), e);
                }
            }

        }
    }

    private void startProcessors() {
        for (final Subscriber<K, V> subscriber : subscribers) {
            final KafkaSubscriberManaged<K, V> task = (KafkaSubscriberManaged<K, V>) subscriber;
            executor.submit(task);
            task.start();
        }
    }

    /*
     * subscribe this processors to a list kafka topics
     */
    @Override
    public void subscribe(final List<String> topics) {
        for (final Subscriber<K, V> subscriber : subscribers) {
            subscriber.subscribe(topics);
        }

    }

    @Override
    public Collection<V> collectStream() {

        try {
            return queue.poll(timeOut, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            // ignore this exception as this is occured while waiting.
            if (LOG.isErrorEnabled()) {
                LOG.error("Error processing data from the internal consumer queue. Reason : " + e.getMessage(), e);
            }
        }
        return new ArrayList<>();
    }

    /**
     * A method to transform the Consumer records to Collection of values of type V
     *
     * @param result
     *            result that needs to transform.
     * @return lists of collection object
     */
    protected Collection<V> trasform(final ConsumerRecords<K, V> result) {
        final Iterable<V> transform = Iterables.transform(result, new Function<ConsumerRecord<K, V>, V>() {
            @Override
            public V apply(final ConsumerRecord<K, V> input) {
                return input.value();
            }

        });
        final Collection<V> output = new ArrayList<>();
        // transform.forEach(output::add);
        for (final V outputValue : transform) {
            output.add(outputValue);
        }
        return output;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.aia.common.datarouting.api.Source#registerEventListener(com.ericsson.aia.common.datarouting.api.EventListener)
     */
    @Override
    public void registerEventListener(final EventListener<V> listener) {
        checkArgument(listener != null, "Listener cannot be null.");
        listeners.remove(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.aia.common.datarouting.api.Source#removeEventListener(com.ericsson.aia.common.datarouting.api.EventListener)
     */
    @Override
    public void removeEventListener(final EventListener<V> listener) {
        checkArgument(listener != null, "Listener cannot be null.");
        listeners.remove(listener);
    }

}
