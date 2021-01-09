package io.nanovc.agentsim;

import java.io.IOException;

/**
 * The interface for a persister that loads and saves environments to durable storage.
 * @param <TEnvironment> The specific type of environment to persist.
 * @param <TConfig>        The specific configuration to use.
 */
public interface EnvironmentPersisterAPI<TEnvironment extends EnvironmentModel, TConfig extends EnvironmentPersisterConfigAPI>
{
    /**
     * Gets the configuration for this persister.
     * @return The configuration for this persister.
     */
    TConfig getConfig();

    /**
     * Sets the configuration for this persister.
     * @param config The configuration for this persister.
     */
    void setConfig(TConfig config);

    /**
     * Loads the environment.
     * @return The environment.
     */
    TEnvironment loadEnvironment() throws IOException;

    /**
     * Saves the given environment.
     * @param environment The environment to save.
     */
    void saveEnvironment(TEnvironment environment) throws IOException;
}
