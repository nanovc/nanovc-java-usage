package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.EnvironmentController;
import io.nanovc.agentsim.aws.organizations.MemberAccount;
import io.nanovc.agentsim.aws.organizations.Organization;
import io.nanovc.agentsim.pricecalc.Clock;
import io.nanovc.agentsim.pricecalc.Timeline;
import io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTestsBase;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
            clock.now.startInclusive = Instant.parse("2021-01-01T00:00:00Z");
            clock.now.endExclusive = clock.now.startInclusive.plus(1, ChronoUnit.HOURS);
            controller.addModel(clock);

            // Create a time line that captures what periods of time have been simulated:
            Timeline timeline = new Timeline();
            controller.addModel(timeline);

            AWSAgent.Config awsAgentConfig = new AWSAgent.Config();
            awsAgentConfig.period.startInclusive = clock.now.startInclusive.plus(30, ChronoUnit.MINUTES);
            awsAgentConfig.period.endExclusive = clock.now.startInclusive.plus(2, ChronoUnit.HOURS);

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
            "      \"now\" : {\n" +
            "        \"startInclusive\" : \"2021-01-01T00:00:00Z\",\n" +
            "        \"endExclusive\" : \"2021-01-01T01:00:00Z\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.pricecalc.Timeline\",\n" +
            "      \"name\" : \"timeline\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.AWSAgent$Config\",\n" +
            "      \"period\" : {\n" +
            "        \"startInclusive\" : \"2021-01-01T00:30:00Z\",\n" +
            "        \"endExclusive\" : \"2021-01-01T02:00:00Z\"\n" +
            "      },\n" +
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
            "      \"name\" : \"clock\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.pricecalc.Timeline\",\n" +
            "      \"name\" : \"timeline\",\n" +
            "      \"timeSlices\" : [\n" +
            "        {\n" +
            "          \"startInclusive\" : \"2021-01-01T00:30:00Z\",\n" +
            "          \"endExclusive\" : \"2021-01-01T01:00:00Z\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"startInclusive\" : \"2021-01-01T01:00:00Z\",\n" +
            "          \"endExclusive\" : \"2021-01-01T02:00:00Z\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.AWSAgent$Config\",\n" +
            "      \"period\" : {\n" +
            "        \"startInclusive\" : \"2021-01-01T00:30:00Z\",\n" +
            "        \"endExclusive\" : \"2021-01-01T02:00:00Z\"\n" +
            "      },\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        assert_AWS_InputJSON_Simulation_OutputJSON(inputModelCreator, expectedInputJSON, expectedOutputJSON);
    }
}
