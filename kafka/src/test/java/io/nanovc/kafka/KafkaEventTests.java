package io.nanovc.kafka;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}
