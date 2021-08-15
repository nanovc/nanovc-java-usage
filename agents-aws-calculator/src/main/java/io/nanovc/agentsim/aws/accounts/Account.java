package io.nanovc.agentsim.aws.accounts;

import io.nanovc.agentsim.aws.ARN;
import io.nanovc.agentsim.aws.AWSConceptBase;
import io.nanovc.agentsim.aws.organizations.Organization;

/**
 * A base class for an AWS Account in an {@link Organization organization}.
 * https://docs.aws.amazon.com/organizations/latest/userguide/orgs_getting-started_concepts.html#account
 */
public abstract class Account extends AWSConceptBase
{
    /**
     * The ID of the account.
     * The ID of the {@link Account AWS account } without the hyphens.
     * For example, 123456789012.
     * <p>
     * https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html
     *
     * A 12-digit number, such as 123456789012, that uniquely identifies an AWS account.
     * Many AWS resources include the account ID in their Amazon Resource Names ({@link ARN ARNs}).
     * The account ID portion distinguishes resources in one account from the resources in another account.
     * If you are an IAM user, you can sign in to the AWS Management Console using either the account ID or account alias.
     * https://docs.aws.amazon.com/general/latest/gr/acct-identifiers.html
     */
    public String accountID;
}
