package io.nanovc.agentsim.pricecalc;

import io.nanovc.agentsim.*;

/**
 * An agent that is aware of the current simulation time (from the {@link Clock clock}.
 */
public abstract class TimeAwareAgentBase<TConfig extends TimeAwareAgentBase.ConfigAPI> extends AgentBase<TConfig>
{
    /**
     * The API for the config for a {@link TimeAwareAgentBase time aware agent}.
     * The agent needs to be named because that is needed for the time based simulation.
     */
    public interface ConfigAPI extends NamedAgentConfigAPI
    {
    }

    /**
     * The config for a {@link TimeAwareAgentBase time aware agent}.
     * The agent needs to be named because that is needed for the time based simulation.
     */
    public static class Config extends AgentConfigBase implements ConfigAPI
    {
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
    @Override public void modifyEnvironment(ReadOnlyEnvironmentController input, EnvironmentController output, SimulationIterationAPI iteration, SimulationController simulation, TConfig config) throws Exception
    {
        // Get a controller for time:
        TimeController timeController = new TimeController(output);

        // Get the period of interest that this agent has:
        TimeSlice periodOfInterest = definePeriodOfInterest(config, timeController);
        if (periodOfInterest != null)
        {
            // The agent has a period of interest.

            // Register a period of interest for this agent:
            timeController.registerPeriodOfInterest(periodOfInterest, config.getAgentName());

            // Define the period of time that this agent is interested in and handle the logic if we currently intersect with it:
            timeController.handlePeriodOfInterest(
                periodOfInterest,
                (past, overlap, future) ->
                {
                    // Handle the current overlap:
                    if (overlap != null)
                    {
                        // We have an overlap for the agent at the given clock time.

                        // Handle the currently simulated period, based on the simulation time on the clock:
                        handleCurrentPeriod(config, overlap, timeController, input, output, iteration, simulation);
                    }

                    // Check if there is still a future component for this agent:
                    if (future != null)
                    {
                        // There is a future component for this agent.

                        // Register a period of interest only for the future period that is still outstanding (for the next iteration):
                        timeController.registerPeriodOfInterest(future, config.getAgentName());
                    }
                    else
                    {
                        // There is no future component for this agent.

                        // Deregister the period of interest for this agent:
                        timeController.deRegisterPeriodOfInterest(config.getAgentName());
                    }
                }
            );
        }
    }

    /**
     * Defines the {@link TimeSlice period of interest} that the agent is interested in for the simulation.
     *
     * @param config         The configuration for this agent.
     * @param timeController The time controller that makes it easier to understand the simulation time.
     * @return The {@link TimeSlice time slice} that the agent is interested in for the simulation.
     */
    protected abstract TimeSlice definePeriodOfInterest(TConfig config, TimeController timeController);

    /**
     * Handles the current period in the simulation for this agent.
     * This is the period that overlaps with the agents period of interest and the current {@link Clock simulation clock}.
     * @param config         The configuration for this agent.
     * @param currentPeriod  The currently simulated period, based on the simulation time on the clock.
     * @param timeController The time controller that makes it easier to understand the simulation time.
     * @param input      The input environment model to this iteration. This is provided for reference. It must not be modified.
     * @param output     The output environment model for this iteration. The agent is allowed to modify this output model for the next iteration.
     * @param iteration  The current simulation iteration that is running.
     * @param simulation The simulation that is running.
     */
    protected abstract void handleCurrentPeriod(TConfig config, TimeSlice currentPeriod, TimeController timeController, ReadOnlyEnvironmentController input, EnvironmentController output, SimulationIterationAPI iteration, SimulationController simulation);
}
