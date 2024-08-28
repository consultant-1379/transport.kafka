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
package com.ericsson.component.aia.common.transport.kafka.writer.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.transport.config.PublisherConfiguration;
import com.ericsson.component.aia.common.transport.service.MessageServiceTypes;
import com.ericsson.component.aia.common.transport.service.Publisher;
import com.ericsson.component.aia.common.transport.service.kafka.KafkaFactory;

/**
 * The <code>KafkaGenericWriter</code> provides generic way to published {@link GenericRecord} to kafka.
 *
 * KafkaGenericWriter provides builder to build the kafka with specific configuration.
 *
 * Note:<code> KafkaGenericWriter</code> is Serializable but it can not Serialize kafka publisher but it will create new instance of kafka publisher
 * for the target jvm.
 *
 * <pre>
 * KafkaGenericWriter.Builder.create().withBrokers(properties.getProperty("bootstrap.servers"))
 *         .withKeySerializer("org.apache.kafka.common.serialization.StringSerializer")
 *         .withValueSerializer("com.ericsson.component.aia.common.avro.kafka.encoder.KafkaGenericRecordEncoder").withTopic("TOPIC_NAME").build();
 * </pre>
 *
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 */
public abstract class KafkaGenericWriter<K, V> implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4995137291563255751L;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaGenericWriter.class);

    /** The publisher no needs to be Serialized. */
    private static Publisher publisher;

    /** The topic. */
    protected String topic;

    /** The properties. */
    private Properties properties;

    /**
     * package private constructor for KafkaGenericWriter
     */
    KafkaGenericWriter() {

    }

    /**
     * Close resources allocated for writer.
     */
    public void close() {
        LOGGER.trace("Trying to closed {} ", KafkaGenericWriter.class.getName());
        if (getPublisher() != null) {
            LOGGER.debug("Trying to flush publisher befor closing the connection for the {} ", KafkaGenericWriter.class.getName());
            getPublisher().flush();
            LOGGER.debug("Flushed successfully");
            LOGGER.debug("Trying to closed the publisher associated with {} ", KafkaGenericWriter.class.getName());
            getPublisher().close();
            LOGGER.debug("Publisher associated with {} flushed and closed successfully.", KafkaGenericWriter.class.getName());
        }
    }

    /**
     * Write the records to the topic specified during the construction of the writer.
     *
     * @param records
     *            the records
     * @return true, if successful
     */

    public boolean write(final Collection<V> records) {
        LOGGER.trace("Trying to write {} records of GenericRecord to kafka topic {}", records.size(), topic);
        for (final V record : records) {
            write(record);
        }
        return true;
    }

    /**
     * Write the record to the kafka topic.
     *
     * @param record
     *            the generic record.
     *
     * @return true, if successful
     */
    public abstract boolean write(V record);

    /**
     * Gets the topic.
     *
     * @return the topic
     */

    public String getTopic() {
        return topic;
    }

    /**
     * Sets the publisher.
     *
     * @param publisher
     *            the publisher to set
     */
    public void setPublisher(final Publisher<?, ?> publisher) {
        KafkaGenericWriter.publisher = publisher;
    }

    /**
     * Sets the topic.
     *
     * @param topic
     *            the topic to set
     */
    public void setTopic(final String topic) {
        this.topic = topic;
    }

    /**
     * Sets the properties.
     *
     * @param properties
     *            the properties to set
     */
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

    /**
     * Gets the publisher.
     *
     * @return the publisher
     */
    protected Publisher<K, V> getPublisher() {
        if (publisher == null) {
            publisher = KafkaFactory.createKafkaPublisher(new PublisherConfiguration(this.properties, MessageServiceTypes.KAFKA));
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (publisher != null) {
                        LOGGER.info("ShutdownHook executed to close publisher");
                        publisher.close();
                    }
                }
            });
        }
        return publisher;
    }

    /**
     * Flush the buffer.
     */
    public void flush() {
        LOGGER.trace("Trying to flush the kafka publisher");
        getPublisher().flush();
        LOGGER.trace("Flush successfully");
    }

}
