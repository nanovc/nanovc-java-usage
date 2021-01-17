package io.nanovc.agentsim.pricecalc;

import io.nanovc.agentsim.ModelBase;

/**
 * A timeline that records the passage of time for the simulation.
 */
public class Timeline extends ModelBase
{
    /**
     * The name of the timeline in the {@link io.nanovc.agentsim.EnvironmentModel environment}.
     */
    public final static String NAME = "timeline";

    /**
     * Creates a new {@link Timeline timeline}.
     */
    public Timeline()
    {
        // Set the name for this timeline:
        this.setName(NAME);
    }

    /**
     * The time slices that have been simulated for this timeline.
     * As each {@link TimeSlice time slice} from the {@link Clock clock} is finished being simulated,
     * it is added to the {@link Timeline timeline}.
     */
    public TimeSliceCollection timeSlices = new TimeSliceCollection();
}
