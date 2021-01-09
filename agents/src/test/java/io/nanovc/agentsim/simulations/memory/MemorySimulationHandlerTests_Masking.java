package io.nanovc.agentsim.simulations.memory;

import io.nanovc.agentsim.EnvironmentController;
import io.nanovc.agentsim.EnvironmentModel;
import io.nanovc.agentsim.ModelBase;
import org.junit.jupiter.api.Test;

/**
 * This tests that we can mask inputs and outputs to make unit tests less fragile.
 *
 * Tests the {@link MemorySimulationHandler}.
 */
public class MemorySimulationHandlerTests_Masking extends MemorySimulationHandlerTestsBase
{
    @Test
    public void maskingOutputJSON() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            SpecificModelItem modelItem = new SpecificModelItem();
            modelItem.name = "specific item";
            modelItem.generalStuff = "Hello World!";
            modelItem.superSpecificPath = "c:\\Luke\\RoR\\";
            controller.addModel(modelItem);

            //#endregion
        };

        // Define the logic to mask the output model:
        BiConsumerWithException<EnvironmentController, EnvironmentModel> outputMasker = (controller, model) ->
        {
            //#region Output Model Masking

            // Get the specific item that we want to mask:
            SpecificModelItem specificModelItem = controller.getModelByName("specific item");

            // Mask the transient data:
            specificModelItem.superSpecificPath = "XXX";

            //#endregion
        };

        // Make sure that the output model is as expected:
        //#region Output JSON
        //language=JSON
        String expectedOutputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_Masking$SpecificModelItem\",\n" +
            "      \"name\" : \"specific item\",\n" +
            "      \"generalStuff\" : \"Hello World!\",\n" +
            "      \"superSpecificPath\" : \"XXX\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        //#endregion

        assert_Simulation_MaskedOutputJSON(
            inputModelCreator,
            outputMasker,
            expectedOutputJSON
        );
    }

    @Test
    public void maskingInputJSONAndOutputJSON() throws Exception
    {
        // Define the input model using code:
        ConsumerWithException<EnvironmentController> inputModelCreator = controller ->
        {
            //#region Input Model

            SpecificModelItem modelItem = new SpecificModelItem();
            modelItem.name = "specific item";
            modelItem.generalStuff = "Hello World!";
            modelItem.superSpecificPath = "c:\\Luke\\RoR\\";
            controller.addModel(modelItem);

            //#endregion
        };

        // Define the logic to mask the input model:
        BiConsumerWithException<EnvironmentController, EnvironmentModel> inputMasker = (controller, model) ->
        {
            //#region Input Model Masking

            // Get the specific item that we want to mask:
            SpecificModelItem specificModelItem = controller.getModelByName("specific item");

            // Mask the transient data:
            specificModelItem.superSpecificPath = "XXX";

            //#endregion
        };

        // Make sure the model is as expected:
        //#region Input JSON
        //language=JSON
        String expectedInputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_Masking$SpecificModelItem\",\n" +
            "      \"name\" : \"specific item\",\n" +
            "      \"generalStuff\" : \"Hello World!\",\n" +
            "      \"superSpecificPath\" : \"XXX\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        //#endregion

        // Define the logic to mask the output model:
        BiConsumerWithException<EnvironmentController, EnvironmentModel> outputMasker = (controller, model) ->
        {
            //#region Output Model Masking

            // Get the specific item that we want to mask:
            SpecificModelItem specificModelItem = controller.getModelByName("specific item");

            // Mask the transient data:
            specificModelItem.superSpecificPath = "YYY";

            //#endregion
        };

        // Make sure that the output model is as expected:
        //#region Output JSON
        //language=JSON
        String expectedOutputJSON =
            "{\n" +
            "  \"models\" : [\n" +
            "    {\n" +
            "      \"type\" : \"io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTests_Masking$SpecificModelItem\",\n" +
            "      \"name\" : \"specific item\",\n" +
            "      \"generalStuff\" : \"Hello World!\",\n" +
            "      \"superSpecificPath\" : \"YYY\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        //#endregion

        assert_MaskedInputJSON_Simulation_MaskedOutputJSON(
            inputModelCreator,
            inputMasker,
            expectedInputJSON,
            outputMasker,
            expectedOutputJSON
        );
    }

    public static class SpecificModelItem extends ModelBase
    {
        public String generalStuff;
        public String superSpecificPath;
    }

}
