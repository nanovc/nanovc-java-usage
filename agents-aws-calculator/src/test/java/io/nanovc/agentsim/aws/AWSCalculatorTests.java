package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.EnvironmentController;
import io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTestsBase;
import org.junit.jupiter.api.Test;

/**
 * Test the AWS Pricing Calculator.
 */
class AWSCalculatorTests extends MemorySimulationHandlerTestsBase
{

    @Test
    public void test() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            // Create the environment for the test:


            //#endregion
        };

        // Make sure the model is as expected:
        //language=JSON
        String expectedInputJSON =
            "{ }";

        // Make sure that the output model is as expected:
        // Make sure the model is as expected:
        //language=JSON
        String expectedOutputJSON =
            "{ }";

        assert_InputJSON_Simulation_OutputJSON(inputModelCreator, expectedInputJSON, expectedOutputJSON);
    }

}
