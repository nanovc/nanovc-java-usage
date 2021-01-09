package io.nanovc.agentsim.aws.organizations;

/**
 * A standard AWS account that contains your AWS resources.
 * You can attach a policy to an account to apply controls to only that one account.
 * <p>
 * There are two types of accounts in an organization: a single account that is designated as the {@link ManagementAccount management account}, and {@link MemberAccount member accounts}.
 * <p>
 * https://docs.aws.amazon.com/organizations/latest/userguide/orgs_getting-started_concepts.html#account
 * <p>
 * An {@link Organization organization} has one {@link ManagementAccount management account} along with zero or more {@link MemberAccount member accounts}.
 * You can organize the {@link Account accounts} in a hierarchical, tree-like structure with a root at the top and {@link OrganizationalUnit organizational units} nested under the {@link Root root}.
 * Each {@link MemberAccount account} can be directly in the {@link Root root}, or placed in one of the {@link OrganizationalUnit OUs} in the hierarchy.
 * An {@link Organization organization} has the functionality that is determined by the feature set that you enable.
 * AWS Organizations is changing the name of the “master account” to “management account”.
 * <p>
 * The rest of the accounts that belong to an organization are called {@link MemberAccount member accounts}.
 * An account can be a member of only one {@link Organization organization} at a time.
 * <p>
 * https://docs.aws.amazon.com/organizations/latest/userguide/orgs_getting-started_concepts.html#org
 * https://docs.aws.amazon.com/organizations/latest/userguide/orgs_getting-started_concepts.html#account
 */
public class MemberAccount extends Account
{
    /**
     * The name of this {@link MemberAccount account} in the {@link Organization organization}.
     */
    public String accountName;
}
