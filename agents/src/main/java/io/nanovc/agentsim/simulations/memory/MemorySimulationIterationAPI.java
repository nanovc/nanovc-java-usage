package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.SimulationIterationAPI;
import io.nanovc.content.ByteArrayContent;
import io.nanovc.memory.MemoryCommit;

/**
 * The API for one iteration of an in-memory simulation.
 */
public interface MemorySimulationIterationAPI
    extends SimulationIterationAPI
{
    /**
     * Gets the commit in version control that was done for this iteration.
     * @return The commit in version control that was done for this iteration.
     */
    MemoryCommit getCommit();

    /**
     * Sets the commit in version control that was done for this iteration.
     * @param commit The commit in version control that was done for this iteration.
     */
    void setCommit(MemoryCommit commit);

    /**
     * Gets the content permutator that detected clashing permutations during this iteration.
     * This information can be used to spawn new solutions from this solution.
     * @return The content permutator that detected clashing permutations during this iteration. This information can be used to spawn new solutions from this solution.
     */
    ContentPermutator<ByteArrayContent> getContentPermutator();

    /**
     * Sets the content permutator that detected clashing permutations during this iteration.
     * This information can be used to spawn new solutions from this solution.
     * @param contentPermutator The content permutator that detected clashing permutations during this iteration. This information can be used to spawn new solutions from this solution.
     */
    void setContentPermutator(ContentPermutator<ByteArrayContent> contentPermutator);
}
