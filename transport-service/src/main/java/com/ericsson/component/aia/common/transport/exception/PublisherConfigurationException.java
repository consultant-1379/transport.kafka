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
 * The <code>PublisherConfigurationException</code> class represent error condition in the configuration data or operations
 */
public class PublisherConfigurationException extends TransportException {

    /**
     * Genrated version id
     */
    private static final long serialVersionUID = 6583930736654990341L;

    /**
     * {@link PublisherConfigurationException} class constructor;
     * @param message
     *            exception message
     */
    public PublisherConfigurationException(final String message) {
        super(message);
    }

}
