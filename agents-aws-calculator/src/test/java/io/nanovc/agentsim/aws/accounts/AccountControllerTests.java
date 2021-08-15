package io.nanovc.agentsim.aws.accounts;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nanovc.agentsim.aws.AWSCloud;
import io.nanovc.agentsim.aws.AWSTestsBase;
import io.nanovc.agentsim.aws.organizations.Organization;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link AccountController}.
 */
class AccountControllerTests extends AWSTestsBase
{
    @Test
    public void creationTest()
    {
        new AccountController(new AWSCloud());
        AccountController.create(new AWSCloud());
        AccountController.createAndIndex(new AWSCloud());
    }

    /**
     * This is the setup that you may have if you create a {@link NormalAccount normal AWS account} as an individual,
     * without an {@link Organization}.
     */
    @Test
    public void oneNormalAccount() throws JsonProcessingException
    {
        // Create the account controller for a new AWSCloud instance:
        AccountController accountController = new AccountController(new AWSCloud());

        String expectedJSON;

        // Create a normal account without an organization:
        NormalAccount normalAccount = accountController.getOrCreateNormalAccount("Normal Account");

        // Make sure the AWS cloud instance is as expected:
        expectedJSON =
            "{\n" +
            "  \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
            "  \"name\" : \"aws\",\n" +
            "  \"normalAccounts\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.accounts.NormalAccount\",\n" +
            "      \"name\" : \"Normal Account\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(expectedJSON, getJSON(accountController.awsCloud));
    }

    @Test
    public void generatingAccountIDs() throws JsonProcessingException
    {
        // Start with a new AWS cloud model:
        AWSCloud awsCloud = new AWSCloud();

        // Create the account controller that will generate account ID's:
        AccountController accountController = AccountController.createAndIndex(awsCloud);

        // Generate IDs:
        String lastAccountID = null;
        for (int i = 0; i < 10; i++)
        {
            lastAccountID = accountController.generateNextAccountID();
        }
        assertEquals("123456789021", lastAccountID);

        String expectedJSON;

        // Make sure the AWS cloud instance is as expected:
        expectedJSON =
            "{\n" +
            "  \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
            "  \"name\" : \"aws\",\n" +
            "  \"nextAccountSequenceNumber\" : 123456789022\n" +
            "}";
        assertEquals(expectedJSON, getJSON(awsCloud));
    }

    @Test
    public void signUpForNewAccount() throws JsonProcessingException
    {
        // Start with a new AWS cloud model:
        AWSCloud awsCloud = new AWSCloud();

        // Create the account controller for a new AWSCloud instance:
        AccountController accountController = AccountController.createAndIndex(awsCloud);

        // Define the details for signing up for a new account:
        NewAccountSignUpConfig newAccountSignUpConfig = new NewAccountSignUpConfig();
        newAccountSignUpConfig.awsAccountName = "Cool Account";
        newAccountSignUpConfig.emailAddress = "dude@cool.com";
        newAccountSignUpConfig.password = "abc123";

        // Sign up for a new account:
        NewAccountSignUpResult signUpResult = accountController.signUpForNewAccount(newAccountSignUpConfig);

        String expectedJSON;

        // Make sure the result is as expected:
        expectedJSON =
            "{\n" +
            "  \"createdAccount\" : {\n" +
            "    \"type\" : \"io.nanovc.agentsim.aws.accounts.NormalAccount\",\n" +
            "    \"name\" : \"Cool Account\",\n" +
            "    \"accountID\" : \"123456789012\",\n" +
            "    \"emailAddress\" : \"dude@cool.com\",\n" +
            "    \"password\" : \"abc123\"\n" +
            "  }\n" +
            "}";
        assertEquals(expectedJSON, getJSON(signUpResult));

        // Make sure the AWS cloud instance is as expected:
        expectedJSON =
            "{\n" +
            "  \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
            "  \"name\" : \"aws\",\n" +
            "  \"normalAccounts\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.accounts.NormalAccount\",\n" +
            "      \"name\" : \"Cool Account\",\n" +
            "      \"accountID\" : \"123456789012\",\n" +
            "      \"emailAddress\" : \"dude@cool.com\",\n" +
            "      \"password\" : \"abc123\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"nextAccountSequenceNumber\" : 123456789013\n" +
            "}";
        assertEquals(expectedJSON, getJSON(awsCloud));
    }
}
