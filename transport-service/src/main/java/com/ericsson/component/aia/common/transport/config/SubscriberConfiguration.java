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

import static com.google.common.base.Preconditions.*;

import java.util.Properties;

/**
 * The <code>SubscriberConfiguration</code> is a generic subscriber specific configuration. Any technology specific implementation needs to extends
 * <code>SubscriberConfiguration</code>.
 * @param <V>
 *            Value type
 */
public class SubscriberConfiguration<V> {

    /**
     * basic connection properties
     */
    private final Properties properties;

    /**
     * flag to identify the managed or unmanaged type interface.
     */
    private boolean managed;

    /**
     * Constructor for this class accepts {@link Properties} contains basic connection properties.
     * @param props
     *            is an instance of {@link Properties}
     */
    public SubscriberConfiguration(final Properties props) {
        checkNotNull(props, "Consumer Configuration cannot be null");
        properties = props;
    }

    /**
     * @return an instance of this current assigned properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * @return true if it managed otherwise false
     */
    public boolean isManaged() {
        return managed;
    }

    public void setManaged(final boolean managed) {
        this.managed = managed;
    }

}
