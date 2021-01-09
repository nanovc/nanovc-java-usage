package io.nanovc.agentsim;

/**
 * An interface for a model of something in a {@link EnvironmentModel}.
 */
public interface ModelAPI
{
    /**
     * The name of the model in the {@link EnvironmentModel}.
     * This is used to identify the model across the {@link EnvironmentModel}.
     */
    String getName();

    /**
     * The name of the model in the {@link EnvironmentModel}.
     *
     * @param name The name of the model in the {@link EnvironmentModel}.
     */
    void setName(String name);
}
