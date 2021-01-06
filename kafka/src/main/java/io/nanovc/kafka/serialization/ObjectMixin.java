package io.nanovc.kafka.serialization;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * This is a base class mixin that adds Type info to the JSON serialization.
 * This is useful for providing serialization information to Jackson (used by {@link JSONSerde}
 * so that we don't have to pollute the source classes with serialization annotations.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="type")
public abstract class ObjectMixin
{
}
