package io.nanovc.agentsim;

/**
 * The API for one iteration of a simulation.
 */
public interface SimulationIterationAPI
{
    /**
     * Gets the list of all simulation errors that were created when running the simulation for this iteration.
     * @return The list of all simulation errors that were created when running the simulation for this iteration.
     */
    ValidationResultCollection getSimulationErrors();

    /**
     * Sets the list of all simulation errors that were created when running the simulation for this iteration.
     * @param simulationErrors The list of all simulation errors that were created when running the simulation for this iteration.
     */
    void setSimulationErrors(ValidationResultCollection simulationErrors);

    /**
     * Gets the environment model that was input to the simulation for this iteration.
     * @return The environment model that was input to the simulation for this iteration.
     */
    EnvironmentModel getInputEnvironmentModel();

    /**
     * Sets the environment model that was input to the simulation for this iteration.
     * @param inputEnvironmentModel The environment model that was input to the simulation for this iteration.
     */
    void setInputEnvironmentModel(EnvironmentModel inputEnvironmentModel);

    /**
     * Gets the environment model that was output from the simulation for this iteration.
     * @return The environment model that was output from the simulation for this iteration.
     */
    EnvironmentModel getOutputEnvironmentModel();

    /**
     * Sets the environment model that was output from the simulation for this iteration.
     * @param outputEnvironmentModel The environment model that was output from the simulation for this iteration.
     */
    void setOutputEnvironmentModel(EnvironmentModel outputEnvironmentModel);
}
