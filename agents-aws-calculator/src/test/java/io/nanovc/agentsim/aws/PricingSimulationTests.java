package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.EnvironmentController;
import io.nanovc.agentsim.aws.organizations.MemberAccount;
import io.nanovc.agentsim.aws.organizations.Organization;
import io.nanovc.agentsim.aws.organizations.OrganizationController;
import io.nanovc.agentsim.pricecalc.TimeAgent;
import io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTestsBase;
import org.junit.jupiter.api.Test;

import java.time.Instant;

/**
 * Tests a simulation of pricing AWS consumption using an Agent Based Framework.
 */
class PricingSimulationTests extends AWSTestsBase
{
    @Test
    public void simpleCalculation_1AWSAgent() throws Exception
    {

        // Define the input model using code:
        MemorySimulationHandlerTestsBase.ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            // Create the environment for the pricing simulation:

            // Create the aws cloud controller for a new AWSCloud instance:
            AWSCloudController awsCloudController = new AWSCloudController(new AWSCloud());

            // Create the organization controller:
            OrganizationController organizationController = awsCloudController.createOrganizationControllerAndIndex();

            String expectedJSON;

            // Create an organization:
            Organization companyOrganization = organizationController.getOrCreateOrganization("Company");

            // Create a Dev account in the organization:
            MemberAccount devAccount = organizationController.getOrCreateAccount("Company", "Dev");

            // Add the cloud model to the simulation:
            controller.addModel(awsCloudController.awsCloud);

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

    @Test
    public void simpleCalculation_2AWSAgents() throws Exception
    {

        // Define the input model using code:
        MemorySimulationHandlerTestsBase.ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            // Create the environment for the pricing simulation:

            // Create the aws cloud controller for a new AWSCloud instance:
            AWSCloudController awsCloudController = new AWSCloudController(new AWSCloud());

            // Create the organization controller:
            OrganizationController organizationController = awsCloudController.createOrganizationControllerAndIndex();

            String expectedJSON;

            // Create an organization:
            Organization companyOrganization = organizationController.getOrCreateOrganization("Company");

            // Create a Dev account in the organization:
            MemberAccount devAccount = organizationController.getOrCreateAccount("Company", "Dev");

            // Add the cloud model to the simulation:
            controller.addModel(awsCloudController.awsCloud);

            // Create the Agent that controls simulation time:
            TimeAgent.Config timeAgentConfig = new TimeAgent.Config();
            controller.addAgentConfig(timeAgentConfig);

            AWSAgent.Config awsAgentConfig1 = new AWSAgent.Config();
            awsAgentConfig1.agentName = "aws 1";
            awsAgentConfig1.period.startInclusive = Instant.parse("2021-01-01T00:30:00Z");
            awsAgentConfig1.period.endExclusive = Instant.parse("2021-01-01T02:00:00Z");
            controller.addAgentConfig(awsAgentConfig1);

            AWSAgent.Config awsAgentConfig2 = new AWSAgent.Config();
            awsAgentConfig2.agentName = "aws 2";
            awsAgentConfig2.period.startInclusive = Instant.parse("2021-01-01T01:30:00Z");
            awsAgentConfig2.period.endExclusive = Instant.parse("2021-01-01T02:30:00Z");
            controller.addAgentConfig(awsAgentConfig2);

            //#endregion
        };

        // Make sure that the output model is as expected:
        // Make sure the model is as expected:
        //language=JSON
        String expectedOutputJSON =
            "[\n" +
            "  {\n" +
            "    \"solutionName\" : \"Solution 0\",\n" +
            "    \"environment\" : {\n" +
            "      \"models\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.pricecalc.Timeline\",\n" +
            "          \"name\" : \"timeline\",\n" +
            "          \"timeSlices\" : [\n" +
            "            {\n" +
            "              \"startInclusive\" : \"2021-01-01T00:30:00Z\",\n" +
            "              \"endExclusive\" : \"2021-01-01T01:30:00Z\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"startInclusive\" : \"2021-01-01T01:30:00Z\",\n" +
            "              \"endExclusive\" : \"2021-01-01T02:00:00Z\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
            "          \"name\" : \"aws\",\n" +
            "          \"organizations\" : [\n" +
            "            {\n" +
            "              \"type\" : \"io.nanovc.agentsim.aws.organizations.Organization\",\n" +
            "              \"name\" : \"Company\",\n" +
            "              \"root\" : {\n" +
            "                \"accounts\" : [\n" +
            "                  {\n" +
            "                    \"accountName\" : \"Dev\"\n" +
            "                  }\n" +
            "                ],\n" +
            "                \"managementAccount\" : { }\n" +
            "              }\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.pricecalc.Clock\",\n" +
            "          \"name\" : \"clock\",\n" +
            "          \"now\" : {\n" +
            "            \"startInclusive\" : \"2021-01-01T02:00:00Z\",\n" +
            "            \"endExclusive\" : \"2021-01-01T02:30:00Z\"\n" +
            "          }\n" +
            "        }\n" +
            "      ],\n" +
            "      \"agentConfigs\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.pricecalc.TimeAgent$Config\",\n" +
            "          \"enabled\" : true\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.aws.AWSAgent$Config\",\n" +
            "          \"period\" : {\n" +
            "            \"startInclusive\" : \"2021-01-01T00:30:00Z\",\n" +
            "            \"endExclusive\" : \"2021-01-01T02:00:00Z\"\n" +
            "          },\n" +
            "          \"agentName\" : \"aws 1\",\n" +
            "          \"enabled\" : true\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.aws.AWSAgent$Config\",\n" +
            "          \"period\" : {\n" +
            "            \"startInclusive\" : \"2021-01-01T01:30:00Z\",\n" +
            "            \"endExclusive\" : \"2021-01-01T02:30:00Z\"\n" +
            "          },\n" +
            "          \"agentName\" : \"aws 2\",\n" +
            "          \"enabled\" : true\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }\n" +
            "]";

        assert_AWS_Simulation_OutputJSONSolutions(inputModelCreator, expectedOutputJSON);
    }

