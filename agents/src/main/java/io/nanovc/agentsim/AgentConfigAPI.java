package io.nanovc.agentsim;

/**
 * The API for agent configuration.
 */
public interface AgentConfigAPI
{
    /**
     * Flags whether this agent is enabled.
     * Sometimes it's useful to keep the agent configurations but to disable them as needed.
     * @return True if this agent is enabled. False if it is not enabled.
     */
    boolean isEnabled();

    /**
     * Flags whether this agent is enabled.
     * Sometimes it's useful to keep the agent configurations but to disable them as needed.
     * @param enabled True if this agent is enabled. False if it is not enabled.
     */
    void setEnabled(boolean enabled);
}
