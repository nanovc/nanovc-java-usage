package io.nanovc.agentsim;

import io.nanovc.meh.*;

/**
 * A controller that is used by each agent to control the progression of the simulation.
 * It also keeps the current state of the simulation, solution and iteration that the agent is on.
 * This gives each agent an intuitive API to work with the simulation being run.
 * <p>
 * A {@link MEHConcepts#CONTROLLER controller} is the {@link MEHConcepts#HANDLER handler} and {@link MEHConcepts#ENGINE engine} combined.
 * This follows {@link MEHPatterns#MODEL_CONTROLLER architecture 4 } of the {@link MEHPatterns MEH Pattern}.
 */
public class SimulationController
{
    /**
     * The simulation that is currently being run.
     */
    public final SimulationModelAPI simulation;


    /**
     * The solution this is currently being run.
     */
    public final SimulationSolutionAPI solution;

    /**
     * The iteration that is currently being run.
     */
    public final SimulationIterationAPI iteration;

    /**
     * Creates a new simulation controller which allows the agent to introspect on the simulation, solution and iteration that is currently being run.
     *
     * @param simulation The simulation that is currently being run.
     * @param solution   The solution this is currently being run.
     * @param iteration  The iteration that is currently being run.
     */
    public SimulationController(SimulationModelAPI simulation, SimulationSolutionAPI solution, SimulationIterationAPI iteration)
    {
        this.simulation = simulation;
        this.solution = solution;
        this.iteration = iteration;
    }
}
