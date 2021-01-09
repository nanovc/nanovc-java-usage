package io.nanovc.agentsim;

import io.nanovc.meh.*;

/**
 * The API for a simulation handler.
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
public interface SimulationHandlerAPI<
    TConfig extends SimulationConfigAPI,
    TIteration extends SimulationIterationAPI,
    TSolution extends SimulationSolutionAPI<TIteration>,
    TSimulation extends SimulationModelAPI<TIteration, TSolution>,
    TEngine extends SimulationEngineAPI<TConfig, TIteration, TSolution, TSimulation>
    >
{
    /**
     * Gets the engine to use for this simulation.
     *
     * @return The engine to use for this simulation.
     */
    TEngine getEngine();

    /**
     * Sets the engine to use for this simulation.
     *
     * @param engine The engine to use for this simulation.
     */
    void setEngine(TEngine engine);

    /**
     * Starts a new simulation.
     *
     * @param config           The configuration to run for the simulation.
     * @param environmentModel The environment model to use as input for the simulation.
     * @return The new simulation that was started.
     * @throws SimulationException If an error occurred during the simulation.
     */
    TSimulation runSimulation(TConfig config, EnvironmentModel environmentModel) throws SimulationException;

    /**
     * Clones the given environment model according to the algorithm used during simulation.
     * This is useful for duplicating inputs or outputs for unit testing or other higher level operations.
     *
     * @param config           The configuration to use when cloning.
     * @param environmentModel The environment model to clone.
     * @return A new clone of the environment model according to the algorithm used during simulation.
     * @throws SimulationException If an error occurred during the simulation.
     */
    EnvironmentModel cloneEnvironmentModel(TConfig config, EnvironmentModel environmentModel) throws SimulationException;
}
