package io.nanovc.agentsim.simulations.memory;

import io.nanovc.*;

/**
 * The merge handler that tracks any merge conflicts and adds them to a {@link ContentPermutator} so that we can spawn multiple solutions for them.
 * @param <TPermutatorContent> The specific type of content that the content permutator gathers.
 */
public class MemorySimulationMergerWithContentPermutator<TPermutatorContent extends ContentAPI>
    extends MemorySimulationMerger
{
    /**
     * The content permutator to update with all the content that has merge conflicts.
     */
    private ContentPermutator<TPermutatorContent> contentPermutator;

    /**
     * Creates a merger that adds content that has conflicts to the given {@link ContentPermutator}.
     * @param contentPermutator The content permutator to update with all the content that has merge conflicts.
     */
    public MemorySimulationMergerWithContentPermutator(ContentPermutator<TPermutatorContent> contentPermutator)
    {
        this.contentPermutator = contentPermutator;
    }

    /**
     * Merges the given changes between two commits into the given area.
     * This is a three way merge.
     *
     * @param mergedAreaToUpdate                      The content area where the resulting merged content should be placed. What ever is in this area after this call will be used for the merged commit.
     * @param commonAncestorCommit                    The first common ancestor of both commits being merged. This allows us to perform a three way diff to detect how to automatically merge changes.
     * @param sourceCommit                            The source commit that we are merging from.
     * @param destinationCommit                       The destination commit that we are merging to.
     * @param commonAncestorArea                      The content area for the common ancestor between the two commits.
     * @param sourceArea                              The content area at the source commit.
     * @param destinationArea                         The content area at the destination commit.
     * @param comparisonBetweenSourceAndDestination   The comparison between the source and destination content areas. This is useful to understand what the two areas look like and how they differ.
     * @param differenceBetweenAncestorAndSource      The difference between the content at the common ancestor and the source area. This is useful to understand what has changed between the two branches.
     * @param differenceBetweenAncestorAndDestination The difference between the content at the common ancestor and the destination area. This is useful to understand what has changed between the two branches.
     * @param contentFactory                          The factory to use for extracting content from the areas.
     * @param byteArrayIndex                          The byte array index to use when creating snap-shots for the content.
     */
    @Override public <TContent extends ContentAPI> void mergeIntoAreaWithThreeWayDiff(
        AreaAPI<TContent> mergedAreaToUpdate,
        CommitAPI commonAncestorCommit,
        CommitAPI sourceCommit,
        CommitAPI destinationCommit,
        AreaAPI<TContent> commonAncestorArea,
        AreaAPI<TContent> sourceArea,
        AreaAPI<TContent> destinationArea,
        ComparisonAPI comparisonBetweenSourceAndDestination,
        DifferenceAPI differenceBetweenAncestorAndSource,
        DifferenceAPI differenceBetweenAncestorAndDestination,
        ContentFactory<TContent> contentFactory,
        ByteArrayIndex byteArrayIndex)
    {
        // Apply the base algorithm:
        super.mergeIntoAreaWithThreeWayDiff(mergedAreaToUpdate, commonAncestorCommit, sourceCommit, destinationCommit, commonAncestorArea, sourceArea, destinationArea, comparisonBetweenSourceAndDestination, differenceBetweenAncestorAndSource, differenceBetweenAncestorAndDestination, contentFactory, byteArrayIndex);

        // Go through the comparison between the source and destination, looking for changes:
        for (ComparisonEntry comparisonEntry : comparisonBetweenSourceAndDestination)
        {
            // Check whether the content changed:
            if (comparisonEntry.state == ComparisonState.UNCHANGED)
            {
                // The content was unchanged, therefore no conflict.
                // Skip to the next entry:
                continue;
            }
            // Now we know that the content at this path was changed between the source and destination.

            // Get the path of the content that changed:
            RepoPath repoPath = comparisonEntry.path;

            // Check whether we have a merge conflict by seeing if both branches changed the same content:
            DifferenceState sourceDifference = differenceBetweenAncestorAndSource.getDifference(repoPath);
            DifferenceState destinationDifference = differenceBetweenAncestorAndDestination.getDifference(repoPath);
            if (sourceDifference != null && destinationDifference != null)
            {
                // There was a change in the source branch and the destination branch, meaning that we have a conflict.

                // Get the content from both branches:
                TPermutatorContent sourceContent = (TPermutatorContent) sourceArea.getContent(repoPath);
                TPermutatorContent destinationContent = (TPermutatorContent) destinationArea.getContent(repoPath);

                // Add this to the content permutator so that we can spawn alternate solutions for the next iterations:
                this.getContentPermutator().addClashingContent(repoPath, sourceContent);
                this.getContentPermutator().addClashingContent(repoPath, destinationContent);
            }
        }


    }

    /**
     * Gets the content permutator to update with all the content that has merge conflicts.
     * @return The content permutator to update with all the content that has merge conflicts.
     */
    public ContentPermutator<TPermutatorContent> getContentPermutator()
    {
        return contentPermutator;
    }

    /**
     * Sets the content permutator to update with all the content that has merge conflicts.
     * @param contentPermutator The content permutator to update with all the content that has merge conflicts.
     */
    public void setContentPermutator(ContentPermutator<TPermutatorContent> contentPermutator)
    {
        this.contentPermutator = contentPermutator;
    }
}
