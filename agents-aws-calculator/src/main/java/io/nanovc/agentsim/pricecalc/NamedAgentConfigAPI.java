package io.nanovc.agentsim.pricecalc;

import io.nanovc.agentsim.AgentConfigAPI;

/**
 * This is the interface for the configuration of an agent with a name.
 */
public interface NamedAgentConfigAPI extends AgentConfigAPI
{
    /**
     * Gets the name of the agent.
     * @return The name of the agent.
     */
    String getAgentName();
}
