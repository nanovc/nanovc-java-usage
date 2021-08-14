package io.nanovc.agentsim.aws.organizations;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nanovc.agentsim.aws.AWSCloud;
import io.nanovc.agentsim.aws.AWSTestsBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link OrganizationController}.
 */
class OrganizationControllerTests extends AWSTestsBase
{

    @Test
    public void creationTest()
    {
        new OrganizationController(new AWSCloud());
        OrganizationController.create(new AWSCloud());
        OrganizationController.createAndIndex(new AWSCloud());
    }

    @Test
    public void oneOrganization_oneProdAccount() throws JsonProcessingException
    {
        // Create the organization controller for a new AWSCloud instance:
        OrganizationController organizationController = new OrganizationController(new AWSCloud());

        String expectedJSON;

        // Make sure the AWS cloud instance is empty to begin with:
        expectedJSON =
        "{\n" +
        "  \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
        "  \"name\" : \"aws\"\n" +
        "}";
        assertEquals(expectedJSON, getJSON(organizationController.awsCloud));

        // Create an organization:
        Organization companyOrganization = organizationController.getOrCreateOrganization("Company");

        // Create a Prod account in the organization:
        MemberAccount prodAccount = organizationController.getOrCreateAccount("Company", "A", "B", "Prod");

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
            "        \"children\" : [\n" +
            "          {\n" +
            "            \"type\" : \"io.nanovc.agentsim.aws.organizations.OrganizationalUnit\",\n" +
            "            \"organizationalUnitName\" : \"A\",\n" +
            "            \"children\" : [\n" +
            "              {\n" +
            "                \"type\" : \"io.nanovc.agentsim.aws.organizations.OrganizationalUnit\",\n" +
            "                \"organizationalUnitName\" : \"B\",\n" +
            "                \"accounts\" : [\n" +
            "                  {\n" +
            "                    \"accountName\" : \"Prod\"\n" +
            "                  }\n" +
            "                ]\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        ],\n" +
            "        \"managementAccount\" : { }\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(expectedJSON, getJSON(organizationController.awsCloud));
    }

    @Test
    public void oneOrganization_oneProdAccount_atRoot() throws JsonProcessingException
    {
        // Create the organization controller for a new AWSCloud instance:
        OrganizationController organizationController = new OrganizationController(new AWSCloud());

        String expectedJSON;

        // Make sure the AWS cloud instance is empty to begin with:
        expectedJSON =
            "{\n" +
            "  \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
            "  \"name\" : \"aws\"\n" +
            "}";
        assertEquals(expectedJSON, getJSON(organizationController.awsCloud));

        // Create an organization:
        Organization companyOrganization = organizationController.getOrCreateOrganization("Company");

        // Create a Prod account in the organization:
        MemberAccount prodAccount = organizationController.getOrCreateAccount("Company", "Prod");

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
            "        \"accounts\" : [\n" +
            "          {\n" +
            "            \"accountName\" : \"Prod\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"managementAccount\" : { }\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(expectedJSON, getJSON(organizationController.awsCloud));
    }
}