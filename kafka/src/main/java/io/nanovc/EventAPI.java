package io.nanovc;

/**
 * The interface for an event.
 * This is part of the {@link io.nanovc.CRUSHED#EVENT} concepts.
 */
public interface EventAPI
{
    /**
     * Gets the record for this event.
     * @return The record for this event.
     */
    Object getRecord();

    /**
     * Sets the record for the event.
     * @param record The record for the event.
     */
    void setRecord(Object record);
}
