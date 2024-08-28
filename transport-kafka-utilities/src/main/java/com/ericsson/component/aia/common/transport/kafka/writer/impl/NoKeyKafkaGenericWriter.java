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

/**
 * The Class NoKeyKafkaGenericWriter is an implementation of KafkaGenericWriter which will write records without a key.
 *
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 */
public class NoKeyKafkaGenericWriter<K, V> extends KafkaGenericWriter<K, V> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4917684718709766972L;

    /**
     * Write the record to the kafka topic without a key.
     *
     * @param record
     *            the record
     * @return true, if successful
     */
    @Override
    public boolean write(final V record) {
        super.getPublisher().sendMessage(topic, record);
        return true;
    }
}