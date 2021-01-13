package io.nanovc.agentsim.aws.organizations;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nanovc.agentsim.aws.AWSTestsBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests the {@link Organization} model.
 */
class OrganizationTests extends AWSTestsBase
{
    @Test
    public void creationTest()
    {
        new Organization();
    }

    @Test
    public void organizationHasRootAndManagementAccount() throws JsonProcessingException
    {
        // Create an empty organization:
        Organization organization = new Organization();

        // Make sure that the organization has a root:
        assertNotNull(organization.root);

        // Make sure that the root has a management account:
        assertNotNull(organization.root.managementAccount);

        // Make sure the organization looks as expected:
        String expectedJSON =
            "{\n" +
            "  \"type\" : \"io.nanovc.agentsim.aws.organizations.Organization\",\n" +
            "  \"root\" : {\n" +
            "    \"managementAccount\" : { }\n" +
            "  }\n" +
            "}";
        assertEquals(expectedJSON, getJSON(organization));
    }

    @Test
    public void oneAccountAtTheRoot() throws JsonProcessingException
    {
        // Create the organization:
        Organization organization = new Organization();
        organization.name = "Company";

        // Create one account at the root:
        MemberAccount resourceAccount = new MemberAccount();
        resourceAccount.accountName = "Resources";
        organization.root.accounts.add(resourceAccount);

        // Make sure the organization looks as expected:
        String expectedJSON =
            "{\n" +
            "  \"type\" : \"io.nanovc.agentsim.aws.organizations.Organization\",\n" +
            "  \"name\" : \"Company\",\n" +
            "  \"root\" : {\n" +
            "    \"accounts\" : [\n" +
            "      {\n" +
            "        \"accountName\" : \"Resources\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"managementAccount\" : { }\n" +
            "  }\n" +
            "}";
        assertEquals(expectedJSON, getJSON(organization));
    }

    @Test
    public void prodAndQAAccounts_TwoClientOUs() throws JsonProcessingException
    {
        // Create the organization:
        Organization organization = new Organization();
        organization.name = "Company";

        // Create the OU for the first client:
        OrganizationalUnit clientOU1 = new OrganizationalUnit();
        clientOU1.organizationalUnitName = "client 1";
        organization.root.children.add(clientOU1);

        // Create the QA account for the first client:
        MemberAccount qaAccountClient1 = new MemberAccount();
        qaAccountClient1.accountName = "Client 1 QA";
        clientOU1.accounts.add(qaAccountClient1);

        // Create the Prod account for the first client:
        MemberAccount prodAccountClient1 = new MemberAccount();
        prodAccountClient1.accountName = "Client 1 Prod";
        clientOU1.accounts.add(prodAccountClient1);

        // Create the OU for the second client:
        OrganizationalUnit clientOU2 = new OrganizationalUnit();
        clientOU2.organizationalUnitName = "client 2";
        organization.root.children.add(clientOU2);

        // Create the QA account for the second client:
        MemberAccount qaAccountClient2 = new MemberAccount();
        qaAccountClient2.accountName = "Client 2 QA";
        clientOU2.accounts.add(qaAccountClient2);

        // Create the Prod account for the second client:
        MemberAccount prodAccountClient2 = new MemberAccount();
        prodAccountClient2.accountName = "Client 2 Prod";
        clientOU2.accounts.add(prodAccountClient2);

        // Make sure the organization looks as expected:
        String expectedJSON =
            "{\n" +
            "  \"type\" : \"io.nanovc.agentsim.aws.organizations.Organization\",\n" +
            "  \"name\" : \"Company\",\n" +
            "  \"root\" : {\n" +
            "    \"children\" : [\n" +
            "      {\n" +
            "        \"accounts\" : [\n" +
            "          {\n" +
            "            \"accountName\" : \"Client 1 QA\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"accountName\" : \"Client 1 Prod\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"organizationalUnitName\" : \"client 1\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"accounts\" : [\n" +
            "          {\n" +
            "            \"accountName\" : \"Client 2 QA\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"accountName\" : \"Client 2 Prod\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"organizationalUnitName\" : \"client 2\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"managementAccount\" : { }\n" +
            "  }\n" +
            "}";
        assertEquals(expectedJSON, getJSON(organization));
    }
}
