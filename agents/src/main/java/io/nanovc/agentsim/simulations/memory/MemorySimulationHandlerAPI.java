package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.SimulationHandlerAPI;

import io.nanovc.meh.*;

/**
 * The API for an in-memory simulation handler.
 * <p>
 * This captures the public API between the {@link MEHConcepts#HANDLER handler} and code running simulations.
 * This follows {@link MEHPatterns#MODEL_ENGINE_HANDLER architecture 3} of the {@link MEHPatterns MEH Pattern}.
 *
 * @param <TConfig>     The specific type of configuration that the simulation takes.
 * @param <TIteration>  The specific type of iteration for the simulation.
 * @param <TSolution>   The specific type of solution for the simulation.
 * @param <TSimulation> The specific type of simulation that we are running.
 * @param <TEngine>     The specific type of engine that runs the simulation.
 */
public interface MemorySimulationHandlerAPI<
    TConfig extends MemorySimulationConfigAPI,
    TIteration extends MemorySimulationIterationAPI,
    TSolution extends MemorySimulationSolutionAPI<TIteration>,
    TSimulation extends MemorySimulationModelAPI<TIteration, TSolution>,
    TEngine extends MemorySimulationEngineAPI<TConfig, TIteration, TSolution, TSimulation>
    >
    extends SimulationHandlerAPI<TConfig, TIteration, TSolution, TSimulation, TEngine>
{
}
