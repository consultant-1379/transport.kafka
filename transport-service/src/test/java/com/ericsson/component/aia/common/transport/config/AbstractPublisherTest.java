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

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.component.aia.common.transport.exception.PublisherConfigurationException;
import com.ericsson.component.aia.common.transport.service.AbstractPublisher;
import com.ericsson.component.aia.common.transport.service.MessageServiceTypes;
import com.ericsson.component.aia.common.transport.service.Publisher;

/**
 * Test to validate default implementation of AbstractPublisher.
 */
public class AbstractPublisherTest {

    private static AbstractPublisher<String, String> publisher;
    private static PublisherConfiguration config;

    /**
     * Initialized basic settings required for testing.
     */
    @BeforeClass
    public static void setUpBeforeClass() {

        final Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "0");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        config = new PublisherConfiguration(props, MessageServiceTypes.KAFKA);
        publisher = new AbstractPublisher<String, String>() {
            @Override
            public void close() {
            }

            @Override
            public void flush() {
            }

            @Override
            public void sendMessage(final String topic, final Integer pKey, final String key, final String value) {
            }

            @Override
            public void sendMessage(final String topic, final String value) {
            }

            @Override
            public void sendMessage(final String topic, final String key, final String value) {
            }

            @Override
            public int getNoPartitions(final String topicName) {
                return 0;
            }
        };

    }

    /**
     * Test {@link AbstractPublisher#init(PublisherConfiguration)} method with null configuration.
     */
    @Test(expected = PublisherConfigurationException.class)
    public void testInitWithNullValue() {
        @SuppressWarnings("unused")
        final Publisher<String, String> init = publisher.init(null);
    }

    /**
     * Test {@link AbstractPublisher#init(PublisherConfiguration)} method with proper configuration.
     */
    @Test
    public void testInitWithProperValue() {
        final Publisher<String, String> init = publisher.init(config);
        assertEquals(publisher, init);
        assertEquals(config, init.getConfig());
        assertEquals(false, init.isconnected());
    }
}
