package io.nanovc.agentsim.aws.ec2;

import io.nanovc.agentsim.aws.AWSCloud;
import io.nanovc.meh.MEHConcepts;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@link MEHConcepts#CONTROLLER controller} for a {@link EC2Instance EC2 instances} in an {@link AWSCloud} model.
 * This is useful for an intuitive API around querying and manipulating the {@link EC2Instance EC2 instance} models.
 */
public class EC2Controller
{

    /**
     * The {@link AWSCloud} model being managed by this {@link MEHConcepts#CONTROLLER controller}.
     */
    public AWSCloud awsCloud;

    /**
     * The {@link EC2Instance EC2 instances} being controlled,
     * indexed by EC2 instance name.
     */
    private final Map<String, EC2Instance> ec2InstancesByName = new LinkedHashMap<>();

    /**
     * Creates a new {@link MEHConcepts#CONTROLLER controller} that manages the given {@link AWSCloud} model.
     *
     * @param awsCloud The {@link AWSCloud} model to manage.
     */
    public EC2Controller(AWSCloud awsCloud)
    {
        this.awsCloud = awsCloud;
    }

    /**
     * Creates a new {@link EC2Controller} which can be used to manage the {@link EC2Instance EC2 instances} in the given {@link AWSCloud} model.
     * The controller needs to have {@link EC2Controller#updateIndex()} called on it after this.
     *
     * @param awsCloud The {@link AWSCloud} model to manage.
     * @return A new {@link EC2Controller EC2 controller} that can be used to manage the {@link EC2Instance EC2 instances} in the given {@link AWSCloud} model.
     *     The controller needs to have {@link EC2Controller#updateIndex()} called on it after this.
     */
    public static EC2Controller create(AWSCloud awsCloud)
    {
        return new EC2Controller(awsCloud);
    }

    /**
     * Creates a new {@link EC2Controller} which can be used to manage the {@link EC2Instance EC2 Instances} in the given {@link AWSCloud} model.
     * The controller immediately calls {@link EC2Controller#updateIndex()} to index the {@link EC2Instance EC2 instances}.
     *
     * @param awsCloud The {@link AWSCloud} model to manage.
     * @return A new {@link EC2Controller EC2 controller} that can be used to manage the {@link EC2Instance EC2 instances} in the given {@link AWSCloud} model.
     *     The automatically calls {@link EC2Controller#updateIndex()} before returning the new controller.
     */
    public static EC2Controller createAndIndex(AWSCloud awsCloud)
    {
        EC2Controller controller = new EC2Controller(awsCloud);
        controller.updateIndex();
        return controller;
    }

    /**
     * This updates the internal indexes for the {@link #awsCloud} model.
     */
    public void updateIndex()
    {
        // Clear our indexes:
        this.ec2InstancesByName.clear();

        // Index the EC2 instances:
        for (EC2Instance ec2Instance : this.awsCloud.ec2Instances)
        {
            this.ec2InstancesByName.put(ec2Instance.name, ec2Instance);
        }
    }

    /**
     * This gets or creates an {@link EC2Instance EC2 instance} with the given name.
     *
     * @param ec2InstanceName The name of the {@link EC2Instance EC2 instance} to get.
     * @return The {@link EC2Instance EC2 instance} with the given name.
     */
    public EC2Instance getOrCreateEC2Instance(String ec2InstanceName)
    {
        // Check whether we already have an EC2 instance with the given name:
        return this.ec2InstancesByName.computeIfAbsent(ec2InstanceName, s ->
        {
            // Create the EC2 instance:
            EC2Instance ec2Instance = new EC2Instance();

            // Set the name for the EC2 instance:
            ec2Instance.name = ec2InstanceName;

            // Add the EC2 instance to the model:
            this.awsCloud.ec2Instances.add(ec2Instance);

            // Index this EC2 instance:
            return ec2Instance;
        });
    }
}
