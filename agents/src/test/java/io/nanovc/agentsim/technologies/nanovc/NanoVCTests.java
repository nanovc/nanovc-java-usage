package io.nanovc.agentsim.technologies.nanovc;

import io.nanovc.agentsim.AgentSimTestsBase;
import io.nanovc.areas.ByteArrayHashMapArea;
import io.nanovc.areas.StringHashMapArea;
import io.nanovc.memory.MemoryCommit;
import io.nanovc.memory.bytes.ByteArrayNanoRepo;
import io.nanovc.memory.strings.StringNanoRepo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests the NanoVC library for in-memory version control.
 */
public class NanoVCTests extends AgentSimTestsBase
{
    @Test
    public void creationTest()
    {
        new StringNanoRepo();
        new ByteArrayNanoRepo();
    }

    @Test
    public void simpleStringTest()
    {
        // Create the repo we are testing:
        StringNanoRepo repo = new StringNanoRepo();

        // Get a content area to put content:
        StringHashMapArea area = repo.createArea();
        area.putString("/", "Hello World");

        // Commit the area:
        MemoryCommit commit = repo.commitToBranch(area, "master", "First Commit", null);
        assertNotNull(commit);
    }

    @Test
    public void simpleByteTest()
    {
        // Create the repo we are testing:
        ByteArrayNanoRepo repo = new ByteArrayNanoRepo();

        // Get a content area to put content:
        ByteArrayHashMapArea area = repo.createArea();
        area.putBytes("/", "Hello World".getBytes());

        // Commit the area:
        MemoryCommit commit = repo.commitToBranch(area, "master", "First Commit", null);
        assertNotNull(commit);
    }

}
