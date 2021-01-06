package io.nanovc;

/**
 * A base class for events.
 * This is part of the {@link io.nanovc.CRUSHED#EVENT} concepts.
 */
public abstract class EventBase implements EventAPI
{
    /**
     * The record for the event.
     */
    protected Object record;

    /**
     * Gets the record for the event.
     * @return The record for the event.
     */
    @Override public Object getRecord()
    {
        return record;
    }

    /**
     * Sets the record for the event.
     * @param record The record for the event.
     */
    @Override public void setRecord(Object record)
    {
        this.record = record;
    }
}
