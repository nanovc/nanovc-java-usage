package io.nanovc.agentsim.aws.ec2;

/**
 * The configuration for launching an {@link EC2Instance EC2 instance}.
 *
 * These options come from:
 * https://af-south-1.console.aws.amazon.com/ec2/v2/home?region=af-south-1#LaunchInstanceWizard:
 */
public class EC2InstanceLaunchConfig
{
    /**
     * The name of the AMI image to launch.
     */
    public String AMIName;

    /**
     * The name of the instance type to launch.
     */
    public String instanceTypeName;

    /**
     * The number of instances to launch.
     * You can choose to launch more than one instance at a time.
     */
    public int numberOfInstances = 1;

}
