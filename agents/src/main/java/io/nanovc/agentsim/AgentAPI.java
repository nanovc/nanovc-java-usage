package io.nanovc.agentsim;

/**
 * The API for an agent.
 * Agents manipulate diagrams and models to get to an implementable state.
 *
 * @param <TConfig> The specific type of config that this agent takes.
 */
public interface AgentAPI<TConfig extends AgentConfigAPI>
{
    /**
     * Gets the configuration for this agent.
     *
     * @return The configuration for this agent.
     */
    TConfig getConfig();

    /**
     * Sets the configuration for this agent.
     *
     * @param config The configuration for this agent.
     */
    void setConfig(TConfig config);

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
    void modifyEnvironment(ReadOnlyEnvironmentController input, EnvironmentController output, SimulationIterationAPI iteration, SimulationController simulation, TConfig config) throws Exception;
}
