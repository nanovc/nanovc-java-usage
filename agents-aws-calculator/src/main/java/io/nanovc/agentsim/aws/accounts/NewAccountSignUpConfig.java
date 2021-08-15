package io.nanovc.agentsim.aws.accounts;

/**
 * The configuration for signing up for a new {@link NormalAccount Normal AWS Account} which is not associated with an {@link io.nanovc.agentsim.aws.organizations.Organization}.
 * To associate an {@link Account} with an {@link io.nanovc.agentsim.aws.organizations.Organization},
 * you first have to create a {@link NormalAccount normal account} and then activate it with an {@link io.nanovc.agentsim.aws.organizations.Organization}.
 * <p>
 * https://portal.aws.amazon.com/billing/signup
 */
public class NewAccountSignUpConfig
{
    /**
     * You will use this email address to sign in to your new AWS account.
     */
    public String emailAddress;

    /**
     * The password for the account.
     */
    public String password;

    /**
     * Choose a name for your account.
     * You can change this name in your account settings after you sign up.
     */
    public String awsAccountName;
}
