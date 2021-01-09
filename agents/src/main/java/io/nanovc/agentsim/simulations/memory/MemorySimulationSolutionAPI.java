package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.SimulationSolutionAPI;
import io.nanovc.memory.MemoryCommit;

/**
 * The API for an in-memory simulation solution.
 * @param <TIteration> The specific type of iteration for the simulation.
 */
public interface MemorySimulationSolutionAPI<
    TIteration extends MemorySimulationIterationAPI
  >
    extends SimulationSolutionAPI<TIteration>
{
    /**
     * Gets the index of the solution in the simulation, for reference purposes.
     * @return The index of the solution in the simulation, for reference purposes.
     */
    int getSolutionIndex();

    /**
     * Sets the index of the solution in the simulation, for reference purposes.
     * @param solutionIndex The index of the solution in the simulation, for reference purposes.
     */
    void setSolutionIndex(int solutionIndex);

    /**
     * Gets the name of this solution.
     * This is also used as the {@link #getBranchName} in the simulation repo.
     * @return The name of this solution.
     */
    String getSolutionName();

    /**
     * Sets the name of this solution.
     * This is also used as the {@link #getBranchName} in the simulation repo.
     * @param solutionName The name of this solution.
     */
    void setSolutionName(String solutionName);

    /**
     * Gets the name of the branch that we use in the simulation repo to track the history of this solution.
     * This is the same as the {@link #getSolutionName}.
     * @return The name of the branch that we use in the simulation repo to track the history of this solution.
     */
    String getBranchName();

    /**
     * Sets the name of the branch that we use in the simulation repo to track the history of this solution.
     * This is the same as the {@link #getSolutionName}.
     * @param branchName The name of the branch that we use in the simulation repo to track the history of this solution.
     */
    void setBranchName(String branchName);

    /**
     * Gets the last commit for this solution.
     * This is the tip of the branch called {@link #getBranchName}.
     * @return This is the last commit for this solution.
     */
    MemoryCommit getLastCommit();

    /**
     * Sets the last commit for this solution.
     * This is the tip of the branch called {@link #getBranchName}.
     * @param lastCommit This is the last commit for this solution.
     */
    void setLastCommit(MemoryCommit lastCommit);
}
