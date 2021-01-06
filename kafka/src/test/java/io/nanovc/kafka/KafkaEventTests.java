package io.nanovc.kafka;

import io.nanovc.kafka.serialization.JSONSerde;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Properties;

import static io.nanovc.kafka.KafkaHelper.*;

/**
 * Tests the {@link KafkaEvent}'s.
 */
class KafkaEventTests
{
    @Test
    public void creationTest()
    {
        new KafkaEvent();
    }

    /**
     * An example of using the streams API.
     * Inspired by:
     * https://github.com/confluentinc/kafka-streams-examples/blob/6.0.1-post/src/main/java/io/confluent/examples/streams/MapFunctionLambdaExample.java
     */
    @Test
    public void streamsExample() throws IOException, InterruptedException
    {
        final String eventTopicName = "kafka-events";

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

        // Add additional properties:
        ProducerConfigInfo infoForProducer = prepareConfigForProducers(
            props,
            "kafka-repo-stream-producer",
            StringSerializer.class,
            JSONSerde.class
        );

        StreamConfigInfo infoForStreams = prepareConfigForStreams(
            props,
            "kafka-repo-stream", true,
            "kafka-repo-stream-client"
        );

        // Make sure that the event topic exists:
        createTopic(eventTopicName, 1, 3, props);

        // Set up serializers and deserializers, which we will use for overriding the default serdes specified above.
        final Serde<String> stringSerde = Serdes.String();
        JSONSerde<KafkaEvent> kafkaEventJSONSerde = new JSONSerde<>(
            KafkaEvent.class,
            null,
            builder -> builder.allowIfBaseType(Object.class)
        );
        kafkaEventJSONSerde.configure(Collections.emptyMap(), false);

        // Create the producer:
        Producer<String, KafkaEvent> producer = new KafkaProducer<String, KafkaEvent>(props, stringSerde.serializer(), kafkaEventJSONSerde.serializer());

        // Produce sample data
        final Long numMessages = 10L;
        for (Long i = 0L; i < numMessages; i++)
        {
            String key = "key-" + i;
            KafkaEvent event = new KafkaEvent();
            event.setRecord("Hello World " + i);

            System.out.printf("Producing event: %s\t%s%n", key, event);
            producer.send(new ProducerRecord<String, KafkaEvent>(eventTopicName, key, event), new Callback()
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
                        System.out.printf("Produced event to topic %s partition [%d] @ offset %d%n", m.topic(), m.partition(), m.offset());
                    }
                }
            });
        }

        producer.flush();

        System.out.printf(numMessages + " events were produced to topic %s%n", eventTopicName);

        producer.close();
    }
}
