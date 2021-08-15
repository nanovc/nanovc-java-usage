package io.nanovc.agentsim.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nanovc.agentsim.aws.organizations.MemberAccount;
import io.nanovc.agentsim.aws.organizations.Organization;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link AWSCloud} model.
 */
class AWSCloudTests extends AWSTestsBase
{
    @Test
    public void creationTest()
    {
        new AWSCloud();
    }


    @Test
    public void oneOrganization_oneProdAccount() throws JsonProcessingException
    {
        AWSCloud awsCloud = new AWSCloud();

        Organization organization = new Organization();
        awsCloud.organizations.add(organization);
        organization.setName("Company");

        MemberAccount prodAccount = new MemberAccount();
        organization.root.accounts.add(prodAccount);
        prodAccount.accountName = "Prod";

        String expectedJSON =
            "{\n" +
            "  \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
            "  \"name\" : \"aws\",\n" +
            "  \"organizations\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.organizations.Organization\",\n" +
            "      \"name\" : \"Company\",\n" +
            "      \"root\" : {\n" +
            "        \"type\" : \"io.nanovc.agentsim.aws.organizations.Root\",\n" +
            "        \"accounts\" : [\n" +
            "          {\n" +
            "            \"type\" : \"io.nanovc.agentsim.aws.organizations.MemberAccount\",\n" +
            "            \"accountName\" : \"Prod\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"managementAccount\" : {\n" +
            "          \"type\" : \"io.nanovc.agentsim.aws.organizations.ManagementAccount\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        assertEquals(expectedJSON, getJSON(awsCloud));
    }
}
