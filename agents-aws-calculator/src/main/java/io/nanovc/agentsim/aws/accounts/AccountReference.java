package io.nanovc.agentsim.aws.accounts;

import io.nanovc.agentsim.aws.organizations.Organization;

/**
 * A reference to an {@link Account account} in an {@link Organization organization}.
 * Use this in other classes to refer to a specific account.
 */
public abstract class AccountReference
{
    /**
     * The name of the organization that the account belongs to.
     * This corresponds to the model name in the {@link io.nanovc.agentsim.EnvironmentModel environment} for the {@link Organization organization}.
     */
    public String organizationName;
}