    @Test
    public void simpleCalculation_1EC2_1Client() throws Exception
    {

        // Define the input model using code:
        MemorySimulationHandlerTestsBase.ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            // Create the environment for the pricing simulation:

            // Create the aws cloud controller for a new AWSCloud instance:
            AWSCloudController awsCloudController = new AWSCloudController(new AWSCloud());

            // Create the organization controller:
            OrganizationController organizationController = awsCloudController.createOrganizationControllerAndIndex();

            String expectedJSON;

            // Create an organization:
            Organization companyOrganization = organizationController.getOrCreateOrganization("Company");

            // Create a Dev account in the organization:
            MemberAccount devAccount = organizationController.getOrCreateAccount("Company", "Dev");

            // Add the cloud model to the simulation:
            controller.addModel(awsCloudController.awsCloud);

            // Create the Agent that controls simulation time:
            TimeAgent.Config timeAgentConfig = new TimeAgent.Config();
            controller.addAgentConfig(timeAgentConfig);

            EC2Agent.Config ec2AgentConfig1 = new EC2Agent.Config();
            ec2AgentConfig1.agentName = "EC2 1";
            ec2AgentConfig1.activePeriod.startInclusive = Instant.parse("2021-01-01T00:30:00Z");
            ec2AgentConfig1.activePeriod.endExclusive = Instant.parse("2021-01-01T02:00:00Z");
            controller.addAgentConfig(ec2AgentConfig1);

            //#endregion
        };

        // Make sure that the output model is as expected:
        // Make sure the model is as expected:
        //language=JSON
        String expectedOutputJSON =
            "[\n" +
            "  {\n" +
            "    \"solutionName\" : \"Solution 0\",\n" +
            "    \"environment\" : {\n" +
            "      \"models\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.pricecalc.Timeline\",\n" +
            "          \"name\" : \"timeline\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
            "          \"name\" : \"aws\",\n" +
            "          \"organizations\" : [\n" +
            "            {\n" +
            "              \"type\" : \"io.nanovc.agentsim.aws.organizations.Organization\",\n" +
            "              \"name\" : \"Company\",\n" +
            "              \"root\" : {\n" +
            "                \"accounts\" : [\n" +
            "                  {\n" +
            "                    \"accountName\" : \"Dev\"\n" +
            "                  }\n" +
            "                ],\n" +
            "                \"managementAccount\" : { }\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\" : \"io.nanovc.agentsim.aws.organizations.Organization\",\n" +
            "              \"name\" : \"Test\",\n" +
            "              \"root\" : {\n" +
            "                \"children\" : [\n" +
            "                  {\n" +
            "                    \"type\" : \"io.nanovc.agentsim.aws.organizations.OrganizationalUnit\",\n" +
            "                    \"organizationalUnitName\" : \"Yay\",\n" +
            "                    \"accounts\" : [\n" +
            "                      {\n" +
            "                        \"accountName\" : \"Nay\"\n" +
            "                      }\n" +
            "                    ]\n" +
            "                  }\n" +
            "                ],\n" +
            "                \"managementAccount\" : { }\n" +
            "              }\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.pricecalc.Clock\",\n" +
            "          \"name\" : \"clock\",\n" +
            "          \"now\" : {\n" +
            "            \"startInclusive\" : \"2021-01-01T00:30:00Z\",\n" +
            "            \"endExclusive\" : \"2021-01-01T02:00:00Z\"\n" +
            "          }\n" +
            "        }\n" +
            "      ],\n" +
            "      \"agentConfigs\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.pricecalc.TimeAgent$Config\",\n" +
            "          \"enabled\" : true\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.aws.EC2Agent$Config\",\n" +
            "          \"agentName\" : \"EC2 1\",\n" +
            "          \"activePeriod\" : {\n" +
            "            \"startInclusive\" : \"2021-01-01T00:30:00Z\",\n" +
            "            \"endExclusive\" : \"2021-01-01T02:00:00Z\"\n" +
            "          },\n" +
            "          \"enabled\" : true\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }\n" +
            "]";

        assert_AWS_Simulation_OutputJSONSolutions(inputModelCreator, expectedOutputJSON);
    }
}
