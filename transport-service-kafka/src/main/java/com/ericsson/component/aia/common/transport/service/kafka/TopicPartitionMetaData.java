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

/**
 * The <code>TopicPartitionMetaData</code> holds the topic name and partition number.
 */
public final class TopicPartitionMetaData {
    /**
     * Partition of this topic
     */
    private final int partition;
    /**
     * String topic name
     */
    private final String topic;

    /**
     * Constructor to create instance of TopicPartitionMetaData with topic name and partition number.
     * @param partition
     *            partition number
     * @param topic
     *            Topic Name
     */
    public TopicPartitionMetaData(final int partition, final String topic) {
        super();
        this.partition = partition;
        this.topic = topic;
    }

    /**
     * Compares this TopicPartitionMetaData to the specified object. The result is {@code
     * true} if and only if the argument is not {@code null} and is a {@code
     * TopicPartitionMetaData } object that represents the same topic name and partition number as this object.
     * @param obj
     *            The object to compare this {@code TopicPartitionMetaData} against
     * @return {@code true} if the given object represents a {@code TopicPartitionMetaData} equivalent to this TopicPartitionMetaData, {@code false}
     *         otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TopicPartitionMetaData other = (TopicPartitionMetaData) obj;
        if (partition != other.partition) {
            return false;
        }
        if (topic == null) {
            if (other.topic != null) {
                return false;
            }
        } else if (!topic.equals(other.topic)) {
            return false;
        }
        return true;
    }

    /**
     * @return the partition id
     */
    public int getPartition() {
        return partition;
    }

    /**
     * @return the partition name.
     */
    public String getTopic() {
        return topic;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + partition;
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Topic Partition Meta Data [Topic Name =" + topic + ", partition=" + partition + "]";
    }

}
