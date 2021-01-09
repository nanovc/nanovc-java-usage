package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.*;

public class AWSAgent extends AgentBase<AWSAgent.Config>
{
    /**
     * The config for an AWS Agent.
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

    }


}
