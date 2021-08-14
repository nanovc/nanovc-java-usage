package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.aws.organizations.OrganizationController;
import io.nanovc.meh.MEHConcepts;

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
     * Creates a new {@link MEHConcepts#CONTROLLER controller} that manages the given {@link AWSCloud} model.
     *
     * @param awsCloud The {@link AWSCloud} model to manage.
     */
    public AWSCloudController(AWSCloud awsCloud)
    {
        this.awsCloud = awsCloud;
    }

    /**
     * Creates a new {@link AWSCloudController} for the given {@link AWSCloud} model.
     * @param awsCloud The {@link AWSCloud} model to manage.
     * @return A new {@link AWSCloudController} for the given {@link AWSCloud} model.
     */
    public static AWSCloudController create(AWSCloud awsCloud)
    {
        return new AWSCloudController(awsCloud);
    }

    /**
     * Creates a new {@link OrganizationController organization controller} for the {@link AWSCloud} model being managed.
     * The controller still needs to be indexed by calling {@link OrganizationController#updateIndex()} after it is returned.
     * @return A new organization controller for the {@link AWSCloud} model being managed.
     * It still needs to be indexed by calling {@link OrganizationController#updateIndex()} before using it.
     */
    public OrganizationController createOrganizationController()
    {
        return OrganizationController.create(this.awsCloud);
    }

    /**
     * Creates a new {@link OrganizationController organization controller} for the {@link AWSCloud} model being managed.
     * The controller is indexed before returning.
     * @return A new organization controller for the {@link AWSCloud} model being managed. It has already been indexed.
     */
    public OrganizationController createOrganizationControllerAndIndex()
    {
        return OrganizationController.createAndIndex(this.awsCloud);
    }

}
