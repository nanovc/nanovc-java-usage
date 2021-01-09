package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.EnvironmentModel;
import io.nanovc.agentsim.SimulationException;
import io.nanovc.agentsim.SimulationHandlerBase;

import io.nanovc.meh.*;

/**
 * A base class for an in-memory simulation handler.
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
public abstract class MemorySimulationHandlerBase<
    TConfig extends MemorySimulationConfigAPI,
    TIteration extends MemorySimulationIterationAPI,
    TSolution extends MemorySimulationSolutionAPI<TIteration>,
    TSimulation extends MemorySimulationModelAPI<TIteration, TSolution>,
    TEngine extends MemorySimulationEngineAPI<TConfig, TIteration, TSolution, TSimulation>
    >
    extends SimulationHandlerBase<TConfig, TIteration, TSolution, TSimulation, TEngine>
    implements MemorySimulationHandlerAPI<TConfig, TIteration, TSolution, TSimulation, TEngine>
{
    /**
     * Runs a new simulation.
     *
     * @param config             The configuration to run for the simulation.
     * @param environmentModel The environment model to use as input for the simulation.
     * @return The new simulation that was started.
     * @throws SimulationException If an error occurred during the simulation.
     */
    @Override public TSimulation runSimulation(TConfig config, EnvironmentModel environmentModel) throws SimulationException
    {
        return getOrCreateEngine().runSimulation(config, environmentModel);
    }

    /**
     * Clones the given environment model according to the algorithm used during simulation.
     * This is useful for duplicating inputs or outputs for unit testing or other higher level operations.
     *
     * @param config             The configuration to use when cloning.
     * @param environmentModel The environment model to clone.
     * @return A new clone of the environment model according to the algorithm used during simulation.
     * @throws SimulationException If an error occurred during the simulation.
     */
    @Override public EnvironmentModel cloneEnvironmentModel(TConfig config, EnvironmentModel environmentModel) throws SimulationException
    {
        return getOrCreateEngine().cloneEnvironmentModel(config, environmentModel);
    }
}
