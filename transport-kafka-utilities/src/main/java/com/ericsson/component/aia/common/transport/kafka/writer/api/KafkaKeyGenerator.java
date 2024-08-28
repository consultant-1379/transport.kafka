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
package com.ericsson.component.aia.common.transport.kafka.writer.api;

import java.io.Serializable;

/**
 * The Interface KafkaKeyGenerator is used to generate a key for each record prior to publishing to KAFKA. An implementation of this class can be
 * passed into KafkaGenericWriterBuilder in order to publish records with keys to KAFKA.
 *
 * @param <K>
 *            the type of the key to be generated
 * @param <V>
 *            the type of the record being sent.
 */
public interface KafkaKeyGenerator<K, V> extends Serializable {

    /**
     * Generate a key for the record.
     *
     * @param record
     *            the record
     * @return the key to send the record with.
     */
    K getKey(final V record);

}
