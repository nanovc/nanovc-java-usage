package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.ModelBase;
import io.nanovc.agentsim.aws.accounts.NormalAccount;
import io.nanovc.agentsim.aws.ec2.EC2InstanceCollection;
import io.nanovc.agentsim.aws.accounts.NormalAccountCollection;
import io.nanovc.agentsim.aws.organizations.OrganizationCollection;
import io.nanovc.agentsim.aws.regions.RegionCollection;

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
     * The {@link NormalAccount normal AWS accounts} that aren't linked to {@link io.nanovc.agentsim.aws.organizations.Organization organizations}.
     */
    public NormalAccountCollection normalAccounts = new NormalAccountCollection();

    /**
     * The {@link io.nanovc.agentsim.aws.organizations.Organization organizations} running in this {@link AWSCloud AWS Cloud model}.
     */
    public OrganizationCollection organizations = new OrganizationCollection();

    /**
     * The {@link io.nanovc.agentsim.aws.regions.Region regions} in the {@link AWSCloud AWS Cloud model}.
     */
    public RegionCollection regions = new RegionCollection();

    /**
     * The {@link io.nanovc.agentsim.aws.ec2.EC2Instance EC2 instances} running in this {@link AWSCloud AWS Cloud model}.
     */
    public EC2InstanceCollection ec2Instances = new EC2InstanceCollection();

    /**
     * The next account sequence number to use when creating an account.
     * https://docs.aws.amazon.com/general/latest/gr/acct-identifiers.html
     */
    public long nextAccountSequenceNumber = 123456789012L;

    public AWSCloud()
    {
        this.setName(AWS);
    }
}
