package io.nanovc.agentsim.simulations.memory;

import io.nanovc.AreaEntry;
import io.nanovc.RepoPath;
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.content.StringContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This tests the {@link ContentPermutator} to make sure that it works as expected.
 */
class ContentPermutatorTests
{
    /**
     * The content permutator that is being tested.
     * This is initialised to a new instance for each test.
     */
    public ContentPermutator<StringContent> permutator;

    @BeforeEach
    public void beforeTest()
    {
        this.permutator = new ContentPermutator<>();
    }

    @Test
    public void creationTest()
    {
        new ContentPermutator<>();
    }

    @Test
    public void addingContent()
    {
        // Add content directly:
        permutator.addClashingContent(RepoPath.atRoot().resolve("A"), new StringContent("A1"));

        // Create a content area so we can use the area entry API:
        StringHashMapArea stringArea = new StringHashMapArea();
        stringArea.putString("/A", "A2");
        stringArea.getTypedContentStream().forEach(permutator::addClashingContent);

        // Make sure that the content is as expected:
        String expectedContent =
            "/A:A1\n" +
            "/A:A2";
        assertEquals(expectedContent, permutator.asListString());
    }

    @Test
    public void iteratingContent_A3()
    {
        // Add content:
        permutator.addClashingContent(RepoPath.at("/A"), new StringContent("A1"));
        permutator.addClashingContent(RepoPath.at("/A"), new StringContent("A2"));
        permutator.addClashingContent(RepoPath.at("/A"), new StringContent("A3"));

        // Make sure that iterating the permutations is as expected:
        String expectedContent =
            "Permutation: 1\n" +
            "/A:A1\n" +
            "\n" +
            "Permutation: 2\n" +
            "/A:A2\n" +
            "\n" +
            "Permutation: 3\n" +
            "/A:A3\n";
        assertPermutations(this.permutator, expectedContent);
    }

    @Test
    public void iteratingContent_A2_B2()
    {
        // Add content:
        permutator.addClashingContent(RepoPath.at("/A"), new StringContent("A1"));
        permutator.addClashingContent(RepoPath.at("/A"), new StringContent("A2"));

        permutator.addClashingContent(RepoPath.at("/B"), new StringContent("B1"));
        permutator.addClashingContent(RepoPath.at("/B"), new StringContent("B2"));

        // Make sure that iterating the permutations is as expected:
        String expectedContent =
            "Permutation: 1\n" +
            "/A:A1\n" +
            "/B:B1\n" +
            "\n" +
            "Permutation: 2\n" +
            "/A:A2\n" +
            "/B:B1\n" +
            "\n" +
            "Permutation: 3\n" +
            "/A:A1\n" +
            "/B:B2\n" +
            "\n" +
            "Permutation: 4\n" +
            "/A:A2\n" +
            "/B:B2\n";
        assertPermutations(this.permutator, expectedContent);
    }

    @Test
    public void iteratingContent_A2_B3()
    {
        // Add content:
        permutator.addClashingContent(RepoPath.at("/A"), new StringContent("A1"));
        permutator.addClashingContent(RepoPath.at("/A"), new StringContent("A2"));

        permutator.addClashingContent(RepoPath.at("/B"), new StringContent("B1"));
        permutator.addClashingContent(RepoPath.at("/B"), new StringContent("B2"));
        permutator.addClashingContent(RepoPath.at("/B"), new StringContent("B3"));

        // Make sure that iterating the permutations is as expected:
        String expectedContent =
            "Permutation: 1\n" +
            "/A:A1\n" +
            "/B:B1\n" +
            "\n" +
            "Permutation: 2\n" +
            "/A:A2\n" +
            "/B:B1\n" +
            "\n" +
            "Permutation: 3\n" +
            "/A:A1\n" +
            "/B:B2\n" +
            "\n" +
            "Permutation: 4\n" +
            "/A:A2\n" +
            "/B:B2\n" +
            "\n" +
            "Permutation: 5\n" +
            "/A:A1\n" +
            "/B:B3\n" +
            "\n" +
            "Permutation: 6\n" +
            "/A:A2\n" +
            "/B:B3\n";
        assertPermutations(this.permutator, expectedContent);
    }

