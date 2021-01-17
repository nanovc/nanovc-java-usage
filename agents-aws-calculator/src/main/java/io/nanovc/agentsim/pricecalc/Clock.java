package io.nanovc.agentsim.pricecalc;

import io.nanovc.agentsim.ModelBase;

/**
 * A model of a clock for the pricing calculation.
 * This is used to signify the point in time that we are simulating.
 */
public class Clock extends ModelBase
{
    /**
     * The name of the clock in the {@link io.nanovc.agentsim.EnvironmentModel environment}.
     */
    public final static String NAME = "clock";

    /**
     * Creates a new {@link Clock clock}.
     */
    public Clock()
    {
        // Set the name for this clock:
        this.setName(NAME);
    }

    /**
     * The period that we are simulating.
     */
    public TimeSlice now = new TimeSlice();

}
