package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.SimulationModelAPI;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The API for an in-memory simulation model.
 * @param <TIteration> The specific type of iteration for the simulation.
 * @param <TSolution>  The specific type of solution for the simulation.
 */
public interface MemorySimulationModelAPI<
    TIteration extends MemorySimulationIterationAPI,
    TSolution extends MemorySimulationSolutionAPI<TIteration>
    >
    extends SimulationModelAPI<TIteration, TSolution>
{
    /**
     * Gets the object that keeps track of the next solution index to use when creating a solution.
     * @return This keeps track of the next solution index to use when creating a solution.
     */
    AtomicInteger getNextSolutionIndex();
}
