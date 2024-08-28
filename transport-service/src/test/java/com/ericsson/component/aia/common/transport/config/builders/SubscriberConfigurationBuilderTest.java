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

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.component.aia.common.transport.config.KafkaSubscriberConfiguration.KafkaSubScriberConfigurationBuilder;
import com.ericsson.component.aia.common.transport.exception.SubscriberConfigurationException;

/**
 * The <code>SubscriberConfigurationBuilderTest</code> test the builder functionality. This is not implemented yet. Hence this class is stub check
 * this functionality in future.
 */
public class SubscriberConfigurationBuilderTest {

    private static Properties properties;

    /**
     * setup basic properties.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 1);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 1024);
        properties.put("partitioner.class", "integration.MyPartioner");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
    }

    /**
     * This is useless test as implementation is not done yet.
     */
    @Test(expected = IllegalStateException.class)
    public void testAMQType() {
        final SubscriberConfigurationBuilder builder = new SubscriberConfigurationBuilder();
        builder.addAMQType();
    }

    /**
     * This is useless test as implementation is not done yet.
     */
    @Test(expected = IllegalStateException.class)
    public void testKafkaType() {
        final SubscriberConfigurationBuilder builder = new SubscriberConfigurationBuilder();
        builder.addKafkaType();
    }

    /**
     * Place holder to test SubscriberConfigurationBuilder properties.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testWithAllProperties() {
        final  SubscriberConfigurationBuilder builder = new SubscriberConfigurationBuilder();
        builder.createZMQConsumerBuilder().build();
    }


    /**
     * Test {@link SubscriberConfigurationBuilder#createkafkaConsumerBuilder(Properties)} with null value.
     */
    @Test(expected = SubscriberConfigurationException.class)
    public void testWithNullProperties() {
        final KafkaSubScriberConfigurationBuilder<Object, Object> builder = SubscriberConfigurationBuilder.createkafkaConsumerBuilder(null);
        builder.build();
    }

    /**
     * This is useless test as implementation is not done yet.
     */
    @Test(expected = IllegalStateException.class)
    public void testZeroMQType() {
        final SubscriberConfigurationBuilder builder = new SubscriberConfigurationBuilder();
        builder.addZMQType();
    }

}
