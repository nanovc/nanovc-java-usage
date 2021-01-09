package io.nanovc.agentsim;

import java.util.List;

/**
 * The API for a simulation solution.
 * Each simulation can have multiple solutions depending on the actions of the agents during the simulation.
 * @param <TIteration> The specific type of iteration for the simulation.
 */
public interface SimulationSolutionAPI<
    TIteration extends SimulationIterationAPI
    >
{
    /**
     * Gets the list of all simulation errors that were created when running the simulation for this solution.
     * @return The list of all simulation errors that were created when running the simulation for this solution.
     */
    ValidationResultCollection getSimulationErrors();

    /**
     * Sets the list of all simulation errors that were created when running the simulation for this solution.
     * @param simulationErrors The list of all simulation errors that were created when running the simulation for this solution.
     */
    void setSimulationErrors(ValidationResultCollection simulationErrors);

    /**
     * Gets the environment model that was input to the simulation for this solution.
     * @return The environment model that was input to the simulation for this solution.
     */
    EnvironmentModel getInputEnvironmentModel();

    /**
     * Sets the environment model that was input to the simulation for this solution.
     * @param inputEnvironmentModel The environment model that was input to the simulation for this solution.
     */
    void setInputEnvironmentModel(EnvironmentModel inputEnvironmentModel);

    /**
     * Gets the environment model that was output from the simulation for this solution.
     * @return The environment model that was output from the simulation for this solution.
     */
    EnvironmentModel getOutputEnvironmentModel();

    /**
     * Sets the environment model that was output from the simulation for this solution.
     * @param outputEnvironmentModel The environment model that was output from the simulation for this solution.
     */
    void setOutputEnvironmentModel(EnvironmentModel outputEnvironmentModel);

    /**
     * Gets the iterations that were run during the simulation for this solution.
     * @return The iterations that were run during the simulation for this solution.
     */
    List<TIteration> getIterations();

    /**
     * Sets the iterations that were run during the simulation for this solution.
     * @param iterations The iterations that were run during the simulation for this solution.
     */
    void setIterations(List<TIteration> iterations);

    /**
     * Gets whether this solution is completed and has finished running.
     * @return This flags whether this solution is completed and has finished running.
     */
    boolean isCompleted();

    /**
     * Sets whether this solution is completed and has finished running.
     * @param completed This flags whether this solution is completed and has finished running.
     */
    void setCompleted(boolean completed);
}
