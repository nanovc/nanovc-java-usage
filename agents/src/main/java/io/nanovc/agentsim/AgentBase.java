package io.nanovc.agentsim;

/**
 * A base class for an agent.
 * Agents manipulate diagrams and models to get to an implementable state.
 * @param <TConfig> The specific type of config that this agent takes.
 */
public abstract class AgentBase<TConfig extends AgentConfigAPI>
    implements AgentAPI<TConfig>
{
    /**
     * The configuration for this agent.
     */
    private TConfig config;

    /**
     * Gets the configuration for this agent.
     * @return The configuration for this agent.
     */
    public TConfig getConfig()
    {
        return config;
    }

    /**
     * Sets the configuration for this agent.
     * @param config The configuration for this agent.
     */
    public void setConfig(TConfig config)
    {
        this.config = config;
    }
}
