package io.nanovc.agentsim;

/**
 * The base class for a model of something in an {@link EnvironmentModel}.
 */
public abstract class ModelBase implements ModelAPI
{
    /**
     * The name of the model item.
     * This is used to identify the model item across the {@link EnvironmentModel}.
     */
    public String name;

    /**
     * The name of the model in the {@link EnvironmentModel}.
     * This is used to identify the model across the {@link EnvironmentModel}.
     */
    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * The name of the model in the {@link EnvironmentModel}.
     *
     * @param name The name of the model in the {@link EnvironmentModel}.
     */
    @Override
    public void setName(String name)
    {
        this.name = name;
    }
}
