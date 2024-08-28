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

import java.util.Collection;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.component.aia.common.transport.exception.SubscriberConfigurationException;
import com.ericsson.component.aia.common.transport.service.GenericEventListener;

/**
 * The <code>KafkaSubScriberConfigurationBuilderTest</code> test the builder functionality.
 */
public class KafkaSubScriberConfigurationBuilderTest {

    private static Properties props;

    /**
     * setup basic properties.
     */
    @BeforeClass
    public static void setUpBeforeClass()  {
        props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
    }

    /**
     * Test KafkaSubScriberConfigurationBuilder with empty topics.
     */
    @Test(expected = SubscriberConfigurationException.class)
    public void testWithEmptyTopics() {
        new KafkaSubscriberConfiguration.KafkaSubScriberConfigurationBuilder<String, String>(props).build();
    }

    /**
     * Test KafkaSubScriberConfigurationBuilder with all properties.
     */
    @Test
    public void testWithFullModel() {
        final KafkaSubscriberConfiguration<String> build = new KafkaSubscriberConfiguration.KafkaSubScriberConfigurationBuilder<String, String>(props)
                .addKeyDeserializer("org.apache.kafka.common.serialization.StringDeserializer")
                .addValueDeserializer("org.apache.kafka.common.serialization.StringDeserializer").addTopic("T1").addTopic("T2").addTopic("T1")
                .addListener(new GenericEventListener<String>() {
                    @Override
                    public void onEvent(final Collection<String> microBatch) {
                    }

                }).addProcessors(3)

                .build();

        assertEquals(2, build.getTopicList().size());
        assertEquals(1, build.getListeners().size());
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer",
                build.getProperties().get(ConfigurationIdentity.KEY_DESERIALIZER_CLASS_CONFIG));
    }

    /**
     * Test KafkaSubScriberConfigurationBuilder with null property.
     */
    @Test(expected = SubscriberConfigurationException.class)
    public void testWithNullProperties() {
        new KafkaSubscriberConfiguration.KafkaSubScriberConfigurationBuilder<String, String>(null).build();
    }

}
