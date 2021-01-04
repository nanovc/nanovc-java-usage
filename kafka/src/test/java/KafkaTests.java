import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.errors.TopicExistsException;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

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

    public static void main(final String[] args) throws IOException
    {
        if (args.length != 2)
        {
            System.out.println("Please provide command line arguments: configPath topic");
            System.exit(1);
        }

        // Load properties from a local configuration file
        // Create the configuration file (e.g. at '$HOME/.confluent/java.config') with configuration parameters
        // to connect to your Kafka cluster, which can be on your local host, Confluent Cloud, or any other cluster.
        // Follow these instructions to create this file: https://docs.confluent.io/current/tutorials/examples/clients/docs/java.html
        final Properties props = loadConfig(Path.of(".", "config", "java.config"));

        // Create topic if needed
        final String topic = args[1];
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

}
