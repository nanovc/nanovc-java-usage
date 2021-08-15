package io.nanovc.agentsim.aws.organizations;

import io.nanovc.agentsim.aws.AWSConceptBase;

import java.util.ArrayList;
import java.util.List;

/**
 * A base class for node in an {@link Organization organization} tree.
 * <p>
 * A container for accounts within a {@link Root root}.
 * An {@link OrganizationalUnit OU} also can contain other {@link OrganizationalUnit OUs},
 * enabling you to create a hierarchy that resembles an upside-down tree,
 * with a {@link Root root} at the top and branches of {@link OrganizationalUnit OUs} that reach down,
 * ending in {@link MemberAccount accounts} that are the leaves of the tree.
 * <p>
 * When you attach a policy to one of the nodes in the hierarchy,
 * it flows down and affects all the branches ({@link OrganizationalUnit OUs}) and leaves ({@link MemberAccount accounts}) beneath it.
 * An {@link OrganizationalUnit OU} can have exactly one parent,
 * and currently each {@link MemberAccount account} can be a member of exactly one {@link OrganizationalUnit OU}.
 * <p>
 * https://docs.aws.amazon.com/organizations/latest/userguide/orgs_getting-started_concepts.html#organizationalunit
 */
public class OrganizationalUnitBase extends AWSConceptBase
{
    /**
     * The {@link MemberAccount accounts} in this {@link OrganizationalUnitBase organizational unit}.
     */
    public List<MemberAccount> accounts = new ArrayList<>();

    /**
     * The child {@link OrganizationalUnit organizational units} in this {@link Organization organization}.
     */
    public List<OrganizationalUnit> children = new ArrayList<>();
}
