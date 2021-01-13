package io.nanovc.agentsim.aws.organizations;

/**
 * An {@link Organization organization} has one {@link ManagementAccount management account} along with zero or more {@link MemberAccount member accounts}.
 * <p>
 * You can organize the {@link Account accounts} in a hierarchical, tree-like structure with a root at the top and {@link OrganizationalUnit organizational units} nested under the {@link Root root}.
 * Each {@link MemberAccount account} can be directly in the {@link Root root}, or placed in one of the {@link OrganizationalUnit OUs} in the hierarchy.
 * An {@link Organization organization} has the functionality that is determined by the feature set that you enable.
 * AWS Organizations is changing the name of the “master account” to “management account”.
 * <p>
 * The {@link ManagementAccount management account} is the account that you use to create the {@link Organization organization}.
 * <p>
 * From the {@link Organization organization's} {@link ManagementAccount management account}, you can do the following:
 * <li>Create {@link MemberAccount accounts} in the {@link Organization organization}</li>
 * <li>Invite other existing {@link MemberAccount accounts} to the {@link Organization organization}</li>
 * <li>Remove {@link MemberAccount accounts} from the {@link Organization organization}</li>
 * <li>Manage invitations</li>
 * <li>Apply policies to entities ({@link Root roots}, {@link OrganizationalUnit OUs}, or {@link MemberAccount accounts}) within the {@link Organization organization}</li>
 * <p>
 * The {@link ManagementAccount management account} has the responsibilities of a payer account and is responsible for paying all charges that are accrued by the {@link MemberAccount member accounts}.
 * You can't change an {@link Organization organization's} {@link ManagementAccount management account}.
 * <p>
 * The rest of the {@link MemberAccount accounts} that belong to an organization are called {@link MemberAccount member accounts}.
 * An {@link Account account} can be a member of only one {@link Organization organization} at a time.
 * <p>
 * https://docs.aws.amazon.com/organizations/latest/userguide/orgs_getting-started_concepts.html#org
 * https://docs.aws.amazon.com/organizations/latest/userguide/orgs_getting-started_concepts.html#account
 */
public class ManagementAccount extends Account
{
}
