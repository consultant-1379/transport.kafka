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
package com.ericsson.component.aia.common.transport.kafka.utilities.integration;

import java.net.ServerSocket;

/**
 * Utility class for test.
 */
public final class TestUtility {

    /**
     * Private constructor.
     */
    private TestUtility() {
        super();
    }

    /**
     * @return available port else -1.
     */
    public static int findFreePort() {
        int port;
        try {
            final ServerSocket socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            port = socket.getLocalPort();
            socket.close();
        } catch (Exception e) {
            port = -1;
        }
        return port;
    }

    /**
     * @param delayInSecond
     *            delay thread execution in seconds.
     */
    public static void delay(final int delayInSecond) {
        try {
            Thread.sleep(delayInSecond);
        } catch (Exception e) {
        }
    }
}
