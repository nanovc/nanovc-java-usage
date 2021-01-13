package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.*;
import org.junit.jupiter.api.Test;

import java.util.Objects;

/**
 * This tests that the simulation is run in such a way that each agent thinks that it is the first to run
 * in the simulation and then the changes are merged by the framework.
 * This is the ticket to being able to parallelize the agent processing.
 * <p>
 * Tests the {@link MemorySimulationHandler}.
 */
public class MemorySimulationHandlerTests_AgentThinksItsFirst extends MemorySimulationHandlerTestsBase
{
    @Test
    public void singleModelWithNoAgents() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            AgentModel dataModel = new AgentModel();
            dataModel.name = "data";
            dataModel.data = "abc 123";
            controller.addModel(dataModel);

            //#endregion
        };

        // Make sure that the output model is as expected:
        //#region Output JSON
        //language=JSON
        String expectedOutputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentModel\",\n" +
            "      \"name\" : \"data\",\n" +
            "      \"data\" : \"abc 123\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        //#endregion

        assert_Simulation_OutputJSON(
            inputModelCreator,
            expectedOutputJSON
        );
    }

    @Test
    public void singleAgentWithNoModel() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            AgentConfig agentConfig = new AgentConfig();
            agentConfig.modelNameToManipulate = "data";
            agentConfig.expectedValueBeforeManipulating = "abc 123";
            agentConfig.valueToSet = "value";
            controller.addAgentConfig(agentConfig);

            //#endregion
        };

        // Make sure that the output model is as expected:
        //#region Output JSON
        //language=JSON
        String expectedOutputJSON =
            "{\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "      \"modelNameToManipulate\" : \"data\",\n" +
            "      \"expectedValueBeforeManipulating\" : \"abc 123\",\n" +
            "      \"valueToSet\" : \"value\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        //#endregion

        assert_Simulation_OutputJSON(
            inputModelCreator,
            expectedOutputJSON
        );
    }

    @Test
    public void singleAgentWithOneModel() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            AgentConfig agentConfig = new AgentConfig();
            agentConfig.modelNameToManipulate = "data";
            agentConfig.expectedValueBeforeManipulating = "abc 123";
            agentConfig.valueToSet = "value";
            controller.addAgentConfig(agentConfig);

            AgentModel dataModel = new AgentModel();
            dataModel.name = "data";
            dataModel.data = "abc 123";
            controller.addModel(dataModel);

            //#endregion
        };

        // Make sure that the input model is as expected:
        //#region Input JSON
        //language=JSON
        String expectedInputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentModel\",\n" +
            "      \"name\" : \"data\",\n" +
            "      \"data\" : \"abc 123\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "      \"modelNameToManipulate\" : \"data\",\n" +
            "      \"expectedValueBeforeManipulating\" : \"abc 123\",\n" +
            "      \"valueToSet\" : \"value\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        //#endregion

        // Make sure that the output model is as expected:
        //#region Output JSON
        //language=JSON
        String expectedOutputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentModel\",\n" +
            "      \"name\" : \"data\",\n" +
            "      \"data\" : \"value\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "      \"modelNameToManipulate\" : \"data\",\n" +
            "      \"expectedValueBeforeManipulating\" : \"abc 123\",\n" +
            "      \"valueToSet\" : \"value\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        //#endregion

        assert_InputJSON_Simulation_OutputJSON(
            inputModelCreator,
            expectedInputJSON,
            expectedOutputJSON
        );
    }

    @Test
    public void twoAgentsWithTwoModels() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            AgentConfig agent1Config = new AgentConfig();
            agent1Config.modelNameToManipulate = "data 1";
            agent1Config.expectedValueBeforeManipulating = "abc 123";
            agent1Config.valueToSet = "value 1";
            controller.addAgentConfig(agent1Config);

            AgentModel data1Model = new AgentModel();
            data1Model.name = "data 1";
            data1Model.data = "abc 123";
            controller.addModel(data1Model);

            AgentConfig agent2Config = new AgentConfig();
            agent2Config.modelNameToManipulate = "data 2";
            agent2Config.expectedValueBeforeManipulating = "123 abc";
            agent2Config.valueToSet = "value 2";
            controller.addAgentConfig(agent2Config);

            AgentModel data2Model = new AgentModel();
            data2Model.name = "data 2";
            data2Model.data = "123 abc";
            controller.addModel(data2Model);

            //#endregion
        };

        // Make sure that the input model is as expected:
        //#region Input JSON
        //language=JSON
        String expectedInputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentModel\",\n" +
            "      \"name\" : \"data 1\",\n" +
            "      \"data\" : \"abc 123\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentModel\",\n" +
            "      \"name\" : \"data 2\",\n" +
            "      \"data\" : \"123 abc\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "      \"modelNameToManipulate\" : \"data 1\",\n" +
            "      \"expectedValueBeforeManipulating\" : \"abc 123\",\n" +
            "      \"valueToSet\" : \"value 1\",\n" +
            "      \"enabled\" : true\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "      \"modelNameToManipulate\" : \"data 2\",\n" +
            "      \"expectedValueBeforeManipulating\" : \"123 abc\",\n" +
            "      \"valueToSet\" : \"value 2\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        //#endregion

        // Make sure that the output model is as expected:
        //#region Output JSON
        //language=JSON
        String expectedOutputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentModel\",\n" +
            "      \"name\" : \"data 1\",\n" +
            "      \"data\" : \"value 1\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentModel\",\n" +
            "      \"name\" : \"data 2\",\n" +
            "      \"data\" : \"value 2\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "      \"modelNameToManipulate\" : \"data 1\",\n" +
            "      \"expectedValueBeforeManipulating\" : \"abc 123\",\n" +
            "      \"valueToSet\" : \"value 1\",\n" +
            "      \"enabled\" : true\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "      \"modelNameToManipulate\" : \"data 2\",\n" +
            "      \"expectedValueBeforeManipulating\" : \"123 abc\",\n" +
            "      \"valueToSet\" : \"value 2\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        //#endregion

        assert_InputJSON_Simulation_OutputJSON(
            inputModelCreator,
            expectedInputJSON,
            expectedOutputJSON
        );
    }

    @Test
    public void twoAgentsWithOneModel_FirstSolution() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            AgentConfig agent1Config = new AgentConfig();
            agent1Config.modelNameToManipulate = "data";
            agent1Config.expectedValueBeforeManipulating = "abc 123";
            agent1Config.valueToSet = "value 1";
            controller.addAgentConfig(agent1Config);

            AgentConfig agent2Config = new AgentConfig();
            agent2Config.modelNameToManipulate = "data";
            agent2Config.expectedValueBeforeManipulating = "abc 123";
            agent2Config.valueToSet = "value 2";
            controller.addAgentConfig(agent2Config);

            AgentModel dataModel = new AgentModel();
            dataModel.name = "data";
            dataModel.data = "abc 123";
            controller.addModel(dataModel);

            //#endregion
        };

        // Make sure that the input model is as expected:
        //#region Input JSON
        //language=JSON
        String expectedInputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentModel\",\n" +
            "      \"name\" : \"data\",\n" +
            "      \"data\" : \"abc 123\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "      \"modelNameToManipulate\" : \"data\",\n" +
            "      \"expectedValueBeforeManipulating\" : \"abc 123\",\n" +
            "      \"valueToSet\" : \"value 1\",\n" +
            "      \"enabled\" : true\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "      \"modelNameToManipulate\" : \"data\",\n" +
            "      \"expectedValueBeforeManipulating\" : \"abc 123\",\n" +
            "      \"valueToSet\" : \"value 2\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        //#endregion

        // Make sure that the output model is as expected:
        //#region Output JSON
        //language=JSON
        String expectedOutputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentModel\",\n" +
            "      \"name\" : \"data\",\n" +
            "      \"data\" : \"value 2\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "      \"modelNameToManipulate\" : \"data\",\n" +
            "      \"expectedValueBeforeManipulating\" : \"abc 123\",\n" +
            "      \"valueToSet\" : \"value 1\",\n" +
            "      \"enabled\" : true\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "      \"modelNameToManipulate\" : \"data\",\n" +
            "      \"expectedValueBeforeManipulating\" : \"abc 123\",\n" +
            "      \"valueToSet\" : \"value 2\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        //#endregion

        assert_InputJSON_Simulation_OutputJSON(
            inputModelCreator,
            expectedInputJSON,
            expectedOutputJSON
        );
    }

    @Test
    public void twoAgentsWithOneModel_TwoSolutions() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            AgentConfig agent1Config = new AgentConfig();
            agent1Config.modelNameToManipulate = "data";
            agent1Config.expectedValueBeforeManipulating = "abc 123";
            agent1Config.valueToSet = "value 1";
            controller.addAgentConfig(agent1Config);

            AgentConfig agent2Config = new AgentConfig();
            agent2Config.modelNameToManipulate = "data";
            agent2Config.expectedValueBeforeManipulating = "abc 123";
            agent2Config.valueToSet = "value 2";
            controller.addAgentConfig(agent2Config);

            AgentModel dataModel = new AgentModel();
            dataModel.name = "data";
            dataModel.data = "abc 123";
            controller.addModel(dataModel);

            //#endregion
        };

        // Make sure that the output model is as expected:
        //#region Output JSON
        //language=JSON
        String expectedOutputJSON =
            "[\n" +
            "  {\n" +
            "    \"solutionName\" : \"Solution 1\",\n" +
            "    \"environment\" : {\n" +
            "      \"models\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentModel\",\n" +
            "          \"name\" : \"data\",\n" +
            "          \"data\" : \"value 2\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"agentConfigs\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "          \"modelNameToManipulate\" : \"data\",\n" +
            "          \"expectedValueBeforeManipulating\" : \"abc 123\",\n" +
            "          \"valueToSet\" : \"value 1\",\n" +
            "          \"enabled\" : true\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "          \"modelNameToManipulate\" : \"data\",\n" +
            "          \"expectedValueBeforeManipulating\" : \"abc 123\",\n" +
            "          \"valueToSet\" : \"value 2\",\n" +
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
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentModel\",\n" +
            "          \"name\" : \"data\",\n" +
            "          \"data\" : \"value 1\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"agentConfigs\" : [\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "          \"modelNameToManipulate\" : \"data\",\n" +
            "          \"expectedValueBeforeManipulating\" : \"abc 123\",\n" +
            "          \"valueToSet\" : \"value 1\",\n" +
            "          \"enabled\" : true\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_AgentThinksItsFirst$AgentConfig\",\n" +
            "          \"modelNameToManipulate\" : \"data\",\n" +
            "          \"expectedValueBeforeManipulating\" : \"abc 123\",\n" +
            "          \"valueToSet\" : \"value 2\",\n" +
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
