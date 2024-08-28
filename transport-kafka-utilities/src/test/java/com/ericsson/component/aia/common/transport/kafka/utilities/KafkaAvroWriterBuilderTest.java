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
package com.ericsson.component.aia.common.transport.kafka.utilities;

import static org.junit.Assert.*;

import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.junit.Test;

import com.ericsson.component.aia.common.transport.kafka.writer.api.KafkaKeyGenerator;
import com.ericsson.component.aia.common.transport.kafka.writer.builder.KafkaGenericWriterBuilder;
import com.ericsson.component.aia.common.transport.kafka.writer.impl.*;

/**
 * Test the functionality of {@link KafkaGenericWriter}
 */
public class KafkaAvroWriterBuilderTest {

    @Test
    public void shouldCreateWriterWithoutKeyIfNoneIsProvided() {
        final KafkaGenericWriter<String, GenericRecord> kafkaGenericWriter = KafkaGenericWriterBuilder.<String, GenericRecord> create()
                .withTopic("TestTopic").withBrokers("localhost:9099").withKeySerializer("org.apache.kafka.common.serialization.StringSerializer")
                .withValueSerializer("com.ericsson.component.aia.common.avro.kafka.encoder.KafkaGenericRecordEncoder").build();

        assertTrue(kafkaGenericWriter instanceof NoKeyKafkaGenericWriter);
    }

    @Test
    public void shouldCreateWriterWithKeyIfProvided() {
        final KafkaGenericWriter<String, GenericRecord> kafkaGenericWriter = KafkaGenericWriterBuilder.<String, GenericRecord> create()
                .withTopic("TestTopic").withBrokers("localhost:9099").withKeySerializer("org.apache.kafka.common.serialization.StringSerializer")
                .withValueSerializer("com.ericsson.component.aia.common.avro.kafka.encoder.KafkaGenericRecordEncoder")
                .withKeyGenerator(new KafkaKeyGenerator<String, GenericRecord>() {
                    @Override
                    public String getKey(final GenericRecord grecord) {
                        return null;
                    }
                }).build();

        assertTrue(kafkaGenericWriter instanceof KeyedKafkaGenericWriter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWithBrokerNameRequiredMessage() {
        KafkaGenericWriterBuilder.create().withKeySerializer("org.apache.kafka.common.serialization.StringSerializer")
                .withValueSerializer("com.ericsson.component.aia.common.avro.kafka.encoder.KafkaGenericRecordEncoder").withTopic("TOPIC_NAME")
                .build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWithTopicNameRequiredMessage() {
        KafkaGenericWriterBuilder.create().withBrokers("localhost:9099").withKeySerializer("org.apache.kafka.common.serialization.StringSerializer")
                .withValueSerializer("com.ericsson.component.aia.common.avro.kafka.encoder.KafkaGenericRecordEncoder").build();

    }

}
