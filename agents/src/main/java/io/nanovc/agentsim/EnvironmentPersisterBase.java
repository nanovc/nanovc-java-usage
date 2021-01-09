package io.nanovc.agentsim;

import java.io.IOException;

/**
 * A base class for environment persisters.
 * @param <TEnvironment> The specific type of environment to persist.
 * @param <TConfig>        The specific configuration to use.
 */
public abstract class EnvironmentPersisterBase<TEnvironment extends EnvironmentModel, TConfig extends EnvironmentPersisterConfigAPI>
    implements EnvironmentPersisterAPI<TEnvironment, TConfig>
{
    /**
     * The configuration for this persister.
     */
    private TConfig config;

    /**
     * Gets the configuration for this persister.
     *
     * @return The configuration for this persister.
     */
    @Override public TConfig getConfig()
    {
        return this.config;
    }

    /**
     * Sets the configuration for this persister.
     *
     * @param config The configuration for this persister.
     */
    @Override public void setConfig(TConfig config)
    {
        this.config = config;
    }

    /**
     * Loads the environment.
     *
     * @return The environment.
     */
    @Override public TEnvironment loadEnvironment() throws IOException
    {
        return loadEnvironment(this.config);
    }

    /**
     * Loads the environment with the given config.
     * @param config The configuration to use to load the environment.
     * @return The environment model.
     */
    protected abstract TEnvironment loadEnvironment(TConfig config) throws IOException;

    /**
     * Saves the given environment.
     *
     * @param environment The environment to save.
     */
    @Override public void saveEnvironment(TEnvironment environment) throws IOException
    {
        this.saveEnvironment(environment, config);
    }

    /**
     * Saves the environment with the given config.
     * @param environment The environment to save.
     * @param config The configuration to use to save the environment.
     */
    protected abstract void saveEnvironment(TEnvironment environment, TConfig config) throws IOException;
}
