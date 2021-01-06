package io.nanovc.kafka.serialization;

import io.nanovc.Event;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Tests the {@link JSONSerde} serializer and deserializer.
 */
class JSONSerdeTests
{

    @Test
    public void creationTest()
    {
        new JSONSerde<>(Object.class);
        new JSONSerde<>(Object.class, Map.of(Object.class, ObjectMixin.class), null);
    }

    @Test
    public void recordSerializationTest()
    {
        // Create the record we want to serialize:
        Record record = new Record();
        record.name = "Luke";

        // Create the serde:
        JSONSerde<Record> serde = new JSONSerde<>(Record.class, Map.of(Record.class, RecordMixin.class), null);

        // Configure the serializer:
        serde.configure(Collections.emptyMap(), false);

        // Serialize the record:
        byte[] bytes = serde.serialize("none", record);
        assertEquals("{\n" +
                     "  \"type\" : \"io.nanovc.kafka.serialization.JSONSerdeTests$Record\",\n" +
                     "  \"name\" : \"Luke\"\n" +
                     "}", new String(bytes, StandardCharsets.UTF_8));

        // Deserialize the record:
        Record deserializedRecord = serde.deserialize("none", bytes);

        // Make sure the record is as expected:
        assertNotSame(record, deserializedRecord);
        assertEquals("Luke", record.name);
    }

    @Test
    public void eventRecordSerializationTest()
    {
        // Create the record we want to serialize:
        Record record = new Record();
        record.name = "Luke";

        // Wrap the record as an event:
        Event event = new Event();
        event.setRecord(record);

        // Create the serde:
        JSONSerde<Event> serde = new JSONSerde<>(
            Event.class,
            Map.of(
                Event.class, EventMixin.class,
                Record.class, RecordMixin.class
            ),
            null
        );

        // Configure the serializer:
        serde.configure(Collections.emptyMap(), false);

        // Serialize the record:
        byte[] bytes = serde.serialize("none", event);
        assertEquals("{\n" +
                     "  \"type\" : \"io.nanovc.Event\",\n" +
                     "  \"record\" : {\n" +
                     "    \"type\" : \"io.nanovc.kafka.serialization.JSONSerdeTests$Record\",\n" +
                     "    \"name\" : \"Luke\"\n" +
                     "  }\n" +
                     "}", new String(bytes, StandardCharsets.UTF_8));

        // Deserialize the record:
        Event deserializedEvent = serde.deserialize("none", bytes);

        // Make sure the event is as expected:
        assertNotSame(event, deserializedEvent);
        assertNotSame(record, deserializedEvent.getRecord());
        assertEquals("Luke", ((Record) deserializedEvent.getRecord()).name);
    }

    @Test
    public void eventRecordSerializationWithDefaultTypingTest()
    {
        // Create the record we want to serialize:
        Record record = new Record();
        record.name = "Luke";

        // Wrap the record as an event:
        Event event = new Event();
        event.setRecord(record);

        // Create the serde:
        JSONSerde<Event> serde = new JSONSerde<>(
            Event.class,
            null,
            builder -> builder.allowIfBaseType(Object.class)
        );

        // Configure the serializer:
        serde.configure(Collections.emptyMap(), false);

        // Serialize the record:
        byte[] bytes = serde.serialize("none", event);
        assertEquals("{\n" +
                     "  \"record\" : [\n" +
                     "    \"io.nanovc.kafka.serialization.JSONSerdeTests$Record\",\n" +
                     "    {\n" +
                     "      \"name\" : \"Luke\"\n" +
                     "    }\n" +
                     "  ]\n" +
                     "}", new String(bytes, StandardCharsets.UTF_8));

        // Deserialize the record:
        Event deserializedEvent = serde.deserialize("none", bytes);

        // Make sure the event is as expected:
        assertNotSame(event, deserializedEvent);
        assertNotSame(record, deserializedEvent.getRecord());
        assertEquals("Luke", ((Record) deserializedEvent.getRecord()).name);
    }

    public static class Record
    {
        public String name;
    }

    public static class RecordMixin extends ObjectMixin
    {
    }
}
