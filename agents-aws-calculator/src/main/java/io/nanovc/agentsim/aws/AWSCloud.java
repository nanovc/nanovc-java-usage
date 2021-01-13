package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.aws.organizations.OrganizationCollection;

/**
 * The AWS Cloud Infrastructure.
 */
public class AWSCloud extends AWSConcept
{

    /**
     * The standard model name of the AWS cloud model.
     */
    public static final String AWS = "aws";

    /**
     * The organizations in AWS for this model.
     */
    public OrganizationCollection organizations = new OrganizationCollection();

    public AWSCloud()
    {
        this.setName(AWS);
    }
}
