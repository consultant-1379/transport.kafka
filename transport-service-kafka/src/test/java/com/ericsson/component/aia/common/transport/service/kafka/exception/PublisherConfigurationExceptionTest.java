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
package com.ericsson.component.aia.common.transport.service.kafka.exception;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ericsson.component.aia.common.transport.exception.PublisherConfigurationException;

/**
 * Test to validate PublisherConfigurationException.
 */
public class PublisherConfigurationExceptionTest {

    /**
     * Test PublisherConfigurationException with specific message.
     */
    @Test
    public void testCommonPublisherConfigurationException() {
        final  PublisherConfigurationException exception = new PublisherConfigurationException("Common-PublisherConfigurationException");
        assertEquals("Common-PublisherConfigurationException", exception.getMessage());
    }
}
