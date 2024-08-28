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
package com.ericsson.component.aia.common.transport.service.util;

/**
 * Defines Kafka specific constants
 */
public final class KafkaConstants {

    public static final String PARTITIONER_CLASS = "partitioner.class";

    /**
     * input thread count for the consumers
     */
    public static final String INPUT_THREAD_POOL_SIZE_PARAM = "input.thread.pool.size";

    /**
     * output threads for producers
     */
    public static final String OUTPUT_THREAD_POOL_SIZE_PARAM = "output.thread.pool.size";

    /**
     * Deserializer class
     */
    public static final String KAFKA_KEY_DESERIALIZER_CLASS = "key.deserializer";

    /**
     * Deserializer class
     */
    public static final String KAFKA_VALUE_DE_SERIALIZER_CLASS = "value.deserializer";

    /**
     * Serializer class
     */
    public static final String KEY_SERIALIZER = "key.serializer";

    /**
     * Serializer class
     */
    public static final String VALUE_SERIALIZER = "value.serializer";

    private KafkaConstants() {
    }
}
