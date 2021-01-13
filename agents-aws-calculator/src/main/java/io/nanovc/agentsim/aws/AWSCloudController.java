package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.aws.organizations.Organization;
import io.nanovc.meh.MEHConcepts;

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
}
