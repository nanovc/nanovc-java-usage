package io.nanovc.agentsim.pricecalc;

import io.nanovc.agentsim.ModelBase;

/**
 * This is a registered period of interest for a given agent.
 * This
 */
public class PeriodOfInterestForAgent extends ModelBase
{
    /**
     * The prefix for the name of the period of interest in the {@link io.nanovc.agentsim.EnvironmentModel environment}.
     */
    public final static String NAME_PREFIX = "period-of-interest-for-";

    /**
     * The period of interest for the given agent.
     */
    public TimeSlice period = new TimeSlice();

    /**
     * The name of the agent that we want to register the period of interest for.
     */
    public String agentName;

}
