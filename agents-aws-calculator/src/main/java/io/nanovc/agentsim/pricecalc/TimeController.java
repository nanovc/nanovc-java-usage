package io.nanovc.agentsim.pricecalc;

import io.nanovc.agentsim.EnvironmentController;
import io.nanovc.agentsim.EnvironmentModel;
import io.nanovc.agentsim.ModelAPI;
import io.nanovc.agentsim.SimulationException;
import io.nanovc.meh.MEHConcepts;
import io.nanovc.meh.MEHPatterns;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A controller for time.
 * This contains common functionality that helps with performing simulations
 * using the {@link Clock clock} and {@link Timeline timeline}.
 * <p>
 * This follows the {@link MEHPatterns#MODEL_CONTROLLER architecture 4, model-controller} pattern.
 */
public class TimeController
{
    /**
     * The {@link Clock clock} to control.
     * <p>
     * This is a {@link MEHConcepts#MODEL}.
     */
    public final Clock clock;

    /**
     * The {@link Timeline timeline} to control.
     * <p>
     * This is a {@link MEHConcepts#MODEL}.
     */
    public final Timeline timeline;

    /**
     * The {@link EnvironmentController environment controller} that we use to interact with the {@link EnvironmentModel environment} from this {@link TimeController time controller}.
     */
    public final EnvironmentController environmentController;

    /**
     * Creates a new time controller for the given {@link Clock clock} and {@link Timeline timeline}.
     *
     * @param clock                 The {@link Clock clock} to control.
     * @param timeline              The {@link Timeline timeline} to control.
     * @param environmentController The {@link EnvironmentController environment controller} to use to get access to the {@link EnvironmentModel environment}.
     */
    public TimeController(Clock clock, Timeline timeline, EnvironmentController environmentController)
    {
        this.clock = clock;
        this.timeline = timeline;
        this.environmentController = environmentController;

        // Make sure we have the dependencies:
        Objects.requireNonNull(this.clock, "The time controller needs a clock");
        Objects.requireNonNull(this.timeline, "The time controller needs a timeline");
        Objects.requireNonNull(this.environmentController, "The time controller needs an environment controller so that it can access other models");
    }

    /**
     * Creates a new time controller and extracts the {@link Clock clock} and {@link Timeline timeline}
     * from the {@link EnvironmentModel environment} so that it can control them.
     *
     * @param environmentController The {@link EnvironmentController environment controller} from which to extract the {@link Clock clock} (named {@link Clock#NAME}) and {@link Timeline timeline} (named {@link Timeline#NAME}).
     */
    public TimeController(EnvironmentController environmentController)
    {
        Objects.requireNonNull(environmentController, "The time controller needs an environment controller so that it can access other models");
        this.environmentController = environmentController;

        // Get the clock from the environment:
        Clock clock = environmentController.getModelByName(Clock.NAME);
        if (clock == null)
        {
            // We don't have a clock in the environment.
            // Create a clock:
            clock = new Clock();
            clock.name = Clock.NAME;

            // Add the clock to the environment:
            environmentController.addModel(clock);
        }
        this.clock = clock;

        // Get the timeline from the environment:
        Timeline timeline = environmentController.getModelByName(Timeline.NAME);
        if (timeline == null)
        {
            // We don't have a timeline in the environment.
            // Create a timeline:
            timeline = new Timeline();
            timeline.name = Timeline.NAME;

            // Add the timeline to the environment:
            environmentController.addModel(timeline);
        }
        this.timeline = timeline;
    }

    /**
     * This registers the period of interest and handles the given logic if the current simulation time intersects with that period.
     *
     * @param periodOfInterest The period that we are interested in seeing if it overlaps with the current simulation period.
     * @param overlapHandler   The callback in the event that there is an overlap between the period of interest and the current simulation period.
     * @throws SimulationException If there is an error handling the period of interest.
     */
    public void handlePeriodOfInterest(
        TimeSlice periodOfInterest,
        SliceIntersectionHandlerWithException overlapHandler
    ) throws SimulationException
    {
        Objects.requireNonNull(overlapHandler, "An overlap handler must be provided.");

        // Get the current period we are simulating:
        TimeSlice now = this.clock.now;

        // Define the overlap in the time slices:
        TimeSlice past = null, overlap = null, future = null;

        // Make sure we have a current simulation period:
        if (now == null || now.startInclusive == null || now.endExclusive == null)
        {
            // We don't have a current simulation period.

            // Don't process any further:
            return;
        }
        // Now we know that we have a current simulation period.

        // Make sure that the period of interest is ordered correctly:
        if (periodOfInterest.endExclusive.isBefore(periodOfInterest.startInclusive))
        {
            throw new SimulationException("The period of interest can't end before it starts. Fix the order of the time period.");
        }

        // Make sure that the current simulation period is ordered correctly:
        if (now.endExclusive.isBefore(now.startInclusive))
        {
            throw new SimulationException("The current simulation period from the clock can't end before it starts. Fix the order of the time period.");
        }

        // Check if there is an intersection:
        if (periodOfInterest.startInclusive.isBefore(now.startInclusive))
        {
            // The start of the period of interest is before the start of the current time period.
            // Interest:    [<--...
            // Now:                 [<--...


            if (periodOfInterest.endExclusive.isBefore(now.startInclusive))
            {
                // The start and end of the period of interest is before the start of the current time period.
                // NOTE: No overlap.
                // Case 1:
                // Interest:    [<------>)
                // Now:                     [<------>)
                // Past:        [<------>)
                past = new TimeSlice();
                past.startInclusive = periodOfInterest.startInclusive;
                past.endExclusive = periodOfInterest.endExclusive;

            }
            else
            {
                // The end of the period of interest is not before the current time period.

                if (periodOfInterest.endExclusive.equals(now.startInclusive))
                {
                    // The start of the period is before the current period
                    // but ends exactly when the current simulation period begins.
                    // NOTE: No overlap. End is exclusive.
                    // Case 2:
                    // Interest:    [<------>)
                    // Now:                  [<------>)
                    // Past:        [<------>)
                    past = new TimeSlice();
                    past.startInclusive = periodOfInterest.startInclusive;
                    past.endExclusive = periodOfInterest.endExclusive;

                }
                else
                {
                    // The start of the period of interest is before the current simulation period
                    // but the end of the period of interest is somewhere after the start of the current simulation period.
                    // NOTE: Has overlap.
                    // Interest:    [<---------...
                    // Now:                  [<------>)

                    if (periodOfInterest.endExclusive.isBefore(now.endExclusive))
                    {
                        // The start of the period of interest is before the current simulation period
                        // and the end of the period of interest is before the end of the current simulation period.
                        // NOTE: Has overlap.
                        // Case 3:
                        // Interest:    [<---------->)
                        // Now:                  [<------>)
                        // Overlap:              [<->)
                        // Past:        [<------>)
                        overlap = new TimeSlice();
                        overlap.startInclusive = now.startInclusive;
                        overlap.endExclusive = periodOfInterest.endExclusive;

                        past = new TimeSlice();
                        past.startInclusive = periodOfInterest.startInclusive;
                        past.endExclusive = now.startInclusive;

                    }
                    else
                    {
                        // The end of the period of interest is not before the end of the simulation period.

                        if (periodOfInterest.endExclusive.equals(now.endExclusive))
                        {
                            // The start of the period of interest is before the current simulation period
                            // and the end of the period of interest is exactly the same as the end of the current simulation period.
                            // NOTE: Has overlap.
                            // Case 4:
                            // Interest:    [<--------------->)
                            // Now:                  [<------>)
                            // Overlap:              [<------>)
                            // Past:        [<------>)
                            overlap = new TimeSlice();
                            overlap.startInclusive = now.startInclusive;
                            overlap.endExclusive = now.endExclusive;

                            past = new TimeSlice();
                            past.startInclusive = periodOfInterest.startInclusive;
                            past.endExclusive = now.startInclusive;
                        }
                        else
                        {
                            // The start of the period of interest is before the current simulation period
                            // and the end of the period of interest is after the end of the current simulation period.
                            // NOTE: Has overlap.
                            // Case 5:
                            // Interest:    [<-------------------->)
                            // Now:                  [<------>)
                            // Overlap:              [<------>)
                            // Past:        [<------>)
                            // Future:                        [<-->)
                            overlap = new TimeSlice();
                            overlap.startInclusive = now.startInclusive;
                            overlap.endExclusive = now.endExclusive;

                            past = new TimeSlice();
                            past.startInclusive = periodOfInterest.startInclusive;
                            past.endExclusive = now.startInclusive;

                            future = new TimeSlice();
                            future.startInclusive = now.endExclusive;
                            future.endExclusive = periodOfInterest.endExclusive;

                        }
                    }

                }
            }


        }
        else
        {
            // The start of the period of interest is not before the current time period.

            if (periodOfInterest.startInclusive.equals(now.startInclusive))
            {
                // The start of the period of interest is exactly the same as the start of the current time period.
                // Interest:    [<--...
                // Now:         [<--...

                if (periodOfInterest.endExclusive.isBefore(now.endExclusive))
                {
                    // The start of the period of interest is exactly the same as the start of the current time period
                    // and the end of the period of interest is before the end of the current simulation period.
                    // NOTE: Has overlap.
                    // Case 6:
                    // Interest:    [<--->)
                    // Now:         [<------>)
                    // Overlap:     [<--->)
                    overlap = new TimeSlice();
                    overlap.startInclusive = periodOfInterest.startInclusive;
                    overlap.endExclusive = periodOfInterest.endExclusive;

                }
                else
                {
                    if (periodOfInterest.endExclusive.equals(now.endExclusive))
                    {
                        // Both periods are exactly the same.
                        // NOTE: Has overlap.
                        // Case 7:
                        // Interest:    [<------>)
                        // Now:         [<------>)
                        // Overlap:     [<------>)
                        overlap = new TimeSlice();
                        overlap.startInclusive = periodOfInterest.startInclusive;
                        overlap.endExclusive = periodOfInterest.endExclusive;
                    }
                    else
                    {
                        // The start of the period of interest is exactly the same as the start of the current time period
                        // and the end of the period of interest is after the end of the current simulation period.
                        // Case 8:
                        // Interest:    [<----------->)
                        // Now:         [<------>)
                        // Overlap:     [<------>)
                        // Future:               [<-->)
                        overlap = new TimeSlice();
                        overlap.startInclusive = periodOfInterest.startInclusive;
                        overlap.endExclusive = now.endExclusive;

                        future = new TimeSlice();
                        future.startInclusive = now.endExclusive;
                        future.endExclusive = periodOfInterest.endExclusive;
                    }
                }


            }
            else
            {
                // The start of the period of interest is after the start of the current simulation period.
                // Interest:                [<--...
                // Now:         [<--...


                if (periodOfInterest.endExclusive.equals(now.endExclusive))
                {
                    // The start of the period of interest is after the start of the current simulation period
                    // and the ends of both periods are exactly the same.
                    // NOTE: Has overlap.
                    // Case 9:
                    // Interest:                [<----->)
                    // Now:         [<----------------->)
                    // Overlap:                 [<----->)
                    overlap = new TimeSlice();
                    overlap.startInclusive = periodOfInterest.startInclusive;
                    overlap.endExclusive = periodOfInterest.endExclusive;
                }
                else
                {
                    // The start of the period of interest is after the start of the current simulation period
                    // and the end of the period of interest is after the end of the current simulation period.
                    // NOTE: Has overlap.
                    // Case 10:
                    // Interest:                [<------------>)
                    // Now:         [<----------------->)
                    // Overlap:                 [<----->)
                    // Future:                          [<---->)
                    overlap = new TimeSlice();
                    overlap.startInclusive = periodOfInterest.startInclusive;
                    overlap.endExclusive = now.endExclusive;

                    future = new TimeSlice();
                    future.startInclusive = now.endExclusive;
                    future.endExclusive = periodOfInterest.endExclusive;

                }
            }
        }
        // Now we have the overlap if there is one.

        // Squash any time periods where the start and end are the same values:
        if (past != null && past.startInclusive.equals(past.endExclusive)) past = null;
        if (overlap != null && overlap.startInclusive.equals(overlap.endExclusive)) overlap = null;
        if (future != null && future.startInclusive.equals(future.endExclusive)) future = null;

        // Check whether we have an overlap:
        if (past != null || overlap != null || future != null)
        {
            // We have an overlap of time for the period of interest and the current simulation period.
            try
            {
                // Handle the overlap:
                overlapHandler.handle(past, overlap, future);
            }
            catch (Exception e)
            {
                throw new SimulationException("Could not handle overlap in time.", e);
            }
        }

    }

    /**
     * This registers a period of interest (in time) for the given agent.
     *
     * @param period    The period of interest that we want to register for the given agent.
     * @param agentName The name of the agent that we want to register interest for.
     */
    public void registerPeriodOfInterest(TimeSlice period, String agentName)
    {
        // Create the model for the period of interest for the given agent:
        PeriodOfInterestForAgent periodOfInterestForAgent = new PeriodOfInterestForAgent();

        // Give the model a name:
        periodOfInterestForAgent.name = PeriodOfInterestForAgent.NAME_PREFIX + agentName;

        // Register the period of interest:
        periodOfInterestForAgent.agentName = agentName;
        periodOfInterestForAgent.period = period;

        // Add this period of interest to the environment:
        this.environmentController.addOrReplaceModel(periodOfInterestForAgent);
    }

    /**
     * This de-registers interest (in time) for the given agent.
     *
     * @param agentName The name of the agent that we want to de-register interest for.
     */
    public void deRegisterPeriodOfInterest(String agentName)
    {
        // Get the name of the model that we need to search for:
        String modelNameToRemove = PeriodOfInterestForAgent.NAME_PREFIX + agentName;

        // Check whether we have a model with that name:
        ModelAPI modelToRemove = this.environmentController.getModelByName(modelNameToRemove);
        if (modelToRemove != null)
        {
            // We found the model to remove.

            // Remove it:
            this.environmentController.removeModel(modelToRemove);
        }
    }

    /**
     * This updates the {@link Clock clock} for the time simulation based on the {@link PeriodOfInterestForAgent periods of interest} that were registered for agents.
     *
     * @param periodOfInterestForAgentMap The mapping of {@link NamedAgentConfigAPI named agents} to their {@link PeriodOfInterestForAgent periods of interest} to use for updating the {@link Clock clock}.
     */
    public void updateClockBasedOnPeriodsOfInterest(Map<NamedAgentConfigAPI, PeriodOfInterestForAgent> periodOfInterestForAgentMap) throws SimulationException
    {
        // Make sure we have some periods of interest:
        if (periodOfInterestForAgentMap.size() == 0) return;
        // Now we know that we have some periods of interest.

        // Get references to state:
        Clock clock = this.clock;
        Timeline timeline = this.timeline;

        // Build a flattened timeline of all the moments in time that agents are interested in (start or end):
        NavigableSet<Instant> instantsOfInterest = new TreeSet<>();

        // Go through all the agents that have registered a period of interest for the time simulation and flatten their periods of interest:
        for (Map.Entry<NamedAgentConfigAPI, PeriodOfInterestForAgent> entry : periodOfInterestForAgentMap.entrySet())
        {
            // Get the period of interest for this agent:
            PeriodOfInterestForAgent periodOfInterestForAgent = entry.getValue();

            // Add the start and end points to our timeline of interesting points:
            instantsOfInterest.add(periodOfInterestForAgent.period.startInclusive);
            instantsOfInterest.add(periodOfInterestForAgent.period.endExclusive);
        }
        // We have a flattened timeline of all the start and end points of interest. This helps us determine what to set the clock to next.


        // Check whether we have started our clock yet:
        if (clock.now == null)
        {
            // We have not started our clock yet.

            // Start the clock:
            clock.now = new TimeSlice();
        }
        else
        {
            // We have already started our clock.

            // Add the clock details to our flattened timeline:
            if (clock.now.startInclusive != null) instantsOfInterest.add(clock.now.startInclusive);
            if (clock.now.endExclusive != null) instantsOfInterest.add(clock.now.endExclusive);
        }
        // Now we know we have started out clock.

        // Set the clock to the earliest period on our flattened timeline:
        clock.now.startInclusive = instantsOfInterest.pollFirst();
        clock.now.endExclusive = instantsOfInterest.pollFirst();
        if (clock.now.endExclusive == null)
        {
            // Use the start as the end:
            clock.now.endExclusive = clock.now.startInclusive;
        }

        // Create a set of agents to prune for the periods of interest:
        Set<NamedAgentConfigAPI> agentsToPrune = new HashSet<>();

        // Start pruning old agent configurations until we find the first active one:
        AtomicBoolean hasActiveAgent = new AtomicBoolean(false);
        do
        {
            // Clear the list of agents to prune for this iteration:
            agentsToPrune.clear();

            // Go through each of the agents that have periods of interest to see if we can prune any:
            for (Map.Entry<NamedAgentConfigAPI, PeriodOfInterestForAgent> entry : periodOfInterestForAgentMap.entrySet())
            {
                // Get the period of interest for this agent:
                PeriodOfInterestForAgent periodOfInterestForAgent = entry.getValue();

                // Check how this agents period of interest intersects with the current clock:
                handlePeriodOfInterest(
                    periodOfInterestForAgent.period,
                    (past, overlap, future) ->
                    {
                        // Check whether there is an active overlap with the current clock:
                        if (overlap != null)
                        {
                            // There is an active overlap with the current clock.
                            // Flag that there is an active agent:
                            hasActiveAgent.set(true);
                        }
                        else
                        {
                            // There is no active overlap between this agent and the clock.

                            // Check whether the agent is in the past:
                            if (past != null)
                            {
                                // This agent is in the past.

                                // Flag this agent as one that can be pruned:
                                NamedAgentConfigAPI agentToPrune = entry.getKey();
                                agentsToPrune.add(agentToPrune);

                                // Prune this period for the agent:
                                this.environmentController.removeModel(periodOfInterestForAgent);
                            }
                        }
                    }
                );
            }
            // Now we know whether we have any agents to prune.

            // Prune any agents that we found:
            agentsToPrune.forEach(periodOfInterestForAgentMap::remove);

            // Check whether there is still something to do:
            if (periodOfInterestForAgentMap.size() == 0) break;
            // Now we know that there is still something to do.

            // Check whether there are any active agents yet, and if not then move the clock time along:
            if (!hasActiveAgent.get())
            {
                // We don't have any active agents for the current period in the clock.

                // Capture this period on the timeline:
                TimeSlice previousClockTimeSlice = clock.now;
                timeline.timeSlices.add(previousClockTimeSlice);

                // Move the clock along:
                TimeSlice nextClockTimeSlice = new TimeSlice();
                clock.now = nextClockTimeSlice;
                nextClockTimeSlice.startInclusive = previousClockTimeSlice.endExclusive;
                nextClockTimeSlice.endExclusive = instantsOfInterest.pollFirst();
                if (nextClockTimeSlice.endExclusive == null)
                {
                    // Use the start as the end:
                    nextClockTimeSlice.endExclusive = nextClockTimeSlice.startInclusive;
                }
            }
        }
        while (!hasActiveAgent.get());

    }

    /**
     * A lambda for handling the intersection of {@link TimeSlice slices}.
     * It may throw an exception.
     */
    @FunctionalInterface
    public interface SliceIntersectionHandlerWithException
    {
        /**
         * Handles the intersection of the {@link TimeSlice timeslice periods}.
         *
         * @param past    The period of time in the past. Null if there is no period in the past.
         * @param overlap The period of time that overlaps. Null if there is no overlap.
         * @param future  The period of time in the future. Null if there is no period in the future.
         * @throws Exception If an error occurs handling the intersection.
         */
        void handle(TimeSlice past, TimeSlice overlap, TimeSlice future) throws Exception;
    }
}
