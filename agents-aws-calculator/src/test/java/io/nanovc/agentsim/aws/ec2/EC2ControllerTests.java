package io.nanovc.agentsim.aws.ec2;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nanovc.agentsim.aws.AWSCloud;
import io.nanovc.agentsim.aws.AWSTestsBase;
import io.nanovc.agentsim.aws.organizations.Organization;
import io.nanovc.agentsim.aws.organizations.OrganizationController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link EC2Controller}.
 */
class EC2ControllerTests extends AWSTestsBase
{

    @Test
    public void creationTest()
    {
        new EC2Controller(new AWSCloud());
        EC2Controller.create(new AWSCloud());
        EC2Controller.createAndIndex(new AWSCloud());
    }

    @Test
    public void oneEC2Instance() throws JsonProcessingException
    {
        // Create the organization controller for a new AWSCloud instance:
        EC2Controller ec2Controller = new EC2Controller(new AWSCloud());

        String expectedJSON;

        // Make sure the AWS cloud instance is empty to begin with:
        expectedJSON =
        "{\n" +
        "  \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
        "  \"name\" : \"aws\"\n" +
        "}";
        assertEquals(expectedJSON, getJSON(ec2Controller.awsCloud));

        // Create an EC2 instance:
        EC2Instance ec2Instance = ec2Controller.getOrCreateEC2Instance("EC2 Instance");

        // Make sure the AWS cloud is as expected:
        expectedJSON =
            "{\n" +
            "  \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
            "  \"name\" : \"aws\",\n" +
            "  \"ec2Instances\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.ec2.EC2Instance\",\n" +
            "      \"name\" : \"EC2 Instance\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(expectedJSON, getJSON(ec2Controller.awsCloud));
    }

    @Test
    public void launchOneEC2Instance() throws JsonProcessingException
    {
        // Create the organization controller for a new AWSCloud instance:
        EC2Controller ec2Controller = new EC2Controller(new AWSCloud());

        String expectedJSON;

        // Define the launch configuration for the EC2 instance that we want to create:
        EC2InstanceLaunchConfig launchConfig = new EC2InstanceLaunchConfig();
        launchConfig.regionName = "af-south-1";

        // Create the root account:
        OrganizationController organizationController = OrganizationController.createAndIndex(ec2Controller.awsCloud);
        Organization org = organizationController.getOrCreateOrganization("Org");

        // Launch the EC2 instance:
        EC2InstanceLaunchResult launchResult = ec2Controller.launchEC2Instances("EC2 Instance", launchConfig);

        // Make sure the result is as expected:
        expectedJSON =
            "{\n" +
            "  \"launchedEC2Instances\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.ec2.EC2Instance\",\n" +
            "      \"name\" : \"EC2 Instance\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(expectedJSON, getJSON(launchResult));

        // Make sure the AWS cloud is as expected:
        expectedJSON =
            "{\n" +
            "  \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
            "  \"name\" : \"aws\",\n" +
            "  \"organizations\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.organizations.Organization\",\n" +
            "      \"name\" : \"Org\",\n" +
            "      \"root\" : {\n" +
            "        \"type\" : \"io.nanovc.agentsim.aws.organizations.Root\",\n" +
            "        \"managementAccount\" : {\n" +
            "          \"type\" : \"io.nanovc.agentsim.aws.organizations.ManagementAccount\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"regions\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.regions.Region\",\n" +
            "      \"name\" : \"af-south-1\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"ec2Instances\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.ec2.EC2Instance\",\n" +
            "      \"name\" : \"EC2 Instance\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(expectedJSON, getJSON(ec2Controller.awsCloud));
    }

    @Test
    public void launchTwoEC2InstancesFromOneConfig() throws JsonProcessingException
    {
        // Create the organization controller for a new AWSCloud instance:
        EC2Controller ec2Controller = new EC2Controller(new AWSCloud());

        String expectedJSON;

        // Define the launch configuration for the EC2 instance that we want to create:
        EC2InstanceLaunchConfig launchConfig = new EC2InstanceLaunchConfig();
        launchConfig.regionName = "af-south-1";

        // Define that we want to launch two EC2 instances at once:
        launchConfig.numberOfInstances = 2;

        // Launch the EC2 instance:
        EC2InstanceLaunchResult launchResult = ec2Controller.launchEC2Instances("EC2 Instance", launchConfig);

        // Make sure the result is as expected:
        expectedJSON =
            "{\n" +
            "  \"launchedEC2Instances\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.ec2.EC2Instance\",\n" +
            "      \"name\" : \"EC2 Instance\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.ec2.EC2Instance\",\n" +
            "      \"name\" : \"EC2 Instance-1\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(expectedJSON, getJSON(launchResult));

        // Make sure the AWS cloud is as expected:
        expectedJSON =
            "{\n" +
            "  \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
            "  \"name\" : \"aws\",\n" +
            "  \"regions\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.regions.Region\",\n" +
            "      \"name\" : \"af-south-1\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"ec2Instances\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.ec2.EC2Instance\",\n" +
            "      \"name\" : \"EC2 Instance\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.ec2.EC2Instance\",\n" +
            "      \"name\" : \"EC2 Instance-1\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(expectedJSON, getJSON(ec2Controller.awsCloud));
    }
}
