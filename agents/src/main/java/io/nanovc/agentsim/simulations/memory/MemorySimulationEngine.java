package io.nanovc.agentsim.simulations.memory;

import io.nanovc.meh.*;

/**
 * An in-memory simulation engine.
 * <p>
 * This captures the private API between the {@link MEHConcepts#HANDLER handler} and the {@link MEHConcepts#ENGINE engine} to run the actual simulations.
 * This follows {@link MEHPatterns#MODEL_ENGINE_HANDLER architecture 3}  of the {@link MEHPatterns MEH Pattern}.
 */
public class MemorySimulationEngine
    extends MemorySimulationEngineBase<MemorySimulationConfig, MemorySimulationIteration, MemorySimulationSolution, MemorySimulationModel>
{
    /**
     * A factory that creates a new strongly typed simulation iteration.
     *
     * @return A new simulation iteration.
     */
    @Override protected MemorySimulationIteration createSimulationIteration()
    {
        return new MemorySimulationIteration();
    }

    /**
     * A factory that creates a new strongly typed simulation solution.
     *
     * @return A new simulation solution.
     */
    @Override protected MemorySimulationSolution createSimulationSolution()
    {
        return new MemorySimulationSolution();
    }

    /**
     * A factory that creates a new strongly typed simulation model.
     *
     * @return A new simulation model.
     */
    @Override protected MemorySimulationModel createSimulationModel()
    {
        return new MemorySimulationModel();
    }
}
