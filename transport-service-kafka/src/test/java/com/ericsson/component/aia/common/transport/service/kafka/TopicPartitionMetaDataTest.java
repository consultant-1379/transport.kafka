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
package com.ericsson.component.aia.common.transport.service.kafka;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test to validate the TopicPartitionMetaData class functionality.
 */
public class TopicPartitionMetaDataTest {

    /**
     * Test functionality of {@link TopicPartitionMetaData#equals(Object)} and {@link TopicPartitionMetaData#hashCode()}
     */
    @Test
    public void testTopicPartitionEquality() {
        final TopicPartitionMetaData topic1 = new TopicPartitionMetaData(1, "TEST");
        final TopicPartitionMetaData topic2 = new TopicPartitionMetaData(2, "TEST");
        final TopicPartitionMetaData topic3 = new TopicPartitionMetaData(2, "TEST");
        final TopicPartitionMetaData topic4 = null;
        assertEquals(true, topic2.equals(topic2));
        assertEquals(false, topic2.equals(topic4));
        assertEquals(false, topic2.equals(Integer.valueOf(1)));
        assertEquals(false, topic2.equals(new TopicPartitionMetaData(1, null)));
        assertEquals(false, new TopicPartitionMetaData(1, null).equals(topic2));
        assertEquals(true, topic2.equals(topic3));
        assertEquals(topic2.hashCode(), topic3.hashCode());
        assertEquals(topic2.toString(), topic3.toString());
        assertEquals(false, topic1.equals(topic2));
    }

}
