package io.nanovc.agentsim;

import io.nanovc.meh.*;

/**
 * The API for a simulation engine.
 * <p>
 * This captures the private API between the {@link ModelEngineHandlerConcepts#HANDLER handler} and the {@link ModelEngineHandlerConcepts#ENGINE engine} to run the actual simulations.
 * This follows {@link MEHPatterns#MODEL_ENGINE_HANDLER architecture 3} of the {@link MEHPatterns MEH Pattern}.
 *
 * @param <TConfig>     The specific type of configuration that the simulation takes.
 * @param <TIteration>  The specific type of iteration for the simulation.
 * @param <TSolution>   The specific type of solution for the simulation.
 * @param <TSimulation> The specific type of simulation that we are running.
 */
public interface SimulationEngineAPI<
    TConfig extends SimulationConfigAPI,
    TIteration extends SimulationIterationAPI,
    TSolution extends SimulationSolutionAPI<TIteration>,
    TSimulation extends SimulationModelAPI<TIteration, TSolution>
    >
{
    // NOTE: There is no implied contract between generic handlers and engines. Sub classes must define their private API depending on how they are implemented.
}
