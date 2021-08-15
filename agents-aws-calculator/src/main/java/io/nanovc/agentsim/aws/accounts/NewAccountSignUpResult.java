package io.nanovc.agentsim.aws.accounts;

import io.nanovc.agentsim.aws.ErrorList;

/**
 * The result of an attempt to sign up for a new {@link NormalAccount Normal AWS Account} which is not associated with an {@link io.nanovc.agentsim.aws.organizations.Organization}.
 * <p>
 * https://portal.aws.amazon.com/billing/signup
 */
public class NewAccountSignUpResult
{
    /**
     * The account that was created.
     * Null if the account could not be created and then {@link #errors} will contain the reason for not being able to create the account.
     */
    public NormalAccount createdAccount;

    /**
     * The errors if a new account couldn't be created.
     */
    public ErrorList errors = new ErrorList();
}
