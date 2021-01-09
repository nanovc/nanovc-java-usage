package io.nanovc.agentsim;

import io.nanovc.meh.*;

/**
 * A base class for a simulation handler.
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
public abstract class SimulationHandlerBase<
    TConfig extends SimulationConfigAPI,
    TIteration extends SimulationIterationAPI,
    TSolution extends SimulationSolutionAPI<TIteration>,
    TSimulation extends SimulationModelAPI<TIteration, TSolution>,
    TEngine extends SimulationEngineAPI<TConfig, TIteration, TSolution, TSimulation>
    >
    implements SimulationHandlerAPI<TConfig, TIteration, TSolution, TSimulation, TEngine>
{
    /**
     * The engine to use for this simulation.
     */
    private TEngine engine;

    /**
     * Gets the engine to use for this simulation.
     *
     * @return The engine to use for this simulation.
     */
    public TEngine getEngine()
    {
        return engine;
    }

    /**
     * Sets the engine to use for this simulation.
     *
     * @param engine The engine to use for this simulation.
     */
    public void setEngine(TEngine engine)
    {
        this.engine = engine;
    }

    /**
     * A factory method for a new engine to use for the simulation.
     *
     * @return A new engine to use for the simulation.
     */
    protected abstract TEngine createEngine();

    /**
     * Gets the engine to use for this simulation.
     *
     * @return The engine to use for this simulation.
     */
    protected TEngine getOrCreateEngine()
    {
        // Check whether we already have an engine:
        if (this.engine == null)
        {
            // We don't have an engine yet.
            // Create the engine:
            this.engine = createEngine();
        }
        return engine;
    }
}
