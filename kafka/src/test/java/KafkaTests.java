import io.confluent.kafka.serializers.KafkaJsonDeserializer;
import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import io.confluent.kafka.serializers.KafkaJsonSerializer;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.internals.InternalTopologyBuilder;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Tests Kafka.
 * https://docs.confluent.io/platform/current/tutorials/examples/clients/docs/java.html
 */
public class KafkaTests
{
    /**
     * This test shows an example of producer code in Java.
     * <p>
     * https://docs.confluent.io/platform/current/tutorials/examples/clients/docs/java.html
     * https://github.com/confluentinc/examples/blob/6.0.1-post/clients/cloud/java/src/main/java/io/confluent/examples/clients/cloud/ProducerExample.java
     */
    @Test
    public void producerExample() throws IOException
    {
        // Define the properties that we need for this test:
        // NOTE: You can get these properties by going to the "Tools and client config" page of your cluster in confluent cloud.
        // The instructions from the example web page were:
        //      Load properties from a local configuration file
        //      Create the configuration file (e.g. at '$HOME/.confluent/java.config') with configuration parameters
        //      to connect to your Kafka cluster, which can be on your local host, Confluent Cloud, or any other cluster.
        //      Follow these instructions to create this file: https://docs.confluent.io/current/tutorials/examples/clients/docs/java.html
        // To make the unit test stand-alone, we replicate the contents in this test and use environment variables for the sensitive data.
        // Make sure to set the following environment variables
        final Properties props = loadConfig(Path.of(".", "config", "java.config"));

        // Create topic if needed
        final String topic = "test1";
        createTopic(topic, 1, 3, props);

        // Add additional properties.
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonSerializer");

        Producer<String, DataRecord> producer = new KafkaProducer<String, DataRecord>(props);

        // Produce sample data
        final Long numMessages = 10L;
        for (Long i = 0L; i < numMessages; i++)
        {
            String key = "alice";
            DataRecord record = new DataRecord(i);

            System.out.printf("Producing record: %s\t%s%n", key, record);
            producer.send(new ProducerRecord<String, DataRecord>(topic, key, record), new Callback()
            {
                @Override
                public void onCompletion(RecordMetadata m, Exception e)
                {
                    if (e != null)
                    {
                        e.printStackTrace();
                    }
                    else
                    {
                        System.out.printf("Produced record to topic %s partition [%d] @ offset %d%n", m.topic(), m.partition(), m.offset());
                    }
                }
            });
        }

        producer.flush();

        System.out.printf("10 messages were produced to topic %s%n", topic);

        producer.close();
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

    public static class DataRecord
    {

        Long count;

        public DataRecord()
        {
        }

        public DataRecord(Long count)
        {
            this.count = count;
        }

        public Long getCount()
        {
            return count;
        }

        public String toString()
        {
            return new com.google.gson.Gson().toJson(this);
        }

    }

    /**
     * This test shows an example of consumer code in Java.
     * https://github.com/confluentinc/examples/blob/6.0.1-post/clients/cloud/java/src/main/java/io/confluent/examples/clients/cloud/ConsumerExample.java
     */
    @Test
    public void consumerExample() throws IOException
    {
        final String topic = "test1";

        // Define the properties that we need for this test:
        // NOTE: You can get these properties by going to the "Tools and client config" page of your cluster in confluent cloud.
        // The instructions from the example web page were:
        //      Load properties from a local configuration file
        //      Create the configuration file (e.g. at '$HOME/.confluent/java.config') with configuration parameters
        //      to connect to your Kafka cluster, which can be on your local host, Confluent Cloud, or any other cluster.
        //      Follow these instructions to create this file: https://docs.confluent.io/current/tutorials/examples/clients/docs/java.html
        // To make the unit test stand-alone, we replicate the contents in this test and use environment variables for the sensitive data.
        // Make sure to set the following environment variables
        final Properties props = loadConfig(Path.of(".", "config", "java.config"));

        // Add additional properties.
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonDeserializer");
        props.put(KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, DataRecord.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-1");

        // NOTE: The following doesn't reset the offset each time, only when the server doesn't have the consumers last offset from the previous run:
        // https://stackoverflow.com/a/65582541/231860
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final Consumer<String, DataRecord> consumer = new KafkaConsumer<String, DataRecord>(props);

        // Subscribe to the topic but wait for partitions to be assigned so that we can reset offsets to the beginning each time.
        // NOTE: By default, the Kafka server remembers the last offsets of the clients.
        //       If we don't reset them then the second time you run this test we would not re-run from the beginning.
        // https://stackoverflow.com/a/54492802/231860
        consumer.subscribe(Arrays.asList(topic));

        Long total_count = 0L;

        try
        {
            int emptyCount = 0;
            final int MAX_EMPTY_COUNT = 100;

            while (emptyCount < MAX_EMPTY_COUNT)
            {
                ConsumerRecords<String, DataRecord> records = consumer.poll(Duration.ofMillis(100));
                if (records.isEmpty()) emptyCount++;
                for (ConsumerRecord<String, DataRecord> record : records)
                {
                    String key = record.key();
                    DataRecord value = record.value();
                    total_count += value.getCount();
                    if ((total_count % 10_000) == 0)
                    {
                        System.out.printf("Consumed record with key %s and value %s, and updated total count to %d%n", key, value, total_count);
                    }
                }
            }
        }
        finally
        {
            consumer.close();
        }
    }

    /**
     * This test shows an example of consumer code that resets the offset but
     * can only do this by using an explicit assignment to partitions instead of the subscribe method.
     * https://github.com/confluentinc/examples/blob/6.0.1-post/clients/cloud/java/src/main/java/io/confluent/examples/clients/cloud/ConsumerExample.java
     */
    @Test
    public void consumerExampleWithExplicitAssignmentAndResettingOffset() throws IOException
    {
        final String topic = "test1";

        // Define the properties that we need for this test:
        // NOTE: You can get these properties by going to the "Tools and client config" page of your cluster in confluent cloud.
        // The instructions from the example web page were:
        //      Load properties from a local configuration file
        //      Create the configuration file (e.g. at '$HOME/.confluent/java.config') with configuration parameters
        //      to connect to your Kafka cluster, which can be on your local host, Confluent Cloud, or any other cluster.
        //      Follow these instructions to create this file: https://docs.confluent.io/current/tutorials/examples/clients/docs/java.html
        // To make the unit test stand-alone, we replicate the contents in this test and use environment variables for the sensitive data.
        // Make sure to set the following environment variables
        final Properties props = loadConfig(Path.of(".", "config", "java.config"));

        // Add additional properties.
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonDeserializer");
        props.put(KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, DataRecord.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-1");

        // NOTE: The following doesn't reset the offset each time, only when the server doesn't have the consumers last offset from the previous run:
        // https://stackoverflow.com/a/65582541/231860
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Long total_count = 0L;


        // Create the consumer:
        final Consumer<String, DataRecord> consumer = new KafkaConsumer<>(props);

        // Get the partitions that exist for this topic:
        List<PartitionInfo> partitions = consumer.partitionsFor(topic);

        // Get the topic partition info for these partitions:
        List<TopicPartition> topicPartitions = partitions.stream().map(info -> new TopicPartition(info.topic(), info.partition())).collect(Collectors.toList());

        // Assign all the partitions to the topic so that we can seek to the beginning:
        // NOTE: We can't use subscribe if we use assign, but we can't seek to the beginning if we use subscribe.
        consumer.assign(topicPartitions);

        // Make sure we seek to the beginning of the partitions:
        consumer.seekToBeginning(topicPartitions);

        try
        {
            int emptyCount = 0;
            final int MAX_EMPTY_COUNT = 100;

            while (emptyCount < MAX_EMPTY_COUNT)
            {
                ConsumerRecords<String, DataRecord> records = consumer.poll(Duration.ofMillis(100));

                if (records.isEmpty()) emptyCount++;

                for (ConsumerRecord<String, DataRecord> record : records)
                {
                    String key = record.key();
                    DataRecord value = record.value();
                    total_count += value.getCount();
                    if ((total_count % 10_000) == 0)
                    {
                        System.out.printf("Consumed record with key %s and value %s, and updated total count to %d%n", key, value, total_count);
                    }
                }
            }
        }
        finally
        {
            consumer.close();
        }
    }

    /**
     * An example of using the streams API.
     * Inspired by:
     * https://github.com/confluentinc/kafka-streams-examples/blob/6.0.1-post/src/main/java/io/confluent/examples/streams/MapFunctionLambdaExample.java
     */
    @Test
    public void streamsExample() throws IOException, InterruptedException
    {
        final String topic = "test1";
        final String outputTopic = topic + "-modified";

        // Define the properties that we need for this test:
        // NOTE: You can get these properties by going to the "Tools and client config" page of your cluster in confluent cloud.
        // The instructions from the example web page were:
        //      Load properties from a local configuration file
        //      Create the configuration file (e.g. at '$HOME/.confluent/java.config') with configuration parameters
        //      to connect to your Kafka cluster, which can be on your local host, Confluent Cloud, or any other cluster.
        //      Follow these instructions to create this file: https://docs.confluent.io/current/tutorials/examples/clients/docs/java.html
        // To make the unit test stand-alone, we replicate the contents in this test and use environment variables for the sensitive data.
        // Make sure to set the following environment variables
        final Properties props = loadConfig(Path.of(".", "config", "java.config"));

        // Add additional properties.
        // Specify default (de)serializers for record keys and for record values.
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
        // against which the application is run.
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "map-function-lambda-example" + System.nanoTime());
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "map-function-lambda-example-client");

        // NOTE: The following doesn't reset the offset each time, only when the server doesn't have the consumers last offset from the previous run:
        // https://stackoverflow.com/a/65582541/231860
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        // Make sure that the output topic exists:
        createTopic(outputTopic, 1, 3, props);

        // Set up serializers and deserializers, which we will use for overriding the default serdes
        // specified above.
        final Serde<String> stringSerde = Serdes.String();
        KafkaJsonSerializer<DataRecord> dataRecordSerializer = new KafkaJsonSerializer<>();
        KafkaJsonDeserializer<DataRecord> dataRecordDeserializer = new KafkaJsonDeserializer<>();
        Map<String, Object> dataRecordConfigMap = Map.of(KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, DataRecord.class);
        dataRecordSerializer.configure(dataRecordConfigMap, false);
        dataRecordDeserializer.configure(dataRecordConfigMap, false);
        Serde<DataRecord> dataRecordSerde = Serdes.serdeFrom(dataRecordSerializer, dataRecordDeserializer);

        // In the subsequent lines we define the processing topology of the Streams application.
        final StreamsBuilder builder = new StreamsBuilder();

        // Read the input Kafka topic into a KStream instance.
        final KStream<String, DataRecord> dataRecordStream = builder.stream(topic, Consumed.with(stringSerde, dataRecordSerde));

        // Keep track of the last time we processed data:
        AtomicLong lastTimestamp = new AtomicLong(System.nanoTime());

        // Keep track of how many records we have modified:
        AtomicInteger count = new AtomicInteger();

        // Variant 2: using `map`, modify value only (equivalent to variant 1)
        final KStream<String, DataRecord> modifiedDataRecordStream = dataRecordStream.map(
            (key, value) ->
            {
                // Update the last timestamp where we processed something:
                lastTimestamp.set(System.nanoTime());

                // Increment the count and check whether we want to print out progress:
                if ((count.incrementAndGet() % 10_000) == 0)
                {
                    System.out.println("Record " + count.get());
                }

                // Modify the value:
                return new KeyValue<>(key, new DataRecord(-value.getCount()));
            }
        );

        // Write (i.e. persist) the results to a new Kafka topic.
        //
        // In this case we can rely on the default serializers for keys and values because their data
        // types did not change, i.e. we only need to provide the name of the output topic.
        modifiedDataRecordStream.to(outputTopic, Produced.with(stringSerde, dataRecordSerde));

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        // Always (and unconditionally) clean local state prior to starting the processing topology.
        // We opt for this unconditional call here because this will make it easier for you to play around with the example
        // when resetting the application for doing a re-run (via the Application Reset Tool,
        // http://docs.confluent.io/current/streams/developer-guide.html#application-reset-tool).
        //
        // The drawback of cleaning up local state prior is that your app must rebuilt its local state from scratch, which
        // will take time and will require reading all the state-relevant data from the Kafka cluster over the network.
        // Thus in a production scenario you typically do not want to clean up always as we do here but rather only when it
        // is truly needed, i.e., only under certain conditions (e.g., the presence of a command line flag for your app).
        // See `ApplicationResetExample.java` for a production-like example.
        streams.cleanUp();

        streams.setStateListener(
            (newState, oldState) ->
            {
                System.out.printf("%s->%s%n", newState, oldState);
            });


        // Reset the timer:
        lastTimestamp.set(System.nanoTime());

        streams.start();

        while (true)
        {
            // Wait a bit:
            Thread.sleep(1_000);

            // Check when the last time was that we got a value:
            long now = System.nanoTime();
            long lastModification = lastTimestamp.get();
            long delta = now - lastModification;
            if (delta > 10_000_000_000L) break;
        }

        // Gracefully close Kafka Streams:
        streams.close();
    }

    /**
     * An example of using the streams API.
     * Inspired by:
     * https://github.com/confluentinc/kafka-streams-examples/blob/6.0.1-post/src/main/java/io/confluent/examples/streams/SumLambdaExample.java
     */
    @Test
    public void kTableExample() throws IOException, InterruptedException
    {
        final String topic = "test1";

        // Define the properties that we need for this test:
        // NOTE: You can get these properties by going to the "Tools and client config" page of your cluster in confluent cloud.
        // The instructions from the example web page were:
        //      Load properties from a local configuration file
        //      Create the configuration file (e.g. at '$HOME/.confluent/java.config') with configuration parameters
        //      to connect to your Kafka cluster, which can be on your local host, Confluent Cloud, or any other cluster.
        //      Follow these instructions to create this file: https://docs.confluent.io/current/tutorials/examples/clients/docs/java.html
        // To make the unit test stand-alone, we replicate the contents in this test and use environment variables for the sensitive data.
        // Make sure to set the following environment variables
        final Properties props = loadConfig(Path.of(".", "config", "java.config"));

        // Add additional properties.
        // Specify default (de)serializers for record keys and for record values.
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
        // against which the application is run.
        final String APPLICATION_ID = "ktable-function-lambda-example" + System.nanoTime();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "ktable-function-lambda-example-client");

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


        // Set up serializers and deserializers, which we will use for overriding the default serdes
        // specified above.
        final Serde<String> stringSerde = Serdes.String();
        KafkaJsonSerializer<DataRecord> dataRecordSerializer = new KafkaJsonSerializer<>();
        KafkaJsonDeserializer<DataRecord> dataRecordDeserializer = new KafkaJsonDeserializer<>();
        Map<String, Object> dataRecordConfigMap = Map.of(KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, DataRecord.class);
        dataRecordSerializer.configure(dataRecordConfigMap, false);
        dataRecordDeserializer.configure(dataRecordConfigMap, false);
        Serde<DataRecord> dataRecordSerde = Serdes.serdeFrom(dataRecordSerializer, dataRecordDeserializer);

        // In the subsequent lines we define the processing topology of the Streams application.
        final StreamsBuilder builder = new StreamsBuilder();

        // Read the input Kafka topic into a KStream instance.
        final KStream<String, DataRecord> dataRecordStream = builder.stream(topic, Consumed.with(stringSerde, dataRecordSerde));

        // Keep track of the last time we processed data:
        AtomicLong lastTimestamp = new AtomicLong(System.nanoTime());

        // Keep track of how many records we have modified:
        AtomicInteger count = new AtomicInteger();

        // Process the stream using KTables:
        dataRecordStream
            .groupByKey()
            .count()
            .filter((key, value) -> value > 2)
            .toStream()
            .foreach(
                (key, value) ->
                {
                    // Update the timestamp so we can detect when there is no progress:
                    lastTimestamp.set(System.nanoTime());

                    // Update the count:
                    count.incrementAndGet();

                    System.out.printf("%s:%,d Data Records%n", key, value);
                });

        // Build the topology:
        Topology topology = builder.build();

        // Create the streams for the topology:
        final KafkaStreams streams = new KafkaStreams(topology, props);
        // Always (and unconditionally) clean local state prior to starting the processing topology.
        // We opt for this unconditional call here because this will make it easier for you to play around with the example
        // when resetting the application for doing a re-run (via the Application Reset Tool,
        // http://docs.confluent.io/current/streams/developer-guide.html#application-reset-tool).
        //
        // The drawback of cleaning up local state prior is that your app must rebuilt its local state from scratch, which
        // will take time and will require reading all the state-relevant data from the Kafka cluster over the network.
        // Thus in a production scenario you typically do not want to clean up always as we do here but rather only when it
        // is truly needed, i.e., only under certain conditions (e.g., the presence of a command line flag for your app).
        // See `ApplicationResetExample.java` for a production-like example.
        streams.cleanUp();

        streams.setStateListener(
            (newState, oldState) ->
            {
                System.out.printf("%s->%s%n", newState, oldState);
            });


        // Reset the timer:
        lastTimestamp.set(System.nanoTime());

        streams.start();

        while (true)
        {
            // Wait a bit:
            Thread.sleep(1_000);

            // Check when the last time was that we got a value:
            long now = System.nanoTime();
            long lastModification = lastTimestamp.get();
            long delta = now - lastModification;
            if (delta > 10_000_000_000L) break;
        }

        // Gracefully close Kafka Streams:
        streams.close();

        // Clean up the streams:
        streams.cleanUp();

        // Get the description of the topology so that we can clean up the temporary topics:
        TopologyDescription topologyDescription = topology.describe();

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
                        deleteTopic(topicName, props);
                    }
                }
            }
        }
    }
}