    @Test
    public void iteratingContent_A2_B2_C2()
    {
        // Add content:
        permutator.addClashingContent(RepoPath.at("/A"), new StringContent("A1"));
        permutator.addClashingContent(RepoPath.at("/A"), new StringContent("A2"));

        permutator.addClashingContent(RepoPath.at("/B"), new StringContent("B1"));
        permutator.addClashingContent(RepoPath.at("/B"), new StringContent("B2"));

        permutator.addClashingContent(RepoPath.at("/C"), new StringContent("C1"));
        permutator.addClashingContent(RepoPath.at("/C"), new StringContent("C2"));

        // Make sure that iterating the permutations is as expected:
        String expectedContent =
            "Permutation: 1\n" +
            "/A:A1\n" +
            "/B:B1\n" +
            "/C:C1\n" +
            "\n" +
            "Permutation: 2\n" +
            "/A:A2\n" +
            "/B:B1\n" +
            "/C:C1\n" +
            "\n" +
            "Permutation: 3\n" +
            "/A:A1\n" +
            "/B:B2\n" +
            "/C:C1\n" +
            "\n" +
            "Permutation: 4\n" +
            "/A:A2\n" +
            "/B:B2\n" +
            "/C:C1\n" +
            "\n" +
            "Permutation: 5\n" +
            "/A:A1\n" +
            "/B:B1\n" +
            "/C:C2\n" +
            "\n" +
            "Permutation: 6\n" +
            "/A:A2\n" +
            "/B:B1\n" +
            "/C:C2\n" +
            "\n" +
            "Permutation: 7\n" +
            "/A:A1\n" +
            "/B:B2\n" +
            "/C:C2\n" +
            "\n" +
            "Permutation: 8\n" +
            "/A:A2\n" +
            "/B:B2\n" +
            "/C:C2\n";
        assertPermutations(this.permutator, expectedContent);
    }

    @Test
    public void paths_1_values_1()
    {
        ContentPermutator<StringContent> permutator = new ContentPermutator<>();

        // Add content:
        permutator.addClashingContent(RepoPath.atRoot().resolve("A"), new StringContent("A"));

        // Make sure that iterating the permutations is as expected:
        String expectedContent =
            ""; // No clashes, therefore no permutations.
        assertPermutations(this.permutator, expectedContent);
    }

    /**
     * Asserts that the permutations are as expected.
     * @param permutator The permutator to test.
     * @param expectedContent The expected permutations.
     */
    public void assertPermutations(ContentPermutator<StringContent> permutator, String expectedContent)
    {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger permutationCounter = new AtomicInteger(1);
        Stream<List<AreaEntry<StringContent>>> permutationStream = permutator.streamOfPermutations();
        permutationStream.forEachOrdered(
            permutation ->
            {
                // Add a new line if necessary:
                if (stringBuilder.length() > 0)
                {
                    // We already have content.
                    // Add a new line separator:
                    stringBuilder.append('\n');
                }

                // Write what permutation we are on:
                stringBuilder.append("Permutation: ");
                stringBuilder.append(permutationCounter.getAndIncrement());

                // Write the content for this permutation:
                for (AreaEntry<StringContent> entry : permutation)
                {
                    // Get the path:
                    RepoPath path = entry.getPath();

                    // Get the content:
                    StringContent content = entry.getContent();

                    // Write out the path and content:
                    stringBuilder.append("\n");
                    stringBuilder.append(path);
                    stringBuilder.append(':');
                    stringBuilder.append(content.value);
                }

                stringBuilder.append("\n");
            });

        // Make sure that the content is as expected:
        assertEquals(expectedContent, stringBuilder.toString(), "The permutations were not as expected.");
    }
}
