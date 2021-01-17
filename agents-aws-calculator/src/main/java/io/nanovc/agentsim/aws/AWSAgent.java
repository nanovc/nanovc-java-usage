package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.*;
import io.nanovc.agentsim.pricecalc.TimeController;
import io.nanovc.agentsim.pricecalc.TimeSlice;

public class AWSAgent extends AgentBase<AWSAgent.Config>
{
    /**
     * The config for an AWS Agent.
     */
    public static class Config extends AgentConfigBase
    {
        /**
         * The period that this agent should do stuff.
         */
        public TimeSlice period = new TimeSlice();
    }

    /**
     * This allows the agent to modify the environment for an iteration.
     *
     * @param input      The input environment model to this iteration. This is provided for reference. It must not be modified.
     * @param output     The output environment model for this iteration. The agent is allowed to modify this output model for the next iteration.
     * @param iteration  The current simulation iteration that is running.
     * @param simulation The simulation that is running.
     * @param config     The agent configuration.
     * @throws Exception When an error occurs with the agent modifying the environment.
     */
    @Override public void modifyEnvironment(ReadOnlyEnvironmentController input, EnvironmentController output, SimulationIterationAPI iteration, SimulationController simulation, Config config) throws Exception
    {
        // Get a controller for time:
        TimeController timeController = new TimeController(output);

        // Define the period of time that this agent is interested in and handle the logic if we currently intersect with it:
        timeController.handlePeriodOfInterest(
            config.period,
            (past, overlap, future) ->
            {
                if (overlap != null)
                {
                    timeController.timeline.timeSlices.add(overlap);
                }

                timeController.clock.now = future;
            }
        );
    }


}
