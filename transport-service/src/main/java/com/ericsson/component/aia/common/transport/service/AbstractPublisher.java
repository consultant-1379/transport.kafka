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

import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.transport.config.PublisherConfiguration;
import com.ericsson.component.aia.common.transport.exception.PublisherConfigurationException;

/**
 * The <code>AbstractPublisher</code> is the base class for all technology specific implementation of publisher.
 *
 * @param <K>
 *            key type
 * @param <V>
 *            Value type
 */
public abstract class AbstractPublisher<K, V> implements Publisher<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPublisher.class);
    /**
     * Holds the instance of PublisherConfiguration.
     */
    private PublisherConfiguration config;

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.component.aia.common.transport.service.Publisher#close()
     */
    @Override
    public abstract void close();

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.component.aia.common.transport.service.Publisher#flush()
     */
    @Override
    public abstract void flush();

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.component.aia.common.transport.service.Publisher#getConfig()
     */
    @Override
    public PublisherConfiguration getConfig() {
        return config;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.component.aia.common.transport.service.Publisher#init(com.ericsson.component.aia
     * .common.transport.config.PublisherConfiguration)
     */
    @Override
    public Publisher<K, V> init(final PublisherConfiguration config) throws PublisherConfigurationException {
        this.config = config;
        // validate
        validate(config);
        if (LOG.isInfoEnabled()) {
            LOG.info("Basic AbstractPublisher initialization compleated.");
        }
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.component.aia.common.transport.service.Publisher#isconnected()
     */
    @Override
    public boolean isconnected() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.component.aia.common.transport.service.Publisher#sendMessage(java.lang .String, java.lang.Integer, K, V)
     */
    @Override
    public abstract void sendMessage(String topic, Integer pKey, K key, V value);

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.component.aia.common.transport.service.Publisher#sendMessage(java.lang .String, K, V)
     */
    @Override
    public abstract void sendMessage(String topic, K key, V value);

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.component.aia.common.transport.service.Publisher#sendMessage(java.lang .String, V)
     */
    @Override
    public abstract void sendMessage(String topic, V value);

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.aia.common.datarouting.api.Sink#isConnected()
     */
    @Override
    public boolean isConnected() {
        return isconnected();
    }

    /**
     * A generic validation method by default performing null check;
     *
     * @param configuration
     *            contains the required configuration for this publisher to initialize.
     * @throws PublisherConfigurationException
     *             by default if the input argument is null.
     */
    protected void validate(final PublisherConfiguration configuration) throws PublisherConfigurationException {
        try {
            checkNotNull(config);
        } catch (final NullPointerException e) {
            throw new PublisherConfigurationException("AbstractPublisher configuration cannot be null.");
        }
    }
}
