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

import org.apache.kafka.clients.CommonClientConfigs;

/**
 * This class contains some configurations which is shared by both producer and consumer.
 * <p>
 * The some of the configuration are <b>mandatory</b> , some of them are <b>optional</b>.
 */
public final class ConfigurationIdentity {

    /*
     * COMMON CLIENT CONFIG ************************************************************************* ******************************.
     */
    /** <code>bootstrap.servers</code> comma separated list of brokers */
    public static final String BOOTSTRAP_SERVERS_CONFIG = "bootstrap.servers";

    /**
     * <code>metadata.max.age.ms</code> The period of time in milliseconds after which we force a refresh of metadata even if we haven't seen any
     * partition leadership changes to proactively discover any new brokers or partitions.
     */
    public static final String METADATA_MAX_AGE_CONFIG = "metadata.max.age.ms";

    /**
     * <code>send.buffer.bytes</code> The size of the TCP send buffer (SO_SNDBUF) to use when sending data.
     */
    public static final String SEND_BUFFER_CONFIG = CommonClientConfigs.SEND_BUFFER_CONFIG;

    /**
     * <code>client.id</code> An id string to pass to the server when making requests. The purpose of this is to be able to track the source of
     * requests beyond just ip/port by allowing a logical application name to be included in server-side request logging.
     */
    public static final String CLIENT_ID_CONFIG = "client.id";

    /**
     * <code>receive.buffer.bytes</code>
     */
    public static final String RECEIVE_BUFFER_CONFIG = "send.buffer.bytes";

    /**
     * <code>reconnect.backoff.ms</code> The amount of time to wait before attempting to reconnect to a given host. This avoids repeatedly connecting
     * to a host in a tight loop.
     */
    public static final String RECONNECT_BACKOFF_MS_CONFIG = CommonClientConfigs.RECONNECT_BACKOFF_MS_CONFIG;

    /**
     * <code>retry.backoff.ms</code> The amount of time to wait before attempting to retry a failed fetch request to a given topic partition. This
     * avoids repeated fetching-and-failing in a tight loop."
     */
    public static final String RETRY_BACKOFF_MS_CONFIG = "retry.backoff.ms";

    /**
     * <code>metrics.sample.window.ms</code> The number of samples maintained to compute metrics.
     */
    public static final String METRICS_SAMPLE_WINDOW_MS_CONFIG = "metrics.sample.window.ms";

    /**
     * <code>metrics.num.samples</code> The number of samples maintained to compute metrics.
     */
    public static final String METRICS_NUM_SAMPLES_CONFIG = "metrics.num.samples";

    /**
     * <code>metric.reporters</code> A list of classes to use as metrics reporters. Implementing the <code>MetricReporter</code> interface allows
     * plugging in classes that will be notified of new metric creation. The JmxReporter is always included to register JMX statistics
     */
    public static final String METRIC_REPORTER_CLASSES_CONFIG = "metric.reporters";

    /**
     * <code>connections.max.idle.ms</code> Close idle connections after the number of milliseconds specified by this config.
     */
    public static final String CONNECTIONS_MAX_IDLE_MS_CONFIG = "connections.max.idle.ms";

    /**
     * <code>request.timeout.ms</code> "The configuration controls the maximum amount of time the client will wait
     */
    public static final String REQUEST_TIMEOUT_MS_CONFIG = "request.timeout.ms";

    /**
     * The maximum amount of data per-partition the server will return.
     */
    public static final String MAX_PARTITION_FETCH_BYTES_CONFIG = "max.partition.fetch.bytes";;

    /*
     * COMMON CLIENT CONFIG END ************************************************************************* ******************************.
     */

    /**
     * The timeout used to detect failures when using Kafka's group management facilities.
     */
    public static final String SESSION_TIMEOUT_MS_CONFIG = "session.timeout.ms";

