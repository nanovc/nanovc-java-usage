package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.aws.organizations.*;
import io.nanovc.meh.MEHConcepts;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@link MEHConcepts#CONTROLLER controller} for an {@link AWSCloud} model.
 * This is useful for an intuitive API around querying and manipulating the {@link AWSCloud} model.
 */
public class AWSCloudController
{
    /**
     * The {@link AWSCloud} model being managed by this {@link MEHConcepts#CONTROLLER controller}.
     */
    public AWSCloud awsCloud;

    /**
     * The {@link Organization organizations} for {@link #awsCloud},
     * indexed by organizations name.
     */
    private Map<String, Organization> organizationsByName = new LinkedHashMap<>();

    /**
     * Creates a new {@link MEHConcepts#CONTROLLER controller} that manages the given {@link AWSCloud} model.
     *
     * @param awsCloud The {@link AWSCloud} model to manage.
     */
    public AWSCloudController(AWSCloud awsCloud)
    {
        this.awsCloud = awsCloud;

        // Update the indexes:
        updateIndex();
    }

    /**
     * This updates the internal indexes for the {@link #awsCloud} model.
     */
    public void updateIndex()
    {
        // Clear our indexes:
        this.organizationsByName.clear();

        // Index the organizations:
        for (Organization organization : this.awsCloud.organizations)
        {
            this.organizationsByName.put(organization.name, organization);
        }
    }

    /**
     * This gets or creates an organization with the given name.
     *
     * @param organizationName The name of the organization to create.
     * @return The organization with the given name.
     */
    public Organization getOrCreateOrganization(String organizationName)
    {
        // Check whether we already have an organization:
        return this.organizationsByName.computeIfAbsent(organizationName, s ->
        {
            // Create the organization:
            Organization organization = new Organization();

            // Set the name for the organization:
            organization.name = organizationName;

            // Add the organization to the model:
            this.awsCloud.organizations.add(organization);

            // Index this organization:
            return organization;
        });
    }

    /**
     * Gets or creates an {@link OrganizationalUnit organizational unit (OU)} underneath the given {@link Organization organization}.
     *
     * @param organizationName The name of the {@link Organization organization} that this {@link OrganizationalUnit organizational unit (OU)} must belong to.
     * @param orgTreePath      The path within the {@link Root organizational tree} to add the {@link OrganizationalUnit organizational unit (OU)} to.
     *                         Any {@link OrganizationalUnit organizational units} along the way will automatically be created as needed.
     * @return The {@link OrganizationalUnit organizational unit (OU)} for the given {@link Organization organization} that was either there already or that was created.
     */
    public OrganizationalUnit getOrCreateOrganizationalUnit(String organizationName, String... orgTreePath)
    {
        // Get the organization:
        Organization organization = getOrCreateOrganization(organizationName);

        // Walk each element of the organizational tree:
        OrganizationalUnitBase parentOU = organization.root;
        OrganizationalUnit result = null;
        for (int i = 0; i < orgTreePath.length; i++)
        {
            // Get the part of the path we are on:
            String organizationalUnitName = orgTreePath[i];

            // Capture this organizational unit for the stream:
            final OrganizationalUnitBase parentOUFinal = parentOU;

            // Get or create the OU at this level:
            OrganizationalUnit organizationalUnit = parentOU.children
                .stream()
                .filter(ou -> organizationalUnitName.equals(ou.organizationalUnitName))
                .findFirst()
                .orElseGet(
                    () ->
                    {
                        // Create the new organizational unit:
                        OrganizationalUnit ou = new OrganizationalUnit();
                        ou.organizationalUnitName = organizationalUnitName;

                        // Add the organizational unit:
                        parentOUFinal.children.add(ou);

                        // Use this organizational unit that we created:
                        return ou;
                    });

            // Flag this as the potential result:
            result = organizationalUnit;

            // Make this the new parent OU for the next iteration:
            parentOU = organizationalUnit;
        }

        return result;
    }

    /**
     * Gets or creates a {@link MemberAccount member account} underneath the given {@link Organization organization}.
     *
     * @param organizationName The name of the {@link Organization organization} that this {@link MemberAccount account} must belong to.
     * @param orgTreePath      The path within the {@link Root organizational tree} to add the {@link MemberAccount account} to.
     *                         Any {@link OrganizationalUnit organizational units} along the way will automatically be created as needed.
     *                         The last element of this path will be the {@link MemberAccount#accountName member account name}.
     * @return The {@link MemberAccount member account} for the given {@link Organization organization} that was either there already or that was created.
     */
    public MemberAccount getOrCreateAccount(String organizationName, String... orgTreePath)
    {
        // Get the organization:
        Organization organization = getOrCreateOrganization(organizationName);

        // Get the first part of the orgTreePath because that is the tree that we want to traverse:
        String[] path = Arrays.copyOf(orgTreePath, orgTreePath.length - 1);

        // Get the organizational unit for this path:
        OrganizationalUnit organizationalUnit = getOrCreateOrganizationalUnit(organizationName, path);

        // Strip off the last part of the orgTreePath because that is the account name:
        String accountName = orgTreePath[orgTreePath.length - 1];

        // Get or create the account:
        MemberAccount memberAccount = organizationalUnit.accounts
            .stream()
            .filter(account -> accountName.equals(account.accountName))
            .findFirst()
            .orElseGet(
                () ->
                {
                    // Create the member account:
                    MemberAccount ma = new MemberAccount();
                    ma.accountName = accountName;

                    // Add the account to the OU:
                    organizationalUnit.accounts.add(ma);

                    // Use this member account:
                    return ma;
                }
            );

        return memberAccount;
    }
}
