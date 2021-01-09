package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.*;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link MemorySimulationHandler}.
 */
public class MemorySimulationHandlerTests extends MemorySimulationHandlerTestsBase
{
    @Test
    public void creationTest()
    {
        new MemorySimulationHandler();
    }

    @Test
    public void one_model_one_renaming_agent() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            // Create a model:
            MockModel model = new MockModel();
            model.name = "Model";
            controller.addModel(model);

            // Create the agent configuration:
            MockRenameAgentConfig agentConfig = new MockRenameAgentConfig();
            agentConfig.modelToRename = model.name;
            agentConfig.newModelName = "CHANGED MODEL";
            controller.addAgentConfig(agentConfig);

            //#endregion
        };

        // Make sure the model is as expected:
        //language=JSON
        String expectedInputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockModel\",\n" +
            "      \"name\" : \"Model\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockRenameAgentConfig\",\n" +
            "      \"modelToRename\" : \"Model\",\n" +
            "      \"newModelName\" : \"CHANGED MODEL\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        // Make sure that the output model is as expected:
        // Make sure the model is as expected:
        //language=JSON
        String expectedOutputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockModel\",\n" +
            "      \"name\" : \"CHANGED MODEL\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockRenameAgentConfig\",\n" +
            "      \"modelToRename\" : \"Model\",\n" +
            "      \"newModelName\" : \"CHANGED MODEL\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        assert_InputJSON_Simulation_OutputJSON(inputModelCreator, expectedInputJSON, expectedOutputJSON);
    }

    @Test
    public void two_models_one_renaming_agent() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            // Create models:
            MockModel model1 = new MockModel();
            model1.name = "Model 1";
            controller.addModel(model1);

            MockModel model2 = new MockModel();
            model2.name = "Model 2";
            controller.addModel(model2);

            // Create the agent configuration:
            MockRenameAgentConfig agentConfig = new MockRenameAgentConfig();
            agentConfig.modelToRename = model2.name;
            agentConfig.newModelName = "CHANGED MODEL";
            controller.addAgentConfig(agentConfig);

            //#endregion
        };

        // Make sure the model is as expected:
        //language=JSON
        String expectedInputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockModel\",\n" +
            "      \"name\" : \"Model 1\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockModel\",\n" +
            "      \"name\" : \"Model 2\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockRenameAgentConfig\",\n" +
            "      \"modelToRename\" : \"Model 2\",\n" +
            "      \"newModelName\" : \"CHANGED MODEL\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        // Make sure that the output model is as expected:
        // Make sure the model is as expected:
        //language=JSON
        String expectedOutputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockModel\",\n" +
            "      \"name\" : \"CHANGED MODEL\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockModel\",\n" +
            "      \"name\" : \"Model 1\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockRenameAgentConfig\",\n" +
            "      \"modelToRename\" : \"Model 2\",\n" +
            "      \"newModelName\" : \"CHANGED MODEL\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        assert_InputJSON_Simulation_OutputJSON(inputModelCreator, expectedInputJSON, expectedOutputJSON);
    }

    @Test
    public void two_models_two_renaming_agents() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            // Create models:
            MockModel model1 = new MockModel();
            model1.name = "Model 1";
            controller.addModel(model1);

            MockModel model2 = new MockModel();
            model2.name = "Model 2";
            controller.addModel(model2);

            // Create the agent configurations:
            MockRenameAgentConfig agentConfig1 = new MockRenameAgentConfig();
            agentConfig1.modelToRename = model1.name;
            agentConfig1.newModelName = "CHANGED MODEL 1";
            controller.addAgentConfig(agentConfig1);

            MockRenameAgentConfig agentConfig2 = new MockRenameAgentConfig();
            agentConfig2.modelToRename = model2.name;
            agentConfig2.newModelName = "CHANGED MODEL 2";
            controller.addAgentConfig(agentConfig2);

            //#endregion
        };

        // Make sure the model is as expected:
        //language=JSON
        String expectedInputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockModel\",\n" +
            "      \"name\" : \"Model 1\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockModel\",\n" +
            "      \"name\" : \"Model 2\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockRenameAgentConfig\",\n" +
            "      \"modelToRename\" : \"Model 1\",\n" +
            "      \"newModelName\" : \"CHANGED MODEL 1\",\n" +
            "      \"enabled\" : true\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockRenameAgentConfig\",\n" +
            "      \"modelToRename\" : \"Model 2\",\n" +
            "      \"newModelName\" : \"CHANGED MODEL 2\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        // Make sure that the output model is as expected:
        // Make sure the model is as expected:
        //language=JSON
        String expectedOutputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockModel\",\n" +
            "      \"name\" : \"CHANGED MODEL 1\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockModel\",\n" +
            "      \"name\" : \"CHANGED MODEL 2\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockRenameAgentConfig\",\n" +
            "      \"modelToRename\" : \"Model 1\",\n" +
            "      \"newModelName\" : \"CHANGED MODEL 1\",\n" +
            "      \"enabled\" : true\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockRenameAgentConfig\",\n" +
            "      \"modelToRename\" : \"Model 2\",\n" +
            "      \"newModelName\" : \"CHANGED MODEL 2\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        assert_InputJSON_Simulation_OutputJSON(inputModelCreator, expectedInputJSON, expectedOutputJSON);
    }

    @Test
    public void one_model_one_value_changer_agent() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            // Create a model:
            MockModel model = new MockModel();
            model.name = "Model";
            model.value = "Value";
            controller.addModel(model);

            // Create the agent configuration:
            MockValueChangerAgentConfig agentConfig = new MockValueChangerAgentConfig();
            agentConfig.modelValueToChange = model.value;
            agentConfig.newValue = "CHANGED Value";
            controller.addAgentConfig(agentConfig);

            //#endregion
        };

        // Make sure the model is as expected:
        //language=JSON
        String expectedInputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockModel\",\n" +
            "      \"name\" : \"Model\",\n" +
            "      \"value\" : \"Value\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockValueChangerAgentConfig\",\n" +
            "      \"modelValueToChange\" : \"Value\",\n" +
            "      \"newValue\" : \"CHANGED Value\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        // Make sure that the output model is as expected:
        // Make sure the model is as expected:
        //language=JSON
        String expectedOutputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockModel\",\n" +
            "      \"name\" : \"Model\",\n" +
            "      \"value\" : \"CHANGED Value\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockValueChangerAgentConfig\",\n" +
            "      \"modelValueToChange\" : \"Value\",\n" +
            "      \"newValue\" : \"CHANGED Value\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        assert_InputJSON_Simulation_OutputJSON(inputModelCreator, expectedInputJSON, expectedOutputJSON);
    }

    @Test
    public void one_model_two_value_changer_agents_two_solutions() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            // Create model:
            MockModel model = new MockModel();
            model.name = "Model";
            model.value = "Value";
            controller.addModel(model);

            // Create the agent configurations:
            MockValueChangerAgentConfig agentConfig1 = new MockValueChangerAgentConfig();
            agentConfig1.modelValueToChange = model.value;
            agentConfig1.newValue = "CHANGED VALUE 1";
            controller.addAgentConfig(agentConfig1);

            MockValueChangerAgentConfig agentConfig2 = new MockValueChangerAgentConfig();
            agentConfig2.modelValueToChange = model.value;
            agentConfig2.newValue = "CHANGED VALUE 2";
            controller.addAgentConfig(agentConfig2);

            //#endregion
        };

        // Make sure that the output model is as expected:
        // Make sure the model is as expected:
        //language=JSON
        String expectedOutputJSON =
            "[\n" +
            "  {\n" +
            "    \"solutionName\" : \"Solution 1\",\n" +
            "    \"environment\" : {\n" +
            "      \"models\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockModel\",\n" +
            "          \"name\" : \"Model\",\n" +
            "          \"value\" : \"CHANGED VALUE 2\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"agentConfigs\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockValueChangerAgentConfig\",\n" +
            "          \"modelValueToChange\" : \"Value\",\n" +
            "          \"newValue\" : \"CHANGED VALUE 1\",\n" +
            "          \"enabled\" : true\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockValueChangerAgentConfig\",\n" +
            "          \"modelValueToChange\" : \"Value\",\n" +
            "          \"newValue\" : \"CHANGED VALUE 2\",\n" +
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
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockModel\",\n" +
            "          \"name\" : \"Model\",\n" +
            "          \"value\" : \"CHANGED VALUE 1\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"agentConfigs\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockValueChangerAgentConfig\",\n" +
            "          \"modelValueToChange\" : \"Value\",\n" +
            "          \"newValue\" : \"CHANGED VALUE 1\",\n" +
            "          \"enabled\" : true\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests$MockValueChangerAgentConfig\",\n" +
            "          \"modelValueToChange\" : \"Value\",\n" +
            "          \"newValue\" : \"CHANGED VALUE 2\",\n" +
            "          \"enabled\" : true\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }\n" +
            "]";

        assert_Simulation_OutputJSONSolutions(inputModelCreator, expectedOutputJSON);
    }

    /**
     * A model used for testing.
     */
    public static class MockModel extends ModelBase
    {
        /**
         * The value for the model.
         */
        public String value;
    }

    /**
     * A config for a {@link MockRenameAgent} that renames models.
     */
    public static class MockRenameAgentConfig extends AgentConfigBase
    {
        /**
         * This is the name of the model to rename.
         */
        public String modelToRename;

        /**
         * This is the model name to use if a model matches {@link #modelToRename}.
         */
        public String newModelName;
    }

    /**
     * A mock agent for the tests that renames models.
     */
    public static class MockRenameAgent extends AgentBase<MockRenameAgentConfig>
    {
        @Override
        public void modifyEnvironment(ReadOnlyEnvironmentController input, EnvironmentController output, SimulationIterationAPI iteration, SimulationController simulation, MockRenameAgentConfig config) throws Exception
        {
            // Find the model with the given name:
            ModelAPI model = output.getModelByName(config.modelToRename);
            if (model != null)
            {
                // We found the model with the given name.

                // NOTE: Since the environment controller has internal indexes,
                //       we choose to remove and add the model so that the indexes are kept up to date.
                //       The alternative is to modify the model name and then re-index the whole environment model.
                // model.setName(config.newModelName);
                // outputModelControllerToModify.indexEnvironmentModel();

                // Remove the model from the output:
                output.removeModel(model);

                // Change the name of the model:
                model.setName(config.newModelName);

                // Add the model again:
                output.addModel(model);
            }
        }
    }

    /**
     * A config for a {@link MockRenameAgent} that changes model values.
     */
    public static class MockValueChangerAgentConfig extends AgentConfigBase
    {
        /**
         * This is the value to look for in the model to change.
         * If the model has this value then it is changed.
         */
        public String modelValueToChange;

        /**
         * This is the model value to change to if a model with {@link #modelValueToChange} is found.
         */
        public String newValue;
    }

    /**
     * A mock agent for the tests that changes model values.
     */
    public static class MockValueChangerAgent extends AgentBase<MockValueChangerAgentConfig>
    {
        @Override
        public void modifyEnvironment(ReadOnlyEnvironmentController input, EnvironmentController output, SimulationIterationAPI iteration, SimulationController simulation, MockValueChangerAgentConfig config) throws Exception
        {
            // Go through each mock model in the environment:
            output.forEachTypeOfModelExactly(
                MockModel.class,
                mockModel ->
                {
                    // Check whether the value is as expected:
                    if (config.modelValueToChange.equals(mockModel.value))
                    {
                        // We found a model with the given value.
                        // Change the model value:
                        mockModel.value = config.newValue;
                    }
                }
            );
        }
    }
}
