package io.nanovc.agentsim.simulations.memory;

import io.nanovc.meh.*;

/**
 * An in-memory simulation handler.
 * <p>
 * This captures the public API between the {@link MEHConcepts#HANDLER handler} and code running simulations.
 * This follows {@link MEHPatterns#MODEL_ENGINE_HANDLER architecture 3} of the {@link MEHPatterns MEH Pattern}.
 */
public class MemorySimulationHandler
    extends MemorySimulationHandlerBase<MemorySimulationConfig, MemorySimulationIteration, MemorySimulationSolution, MemorySimulationModel, MemorySimulationEngine>
{
    /**
     * A factory method for a new engine to use for the simulation.
     *
     * @return A new engine to use for the simulation.
     */
    @Override protected MemorySimulationEngine createEngine()
    {
        return new MemorySimulationEngine();
    }
}
