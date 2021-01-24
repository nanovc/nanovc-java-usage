package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.EnvironmentController;
import io.nanovc.agentsim.aws.organizations.MemberAccount;
import io.nanovc.agentsim.aws.organizations.Organization;
import io.nanovc.agentsim.pricecalc.Clock;
import io.nanovc.agentsim.pricecalc.TimeAgent;
import io.nanovc.agentsim.pricecalc.Timeline;
import io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTestsBase;
import org.junit.jupiter.api.Test;

import java.time.Instant;

/**
 * Tests a simulation of pricing AWS consumption using an Agent Based Framework.
 */
class PricingSimulationTests extends AWSTestsBase
{
    @Test
    public void simpleCalculation() throws Exception
    {

        // Define the input model using code:
        MemorySimulationHandlerTestsBase.ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            // Create the environment for the pricing simulation:

            // Create the aws cloud controller for a new AWSCloud instance:
            AWSCloudController awsCloudController = new AWSCloudController(new AWSCloud());

            String expectedJSON;

            // Create an organization:
            Organization companyOrganization = awsCloudController.getOrCreateOrganization("Company");

            // Create a Dev account in the organization:
            MemberAccount devAccount = awsCloudController.getOrCreateAccount("Company", "Dev");

            // Add the cloud model to the simulation:
            controller.addModel(awsCloudController.awsCloud);

            // Create a clock for the simulation to capture the period of time that we start simulating at:
            Clock clock = new Clock();
            controller.addModel(clock);

            // Create a time line that captures what periods of time have been simulated:
            Timeline timeline = new Timeline();
            controller.addModel(timeline);

            // Create the Agent that controls simulation time:
            TimeAgent.Config timeAgentConfig = new TimeAgent.Config();
            controller.addAgentConfig(timeAgentConfig);

            AWSAgent.Config awsAgentConfig = new AWSAgent.Config();
            awsAgentConfig.agentName = "aws";
            awsAgentConfig.period.startInclusive = Instant.parse("2021-01-01T00:30:00Z");
            awsAgentConfig.period.endExclusive = Instant.parse("2021-01-01T02:00:00Z");

            controller.addAgentConfig(awsAgentConfig);

            //#endregion
        };

        // Make sure the model is as expected:
        //language=JSON
        String expectedInputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
            "      \"name\" : \"aws\",\n" +
            "      \"organizations\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.aws.organizations.Organization\",\n" +
            "          \"name\" : \"Company\",\n" +
            "          \"root\" : {\n" +
            "            \"accounts\" : [\n" +
            "              {\n" +
            "                \"accountName\" : \"Dev\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"managementAccount\" : { }\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.pricecalc.Clock\",\n" +
            "      \"name\" : \"clock\",\n" +
            "      \"now\" : { }\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.pricecalc.Timeline\",\n" +
            "      \"name\" : \"timeline\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.pricecalc.TimeAgent$Config\",\n" +
            "      \"enabled\" : true\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.AWSAgent$Config\",\n" +
            "      \"period\" : {\n" +
            "        \"startInclusive\" : \"2021-01-01T00:30:00Z\",\n" +
            "        \"endExclusive\" : \"2021-01-01T02:00:00Z\"\n" +
            "      },\n" +
            "      \"agentName\" : \"aws\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        // Make sure that the output model is as expected:
        // Make sure the model is as expected:
        //language=JSON
        String expectedOutputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
            "      \"name\" : \"aws\",\n" +
            "      \"organizations\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.aws.organizations.Organization\",\n" +
            "          \"name\" : \"Company\",\n" +
            "          \"root\" : {\n" +
            "            \"accounts\" : [\n" +
            "              {\n" +
            "                \"accountName\" : \"Dev\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"managementAccount\" : { }\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.pricecalc.Clock\",\n" +
            "      \"name\" : \"clock\",\n" +
            "      \"now\" : {\n" +
            "        \"startInclusive\" : \"2021-01-01T00:30:00Z\",\n" +
            "        \"endExclusive\" : \"2021-01-01T02:00:00Z\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.pricecalc.PeriodOfInterestForAgent\",\n" +
            "      \"name\" : \"period-of-interest-for-aws\",\n" +
            "      \"period\" : {\n" +
            "        \"startInclusive\" : \"2021-01-01T00:30:00Z\",\n" +
            "        \"endExclusive\" : \"2021-01-01T02:00:00Z\"\n" +
            "      },\n" +
            "      \"agentName\" : \"aws\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.pricecalc.Timeline\",\n" +
            "      \"name\" : \"timeline\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.pricecalc.TimeAgent$Config\",\n" +
            "      \"enabled\" : true\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.AWSAgent$Config\",\n" +
            "      \"period\" : {\n" +
            "        \"startInclusive\" : \"2021-01-01T00:30:00Z\",\n" +
            "        \"endExclusive\" : \"2021-01-01T02:00:00Z\"\n" +
            "      },\n" +
            "      \"agentName\" : \"aws\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        assert_AWS_InputJSON_Simulation_OutputJSON(inputModelCreator, expectedInputJSON, expectedOutputJSON);
    }
}
