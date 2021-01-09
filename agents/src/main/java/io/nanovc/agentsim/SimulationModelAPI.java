package io.nanovc.agentsim;

import java.util.List;

/**
 * The API for a simulation model.
 * <p>
 * This represents the full run of the simulation
 * with the input environment that we started with
 * along with all the solutions produced by the simulation.
 *
 * @param <TIteration> The specific type of iteration for the simulation.
 * @param <TSolution>  The specific type of solution for the simulation.
 */
public interface SimulationModelAPI<
    TIteration extends SimulationIterationAPI,
    TSolution extends SimulationSolutionAPI<TIteration>
    >
{
    /**
     * Gets the environment model that was input to the simulation.
     *
     * @return The environment model that was input to the simulation.
     */
    EnvironmentModel getInputEnvironmentModel();

    /**
     * Sets the environment model that was input to the simulation.
     *
     * @param inputEnvironmentModel The environment model that was input to the simulation.
     */
    void setInputEnvironmentModel(EnvironmentModel inputEnvironmentModel);

    /**
     * Gets the solutions that were found during the simulation.
     *
     * @return The solutions that were found during the simulation.
     */
    List<TSolution> getSolutions();

    /**
     * Sets the solutions that were found during the simulation.
     *
     * @param solutions The solutions that were found during the simulation.
     */
    void setSolutions(List<TSolution> solutions);
}