    /**
     * <code>batch.size</code> The producer will attempt to batch records together into fewer requests whenever multiple records are being sent to the
     * same partition. This helps performance on both the client and the server. This configuration controls the default batch size in bytes.
     * <p>
     * "No attempt will be made to batch records larger than this size.
     * <p>
     * Requests sent to brokers will contain multiple batches, one for each partition with data available to be sent
     * <p>
     * A small batch size will make batching less common and may reduce throughput (a batch size of zero will disable batching entirely). A very large
     * batch size may use memory a bit more wastefully as we will always allocate a buffer of the specified batch size in anticipation of additional
     * records.
     */

    public static final String BATCH_SIZE_CONFIG = "batch.size";

    /**
     * <code>buffer.memory</code> The total bytes of memory the producer can use to buffer records waiting to be sent to the server. If records are
     * sent faster than they can be delivered to the server the producer will either block or throw an exception based on the preference specified by
     * <code>block.on.buffer.full</code>
     */
    public static final String BUFFER_MEMORY_CONFIG = "buffer.memory";

    /**
     * <code>acks</code> The number of acknowledgments the producer requires the leader to have received before considering a request complete.
     * at-least-once value = 1 No guarantee =0
     */
    public static final String ACKS_CONFIG = "acks";

    /**
     * <code>linger.ms</code> The producer groups together any records that arrive in between request transmissions into a single batched request.
     */
    public static final String LINGER_MS_CONFIG = "linger.ms";

    /**
     * <code>max.request.size</code> The maximum size of a request. This is also effectively a cap on the maximum record size.
     */
    public static final String MAX_REQUEST_SIZE_CONFIG = "max.request.size";

    /**
     * <code>retries</code> Setting a value greater than zero will cause the client to resend any record whose send fails with a potentially transient
     * error.
     */
    public static final String RETRIES_CONFIG = "retries";

    /**
     * <code>compression.type</code> The compression type for all data generated by the producer. The values are <code>none</code>, <code>gzip</code>,
     * <code>snappy</code>, or <code>lz4</code>
     */
    public static final String COMPRESSION_TYPE_CONFIG = "compression.type";

    /**
     * <code>max.in.flight.requests.per.connection</code> The maximum number of unacknowledged requests the client will send on a single connection
     * before blocking."
     */
    public static final String MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION = "max.in.flight.requests.per.connection";

    /**
     * <code>key.serializer</code> Serializer class for key that implements the <code>Serializer</code> interface.
     */
    public static final String KEY_SERIALIZER_CLASS_CONFIG = "key.serializer";

    /**
     * <code>value.serializer</code> Serializer class for value that implements the <code>Serializer</code> interface."
     */
    public static final String VALUE_SERIALIZER_CLASS_CONFIG = "value.serializer";

    /**
     * <code>partitioner.class</code> Partitioner class that implements the <code>Partitioner</code> interface.
     */
    public static final String PARTITIONER_CLASS_CONFIG = "partitioner.class";

    /**
     * <code>max.block.ms</code> The configuration controls how long KafkaProducer.send() and KafkaProducer.partitionsFor will block.
     */
    public static final String MAX_BLOCK_MS_CONFIG = "max.block.ms";

    /**
     * A unique string that identifies the consumer group this consumer belongs to.
     */
    public static final String GROUP_ID_CONFIG = "group.id";

    /**
     * <code>enable.auto.commit</code> If true the consumer's offset will be periodically committed in the background.
     */
    public static final String ENABLE_AUTO_COMMIT_CONFIG = "enable.auto.commit";
    /**
     * <code>auto.commit.interval.ms</code> The frequency in milliseconds that the consumer offsets are auto-committed to Kafka if
     * <code>enable.auto.commit</code> is set to <code>true</code>.
     */

    public static final String AUTO_COMMIT_INTERVAL_MS_CONFIG = "auto.commit.interval.ms";

    /**
     * <code>value.deserializer</code> Deserializer class for value that implements the <code>Deserializer</code> interface.
     */
    public static final String VALUE_DESERIALIZER_CLASS_CONFIG = "value.deserializer";

    /**
     * <code>key.deserializer</code> Deserializer class for key that implements the <code>Deserializer</code> interface
     */
    public static final String KEY_DESERIALIZER_CLASS_CONFIG = "key.deserializer";
    /**
     * Private constructor to avoid creation of object of this class.
     */
    private ConfigurationIdentity() {
    }

}
