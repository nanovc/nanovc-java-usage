package io.nanovc.agentsim.aws.regions;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nanovc.agentsim.aws.AWSCloud;
import io.nanovc.agentsim.aws.AWSTestsBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link RegionController}.
 */
class RegionControllerTests extends AWSTestsBase
{

    @Test
    public void creationTest()
    {
        new RegionController(new AWSCloud());
        RegionController.create(new AWSCloud());
        RegionController.createAndIndex(new AWSCloud());
    }

    @Test
    public void oneRegion() throws JsonProcessingException
    {
        // Create the organization controller for a new AWSCloud instance:
        RegionController regionController = new RegionController(new AWSCloud());

        String expectedJSON;

        // Make sure the AWS cloud instance is empty to begin with:
        expectedJSON =
        "{\n" +
        "  \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
        "  \"name\" : \"aws\"\n" +
        "}";
        assertEquals(expectedJSON, getJSON(regionController.awsCloud));

        // Create the region:
        Region region = regionController.getOrCreateRegion("Africa Cape Town");

        // Make sure the AWS cloud is as expected:
        expectedJSON =
            "{\n" +
            "  \"type\" : \"io.nanovc.agentsim.aws.AWSCloud\",\n" +
            "  \"name\" : \"aws\",\n" +
            "  \"regions\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.regions.Region\",\n" +
            "      \"name\" : \"Africa Cape Town\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(expectedJSON, getJSON(regionController.awsCloud));
    }

}
