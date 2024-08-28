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
package com.ericsson.component.aia.common.transport.exception;

/**
 * The <code>SubscriberConfigurationException</code> represent miss configuration of transport services.
 */
public class SubscriberConfigurationException extends TransportException {

    /**
     * Generated version id
     */
    private static final long serialVersionUID = 6583930736654990341L;

    /**
     * {@link SubscriberConfigurationException} class constructor;
     * @param message
     *            exception message
     */
    public SubscriberConfigurationException(final String message) {
        super(message);
    }

}
