package io.nanovc.agentsim.pricecalc;

import io.nanovc.agentsim.*;

import java.util.Map;

/**
 * The agent that controls simulation time.
 */
public class TimeAgent extends AgentBase<TimeAgent.Config>
{
    /**
     * The configuration for the time agent.
     */
    public static class Config extends AgentConfigBase
    {

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
        // Create a controller that helps with named agents:
        NamedAgentController namedAgentController = new NamedAgentController(output);
        namedAgentController.indexNamedAgents();

        // Get all the periods of interest for named agents:
        Map<NamedAgentConfigAPI, PeriodOfInterestForAgent> periodOfInterestForAgentMap = namedAgentController.findModelsForNamedAgents(
            PeriodOfInterestForAgent.class,
            periodOfInterestForAgent -> periodOfInterestForAgent.agentName
        );

        // Create the time controller for this agent:
        TimeController timeController = new TimeController(output);

        // Update the clock based on the agents that have a period of interest:
        timeController.updateClockBasedOnPeriodsOfInterest(periodOfInterestForAgentMap);

    }
}
