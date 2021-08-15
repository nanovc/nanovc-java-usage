package io.nanovc.agentsim.aws.accounts;

import io.nanovc.agentsim.aws.NamedAWSConcept;
import io.nanovc.agentsim.aws.organizations.Organization;

/**
 * A {@link NormalAccount normal AWS account} that is not part of an {@link Organization organization}.
 */
public class NormalAccount extends Account implements NamedAWSConcept
{
    /**
     * The name of this account.
     * This is the name of the account in the {@link io.nanovc.agentsim.aws.AWSCloud AWS cloud model}.
     * To get the ID for this account, see {@link #accountID}.
     */
    public String name;

    /**
     * The email address associated with this {@link NormalAccount}.
     * This is the primary contact email for this account.
     */
    public String emailAddress;

    /**
     * The password for this account.
     */
    public String password;

    /**
     * The name of the {@link NormalAccount normal AWS account}.
     * This is the name of the account in the {@link io.nanovc.agentsim.aws.AWSCloud AWS cloud model}.
     *
     * @return The name of the {@link NormalAccount normal AWS account}.
     */
    @Override public String getName()
    {
        return this.name;
    }

    /**
     * The name of the {@link NormalAccount}.
     * This is the name of the account in the {@link io.nanovc.agentsim.aws.AWSCloud AWS cloud model}.
     *
     * @param name The name of the {@link NormalAccount normal AWS account}.
     */
    @Override public void setName(String name)
    {
        this.name = name;
    }
}
