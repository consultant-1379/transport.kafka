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

import static com.google.common.base.Preconditions.*;

/**
 * A representation of supported Messaging technology types. The enumeration <code>MessageServiceTypes</code> provides support for
 * <ul>
 * <li><b>KAFKA ==&gt; MessageServiceTypes.KAFKA</b></li>
 * <li><b>ZeroMQ ==&gt; MessageServiceTypes.ZMQ</b></li>
 * <li><b>RabbitMq ==&gt; MessageServiceTypes.AMQ </b></li>
 * </ul>
 */
public enum MessageServiceTypes {
    /**
     * URI for Kafka
     */
    KAFKA("kafka:/"),
    /**
     * URI for zeroMQ
     */
    ZMQ("zmq:/"),
    /**
     * URI for rabbitMQ
     */
    AMQ("amq:/");
    /**
     * Current Type.
     */
    private String type;

    /**
     * Constructor for MessageServiceTypes.
     * @param types
     *            String representing MessageServiceTypes.
     */
    MessageServiceTypes(final String types) {
        this.type = types;
    }

    /**
     * This methods return MessageServiceTypes equivalent to provided string type.
     * @param type
     *            string type
     * @return MessageServiceTypes type if accepted otherwise throws { @link IllegalArgumentException}.
     */
    public static MessageServiceTypes getURI(final String type) {
        checkArgument(type != null, "Message URI type cannot be null.");

        switch (type.toLowerCase()) {
            case "kafka:/":
                return KAFKA;
            case "zmq:/":
                return ZMQ;
            case "amq:/":
                return AMQ;
            default:
                break;
        }
        throw new IllegalArgumentException("Unknow URI type requested");
    }

    /**
     * A method validates the uri.
     * @param type
     *            string type
     * @return boolean true if accepted otherwise throws {@link UnsupportedOperationException}
     */
    public static boolean validates(final String type) {
        checkArgument(type != null, "Message API type cannot be null.");
        return getURI(type) != null;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

}
