package io.nanovc.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyDescription;
import org.apache.kafka.streams.processor.internals.InternalTopologyBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * A helper class for interacting with Kafka.
 */
public class KafkaHelper
{

    /**
     * Loads the property file from the given path.
     *
     * @param configFile The path to the config file.
     * @return The config properties for Kafka.
     */
    public static Properties loadConfig(final Path configFile) throws IOException
    {
        if (!Files.exists(configFile))
        {
            throw new IOException(configFile + " not found.");
        }
        final Properties cfg = new Properties();
        try (InputStream inputStream = new FileInputStream(configFile.toFile()))
        {
            cfg.load(inputStream);
        }
        return cfg;
    }

    /**
     * Prepares the given config for streams in Kafka.
     *
     * @param props                The properties to update with config for streams in Kafka.
     * @param clientID             The client ID to use to distinguish this producer in the server logs.
     * @param keySerializerClass   The serializer to use for the keys.
     * @param valueSerializerClass The serializer to use for the values.
     * @return The producer config info that was prepared. This could be used by other code to know what specific details were set.
     * @param <K> The specific key serializer type.
     * @param <V> The specific value serializer type.
     */
    public static <K extends Serializer<?>, V extends Serializer<?>> ProducerConfigInfo prepareConfigForProducers(
        Properties props,
        String clientID,
        Class<K> keySerializerClass,
        Class<V> valueSerializerClass
    )
    {
        // Fill in the config info that we are going to use:
        ProducerConfigInfo info = new ProducerConfigInfo();
        info.clientID = clientID;
        info.keySerializerClass = keySerializerClass;
        info.valueSerializerClass = valueSerializerClass;

        // Add additional properties:
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, info.clientID);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, info.keySerializerClass.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, info.valueSerializerClass.getName());

        return info;
    }

    /**
     * Information that was used for the producer config.
     */
    public static class ProducerConfigInfo
    {
        /**
         * The client ID that is used to distinguish this producer in the Kafka server logs.
         */
        public String clientID;

        /**
         * The serializer to use for the keys.
         */
        Class<? extends Serializer<?>> keySerializerClass;

        /**
         * The serializer to use for the values.
         */
        Class<? extends Serializer<?>> valueSerializerClass;
    }

    /**
     * Prepares the given config for streams in Kafka.
     *
     * @param props                          The properties to update with config for streams in Kafka.
     * @param applicationID                  The application ID to register in the Kafka servers.
     * @param appendTimestampToApplicationID True to append a nano timestamp to the applicationID. This gives it a unique name. False to just use the applicationID.
     * @param clientID                       The client ID to report to the Kafka servers.
     * @return The stream config info that was prepared. This could be used by other code to know what specific details were set.
     */
    public static StreamConfigInfo prepareConfigForStreams(
        Properties props,
        String applicationID,
        boolean appendTimestampToApplicationID,
        String clientID
    )
    {
        // Fill in the config info that we are going to use:
        StreamConfigInfo info = new StreamConfigInfo();
        info.applicationID = appendTimestampToApplicationID ? applicationID + System.nanoTime() : applicationID;
        info.clientID = clientID;

        // Add additional properties.
        // Specify default (de)serializers for record keys and for record values.
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
        // against which the application is run.
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, info.applicationID);
        props.put(StreamsConfig.CLIENT_ID_CONFIG, clientID);

        // NOTE: The following doesn't reset the offset each time, only when the server doesn't have the consumers last offset from the previous run:
        // https://stackoverflow.com/a/65582541/231860
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Confluent Cloud doesn't allow auto creation of topics! Bleh!
        // https://riferrei.com/2020/03/17/why-the-property-auto-create-topics-enable-is-disabled-in-confluent-cloud/
        // Therefore, we need to create the intermediate topics that we need for the KTables:
        // String INTERMEDIATE_TOPIC_NAME = APPLICATION_ID + "KSTREAM-AGGREGATE-STATE-STORE-0000000001-changelog";
        // createTopic(INTERMEDIATE_TOPIC_NAME, 1, 3, props);

        // We also need to set properties to match the created topic settings so we don't get exceptions. Bleh!
        // https://stackoverflow.com/a/59532310/231860
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 3);

        return info;
    }

    /**
     * Information that was used for the stream config.
     */
    public static class StreamConfigInfo
    {
        /**
         * The Application ID that was created (including the timestamp if that was appended).
         */
        public String applicationID;

        /**
         * The clientID that was used.
         */
        public String clientID;
    }

    /**
     * Create topic in Confluent Cloud
     */
    public static void createTopic(final String topic,
                                   final int partitions,
                                   final int replication,
                                   final Properties cloudConfig)
    {
        final NewTopic newTopic = new NewTopic(topic, partitions, (short) replication);
        try (final AdminClient adminClient = AdminClient.create(cloudConfig))
        {
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        }
        catch (final InterruptedException | ExecutionException e)
        {
            // Ignore if TopicExistsException, which may be valid if topic exists
            if (!(e.getCause() instanceof TopicExistsException))
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Deletes a topic from Confluent Cloud
     */
    public static void deleteTopic(final String topic,
                                   final Properties cloudConfig)
    {
        try (final AdminClient adminClient = AdminClient.create(cloudConfig))
        {
            adminClient.deleteTopics(Collections.singletonList(topic)).all().get();
        }
        catch (final InterruptedException | ExecutionException e)
        {
            // Ignore if TopicExistsException, which may be valid if topic exists
            if (!(e.getCause() instanceof TopicExistsException))
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Deletes all the intermediate topics that were used in the given topology.
     *
     * @param topologyDescription The topology description that we need to clean up. You can get the Topology from {@link StreamsBuilder#build()} and the TopologyDescription from {@link Topology#describe()}.
     * @param APPLICATION_ID      The application ID that we are running under.
     * @param cloudConfig         The config properties to use to connect to Kafka.
     */
    public static void deleteIntermediateTopics(TopologyDescription topologyDescription, String APPLICATION_ID, final Properties cloudConfig)
    {
        // Delete the temporary topic that we created:
        for (TopologyDescription.Subtopology subtopology : topologyDescription.subtopologies())
        {
            for (TopologyDescription.Node node : subtopology.nodes())
            {
                if (node instanceof InternalTopologyBuilder.Processor)
                {
                    InternalTopologyBuilder.Processor processor = (InternalTopologyBuilder.Processor) node;
                    for (String storeName : ((InternalTopologyBuilder.Processor) node).stores())
                    {
                        // Create the topic name for this store:
                        // NOTE this comes from the KTable.count() documentation:
                        // For failure and recovery the store will be backed by an internal changelog topic that will be created in Kafka.
                        // The changelog topic will be named "${applicationId}-${internalStoreName}-changelog",
                        // where "applicationId" is user-specified in StreamsConfig via parameter APPLICATION_ID_CONFIG,
                        // "internalStoreName" is an internal name
                        // and "-changelog" is a fixed suffix.
                        // Note that the internal store name may not be queryable through Interactive Queries.
                        // You can retrieve all generated internal topic names via Topology.describe().
                        String topicName = APPLICATION_ID + "-" + storeName + "-changelog";

                        // Delete the topic:
                        deleteTopic(topicName, cloudConfig);
                    }
                }
            }
        }
    }
}
