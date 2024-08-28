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

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.component.aia.common.transport.config.KafkaPublisherConfiguration.KafkaPublisherConfigurationBuilder;
import com.ericsson.component.aia.common.transport.exception.PublisherConfigurationException;
import com.ericsson.component.aia.common.transport.service.MessageServiceTypes;

/**
 * The <code>KafkaPublisherConfigurationBuilderTest</code> test the builder functionality.
 */
public class KafkaPublisherConfigurationBuilderTest {

    /**
     * Holds properties.
     */
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
     * Test invalid ack property.
     */
    @Test(expected = PublisherConfigurationException.class)
    public void testInvalidAcksProperty() {
        final Properties props = new Properties();
        props.put("ack", "25");
        final KafkaPublisherConfigurationBuilder<String, String> builder = new
                KafkaPublisherConfiguration.KafkaPublisherConfigurationBuilder<String, String>(
                props);
        builder.build();
    }

    /**
     * Test KafkaPublisherConfigurationBuilder for null KeySerializer value.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidKeySerializerValues() {
        new KafkaPublisherConfiguration.KafkaPublisherConfigurationBuilder<String, String>(props).addKeySerializer(null).build();
    }

    /**
     * Test KafkaPublisherConfigurationBuilder for set default values for retries.
     */
    @Test
    public void testInvalidOptionRetriesPropertyAndSetsDefaultProperty() {
        final Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9098");
        props.put("retres", "0"); // invalid property
        final KafkaPublisherConfiguration<String> config = new KafkaPublisherConfiguration.KafkaPublisherConfigurationBuilder<String, String>(props)
                .build();
        assertEquals("Should have had default Retry supplied by the builder", "0", config.getProperties().get(ConfigurationIdentity.RETRIES_CONFIG));
    }

    /**
     * Test KafkaPublisherConfigurationBuilder for invalid processor value.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidProcessorValues() {
        new KafkaPublisherConfiguration.KafkaPublisherConfigurationBuilder<String, String>(props).addProcessors(0).build();
    }

    /**
     * Test KafkaPublisherConfigurationBuilder for null ValueSerializer value.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidValueSeralizerValues() {
        new KafkaPublisherConfiguration.KafkaPublisherConfigurationBuilder<String, String>(props).addValueSerializer(null).build();
    }

    /**
     * Test KafkaPublisherConfigurationBuilder for invalid ack value.
     */
    @Test(expected = PublisherConfigurationException.class)
    public void testMandatoryBootStrapPropertyMissing() {
        final Properties props = new Properties();
        props.put("bootstra.servers", "localhost:9098");
        final KafkaPublisherConfigurationBuilder<String, String> builder = new
                KafkaPublisherConfiguration.KafkaPublisherConfigurationBuilder<String, String>(
                props);
        builder.build();
    }

    /**
     * Test KafkaPublisherConfigurationBuilder with all property values.
     */
    @Test
    public void testWithFullModel() {
        final KafkaPublisherConfiguration<String> config = new KafkaPublisherConfiguration.KafkaPublisherConfigurationBuilder<String, String>(props)
                .addProcessors(10).addValueSerializer("org.apache.kafka.common.serialization.ByteArraySerializer")
                .addKeySerializer("org.apache.kafka.common.serialization.StringArraySerializer").build();

        assertEquals(10, config.getProcessors());
        assertEquals(MessageServiceTypes.KAFKA, config.getMessageAPIType());
        assertEquals("org.apache.kafka.common.serialization.ByteArraySerializer",
                config.getProperties().get(ConfigurationIdentity.VALUE_SERIALIZER_CLASS_CONFIG));
        assertEquals("org.apache.kafka.common.serialization.StringArraySerializer",
                config.getProperties().get(ConfigurationIdentity.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals("[{Properties: {bootstrap.servers=localhost:9092, value.serializer=org.apache.kafka.common.serialization.ByteArraySerializer,"
                + " partitioner.class=integration.MyPartioner, buffer.memory=1024, "
                + "retries=0, key.serializer=org.apache.kafka.common.serialization.StringArraySerializer,"
                + " linger.ms=1, batch.size=1, acks=all}}, processCount:10]", config.toString());
    }

    /**
     * Test KafkaPublisherConfigurationBuilder with null property value.
     */
    @Test(expected = PublisherConfigurationException.class)
    public void testWithNullProperties() {
        new KafkaPublisherConfiguration.KafkaPublisherConfigurationBuilder<String, String>(null).build();
    }

}
