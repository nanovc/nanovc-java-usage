import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TopicExistsException;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
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

    // Create topic in Confluent Cloud
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

        // Do a dummy position poll to reset the offsets because it is lazy-evaluated:
//        consumer.position(topicPartitions.get(0), Duration.ofMillis(1000));


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
}
