package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.SimulationIterationBase;
import io.nanovc.content.ByteArrayContent;
import io.nanovc.memory.MemoryCommit;

/**
 * The base class for one iteration of an in-memory simulation.
 */
public abstract class MemorySimulationIterationBase
    extends SimulationIterationBase
    implements MemorySimulationIterationAPI
{
    /**
     * The commit in version control that was done for this iteration.
     */
    private MemoryCommit commit;

    /**
     * The content permutator that detected clashing permutations during this iteration.
     * This information can be used to spawn new solutions from this solution.
     */
    private ContentPermutator<ByteArrayContent> contentPermutator;

    /**
     * Gets the commit in version control that was done for this iteration.
     * @return The commit in version control that was done for this iteration.
     */
    public MemoryCommit getCommit()
    {
        return commit;
    }

    /**
     * Sets the commit in version control that was done for this iteration.
     * @param commit The commit in version control that was done for this iteration.
     */
    public void setCommit(MemoryCommit commit)
    {
        this.commit = commit;
    }

    /**
     * Gets the content permutator that detected clashing permutations during this iteration.
     * This information can be used to spawn new solutions from this solution.
     * @return The content permutator that detected clashing permutations during this iteration. This information can be used to spawn new solutions from this solution.
     */
    public ContentPermutator<ByteArrayContent> getContentPermutator()
    {
        return contentPermutator;
    }

    /**
     * Sets the content permutator that detected clashing permutations during this iteration.
     * This information can be used to spawn new solutions from this solution.
     * @param contentPermutator The content permutator that detected clashing permutations during this iteration. This information can be used to spawn new solutions from this solution.
     */
    public void setContentPermutator(ContentPermutator<ByteArrayContent> contentPermutator)
    {
        this.contentPermutator = contentPermutator;
    }
}
