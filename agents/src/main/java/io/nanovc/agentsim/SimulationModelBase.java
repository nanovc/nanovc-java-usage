package io.nanovc.agentsim;

import java.util.ArrayList;
import java.util.List;

/**
 * The base class for a simulation model.
 * <p>
 * This represents the full run of the simulation
 * with the input environment that we started with
 * along with all the solutions produced by the simulation.
 *
 * @param <TIteration> The specific type of iteration for the simulation.
 * @param <TSolution>  The specific type of solution for the simulation.
 */
public abstract class SimulationModelBase<
    TIteration extends SimulationIterationAPI,
    TSolution extends SimulationSolutionAPI<TIteration>
    > implements SimulationModelAPI<TIteration, TSolution>
{

    /**
     * The environment model that was input to the simulation.
     */
    private EnvironmentModel inputEnvironmentModel;

    /**
     * The solutions that were found during the simulation.
     */
    private List<TSolution> solutions = new ArrayList<>();

    /**
     * Gets the environment model that was input to the simulation.
     *
     * @return The environment model that was input to the simulation.
     */
    public EnvironmentModel getInputEnvironmentModel()
    {
        return inputEnvironmentModel;
    }

    /**
     * Sets the environment model that was input to the simulation.
     *
     * @param inputEnvironmentModel The environment model that was input to the simulation.
     */
    public void setInputEnvironmentModel(EnvironmentModel inputEnvironmentModel)
    {
        this.inputEnvironmentModel = inputEnvironmentModel;
    }

    /**
     * Gets the solutions that were found during the simulation.
     *
     * @return The solutions that were found during the simulation.
     */
    @Override public List<TSolution> getSolutions()
    {
        return this.solutions;
    }

    /**
     * Sets the solutions that were found during the simulation.
     *
     * @param solutions The solutions that were found during the simulation.
     */
    @Override public void setSolutions(List<TSolution> solutions)
    {
        this.solutions = solutions;
    }
}
