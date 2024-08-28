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
package com.ericsson.component.aia.common.transport.service.kafka.publisher;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.transport.config.PublisherConfiguration;
import com.ericsson.component.aia.common.transport.exception.InitializationException;
import com.ericsson.component.aia.common.transport.exception.PublisherConfigurationException;
import com.ericsson.component.aia.common.transport.service.AbstractPublisher;
import com.ericsson.component.aia.common.transport.service.Publisher;

/**
 * The <code>KafkaPublisher</code> is specific implementation of {@link AbstractPublisher}.
 *
 * @param <K>
 *            Key type.
 * @param <V>
 *            value type.
 */
public class KafkaPublisher<K, V> extends AbstractPublisher<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaPublisher.class);
    /**
     * Holds instance of producer.
     */
    public Producer<K, V> producer;

    /**
     * A producer is instantiated by providing {@link PublisherConfiguration} contains a set of key-value pairs as configuration. Valid configuration
     * strings are documented <a href="http://kafka.apache.org/documentation.html#producerconfigs">here </a>
     *
     * @param config
     *            configuration object for publisher.
     * @return current object.
     * @throws PublisherConfigurationException
     *             if publisher unable to configured properly.
     */
    @Override
    public Publisher<K, V> init(final PublisherConfiguration config) throws PublisherConfigurationException {
        super.init(config);
        createProducer(config);
        if (LOG.isInfoEnabled()) {
            LOG.info("KafkaPublisher is initialized with configuration {}", config);
        }
        return this;
    }

    /**
     * create instance of kafka producer
     *
     * @param config
     *            instance of {@link Producer}
     */
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    protected void createProducer(final PublisherConfiguration config) {
        final Properties properties = config.getProperties();
        try {
            producer = new KafkaProducer<>(properties);
        } catch (final Throwable error) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Kafka Initialization failed.", error);
            }
            throw new InitializationException(error.getMessage());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("KafkaProducer created. ");
        }
    }

    /**
     * Asynchronously send data to a topic.
     * <p>
     * This method will return immediately once the record has been stored in the buffer of records waiting to be sent.
     * </p>
     **/
    @Override
    public final void sendMessage(final String topic, final K key, final V value) {
        producer.send(new ProducerRecord<>(topic, key, value));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.component.aia.common.transport.Publisher#sendMessage(java.lang.String, java.lang.Object)
     */
    @Override
    public final void sendMessage(final String topic, final V value) {
        producer.send(new ProducerRecord<K, V>(topic, value));

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.component.aia.common.transport.Publisher#sendMessage(java.lang.String, java.lang.Integer, java.lang.Object, java.lang.Object)
     */
    @Override
    public final void sendMessage(final String topic, final Integer pKey, final K key, final V value) {
        producer.send(new ProducerRecord<>(topic, pKey, key, value));

    }

    @Override
    public final void close() {
        producer.close();
        if (LOG.isInfoEnabled()) {
            LOG.info("KafkaProducer is closed");
        }

    }

    @Override
    public void flush() {
        if (LOG.isInfoEnabled()) {
            LOG.debug("KafkaProducer flushing records.");
        }
        producer.flush();

    }

    /**
     * Validate default expected values.
     *
     * @param configuration
     *            configuration object that needs to be validate against null value.
     * @throws PublisherConfigurationException
     *             if required value is missing or configuration contains null properties.
     */
    @Override
    protected void validate(final PublisherConfiguration configuration) throws PublisherConfigurationException {
        super.validate(configuration);
        final Properties properties = configuration.getProperties();
        checkNotNull(properties);
        if (LOG.isInfoEnabled()) {
            LOG.info("Kafka AbstractPublisher Configuration validation compleated.");
        }

    }

    @Override
    public int getNoPartitions(final String topicName) {
        return producer.partitionsFor(topicName).size();
    }

}
