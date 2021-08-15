package io.nanovc.agentsim.aws.accounts;

import io.nanovc.agentsim.aws.ARN;
import io.nanovc.agentsim.aws.AWSCloud;
import io.nanovc.agentsim.aws.organizations.Organization;
import io.nanovc.meh.MEHConcepts;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@link MEHConcepts#CONTROLLER controller} for {@link Account accounts} in an {@link AWSCloud} model.
 * This is useful for an intuitive API around querying and manipulating the {@link Account} model.
 * {@link NormalAccount Normal AWS Accounts} are ones that have not been associated with an {@link Organization}.
 * This controller is used for managing those.
 */
public class AccountController
{

    /**
     * The {@link AWSCloud} model being managed by this {@link MEHConcepts#CONTROLLER controller}.
     */
    public AWSCloud awsCloud;

    /**
     * The {@link NormalAccount normal accounts} which aren't associated with an {@link Organization},
     * indexed by account name.
     */
    private final Map<String, NormalAccount> normalAccountsByName = new LinkedHashMap<>();

    /**
     * The {@link NormalAccount normal accounts} which aren't associated with an {@link Organization},
     * indexed by email address.
     */
    private final Map<String, NormalAccount> normalAccountsByEmailAddress = new LinkedHashMap<>();

    /**
     * Creates a new {@link MEHConcepts#CONTROLLER controller} that manages the given {@link AWSCloud} model.
     *
     * @param awsCloud The {@link AWSCloud} model to manage.
     */
    public AccountController(AWSCloud awsCloud)
    {
        this.awsCloud = awsCloud;
    }

    /**
     * Creates a new {@link AccountController} which can be used to manage the {@link Account accounts} in the given {@link AWSCloud} model.
     * The controller needs to have {@link AccountController#updateIndex()} called on it after this.
     *
     * @param awsCloud The {@link AWSCloud} model to manage.
     * @return A new {@link AccountController organization controller} that can be used to manage the {@link Account accounts} in the given {@link AWSCloud} model.
     *     The controller needs to have {@link AccountController#updateIndex()} called on it after this.
     */
    public static AccountController create(AWSCloud awsCloud)
    {
        return new AccountController(awsCloud);
    }

    /**
     * Creates a new {@link AccountController} which can be used to manage the {@link Account accounts} in the given {@link AWSCloud} model.
     * The controller immediately calls {@link AccountController#updateIndex()} to index the {@link Organization organizations}.
     *
     * @param awsCloud The {@link AWSCloud} model to manage.
     * @return A new {@link AccountController organization controller} that can be used to manage the {@link Account accounts} in the given {@link AWSCloud} model.
     *     The automatically calls {@link AccountController#updateIndex()} before returning the new controller.
     */
    public static AccountController createAndIndex(AWSCloud awsCloud)
    {
        AccountController controller = new AccountController(awsCloud);
        controller.updateIndex();
        return controller;
    }

    /**
     * This updates the internal indexes for the {@link #awsCloud} model.
     */
    public void updateIndex()
    {
        // Clear our indexes:
        this.normalAccountsByName.clear();
        this.normalAccountsByEmailAddress.clear();

        // Index the normal accounts:
        for (NormalAccount normalAccount : this.awsCloud.normalAccounts)
        {
            this.normalAccountsByName.put(normalAccount.getName(), normalAccount);
            this.normalAccountsByEmailAddress.put(normalAccount.emailAddress, normalAccount);
        }
    }

    /**
     * Gets or creates a {@link NormalAccount normal AWS account} with the given name which isn't associated with an {@link Organization}.
     *
     * @param accountName The name of the account to get or create.
     * @return The {@link NormalAccount normal AWS account} with the given name.
     */
    public NormalAccount getOrCreateNormalAccount(String accountName)
    {
        // Check whether we already have an organization:
        return this.normalAccountsByName.computeIfAbsent(accountName, s ->
        {
            // Create the normal AWS account:
            NormalAccount normalAccount = new NormalAccount();

            // Set the name for the normal account:
            normalAccount.setName(accountName);

            // Add the normal account to the model:
            this.awsCloud.normalAccounts.add(normalAccount);

            // Index this normal account:
            return normalAccount;
        });
    }

    /**
     * This attempts to sign up for a new {@link NormalAccount Normal AWS Account} that is not associated with an {@link Organization}.
     *
     * @param newAccountSignUpConfig The details required to sign up for a new account.
     * @return The result of attempting to sign up for the new account.
     */
    public NewAccountSignUpResult signUpForNewAccount(NewAccountSignUpConfig newAccountSignUpConfig)
    {
        // Create the result of the attempt to sign up:
        NewAccountSignUpResult result = new NewAccountSignUpResult();

        // Check whether the email for this account is already associated with another account:
        if (this.normalAccountsByEmailAddress.containsKey(newAccountSignUpConfig.emailAddress))
        {
            // We already have an AWS account associated with this email address.
            result.errors.add("An account with this email already exists.");

            // Don't continue:
            return result;
        }

        // Check whether the account with the given name already exists:
        if (this.normalAccountsByName.containsKey(newAccountSignUpConfig.awsAccountName))
        {
            // We already have an AWS account with this name.
            result.errors.add("An account with this name already exists.");

            // Don't continue:
            return result;
        }

        // Now we know that we can create this account.

        // Create the account:
        NormalAccount account = this.getOrCreateNormalAccount(newAccountSignUpConfig.awsAccountName);

        // Save important account details:
        account.emailAddress = newAccountSignUpConfig.emailAddress;
        account.password = newAccountSignUpConfig.password;

        // Generate a new account ID for this account:
        account.accountID = generateNextAccountID();

        // Add this to the results:
        result.createdAccount = account;

        return result;
    }

    /**
     * Generates a new account id and updates the {@link AWSCloud} state appropriately.
     * <p>
     * A 12-digit number, such as 123456789012, that uniquely identifies an AWS account.
     * Many AWS resources include the account ID in their Amazon Resource Names ({@link ARN ARNs}).
     * The account ID portion distinguishes resources in one account from the resources in another account.
     * If you are an IAM user, you can sign in to the AWS Management Console using either the account ID or account alias.
     * https://docs.aws.amazon.com/general/latest/gr/acct-identifiers.html
     *
     * @return A newly generated account id.
     */
    public String generateNextAccountID()
    {
        // Get the next sequence number for accounts:
        long accountSequenceNumber = this.awsCloud.nextAccountSequenceNumber++;

        // Format it appropriately:
        String accountNumber = Long.toString(accountSequenceNumber);

        return accountNumber;
    }
}
