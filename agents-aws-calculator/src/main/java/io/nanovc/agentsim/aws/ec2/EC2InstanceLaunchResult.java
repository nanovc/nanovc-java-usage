package io.nanovc.agentsim.aws.ec2;

import io.nanovc.agentsim.aws.ErrorList;

/**
 * The result of launching {@link EC2Instance EC2 instances} using the {@link EC2Controller}.
 */
public class EC2InstanceLaunchResult
{
    /**
     * The collection of {@link EC2Instance EC2 instances} that were launched.
     */
    public EC2InstanceCollection launchedEC2Instances = new EC2InstanceCollection();

    /**
     * The list of errors from the attempt at launching the {@link EC2Instance EC2 instances}.
     */
    public ErrorList errors = new ErrorList();
}
