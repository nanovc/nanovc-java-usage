package io.nanovc.agentsim.aws.ec2;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nanovc.agentsim.aws.AWSCloud;
import io.nanovc.agentsim.aws.AWSTestsBase;
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

}
