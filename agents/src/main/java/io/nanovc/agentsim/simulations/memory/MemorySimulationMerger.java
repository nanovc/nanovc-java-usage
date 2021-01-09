package io.nanovc.agentsim.simulations.memory;

import io.nanovc.*;

/**
 * The merge handler to use when merging each agents changes to an iteration.
 */
public class MemorySimulationMerger
    implements MergeEngineAPI, MergeHandlerAPI<MemorySimulationMerger>
{
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
        // First apply the destination content to the merged content area:
        for (AreaEntry<TContent> areaEntry : destinationArea)
        {
            // Get the path that we are at:
            RepoPath path = areaEntry.getPath();

            // Get the bytes for this area entry:
            byte[] bytes = areaEntry.getContent().asByteArray();

            // Make sure to pass the bytes through our byte index:
            bytes = byteArrayIndex.addOrLookup(bytes);

            // Create the merged content:
            TContent mergedContent = contentFactory.createContent(bytes);

            // Copy the content across:
            mergedAreaToUpdate.putContent(path, mergedContent);
        }
        // Now we have all the destination changes.

        // Apply all the source changes on-top of the result:
        for (DifferenceEntry differenceEntry : differenceBetweenAncestorAndSource)
        {
            // Get the path that we are talking about:
            RepoPath path = differenceEntry.path;

            // Check what change was made:
            switch (differenceEntry.state)
            {
                // If the content was added or changed then the source value wins:
                case ADDED, CHANGED ->
                    {
                        // Get the source content:
                        byte[] bytes = sourceArea.getContent(path).asByteArray();

                        // Make sure to pass the bytes through our byte index:
                        bytes = byteArrayIndex.addOrLookup(bytes);

                        // Create the merged content:
                        TContent mergedContent = contentFactory.createContent(bytes);

                        // Copy the content across:
                        mergedAreaToUpdate.putContent(path, mergedContent);
                    }

                // If the content was deleted in source then we delete it from the output:
                case DELETED ->
                    {
                        // Remove the content from the merged area:
                        mergedAreaToUpdate.removeContent(path);
                    }
            }
        }

    }

    /**
     * Merges the given changes between two commits into the given area.
     * This is a two way merge, which means that neither of the commits shared a common ancestor.
     *
     * @param mergedAreaToUpdate                    The content area where the resulting merged content should be placed. What ever is in this area after this call will be used for the merged commit.
     * @param sourceCommit                          The source commit that we are merging from.
     * @param destinationCommit                     The destination commit that we are merging to.
     * @param sourceArea                            The content area at the source commit.
     * @param destinationArea                       The content area at the destination commit.
     * @param comparisonBetweenSourceAndDestination The comparison between the source and destination content areas. This is useful to understand what the two areas look like and how they differ.
     * @param contentFactory                        The factory to use for extracting content from the areas.
     * @param byteArrayIndex                        The byte array index to use when creating snap-shots for the content.
     */
    @Override public <TContent extends ContentAPI> void mergeIntoAreaWithTwoWayDiff(AreaAPI<TContent> mergedAreaToUpdate, CommitAPI sourceCommit, CommitAPI destinationCommit, AreaAPI<TContent> sourceArea, AreaAPI<TContent> destinationArea, ComparisonAPI comparisonBetweenSourceAndDestination, ContentFactory<TContent> contentFactory, ByteArrayIndex byteArrayIndex)
    {

    }

    /**
     * Gets the engine being used by this handler.
     *
     * @return The engine that is being used by this handler
     */
    @Override public MemorySimulationMerger getEngine()
    {
        return this;
    }

    /**
     * Sets the engine to use for this handler.
     *
     * @param merger The engine to use for this handler.
     */
    @Override public void setEngine(MemorySimulationMerger merger)
    {
        throw new RuntimeException("Cannot swap out engines in OOP style.");
    }
}
