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

import com.ericsson.component.aia.common.transport.service.MessageServiceTypes;

/**
 * The <code>PublisherConfiguration</code> class represent the publisher related configuration.
 */
public class PublisherConfiguration {

    /**
     * connection properties
     */
    private final Properties properties;

    /**
     * Holds instance of MessageServiceTypes.
     */
    private final MessageServiceTypes messsageAPIType;

    /**
     * Constructor to initiate instance of PublisherConfiguration with property type and MessageServiceType.
     * @param props
     *            : properties that needs to be configured for publisher.
     * @param type
     *            : supported Messaging technology types {@link MessageServiceTypes }}
     */
    public PublisherConfiguration(final Properties props, final MessageServiceTypes type) {
        properties = props;
        this.messsageAPIType = type;
    }

    /**
     * @return MessageServiceTypes.
     */
    public MessageServiceTypes getMessageAPIType() {
        return this.messsageAPIType;
    }

    /**
     * @return connection properties.
     */
    public Properties getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "PublisherConfiguration [properties=" + properties + ", messsageAPIType=" + messsageAPIType + "]";
    }

}
