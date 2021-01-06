package io.nanovc.kafka.serialization;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link JSONSerde} serializer and deserializer.
 */
class JSONSerdeTests
{

    @Test
    public void creationTest()
    {
        new JSONSerde<>(Object.class);
        new JSONSerde<>(Object.class, Map.of(Object.class, ObjectMixin.class));
    }

    @Test
    public void basicSerializationTest()
    {
        // Create the record we want to serialize:
        Record record = new Record();
        record.name = "Luke";

        // Create the serde:
        JSONSerde<Record> serde = new JSONSerde<>(Record.class, Map.of(Record.class, RecordMixin.class));

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

    public static class Record
    {
        public String name;
    }

    public static class RecordMixin extends ObjectMixin
    {
    }
}
