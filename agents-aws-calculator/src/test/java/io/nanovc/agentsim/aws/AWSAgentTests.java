package io.nanovc.agentsim.aws;

import io.nanovc.agentsim.EnvironmentController;
import io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTestsBase;
import org.junit.jupiter.api.Test;

class AWSAgentTests extends MemorySimulationHandlerTestsBase
{

    @Test
    public void test() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            // Create the agent configuration:
            AWSAgent.Config agentConfig = new AWSAgent.Config();
            controller.addAgentConfig(agentConfig);

            //#endregion
        };

        // Make sure the model is as expected:
        //language=JSON
        String expectedInputJSON =
            "{\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.AWSAgent$Config\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        // Make sure that the output model is as expected:
        // Make sure the model is as expected:
        //language=JSON
        String expectedOutputJSON =
            "{\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.aws.AWSAgent$Config\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        assert_InputJSON_Simulation_OutputJSON(inputModelCreator, expectedInputJSON, expectedOutputJSON);
    }

}
