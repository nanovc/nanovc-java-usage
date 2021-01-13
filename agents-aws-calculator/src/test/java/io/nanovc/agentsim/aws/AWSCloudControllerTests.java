package io.nanovc.agentsim.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nanovc.agentsim.aws.organizations.Organization;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link AWSCloudController}.
 */
class AWSCloudControllerTests extends AWSTestsBase
{

    @Test
    public void creationTest()
    {
        new AWSCloudController(new AWSCloud());
    }

    @Test
    public void oneOrganization_oneProdAccount() throws JsonProcessingException
    {
        // Create the aws cloud controller for a new AWSCloud instance:
        AWSCloudController awsCloudController = new AWSCloudController(new AWSCloud());

        String expectedJSON;

        // Make sure the AWS cloud instance is empty to begin with:
        expectedJSON =
        "{\n" +
        "  \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
        "  \"name\" : \"aws\"\n" +
        "}";
        assertEquals(expectedJSON, getJSON(awsCloudController.awsCloud));

        // Create an organization:
        Organization companyOrganization = awsCloudController.getOrCreateOrganization("Company");

        // Create a Prod account in the organization:
     //   MemberAccount prodAccount = awsCloudController.createAccount("Company", "A", "B", "Prod");

        // Make sure the AWS cloud instance is as expected:
        expectedJSON =
            "{\n" +
            "  \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
            "  \"name\" : \"aws\",\n" +
            "  \"organizations\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.organizations.Organization\",\n" +
            "      \"name\" : \"Company\",\n" +
            "      \"root\" : {\n" +
            "        \"managementAccount\" : { }\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(expectedJSON, getJSON(awsCloudController.awsCloud));

    }
}
