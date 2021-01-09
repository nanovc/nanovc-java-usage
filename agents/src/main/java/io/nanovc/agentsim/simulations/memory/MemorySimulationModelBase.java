package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.SimulationModelBase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The base class for an in-memory simulation model.
 * @param <TIteration> The specific type of iteration for the simulation.
 * @param <TSolution>  The specific type of solution for the simulation.
 */
public abstract class MemorySimulationModelBase<
    TIteration extends MemorySimulationIterationAPI,
    TSolution extends MemorySimulationSolutionAPI<TIteration>
    >
    extends SimulationModelBase<TIteration, TSolution>
    implements MemorySimulationModelAPI<TIteration, TSolution>
{
    /**
     * This keeps track of the next solution index to use when creating a solution.
     */
    private AtomicInteger nextSolutionIndex = new AtomicInteger();

    /**
     * Gets the object that keeps track of the next solution index to use when creating a solution.
     * @return This keeps track of the next solution index to use when creating a solution.
     */
    public AtomicInteger getNextSolutionIndex()
    {
        return nextSolutionIndex;
    }
}
