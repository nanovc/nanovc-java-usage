package io.nanovc.agentsim.aws.organizations;

/**
 * A root node in an {@link Organization organization} tree.
 * The parent container for all the {@link Account accounts} for your {@link Organization organization}.
 * If you apply a policy to the {@link Root root}, it applies to all {@link OrganizationalUnit organizational units (OUs)} and {@link Account accounts} in the {@link Organization organization}.
 * <p>
 * https://docs.aws.amazon.com/organizations/latest/userguide/orgs_getting-started_concepts.html#root
 */
public class Root extends OrganizationalUnitBase
{
    /**
     * The root {@link ManagementAccount management account} for this {@link Organization organization}.
     */
    public ManagementAccount managementAccount = new ManagementAccount();
}
