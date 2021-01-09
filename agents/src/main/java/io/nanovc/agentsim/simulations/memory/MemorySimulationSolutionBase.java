package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.SimulationSolutionBase;
import io.nanovc.memory.MemoryCommit;

/**
 * The base class for an in-memory simulation solution.
 * @param <TIteration> The specific type of iteration for the simulation.
 */
public abstract class MemorySimulationSolutionBase<
    TIteration extends MemorySimulationIterationAPI
    >
    extends SimulationSolutionBase<TIteration>
    implements MemorySimulationSolutionAPI<TIteration>
{
    /**
     * The index of the solution in the simulation, for reference purposes.
     */
    private int solutionIndex;

    /**
     * The name of this solution.
     * This is also used as the {@link #branchName} in the simulation repo.
     */
    private String solutionName;

    /**
     * The name of the branch that we use in the simulation repo to track the history of this solution.
     * This is the same as the {@link #solutionName}.
     */
    private String branchName;

    /**
     * This is the last commit for this solution.
     * This is the tip of the branch called {@link #branchName}.
     */
    private MemoryCommit lastCommit;

    /**
     * Gets the index of the solution in the simulation, for reference purposes.
     * @return The index of the solution in the simulation, for reference purposes.
     */
    public int getSolutionIndex()
    {
        return solutionIndex;
    }

    /**
     * Sets the index of the solution in the simulation, for reference purposes.
     * @param solutionIndex The index of the solution in the simulation, for reference purposes.
     */
    public void setSolutionIndex(int solutionIndex)
    {
        this.solutionIndex = solutionIndex;
    }

    /**
     * Gets the name of this solution.
     * This is also used as the {@link #getBranchName} in the simulation repo.
     * @return The name of this solution.
     */
    public String getSolutionName()
    {
        return solutionName;
    }

    /**
     * Sets the name of this solution.
     * This is also used as the {@link #getBranchName} in the simulation repo.
     * @param solutionName The name of this solution.
     */
    public void setSolutionName(String solutionName)
    {
        this.solutionName = solutionName;
    }

    /**
     * Gets the name of the branch that we use in the simulation repo to track the history of this solution.
     * This is the same as the {@link #getSolutionName}.
     * @return The name of the branch that we use in the simulation repo to track the history of this solution.
     */
    public String getBranchName()
    {
        return branchName;
    }

    /**
     * Sets the name of the branch that we use in the simulation repo to track the history of this solution.
     * This is the same as the {@link #getSolutionName}.
     * @param branchName The name of the branch that we use in the simulation repo to track the history of this solution.
     */
    public void setBranchName(String branchName)
    {
        this.branchName = branchName;
    }

    /**
     * Gets the last commit for this solution.
     * This is the tip of the branch called {@link #getBranchName}.
     * @return This is the last commit for this solution.
     */
    public MemoryCommit getLastCommit()
    {
        return lastCommit;
    }

    /**
     * Sets the last commit for this solution.
     * This is the tip of the branch called {@link #getBranchName}.
     * @param lastCommit This is the last commit for this solution.
     */
    public void setLastCommit(MemoryCommit lastCommit)
    {
        this.lastCommit = lastCommit;
    }
}
