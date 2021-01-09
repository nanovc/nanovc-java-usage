package io.nanovc.agentsim;

import org.junit.jupiter.api.Test;

/**
 * Tests the {@link SimulationController}.
 * This gives each agent an intuitive API to work with the simulation being run.
 */
class SimulationControllerTests
{
    @Test
    public void creationTest()
    {
        new SimulationController(null, null, null);
    }


}
