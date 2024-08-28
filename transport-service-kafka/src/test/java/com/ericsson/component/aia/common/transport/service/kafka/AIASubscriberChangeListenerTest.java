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

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.apache.kafka.common.TopicPartition;
import org.junit.*;

/**
 * Test class to check the functionality of {@link SubscriberChangeListener}
 */
public class AIASubscriberChangeListenerTest {
    private static TestListener instance;

    /**
     * throws Exception
     *             if unable to initialize listener properly.
     */
    @Before
    public void setUpBefore() {
        instance = new TestListener();

    }

    /**
     * Release all resources.
     */
    @After
    public void tearDownAfter()   {
        instance = null;
    }

    /**
     * Test topic assign functionality.
     */
    @Test
    public void testTopicAssign() {
        instance.onPartitionsAssigned(Arrays.asList(new TopicPartition[] { new TopicPartition("test", 1) }));
        final Collection<TopicPartitionMetaData> assigned = instance.getAssigned();
        assertNotNull(assigned);
        assertThat(assigned.toArray(new TopicPartitionMetaData[0])[0].getTopic(), is("test"));
    }

    /**
     * Test topic revoke functionality.
     */
    @Test
    public void testRevoke() {
        instance.onPartitionsRevoked(Arrays.asList(new TopicPartition[] { new TopicPartition("test", 1) }));
        final Collection<TopicPartitionMetaData> assigned = instance.getAssigned();
        assertNull(assigned);
        final Collection<TopicPartitionMetaData> revocked = instance.getRevocked();
        assertNotNull(revocked);
        assertThat(revocked.toArray(new TopicPartitionMetaData[0])[0].getTopic(), is("test"));
        assertThat(revocked.toArray(new TopicPartitionMetaData[0])[0].getPartition(), is(1));
    }

}

/**
 * Stub class to validate SubscriberChangeListener functionality.
 */
final class TestListener extends SubscriberChangeListener {
    /**
     * Hold list of assigned TopicPartitionMetaData.
     */
    Collection<TopicPartitionMetaData> assigned;
    Collection<TopicPartitionMetaData> revocked;

    public Collection<TopicPartitionMetaData> getAssigned() {
        return assigned;
    }

    public void setAssigned(final Collection<TopicPartitionMetaData> assigned) {
        this.assigned = assigned;
    }

    @Override
    protected void onAssign(final Collection<TopicPartitionMetaData> partitions) {
        assigned = partitions;

    }

    @Override
    protected void onRevoke(final Collection<TopicPartitionMetaData> partitions) {
        revocked = partitions;

    }

    public Collection<TopicPartitionMetaData> getRevocked() {
        return revocked;
    }

}