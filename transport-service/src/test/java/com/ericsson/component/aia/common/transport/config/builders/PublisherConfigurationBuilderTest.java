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
package com.ericsson.component.aia.common.transport.config.builders;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.component.aia.common.transport.config.KafkaPublisherConfiguration;
import com.ericsson.component.aia.common.transport.config.KafkaPublisherConfiguration.KafkaPublisherConfigurationBuilder;
import com.ericsson.component.aia.common.transport.exception.PublisherConfigurationException;
import com.ericsson.component.aia.common.transport.service.MessageServiceTypes;

/**
 * The <code>KafkaPublisherConfigurationBuilderTest</code> test the builder functionality. This is not implemented yet. Hence this class is stub check
 * this functionality in future.
 */
public class PublisherConfigurationBuilderTest {

    private static Properties props;

    /**
     * setup basic properties.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 1);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 1024);
        props.put("partitioner.class", "integration.MyPartioner");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
    }

    /**
     * This is useless test as implementation is not done yet.
     */
    @Test(expected = IllegalStateException.class)
    public void testUselessCheckAMQ() {
        final PublisherConfigurationBuilder builder = new PublisherConfigurationBuilder();
        builder.addAMQType();
    }

    /**
     * This is useless test as implementation is not done yet.
     */
    @Test(expected = IllegalStateException.class)
    public void testUselessCheckKafka() {
        final PublisherConfigurationBuilder builder = new PublisherConfigurationBuilder();
        assertEquals(false, builder.isManaged());
        builder.addKafkaType();
    }

    /**
     * This is useless test as implementation is not done yet.
     */
    @Test(expected = IllegalStateException.class)
    public void testUselessCheckZeroMQ() {
        final PublisherConfigurationBuilder builder = new PublisherConfigurationBuilder();
        builder.addZMQType();
    }

    /**
     * Test KafkaPublisherConfiguration with all properties.
     */
    @Test()
    public void testWithAllProperties() {
        final KafkaPublisherConfiguration<Object> build = PublisherConfigurationBuilder.createkafkaPublisherBuilder(props).build();
        assertEquals(1, build.getProcessors());
        assertEquals(MessageServiceTypes.KAFKA, build.getMessageAPIType());
    }

    /**
     * Place holder to validate the ZMQ implementation.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testWithAllPropertiesForZMQPublisherBuilder() {
        PublisherConfigurationBuilder.createZMQPublisherBuilder();
    }

    /**
     * Test KafkaPublisherConfiguration with null properties.
     */
    @Test(expected = PublisherConfigurationException.class)
    public void testWithNullProperties() {
        final KafkaPublisherConfigurationBuilder<Object, Object> builder = PublisherConfigurationBuilder.createkafkaPublisherBuilder(null);
        builder.build();
    }

}
