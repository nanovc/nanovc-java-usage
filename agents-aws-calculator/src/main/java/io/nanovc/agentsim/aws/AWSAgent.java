package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.*;
import io.nanovc.agentsim.pricecalc.NamedAgentConfigAPI;
import io.nanovc.agentsim.pricecalc.TimeController;
import io.nanovc.agentsim.pricecalc.TimeSlice;

public class AWSAgent extends AgentBase<AWSAgent.Config>
{
    /**
     * The config for an AWS Agent.
     */
    public static class Config extends AgentConfigBase implements NamedAgentConfigAPI
    {
        /**
         * The period that this agent should do stuff.
         */
        public TimeSlice period = new TimeSlice();

        /**
         * The name of this agent.
         * This gets used for registering time based events for this agent.
         */
        public String agentName;

        /**
         * Gets the name of the agent.
         *
         * @return The name of the agent.
         */
        @Override public String getAgentName()
        {
            return this.agentName;
        }
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

        // Register a period of interest for this agent:
        timeController.registerPeriodOfInterest(config.period, config.agentName);

        // Define the period of time that this agent is interested in and handle the logic if we currently intersect with it:
        timeController.handlePeriodOfInterest(
            config.period,
            (past, overlap, future) ->
            {
                // Handle the current overlap:
                if (overlap != null)
                {
                    // We have an overlap for the agent at the given clock time.
                }

                // Check if there is still a future component for this agent:
                if (future != null)
                {
                    // There is a future component for this agent.

                    // Register a period of interest only for the future period that is still outstanding (for the next iteration):
                    timeController.registerPeriodOfInterest(future, config.agentName);
                }
            }
        );
    }


}
