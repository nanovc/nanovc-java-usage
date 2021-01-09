package io.nanovc.agentsim.simulations.memory;

import io.nanovc.AreaEntry;
import io.nanovc.ContentAPI;
import io.nanovc.RepoPath;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

/**
 * This is used to gather clashing content at certain paths and then generate all permutations of that content.
 * It keeps a list of all the clashing content at each path.
 * It then walks every permutation of that content.
 * @param <TContent> The specific type of content that we will permute.
 */
public class ContentPermutator<TContent extends ContentAPI>
{
    /**
     * The multi-map of clashing content being stored.
     * The key is the absolute path of the content. These are kept sorted by the path.
     * The value is a list of all the content that is clashing at that path.
     */
    private SortedMap<String, List<TContent>> content = new TreeMap<>();

    /**
     * Adds the content at the given path and flags it as clashing so that it can be permuted later.
     * @param repoPath The path at which the clashing content is located.
     * @param content The content that is clashing.
     */
    public void addClashingContent(RepoPath repoPath, TContent content)
    {
        // Get the absolute path of the content because we use that as the key for our map:
        String path = repoPath.toAbsolutePath().path;

        // Get the list at the given path:
        List<TContent> contentList = this.content.computeIfAbsent(path, s -> new ArrayList<>());

        // Add the content to the list for this path:
        contentList.add(content);
    }

    /**
     * Adds the content at the given path and flags it as clashing so that it can be permuted later.
     * @param areaEntry The entry from the content area that has the clashing content.
     */
    public void addClashingContent(AreaEntry<TContent> areaEntry)
    {
        addClashingContent(areaEntry.getPath(), areaEntry.getContent());
    }

    /**
     * Gets the structure of the clashing content in this permutator as a list of paths.
     * This is useful for debugging.
     * New lines are made with '\n' instead of the system line separator, so that it's easier to unit test with.
     * @return The structure of this area as a list of paths.
     */
    public String asListString()
    {
        StringBuilder stringBuilder = new StringBuilder();

        // Go through each path:
        for (Map.Entry<String, List<TContent>> entry : this.content.entrySet())
        {
            // Get the path:
            String path = entry.getKey();

            // Get the content:
            List<TContent> contentList = entry.getValue();

            // Go through all the content:
            for (TContent content : contentList)
            {
                // Add a new line if necessary:
                if (stringBuilder.length() > 0)
                {
                    // We already have content.
                    // Add a line separator:
                    stringBuilder.append('\n');
                }

                // Write out the path and content:
                stringBuilder.append(path);
                stringBuilder.append(':');
                stringBuilder.append(new String(content.asByteArray(), StandardCharsets.UTF_8));
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Gets a stream of all the permutations of the content.
     * @return A stream of all the permutations of the content.
     */
    public Stream<List<AreaEntry<TContent>>> streamOfPermutations()
    {
        // This algorithm uses a 'co-ordinate' of indexes for each path of content.
        // The order of the coordinates matches the sorted paths (keys) of the content map.
        // We later transform the co-ordinate to the actual area entry on demand by mapping to the desired structure.
        // If the coordinate rolls around then we send back a coordinate with zero dimensions.

        // Create the initial coordinate as the seed for the algorithm:
        int[] startingCoordinate = new int[this.content.size()];

        // Create the co-ordinate stream:
        Stream<int[]> coordinateStream = Stream.iterate(

            // Define the starting coordinate:
            startingCoordinate,

            // Determine whether we still need to iterate:
            coordinate -> {
                // When the coordinate rolls over when we are done with all permutations, the coordinate will have zero dimensions.
                // Check whether we still have more iterations (because we use a coordinate of zero dimensions to flag that we have rolled around and we are done):
                return coordinate.length > 0;
            },

            // Determine the next coordinate:
            coordinate -> {
                // Start at the first index and increment the coordinate, rolling it over to the next index as needed.

                // Copy the current coordinate for the next coordinate:
                int[] nextCoordinate = Arrays.copyOf(coordinate, coordinate.length);

                // Go through each part of the coordinate:
                int coordinateIndex = 0;
                for (List<TContent> contentList : this.content.values())
                {
                    // Increment the coordinate:
                    nextCoordinate[coordinateIndex]++;

                    // Determine whether we need to roll over to the next index or whether we are ok for this iteration:
                    if (nextCoordinate[coordinateIndex] < contentList.size())
                    {
                        // The coordinate is still before the end of the content.
                        // Break out early because this is the new coordinate:
                        return nextCoordinate;
                    }
                    else
                    {
                        // We need to reset the coordinate at this index back to zero and go to the next index:
                        nextCoordinate[coordinateIndex] = 0;

                        // Go to the next index for the coordinate:
                        coordinateIndex++;

                        // Check whether we have rolled around to because we have iterated all permutations:
                        if (coordinateIndex == coordinate.length)
                        {
                            // We have rolled around and we are done.
                            // Use a coordinate with zero dimensions to flag that we are done:
                            return new int[0];
                        }
                    }
                }
                return nextCoordinate;
            }
        );

        // Map the coordinate stream to the content:
        return coordinateStream.map(
            coordinate ->
            {
                // Create the list for the output:
                List<AreaEntry<TContent>> areaEntryList = new ArrayList<>(coordinate.length);

                // Go through each part of the coordinate:
                int coordinateIndex = 0;
                for (Map.Entry<String, List<TContent>> entry : this.content.entrySet())
                {
                    // Get the repo path for this coordinate:
                    RepoPath repoPath = RepoPath.at(entry.getKey());

                    // Get the content list of this index:
                    List<TContent> contentList = entry.getValue();

                    // Get the index of the content from the coordinate:
                    int contentIndex = coordinate[coordinateIndex];

                    // Get the specific content that we want for this index:
                    TContent content = contentList.get(contentIndex);

                    // Create the area entry:
                    AreaEntry<TContent> areaEntry = new AreaEntry<>(repoPath, content);

                    // Add the entry to the output:
                    areaEntryList.add(areaEntry);

                    // Go to the next index for the coordinate:
                    coordinateIndex++;
                }

                return areaEntryList;
            });
    }

    /**
     * Returns whether there is any clashing content.
     * @return True if there is clashing content. False if there is no clashing content.
     */
    public boolean hasClashingContent()
    {
        return !this.content.isEmpty();
    }
}
