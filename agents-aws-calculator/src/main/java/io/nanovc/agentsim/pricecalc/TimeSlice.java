package io.nanovc.agentsim.pricecalc;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * A slice of time for simulation.
 * This is used to signify the point in time as well as the atomic duration that we are simulating.
 */
public class TimeSlice
{
    /**
     * The inclusive start date and time for the period that we are simulating.
     * Inclusive means that this instant in time is considered within the simulation period.
     */
    public Instant startInclusive;

    /**
     * The exclusive end date and time for the period that we are simulating.
     * Exclusive means that this instant in time is not considered within the simulation period.
     */
    public Instant endExclusive;

    @Override public String toString()
    {
        if (this.startInclusive == null)
        {
            if (this.endExclusive == null)
            {
                return "TimeSlice";
            }
            else
            {
                return "TimeSlice[..." +
                       endExclusive +
                       ')';
            }
        }
        else
        {
            if (this.endExclusive == null)
            {
                return "TimeSlice[" +
                       startInclusive +
                       "...)";
            }
            else
            {
                if (Duration.between(startInclusive, endExclusive).toDays() < 1)
                {
                    ZoneId zoneId = ZoneId.of("Z");
                    return "TimeSlice[" +
                           DateTimeFormatter.ISO_LOCAL_TIME.format(LocalTime.ofInstant(startInclusive, zoneId)) +
                           " - to - " +
                           DateTimeFormatter.ISO_LOCAL_TIME.format(LocalTime.ofInstant(endExclusive, zoneId)) +
                           " for " +
                           DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.ofInstant(startInclusive, zoneId)) +
                           ')';
                }
                else
                {
                    return "TimeSlice[" +
                           startInclusive +
                           " - to - " +
                           endExclusive +
                           ')';
                }
            }
        }
    }
}
