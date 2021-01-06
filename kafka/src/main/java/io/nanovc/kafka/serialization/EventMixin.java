package io.nanovc.kafka.serialization;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * This mixin provides the required Jackson annotations so that we can polymorphically deserialize the record on the event.
 */
public class EventMixin extends ObjectMixin
{
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="type")
    public Object record;
}
