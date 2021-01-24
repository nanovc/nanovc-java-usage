package io.nanovc.agentsim.aws.organizations;

/**
 * A reference to a specific {@link MemberAccount member account} in an {@link Organization organization}.
 */
public class MemberAccountReference extends AccountReference
{
    /**
     * The name of the {@link MemberAccount account} being referenced in the {@link #organizationName organization}.
     */
    public String accountName;
}