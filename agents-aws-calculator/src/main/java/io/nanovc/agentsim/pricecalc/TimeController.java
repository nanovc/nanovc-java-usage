package io.nanovc.agentsim.pricecalc;

import io.nanovc.agentsim.EnvironmentController;
import io.nanovc.agentsim.EnvironmentModel;
import io.nanovc.agentsim.SimulationException;
import io.nanovc.meh.MEHConcepts;
import io.nanovc.meh.MEHPatterns;

import java.util.Objects;

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
     * Creates a new time controller for the given {@link Clock clock} and {@link Timeline timeline}.
     *
     * @param clock    The {@link Clock clock} to control.
     * @param timeline The {@link Timeline timeline} to control.
     */
    public TimeController(Clock clock, Timeline timeline)
    {
        this.clock = clock;
        this.timeline = timeline;
    }

    /**
     * Creates a new time controller and extracts the {@link Clock clock} and {@link Timeline timeline}
     * from the {@link EnvironmentModel environment} so that it can control them.
     *
     * @param environmentController The {@link EnvironmentController environment controller} from which to extract the {@link Clock clock} (named {@link Clock#NAME}) and {@link Timeline timeline} (named {@link Timeline#NAME}).
     */
    public TimeController(EnvironmentController environmentController)
    {
        this.clock = environmentController.getModelByName(Clock.NAME);
        this.timeline = environmentController.getModelByName(Timeline.NAME);

        // Make sure we have the models:
        Objects.requireNonNull(this.clock, "The time controller needs a clock called " + Clock.NAME);
        Objects.requireNonNull(this.clock, "The time controller needs a timeline called " + Timeline.NAME);
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
        // Get the current period we are simulating:
        TimeSlice now = this.clock.now;

        // Define the overlap in the time slices:
        TimeSlice past = null, overlap = null, future = null;

        // Make sure we have a current simulation period:
        if (now == null)
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

        // Check whether we have an overlap:
        if (overlap != null)
        {
            // We have an overlap of time for the period of interest and the current simulation period.

            if (overlapHandler != null)
            {
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

    }

    /**
     * A lambda for a method that takes two arguments and returns no outputs but may throw an exception.
     *
     * @param <T> The type of the first argument.
     * @param <U> The type of the second argument.
     */
    @FunctionalInterface
    public interface BiConsumerWithException<T, U>
    {
        /**
         * Performs this operation on the given arguments.
         *
         * @param t the first input argument
         * @param u the second input argument
         */
        void accept(T t, U u) throws Exception;
    }

    /**
     * A lambda for a method that takes two arguments and returns no outputs but may throw an exception.
     *
     * @param <T> The type of the first argument.
     */
    @FunctionalInterface
    public interface ConsumerWithException<T>
    {
        /**
         * Performs this operation on the given arguments.
         *
         * @param t the first input argument
         */
        void accept(T t) throws Exception;
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
