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

import com.ericsson.component.aia.common.transport.exception.InitializationException;
/**
 * Test to validate InitializationException.
 */
public class InitializationExceptionTest {

    /**
     * Test InitializationException with specific message.
     */
    @Test
    public void testCommonInitializationException() {
        final InitializationException exception = new InitializationException("Common-InitializationException");
        assertEquals("Common-InitializationException", exception.getMessage());
        assertEquals(null, exception.getCause());
    }

}

