package io.nanovc.agentsim;

/**
 * The base class for one iteration of a simulation.
 */
public abstract class SimulationIterationBase implements SimulationIterationAPI
{
    /**
     * The list of all simulation errors that were created when running the simulation for this iteration.
     */
    private ValidationResultCollection simulationErrors = new ValidationResultCollection();

    /**
     * The environment model that was input to the simulation for this iteration.
     */
    private EnvironmentModel inputEnvironmentModel;

    /**
     * The environment model that was output from the simulation for this iteration.
     */
    private EnvironmentModel outputEnvironmentModel;

    /**
     * Gets the list of all simulation errors that were created when running the simulation for this iteration.
     * @return The list of all simulation errors that were created when running the simulation for this iteration.
     */
    public ValidationResultCollection getSimulationErrors()
    {
        return simulationErrors;
    }

    /**
     * Sets the list of all simulation errors that were created when running the simulation for this iteration.
     * @param simulationErrors The list of all simulation errors that were created when running the simulation for this iteration.
     */
    public void setSimulationErrors(ValidationResultCollection simulationErrors)
    {
        this.simulationErrors = simulationErrors;
    }

    /**
     * Gets the environment model that was input to the simulation for this iteration.
     * @return The environment model that was input to the simulation for this iteration.
     */
    public EnvironmentModel getInputEnvironmentModel()
    {
        return inputEnvironmentModel;
    }

    /**
     * Sets the environment model that was input to the simulation for this iteration.
     * @param inputEnvironmentModel The environment model that was input to the simulation for this iteration.
     */
    public void setInputEnvironmentModel(EnvironmentModel inputEnvironmentModel)
    {
        this.inputEnvironmentModel = inputEnvironmentModel;
    }

    /**
     * Gets the environment model that was output from the simulation for this iteration.
     * @return The environment model that was output from the simulation for this iteration.
     */
    public EnvironmentModel getOutputEnvironmentModel()
    {
        return outputEnvironmentModel;
    }

    /**
     * Sets the environment model that was output from the simulation for this iteration.
     * @param outputEnvironmentModel The environment model that was output from the simulation for this iteration.
     */
    public void setOutputEnvironmentModel(EnvironmentModel outputEnvironmentModel)
    {
        this.outputEnvironmentModel = outputEnvironmentModel;
    }
}
