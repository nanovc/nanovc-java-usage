package io.nanovc.agentsim;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Tests the {@link EnvironmentController}.
 */
class EnvironmentControllerTests extends EnvironmentControllerTestsBase
{
    @Test
    public void creationTest()
    {
        new EnvironmentController();
        new EnvironmentController(new EnvironmentModel());
    }

    @Test
    public void environmentWithOneModel() throws JsonProcessingException
    {
        // Create the controller that we are testing:
        EnvironmentController controller = new EnvironmentController();

        // Start a new model:
        EnvironmentModel environmentModel = new EnvironmentModel();

        // Attach the environment model to the controller so that we can manipulate it:
        controller.setEnvironmentModel(environmentModel);

        // Re-index the environment model:
        controller.indexEnvironmentModel();

        // Make sure there are no indexing errors:
        assertEquals(0, controller.getIndexingErrors().size());

        // Create a model to add to the environment:
        MockModel model = new MockModel();
        model.name = "A";

        // Add a model:
        controller.addModel(model);

        // Make sure there are no indexing errors:
        assertEquals(0, controller.getIndexingErrors().size());

        // Make sure the model is as expected:
        //language=JSON
        String expectedJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.EnvironmentControllerTests$MockModel\",\n" +
            "      \"name\" : \"A\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(expectedJSON, getJSON(controller.getEnvironmentModel()));

        // Make sure the indexing errors are as expected:
        expectedJSON =
            "[ ]";
        assertEquals(expectedJSON, getJSON(controller.getIndexingErrors()));
    }

    @Test
    public void twoModelsWithTheSameNameCauseIndexingError() throws JsonProcessingException
    {
        // Create the controller that we are testing:
        EnvironmentController controller = new EnvironmentController(new EnvironmentModel());

        // Create the first item:
        MockModel model1 = new MockModel();
        model1.name = "Model";
        controller.addModel(model1);

        // Create the second item:
        MockModel model2 = new MockModel();
        model2.name = "Model";
        controller.addModel(model2);

        // Make sure the model is as expected:
        //language=JSON
        String expectedJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.EnvironmentControllerTests$MockModel\",\n" +
            "      \"name\" : \"Model\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.EnvironmentControllerTests$MockModel\",\n" +
            "      \"name\" : \"Model\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(expectedJSON, getJSON(controller.getEnvironmentModel()));

        // Make sure the indexing errors are as expected:
        expectedJSON =
            "[\n" +
            "  {\n" +
            "    \"validationName\" : \"Duplicate model name\",\n" +
            "    \"message\" : \"A model called 'Model' already exists. The first occurrence used and all the rest are ignored in the environment model when searching by name. Give each model a distinct name.\"\n" +
            "  }\n" +
            "]";
        assertEquals(expectedJSON, getJSON(controller.getIndexingErrors()));
    }

    @Test
    public void environmentWithOneAgentConfig() throws JsonProcessingException
    {
        // Create the controller that we are testing:
        EnvironmentController controller = new EnvironmentController(new EnvironmentModel());

        // Create the agent configuration:
        MockAgentConfig mockAgentConfig = new MockAgentConfig();
        controller.addAgentConfig(mockAgentConfig);

        // Make sure the model is as expected:
        //language=JSON
        String expectedJSON =
            "{\n" +
            "  \"agentConfigs\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.EnvironmentControllerTests$MockAgentConfig\",\n" +
            "      \"enabled\" : true\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(expectedJSON, getJSON(controller.getEnvironmentModel()));

        // Make sure the indexing errors are as expected:
        expectedJSON =
            "[ ]";
        assertEquals(expectedJSON, getJSON(controller.getIndexingErrors()));
    }

    @Test
    public void goThroughModels() throws JsonProcessingException
    {
        // Create the controller that we are testing:
        EnvironmentController controller = new EnvironmentController(new EnvironmentModel());

        // Create some models for this test:
        controller.addModel(new MockModel());

        // Make sure the model is as expected:
        //language=JSON
        String expectedJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.EnvironmentControllerTests$MockModel\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(expectedJSON, getJSON(controller.getEnvironmentModel()));

        // Make sure that it's easy to iterate through all the models:
        List<ModelAPI> itemsFromIteration = new ArrayList<>();
        controller.forEachModel(model -> {
            itemsFromIteration.add(model);
        });

        // Make sure that all the models were iterated through:
        assertIterableEquals(controller.getEnvironmentModel().models, itemsFromIteration);
    }

    /**
     * A model used for testing.
     */
    public static class MockModel extends ModelBase
    {
    }

    /**
     * A config for a {@link MockAgent}.
     */
    public static class MockAgentConfig extends AgentConfigBase
    {
    }

    /**
     * A mock agent for the tests.
     */
    public static class MockAgent extends AgentBase<MockAgentConfig>
    {
        @Override
        public void modifyEnvironment(ReadOnlyEnvironmentController input, EnvironmentController output, SimulationIterationAPI iteration, SimulationController simulation, MockAgentConfig mockAgentConfig) throws Exception
        {
        }
    }

}
