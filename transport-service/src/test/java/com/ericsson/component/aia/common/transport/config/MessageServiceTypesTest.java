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

import static org.junit.Assert.*;

import org.junit.Test;

import com.ericsson.component.aia.common.transport.service.MessageServiceTypes;

/**
 */
public class MessageServiceTypesTest {

    /**
     * Test basic validation and all supported service type.
     */
    @Test
    public void test() {
        assertTrue(MessageServiceTypes.validates("kafka:/"));
        assertTrue(MessageServiceTypes.validates("amq:/"));
        assertTrue(MessageServiceTypes.validates("zmq:/"));
        assertEquals(MessageServiceTypes.KAFKA, MessageServiceTypes.getURI("kafka:/"));
        assertEquals(MessageServiceTypes.AMQ, MessageServiceTypes.getURI("amq:/"));
        assertEquals(MessageServiceTypes.ZMQ, MessageServiceTypes.getURI("zmq:/"));
        final MessageServiceTypes uri = MessageServiceTypes.getURI("kafka:/");
        assertEquals(MessageServiceTypes.KAFKA.getType(), uri.getType());
    }

    /**
     * Test unknown URI type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnsupportedURI() {
        MessageServiceTypes.getURI("JAVA");
    }
}
