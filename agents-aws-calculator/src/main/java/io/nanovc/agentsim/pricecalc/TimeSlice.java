package io.nanovc.agentsim.pricecalc;

import java.time.Instant;

/**
 * A slice of time for simulation.
 * This is used to signify the point in time as well as the atomic duration that we are simulating.
 */
public class TimeSlice
{
    /**
     * The inclusive start date and time for the period that we are simulating.
     * Inclusive means that this instant in time is considered within the simulation period.
     */
    public Instant startInclusive;

    /**
     * The exclusive end date and time for the period that we are simulating.
     * Exclusive means that this instant in time is not considered within the simulation period.
     */
    public Instant endExclusive;
}
