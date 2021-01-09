package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.*;
import org.junit.jupiter.api.Test;

import java.util.Objects;

/**
 * This tests that agents that modify the same models spawn parallel solutions.
 * <p>
 * Tests the {@link MemorySimulationHandler}.
 */
public class MemorySimulationHandlerTests_MultipleSolutions extends MemorySimulationHandlerTestsBase
{
    @Test
    public void singleModelModifiedByTwoAgents() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            AgentModel dataModel = new AgentModel();
            dataModel.name = "question";
            dataModel.data = "Are robots the best?";
            controller.addModel(dataModel);

            AgentConfig agentConfig1 = new AgentConfig();
            agentConfig1.modelNameToManipulate = "question";
            agentConfig1.expectedValueBeforeManipulating = "Are robots the best?";
            agentConfig1.valueToSet = "yes";
            controller.addAgentConfig(agentConfig1);

            AgentConfig agentConfig2 = new AgentConfig();
            agentConfig2.modelNameToManipulate = "question";
            agentConfig2.expectedValueBeforeManipulating = "Are robots the best?";
            agentConfig2.valueToSet = "no";
            controller.addAgentConfig(agentConfig2);

            //#endregion
        };

        // Make sure that the output solutions are as expected:
        //#region Output Solution JSON
        //language=JSON
        String expectedOutputJSON =
            "[\n" +
            "  {\n" +
            "    \"solutionName\" : \"Solution 1\",\n" +
            "    \"environment\" : {\n" +
            "      \"models\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_MultipleSolutions$AgentModel\",\n" +
            "          \"name\" : \"question\",\n" +
            "          \"data\" : \"no\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"agentConfigs\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_MultipleSolutions$AgentConfig\",\n" +
            "          \"modelNameToManipulate\" : \"question\",\n" +
            "          \"expectedValueBeforeManipulating\" : \"Are robots the best?\",\n" +
            "          \"valueToSet\" : \"yes\",\n" +
            "          \"enabled\" : true\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_MultipleSolutions$AgentConfig\",\n" +
            "          \"modelNameToManipulate\" : \"question\",\n" +
            "          \"expectedValueBeforeManipulating\" : \"Are robots the best?\",\n" +
            "          \"valueToSet\" : \"no\",\n" +
            "          \"enabled\" : true\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"solutionName\" : \"Solution 2\",\n" +
            "    \"environment\" : {\n" +
            "      \"models\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_MultipleSolutions$AgentModel\",\n" +
            "          \"name\" : \"question\",\n" +
            "          \"data\" : \"yes\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"agentConfigs\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_MultipleSolutions$AgentConfig\",\n" +
            "          \"modelNameToManipulate\" : \"question\",\n" +
            "          \"expectedValueBeforeManipulating\" : \"Are robots the best?\",\n" +
            "          \"valueToSet\" : \"yes\",\n" +
            "          \"enabled\" : true\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_MultipleSolutions$AgentConfig\",\n" +
            "          \"modelNameToManipulate\" : \"question\",\n" +
            "          \"expectedValueBeforeManipulating\" : \"Are robots the best?\",\n" +
            "          \"valueToSet\" : \"no\",\n" +
            "          \"enabled\" : true\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }\n" +
            "]";
        //#endregion

        assert_Simulation_OutputJSONSolutions(
            inputModelCreator,
            expectedOutputJSON
        );
    }

    public static class AgentModel extends ModelBase
    {
        public String data;
    }

    public static class AgentConfig extends AgentConfigBase
    {
        /**
         * The name of the model to manipulate.
         */
        public String modelNameToManipulate;

        /**
         * This is the value that the agent expects to see on the model before manipulating it.
         * If the value is not as expected then the agent does nothing.
         */
        public String expectedValueBeforeManipulating;

        /**
         * The value to set when the agent finds the named model and manipulates it.
         */
        public String valueToSet;
    }

    public static class Agent extends AgentBase<AgentConfig>
    {

        /**
         * This allows the agent to modify the environment for an iteration.
         *
         * @param input      The input model to this iteration. This is provided for reference. It must not be modified.
         * @param output     The output model for this iteration. The agent is allowed to modify this output model for the next iteration.
         * @param iteration  The current simulation iteration that is running.
         * @param simulation The simulation that is running.
         * @param config     The agent configuration.
         */
        @Override public void modifyEnvironment(ReadOnlyEnvironmentController input, EnvironmentController output, SimulationIterationAPI iteration, SimulationController simulation, AgentConfig config) throws Exception
        {
            // Check whether we have the model that we are supposed to modify:
            AgentModel modelToManipulate = output.getModelByName(config.modelNameToManipulate);
            if (modelToManipulate != null)
            {
                // We found the model to manipulate.

                // Get the current value for the model:
                String currentValue = modelToManipulate.data;

                // Make sure that the current value is as expected before we manipulate it:
                if (Objects.equals(currentValue, config.expectedValueBeforeManipulating))
                {
                    // The current value is as expected for the model.

                    // Set the value for the model:
                    modelToManipulate.data = config.valueToSet;
                }
            }
        }
    }


}
