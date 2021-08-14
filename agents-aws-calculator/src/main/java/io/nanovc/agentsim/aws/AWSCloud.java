package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.ModelBase;
import io.nanovc.agentsim.aws.ec2.EC2InstanceCollection;
import io.nanovc.agentsim.aws.organizations.OrganizationCollection;

/**
 * The AWS Cloud Infrastructure.
 */
public class AWSCloud extends ModelBase
{

    /**
     * The standard model name of the AWS cloud model.
     */
    public static final String AWS = "aws";

    /**
     * The {@link io.nanovc.agentsim.aws.organizations.Organization organizations} running in this {@link AWSCloud AWS Cloud model}.
     */
    public OrganizationCollection organizations = new OrganizationCollection();

    /**
     * The {@link io.nanovc.agentsim.aws.ec2.EC2Instance EC2 instances} running in this {@link AWSCloud AWS Cloud model}.
     */
    public EC2InstanceCollection ec2Instances = new EC2InstanceCollection();

    public AWSCloud()
    {
        this.setName(AWS);
    }
}
