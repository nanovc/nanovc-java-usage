package io.nanovc.agentsim.aws.organizations;

import io.nanovc.agentsim.aws.NamedAWSConceptBase;
import io.nanovc.agentsim.aws.accounts.Account;

/**
 * An AWS Organization.
 * Centrally manage and govern your environment as you scale your AWS resources.
 * <p>
 * https://aws.amazon.com/organizations
 * <p>
 * An entity that you create to consolidate your AWS {@link Account accounts} so that you can administer them as a single unit.
 * You can use the AWS Organizations console to centrally view and manage all of your {@link Account accounts} within your {@link Organization organization}.
 * An {@link Organization organization} has one {@link ManagementAccount management account} along with zero or more {@link MemberAccount member accounts}.
 * You can organize the {@link MemberAccount accounts} in a hierarchical, tree-like structure with a {@link Root root} at the top and {@link OrganizationalUnit organizational units} nested under the {@link Root root}.
 * Each {@link MemberAccount account} can be directly in the {@link Root root}, or placed in one of the {@link OrganizationalUnit OUs} in the hierarchy.
 * An {@link Organization organization} has the functionality that is determined by the feature set that you enable.
 * <p>
 * https://docs.aws.amazon.com/organizations/latest/userguide/orgs_getting-started_concepts.html#org
 */
public class Organization extends NamedAWSConceptBase
{
    /**
     * The ID of this {@link Organization}.
     */
    public String organizationID;

    /**
     * The root of the organizational hierarchy.
     */
    public Root root = new Root();
}
