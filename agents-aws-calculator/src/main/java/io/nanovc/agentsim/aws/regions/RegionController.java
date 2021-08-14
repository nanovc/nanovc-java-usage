package io.nanovc.agentsim.aws.regions;

import io.nanovc.agentsim.aws.AWSCloud;
import io.nanovc.meh.MEHConcepts;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@link MEHConcepts#CONTROLLER controller} for {@link Region regions} in an {@link AWSCloud} model.
 * This is useful for an intuitive API around querying and manipulating the {@link Region region} models.
 */
public class RegionController
{

    /**
     * The {@link AWSCloud} model being managed by this {@link MEHConcepts#CONTROLLER controller}.
     */
    public AWSCloud awsCloud;

    /**
     * The {@link Region regions} being controlled,
     * indexed by region name.
     */
    private final Map<String, Region> regionsByName = new LinkedHashMap<>();

    /**
     * Creates a new {@link MEHConcepts#CONTROLLER controller} that manages the given {@link AWSCloud} model.
     *
     * @param awsCloud The {@link AWSCloud} model to manage.
     */
    public RegionController(AWSCloud awsCloud)
    {
        this.awsCloud = awsCloud;
    }

    /**
     * Creates a new {@link RegionController} which can be used to manage the {@link Region regions} in the given {@link AWSCloud} model.
     * The controller needs to have {@link RegionController#updateIndex()} called on it after this.
     *
     * @param awsCloud The {@link AWSCloud} model to manage.
     * @return A new {@link RegionController EC2 controller} that can be used to manage the {@link Region regions} in the given {@link AWSCloud} model.
     *     The controller needs to have {@link RegionController#updateIndex()} called on it after this.
     */
    public static RegionController create(AWSCloud awsCloud)
    {
        return new RegionController(awsCloud);
    }

    /**
     * Creates a new {@link RegionController} which can be used to manage the {@link Region regions} in the given {@link AWSCloud} model.
     * The controller immediately calls {@link RegionController#updateIndex()} to index the {@link Region regions}.
     *
     * @param awsCloud The {@link AWSCloud} model to manage.
     * @return A new {@link RegionController EC2 controller} that can be used to manage the {@link Region regions} in the given {@link AWSCloud} model.
     *     The automatically calls {@link RegionController#updateIndex()} before returning the new controller.
     */
    public static RegionController createAndIndex(AWSCloud awsCloud)
    {
        RegionController controller = new RegionController(awsCloud);
        controller.updateIndex();
        return controller;
    }

    /**
     * This updates the internal indexes for the {@link #awsCloud} model.
     */
    public void updateIndex()
    {
        // Clear our indexes:
        this.regionsByName.clear();

        // Index the regions:
        for (Region region : this.awsCloud.regions)
        {
            this.regionsByName.put(region.getName(), region);
        }
    }

    /**
     * This gets or creates an {@link Region region} with the given name.
     *
     * @param regionName The name of the {@link Region region} to get.
     * @return The {@link Region region} with the given name.
     */
    public Region getOrCreateRegion(String regionName)
    {
        // Check whether we already have a region with the given name:
        return this.regionsByName.computeIfAbsent(regionName, s ->
        {
            // Create the Region:
            Region region = new Region();

            // Set the name for the region:
            region.setName(regionName);

            // Add the region to the model:
            this.awsCloud.regions.add(region);

            // Index this region:
            return region;
        });
    }
}
