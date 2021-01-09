package io.nanovc.agentsim;

import java.util.ArrayList;
import java.util.List;

/**
 * The base class for a simulation solution.
 * Each simulation can have multiple solutions depending on the actions of the agents during the simulation.
 * @param <TIteration> The specific type of iteration for the simulation.
 */
public abstract class SimulationSolutionBase<
    TIteration extends SimulationIterationAPI
    > implements SimulationSolutionAPI<TIteration>
{
    /**
     * The list of all simulation errors throughout all iterations of this solution.
     */
    private ValidationResultCollection simulationErrors = new ValidationResultCollection();

    /**
     * The environment model that was input to the simulation for this solution.
     */
    private EnvironmentModel inputEnvironmentModel;

    /**
     * The environment model that was output from the simulation for this solution.
     */
    private EnvironmentModel outputEnvironmentModel;

    /**
     * The iterations that were run during the simulation for this solution.
     */
    private List<TIteration> iterations = new ArrayList<>();

    /**
     * This flags whether this solution is completed and has finished running.
     */
    private boolean isCompleted;

    /**
     * Gets the list of all simulation errors that were created when running the simulation for this solution.
     * @return The list of all simulation errors that were created when running the simulation for this solution.
     */
    @Override
    public ValidationResultCollection getSimulationErrors()
    {
        return simulationErrors;
    }

    /**
     * Sets the list of all simulation errors that were created when running the simulation for this solution.
     * @param simulationErrors The list of all simulation errors that were created when running the simulation for this solution.
     */
    @Override
    public void setSimulationErrors(ValidationResultCollection simulationErrors)
    {
        this.simulationErrors = simulationErrors;
    }

    /**
     * Gets the environment model that was input to the simulation for this solution.
     * @return The environment model that was input to the simulation for this solution.
     */
    @Override
    public EnvironmentModel getInputEnvironmentModel()
    {
        return inputEnvironmentModel;
    }

    /**
     * Sets the environment model that was input to the simulation for this solution.
     * @param inputEnvironmentModel The environment model that was input to the simulation for this solution.
     */
    @Override public void setInputEnvironmentModel(EnvironmentModel inputEnvironmentModel)
    {
        this.inputEnvironmentModel = inputEnvironmentModel;
    }

    /**
     * Gets the environment model that was output from the simulation for this solution.
     * @return The environment model that was output from the simulation for this solution.
     */
    @Override
    public EnvironmentModel getOutputEnvironmentModel()
    {
        return outputEnvironmentModel;
    }

    /**
     * Sets the environment model that was output from the simulation for this solution.
     * @param outputEnvironmentModel The environment model that was output from the simulation for this solution.
     */
    @Override
    public void setOutputEnvironmentModel(EnvironmentModel outputEnvironmentModel)
    {
        this.outputEnvironmentModel = outputEnvironmentModel;
    }

    /**
     * Gets the iterations that were run during the simulation for this solution.
     * @return The iterations that were run during the simulation for this solution.
     */
    @Override
    public List<TIteration> getIterations()
    {
        return iterations;
    }

    /**
     * Sets the iterations that were run during the simulation for this solution.
     * @param iterations The iterations that were run during the simulation for this solution.
     */
    @Override
    public void setIterations(List<TIteration> iterations)
    {
        this.iterations = iterations;
    }

    /**
     * Gets whether this solution is completed and has finished running.
     * @return This flags whether this solution is completed and has finished running.
     */
    @Override
    public boolean isCompleted()
    {
        return isCompleted;
    }

    /**
     * Sets whether this solution is completed and has finished running.
     * @param completed This flags whether this solution is completed and has finished running.
     */
    @Override
    public void setCompleted(boolean completed)
    {
        isCompleted = completed;
    }
}
