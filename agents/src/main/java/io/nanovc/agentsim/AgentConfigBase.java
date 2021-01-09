package io.nanovc.agentsim;

/**
 * The base class for agent configuration.
 */
public abstract class AgentConfigBase implements AgentConfigAPI
{
    /**
     * Flags whether this agent is enabled.
     * Sometimes it's useful to keep the agent configurations but to disable them as needed.
     */
    private boolean isEnabled = true;

    /**
     * Flags whether this agent is enabled.
     * Sometimes it's useful to keep the agent configurations but to disable them as needed.
     * @return True if this agent is enabled. False if it is not enabled.
     */
    public boolean isEnabled()
    {
        return isEnabled;
    }

    /**
     * Flags whether this agent is enabled.
     * Sometimes it's useful to keep the agent configurations but to disable them as needed.
     * @param enabled True if this agent is enabled. False if it is not enabled.
     */
    public void setEnabled(boolean enabled)
    {
        isEnabled = enabled;
    }
}
