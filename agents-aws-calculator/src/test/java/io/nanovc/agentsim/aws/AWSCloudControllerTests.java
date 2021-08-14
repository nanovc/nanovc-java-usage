package io.nanovc.agentsim.aws;

import org.junit.jupiter.api.Test;

/**
 * Tests the {@link AWSCloudController}.
 */
class AWSCloudControllerTests extends AWSTestsBase
{
    @Test
    public void creationTest()
    {
        new AWSCloudController(new AWSCloud());
        AWSCloudController.create(new AWSCloud());
    }

}
