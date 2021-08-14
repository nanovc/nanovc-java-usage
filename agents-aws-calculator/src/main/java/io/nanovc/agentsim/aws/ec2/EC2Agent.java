package io.nanovc.agentsim.aws.ec2;

import io.nanovc.agentsim.EnvironmentController;
import io.nanovc.agentsim.ReadOnlyEnvironmentController;
import io.nanovc.agentsim.SimulationController;
import io.nanovc.agentsim.SimulationIterationAPI;
import io.nanovc.agentsim.aws.AWSCloud;
import io.nanovc.agentsim.aws.AWSCloudController;
import io.nanovc.agentsim.aws.organizations.MemberAccount;
import io.nanovc.agentsim.aws.organizations.OrganizationController;
import io.nanovc.agentsim.pricecalc.Clock;
import io.nanovc.agentsim.pricecalc.TimeAwareAgentBase;
import io.nanovc.agentsim.pricecalc.TimeController;
import io.nanovc.agentsim.pricecalc.TimeSlice;

/**
 * An agent that simulates an {@link EC2Instance} in {@link AWSCloud AWS}.
 */
public class EC2Agent extends TimeAwareAgentBase<EC2Agent.Config>
{
    /**
     * The config for a {@link TimeAwareAgentBase time aware agent}.
     * The agent needs to be named because that is needed for the time based simulation.
     */
    public static class Config extends TimeAwareAgentBase.Config
    {
        /**
         * The active period when this {@link EC2Instance} is switched on.
         */
        public TimeSlice activePeriod = new TimeSlice();
    }

    /**
     * Defines the {@link TimeSlice period of interest} that the agent is interested in for the simulation.
     *
     * @param config         The configuration for this agent.
     * @param timeController The time controller that makes it easier to understand the simulation time.
     * @return The {@link TimeSlice time slice} that the agent is interested in for the simulation.
     */
    @Override protected TimeSlice definePeriodOfInterest(Config config, TimeController timeController)
    {
        return config.activePeriod;
    }

    /**
     * Handles the current period in the simulation for this agent.
     * This is the period that overlaps with the agents period of interest and the current {@link Clock simulation clock}.
     * @param config         The configuration for this agent.
     * @param currentPeriod  The currently simulated period, based on the simulation time on the clock.
     * @param timeController The time controller that makes it easier to understand the simulation time.
     * @param input          The input environment model to this iteration. This is provided for reference. It must not be modified.
     * @param output         The output environment model for this iteration. The agent is allowed to modify this output model for the next iteration.
     * @param iteration      The current simulation iteration that is running.
     * @param simulation     The simulation that is running.
     */
    @Override protected void handleCurrentPeriod(Config config, TimeSlice currentPeriod, TimeController timeController, ReadOnlyEnvironmentController input, EnvironmentController output, SimulationIterationAPI iteration, SimulationController simulation)
    {
        // Get the AWS Cloud model we are interacting with:
        AWSCloud awsCloud = output.getModelByName(AWSCloud.AWS);

        // Get a controller for AWS:
        AWSCloudController awsCloudController = new AWSCloudController(awsCloud);

        // Get the controller for organizations:
        OrganizationController organizationController = awsCloudController.createOrganizationControllerAndIndex();

        // Get the test account:
        MemberAccount account = organizationController.getOrCreateAccount("Test", "Yay", "Nay");
    }


}
