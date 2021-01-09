package io.nanovc.agentsim;

/**
 * A model of the environment that we are simulating.
 */
public class EnvironmentModel
{
    /**
     * The name of the environment;
     */
    public String name;

    /**
     * A description for this environment which gives more context.
     */
    public String description;

    /**
     * The models in this environment.
     * This is what agents interact with and manipulate during simulations.
     */
    public ModelCollection models = new ModelCollection();

    /**
     * The configurations for the agents to run when determining the output model.
     */
    public AgentConfigCollection agentConfigs = new AgentConfigCollection();
}
