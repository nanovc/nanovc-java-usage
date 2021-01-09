package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.*;
import org.junit.jupiter.api.Test;

import java.util.Objects;

/**
 * This tests that agents that make clashing changes to models distinguish between model names instead of model index.
 * <p>
 * Tests the {@link MemorySimulationHandler}.
 */
public class MemorySimulationHandlerTests_RepoWithModelNames extends MemorySimulationHandlerTestsBase
{
    @Test
    public void twoAgentsInterfering() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            // We have two models with different names.
            // The first agent changes the first models value.
            // The second agent re-orders the second model to the beginning.
            // Incorrectly, the simulation looses the change that was made to the first model because it's index in the model items list was 0.

            AgentModel dataAModel = new AgentModel();
            dataAModel.name = "data A";
            dataAModel.data = "value A";
            dataAModel.expectedIndex = 0;
            controller.addModel(dataAModel);

            AgentModel dataBModel = new AgentModel();
            dataBModel.name = "data B";
            dataBModel.data = "value B";
            dataBModel.expectedIndex = 1;
            controller.addModel(dataBModel);

            AgentConfig agent1Config = new AgentConfig();
            agent1Config.modelNameToManipulate = "data A";
            agent1Config.expectedValueBeforeManipulating = "value A";
            agent1Config.valueToSet = "value A CHANGED";
            controller.addAgentConfig(agent1Config);

            AgentConfig agent2Config = new AgentConfig();
            agent2Config.modelToReOrder = "data B";
            controller.addAgentConfig(agent2Config);

            //#endregion
        };

        // Make sure that the input model is as expected:
        //#region Input JSON
        //language=JSON
        String expectedInputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_RepoWithModelNames$AgentModel\",\n" +
            "      \"name\" : \"data A\",\n" +
            "      \"data\" : \"value A\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_RepoWithModelNames$AgentModel\",\n" +
            "      \"name\" : \"data B\",\n" +
            "      \"data\" : \"value B\",\n" +
            "      \"expectedIndex\" : 1\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_RepoWithModelNames$AgentConfig\",\n" +
            "      \"modelNameToManipulate\" : \"data A\",\n" +
            "      \"expectedValueBeforeManipulating\" : \"value A\",\n" +
            "      \"valueToSet\" : \"value A CHANGED\",\n" +
            "      \"enabled\" : true\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_RepoWithModelNames$AgentConfig\",\n" +
            "      \"modelToReOrder\" : \"data B\",\n" +
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
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_RepoWithModelNames$AgentModel\",\n" +
            "      \"name\" : \"data A\",\n" +
            "      \"data\" : \"value A CHANGED\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_RepoWithModelNames$AgentModel\",\n" +
            "      \"name\" : \"data B\",\n" +
            "      \"data\" : \"value B\",\n" +
            "      \"expectedIndex\" : 1\n" +
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
    public void twoAgentsWithoutNamesInterfering() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            // We have two models with the same blank name.
            // Since the agents can't find any of the models, nothing should change.

            AgentModel dataAModel = new AgentModel();
            // dataAModel.name = "data A";
            dataAModel.data = "value A";
            dataAModel.expectedIndex = 0;
            controller.addModel(dataAModel);

            AgentModel dataBModel = new AgentModel();
            // dataBModel.name = "data B";
            dataBModel.data = "value B";
            dataBModel.expectedIndex = 1;
            controller.addModel(dataBModel);

            AgentConfig agent1Config = new AgentConfig();
            agent1Config.modelNameToManipulate = "data A";
            agent1Config.expectedValueBeforeManipulating = "value A";
            agent1Config.valueToSet = "value A CHANGED";
            controller.addAgentConfig(agent1Config);

            AgentConfig agent2Config = new AgentConfig();
            agent2Config.modelToReOrder = "data B";
            controller.addAgentConfig(agent2Config);

            //#endregion
        };

        // Make sure that the input model is as expected:
        //#region Input JSON
        //language=JSON
        String expectedInputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_RepoWithModelNames$AgentModel\",\n" +
            "      \"data\" : \"value A\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_RepoWithModelNames$AgentModel\",\n" +
            "      \"data\" : \"value B\",\n" +
            "      \"expectedIndex\" : 1\n" +
            "    }\n" +
            "  ],\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_RepoWithModelNames$AgentConfig\",\n" +
            "      \"modelNameToManipulate\" : \"data A\",\n" +
            "      \"expectedValueBeforeManipulating\" : \"value A\",\n" +
            "      \"valueToSet\" : \"value A CHANGED\",\n" +
            "      \"enabled\" : true\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_RepoWithModelNames$AgentConfig\",\n" +
            "      \"modelToReOrder\" : \"data B\",\n" +
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
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_RepoWithModelNames$AgentModel\",\n" +
            "      \"data\" : \"value A\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_RepoWithModelNames$AgentModel\",\n" +
            "      \"data\" : \"value B\",\n" +
            "      \"expectedIndex\" : 1\n" +
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


    public static class AgentModel extends ModelBase
    {
        public String data;

        /**
         * This captures the expected index in the model list that we believe the model is in.
         */
        public int expectedIndex;
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

        /**
         * The name of the model item to reorder to the beginning of the models.
         * This is needed to replicate the bug with MKT-608.
         * If this is null then it does not reorder anything.
         */
        public String modelToReOrder;
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
            // Check whether we need to re-order any models to demonstrate the bug in MKT-608:
            if (config.modelToReOrder != null)
            {
                // We need to re-order a model.
                // Get the model that we need to re-order:
                ModelAPI itemToReOrder = output.getModelByName(config.modelToReOrder);
                if (itemToReOrder != null)
                {
                    // We found the item that we want to re-order.

                    // Re-Order the item:
                    ModelCollection items = output.getEnvironmentModel().models;
                    items.remove(itemToReOrder);
                    items.add(0, itemToReOrder);
                }
            }
            // Now we have deleted any models that we need to.

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

            // Drop a bomb to delete all agents so we don't run again:
            output.getEnvironmentModel().agentConfigs.clear();
        }
    }


}
