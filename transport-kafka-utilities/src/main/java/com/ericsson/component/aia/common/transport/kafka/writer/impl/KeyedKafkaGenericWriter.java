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
package com.ericsson.component.aia.common.transport.kafka.writer.impl;

import com.ericsson.component.aia.common.transport.kafka.writer.api.KafkaKeyGenerator;

/**
 * The Class KeyedKafkaGenericWriter is an implementation of KafkaGenericWriter which will write records with a key generated using an instance of a
 * KafkaKeyGenerator .
 *
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 */
public class KeyedKafkaGenericWriter<K, V> extends KafkaGenericWriter<K, V> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6104400658974538464L;

    /** The kafka key. */
    private final KafkaKeyGenerator<K, V> kafkaKey;

    /**
     * Instantiates a new keyed kafka generic writer.
     *
     * @param kafkaKey
     *            the kafka key
     */
    public KeyedKafkaGenericWriter(final KafkaKeyGenerator<K, V> kafkaKey) {
        this.kafkaKey = kafkaKey;

    }

    /**
     * Write the record to the kafka topic with a key generated using an instance of KafkaKeyGenerator.
     *
     * @param record
     *            the record
     * @return true, if successful
     */
    @Override
    public boolean write(final V record) {
        super.getPublisher().sendMessage(super.getTopic(), kafkaKey.getKey(record), record);
        return true;
    }

}