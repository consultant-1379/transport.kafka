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

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.component.aia.common.transport.config.ZMQSubscriberConfiguration.ZMQScriberConfigurationBuilder;

/**
 * The <code>KafkaZMQSubscriberConfigurationBuilderTest</code> test the builder functionality.
 */

public class KafkaZMQSubscriberConfigurationBuilderTest {

    private static Properties props;
    /**
     * setup basic properties.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
    }

    /**
     * Test ZMQScriberConfigurationBuilder with all properties.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testWithFullModel() {
        final ZMQScriberConfigurationBuilder<String> configurationBuilder = new ZMQSubscriberConfiguration.ZMQScriberConfigurationBuilder<String>(
                props);
        configurationBuilder.build();
    }

    /**
     * Test ZMQScriberConfigurationBuilder with null property. .
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testWithNullProperties() {
        final ZMQScriberConfigurationBuilder<String> configurationBuilder = new ZMQSubscriberConfiguration.ZMQScriberConfigurationBuilder<String>(
                null);
        configurationBuilder.build();
    }

}
