package io.nanovc.agentsim.simulations.memory;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nanovc.agentsim.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The base class for {@link MemorySimulationHandlerAPI}'s.
 */
public class MemorySimulationHandlerTestsBase extends SimulationHandlerTestsBase
{

    /**
     * Runs the simulation for the test and makes sure that the inputs and outputs are as expected.
     *
     * @param modelCreator       The logic to create the specific input model for the simulation for this test.
     * @param expectedInputJSON  The JSON representation of the input model that we expect.
     * @param expectedOutputJSON The JSON representation of the output model that we expect after the simulation runs.
     * @return The simulation that was run so that additional assertions can be done.
     * @throws JsonProcessingException If the JSON could not be created for the environment model.
     * @throws SimulationException     If we had errors while running the simulation.
     */
    public MemorySimulationModel assert_InputJSON_Simulation_OutputJSON(ConsumerWithException<EnvironmentController> modelCreator, String expectedInputJSON, String expectedOutputJSON) throws Exception
    {
        return assert_InputJSON_Simulation_OutputJSON(modelCreator, expectedInputJSON, expectedOutputJSON, null);
    }

    /**
     * Runs the simulation for the test and makes sure that the inputs and outputs are as expected.
     *
     * @param modelCreator       The logic to create the specific input model for the simulation for this test.
     * @param expectedInputJSON  The JSON representation of the input model that we expect.
     * @param expectedOutputJSON The JSON representation of the output model that we expect after the simulation runs.
     * @param simulationConfig   The simulation configuration to use. Null to use the default.
     * @return The simulation that was run so that additional assertions can be done.
     * @throws JsonProcessingException If the JSON could not be created for the environment model.
     * @throws SimulationException     If we had errors while running the simulation.
     */
    public MemorySimulationModel assert_InputJSON_Simulation_OutputJSON(ConsumerWithException<EnvironmentController> modelCreator, String expectedInputJSON, String expectedOutputJSON, MemorySimulationConfig simulationConfig) throws Exception
    {
        return assert_Inputs_Simulation_Outputs(
            modelCreator,
            false,
            (controller, inputEnvironmentModel) ->
            {
                // Make sure the input model is as expected:
                assertEquals(expectedInputJSON, getJSON(inputEnvironmentModel), "INPUT Model Error!");
            },
            false,
            (controller, outputEnvironmentModel) ->
            {
                // Make sure the output model is as expected:
                assertEquals(expectedOutputJSON, getJSON(outputEnvironmentModel), "OUTPUT Model Error!");
            },
            simulationConfig
        );
    }

    /**
     * Runs the simulation for the test and makes sure that the inputs and outputs are as expected.
     *
     * @param modelCreator       The logic to create the specific input model for the simulation for this test.
     * @param inputMasker        The logic to mask the input before asserting the JSON. This is useful for masking fields that are known to change.
     * @param expectedInputJSON  The JSON representation of the input model that we expect.
     * @param outputMasker       The logic to mask the output before asserting the JSON. This is useful for masking fields that are known to change.
     * @param expectedOutputJSON The JSON representation of the output model that we expect after the simulation runs.
     * @return The simulation that was run so that additional assertions can be done.
     * @throws JsonProcessingException If the JSON could not be created for the environment model.
     * @throws SimulationException     If we had errors while running the simulation.
     */
    public MemorySimulationModel assert_MaskedInputJSON_Simulation_MaskedOutputJSON(ConsumerWithException<EnvironmentController> modelCreator, BiConsumerWithException<EnvironmentController, EnvironmentModel> inputMasker, String expectedInputJSON, BiConsumerWithException<EnvironmentController, EnvironmentModel> outputMasker, String expectedOutputJSON) throws Exception
    {
        return assert_MaskedInputJSON_Simulation_MaskedOutputJSON(modelCreator, inputMasker, expectedInputJSON, outputMasker, expectedOutputJSON, null);
    }

    /**
     * Runs the simulation for the test and makes sure that the inputs and outputs are as expected.
     *
     * @param modelCreator       The logic to create the specific input model for the simulation for this test.
     * @param inputMasker        The logic to mask the input before asserting the JSON. This is useful for masking fields that are known to change.
     * @param expectedInputJSON  The JSON representation of the input model that we expect.
     * @param outputMasker       The logic to mask the output before asserting the JSON. This is useful for masking fields that are known to change.
     * @param expectedOutputJSON The JSON representation of the output model that we expect after the simulation runs.
     * @param simulationConfig   The simulation configuration to use. Null to use the default.
     * @return The simulation that was run so that additional assertions can be done.
     * @throws JsonProcessingException If the JSON could not be created for the environment model.
     * @throws SimulationException     If we had errors while running the simulation.
     */
    public MemorySimulationModel assert_MaskedInputJSON_Simulation_MaskedOutputJSON(ConsumerWithException<EnvironmentController> modelCreator, BiConsumerWithException<EnvironmentController, EnvironmentModel> inputMasker, String expectedInputJSON, BiConsumerWithException<EnvironmentController, EnvironmentModel> outputMasker, String expectedOutputJSON, MemorySimulationConfig simulationConfig) throws Exception
    {
        return assert_Inputs_Simulation_Outputs(
            modelCreator,
            true,
            (controller, inputEnvironmentModel) ->
            {
                // Mask out any details in the output that might change:
                if (inputMasker != null) inputMasker.accept(controller, inputEnvironmentModel);

                // Make sure the input model is as expected:
                assertEquals(expectedInputJSON, getJSON(inputEnvironmentModel), "INPUT Model Error!");
            },
            true,
            (controller, outputEnvironmentModel) ->
            {
                // Mask out any details in the output that might change:
                if (outputMasker != null) outputMasker.accept(controller, outputEnvironmentModel);

                // Make sure the output model is as expected:
                assertEquals(expectedOutputJSON, getJSON(outputEnvironmentModel), "OUTPUT Model Error!");
            },
            simulationConfig
        );
    }

    /**
     * Runs the simulation for the test and makes sure that the outputs are as expected.
     *
     * @param modelCreator       The logic to create the specific input model for the simulation for this test.
     * @param expectedOutputJSON The JSON representation of the output model that we expect after the simulation runs.
     * @return The simulation that was run so that additional assertions can be done.
     * @throws JsonProcessingException If the JSON could not be created for the environment model.
     * @throws SimulationException     If we had errors while running the simulation.
     */
    public MemorySimulationModel assert_Simulation_OutputJSON(ConsumerWithException<EnvironmentController> modelCreator, String expectedOutputJSON) throws Exception
    {
        return assert_Inputs_Simulation_Outputs(
            modelCreator,
            false,
            null,
            false,
            (controller, outputEnvironmentModel) ->
            {
                // Make sure the output model is as expected:
                assertEquals(expectedOutputJSON, getJSON(outputEnvironmentModel), "OUTPUT Model Error!");
            },
            null
        );
    }

    /**
     * Runs the simulation for the test and makes sure that the outputs are as expected.
     *
     * @param modelCreator                The logic to create the specific input model for the simulation for this test.
     * @param expectedOutputJSONSolutions The JSON representation of the output solutions that we expect after the simulation runs. This is the JSON value for the array of solutions that we expect from the simulation.
     * @return The simulation that was run so that additional assertions can be done.
     * @throws JsonProcessingException If the JSON could not be created for the environment model.
     * @throws SimulationException     If we had errors while running the simulation.
     */
    public MemorySimulationModel assert_Simulation_OutputJSONSolutions(ConsumerWithException<EnvironmentController> modelCreator, String expectedOutputJSONSolutions) throws Exception
    {
        return assert_Simulation_OutputJSONSolutions(modelCreator, expectedOutputJSONSolutions, null);
    }

    /**
     * Runs the simulation for the test and makes sure that the outputs are as expected.
     *
     * @param modelCreator                The logic to create the specific input model for the simulation for this test.
     * @param expectedOutputJSONSolutions The JSON representation of the output solutions that we expect after the simulation runs. This is the JSON value for the array of solutions that we expect from the simulation.
     * @param simulationConfig            The simulation configuration to use. Null to use the default.
     * @return The simulation that was run so that additional assertions can be done.
     * @throws JsonProcessingException If the JSON could not be created for the environment model.
     * @throws SimulationException     If we had errors while running the simulation.
     */
    public MemorySimulationModel assert_Simulation_OutputJSONSolutions(ConsumerWithException<EnvironmentController> modelCreator, String expectedOutputJSONSolutions, MemorySimulationConfig simulationConfig) throws Exception
    {
        // Run the simulation:
        MemorySimulationModel simulation = assert_Inputs_Simulation_Outputs(
            modelCreator,
            false,
            null,
            false,
            (controller, outputEnvironmentModel) ->
            {

            },
            simulationConfig
        );

        // Map the solutions so that we only see the output models:
        List<SolutionWrapper> solutionModels = simulation.getSolutions().stream()
            .sorted(Comparator.comparingInt(MemorySimulationSolutionBase::getSolutionIndex))
            .map(solution -> {
                // Create the wrapper for the solution:
                SolutionWrapper solutionWrapper = new SolutionWrapper();
                solutionWrapper.solutionName = solution.getSolutionName();
                solutionWrapper.environment = solution.getOutputEnvironmentModel();
                return solutionWrapper;
            })
            .collect(Collectors.toList());

        // Get the JSON representation of all the solutions:
        String json = getJSON(solutionModels);

        // Make sure the output model is as expected:
        assertEquals(expectedOutputJSONSolutions, json, "OUTPUT SOLUTION Model Error!");

        return simulation;
    }

    /**
     * A wrapper to use when we have multiple solutions that we are converting to JSON.
     */
    public static class SolutionWrapper
    {
        /**
         * The name of the solution.
         */
        public String solutionName;

        /**
         * The environment model for this solution.
         */
        public EnvironmentModel environment;
    }

    /**
     * Runs the simulation for the test and makes sure that the outputs are as expected.
     *
     * @param modelCreator       The logic to create the specific input model for the simulation for this test.
     * @param outputMasker       The logic to mask the output before asserting the JSON. This is useful for masking fields that are known to change.
     * @param expectedOutputJSON The JSON representation of the output model that we expect after the simulation runs.
     * @return The simulation that was run so that additional assertions can be done.
     * @throws JsonProcessingException If the JSON could not be created for the environment model.
     * @throws SimulationException     If we had errors while running the simulation.
     */
    public MemorySimulationModel assert_Simulation_MaskedOutputJSON(ConsumerWithException<EnvironmentController> modelCreator, BiConsumerWithException<EnvironmentController, EnvironmentModel> outputMasker, String expectedOutputJSON) throws Exception
    {
        return assert_Simulation_MaskedOutputJSON(modelCreator, outputMasker, expectedOutputJSON, null);
    }

    /**
     * Runs the simulation for the test and makes sure that the outputs are as expected.
     *
     * @param modelCreator       The logic to create the specific input model for the simulation for this test.
     * @param outputMasker       The logic to mask the output before asserting the JSON. This is useful for masking fields that are known to change.
     * @param expectedOutputJSON The JSON representation of the output model that we expect after the simulation runs.
     * @param simulationConfig   The simulation config to use. Null to use the default.
     * @return The simulation that was run so that additional assertions can be done.
     * @throws JsonProcessingException If the JSON could not be created for the environment model.
     * @throws SimulationException     If we had errors while running the simulation.
     */
    public MemorySimulationModel assert_Simulation_MaskedOutputJSON(ConsumerWithException<EnvironmentController> modelCreator, BiConsumerWithException<EnvironmentController, EnvironmentModel> outputMasker, String expectedOutputJSON, MemorySimulationConfig simulationConfig) throws Exception
    {
        return assert_Inputs_Simulation_Outputs(
            modelCreator,
            false,
            null,
            true,
            (controller, outputEnvironmentModel) ->
            {
                // Mask out any details in the output that might change:
                if (outputMasker != null) outputMasker.accept(controller, outputEnvironmentModel);

                // Make sure the output model is as expected:
                assertEquals(expectedOutputJSON, getJSON(outputEnvironmentModel), "OUTPUT Model Error!");
            },
            simulationConfig
        );
    }

    /**
     * Runs the simulation for the test and makes sure that it is as expected.
     *
     * @param modelCreator     The logic to create the specific input model for the simulation for this test.
     * @param cloneInputModel  Cloning is useful for masking and other changes before asserting. True to clone the input model before passing it to the input asserter. False to use the actual model used for the simulation.
     * @param inputAsserter    The logic to assert the input. If this is null then this step is skipped.
     * @param cloneOutputModel Cloning is useful for masking and other changes before asserting. True to clone the output model before passing it to the output asserter. False to use the actual model used from the simulation.
     * @param outputAsserter   The logic to assert the output. If this is null then this step is skipped.
     * @param simulationConfig The simulation configuration to use. Null to use the default.
     * @return The simulation that was run so that additional assertions can be done.
     * @throws JsonProcessingException If the JSON could not be created for the environment model.
     * @throws SimulationException     If we had errors while running the simulation.
     */
    public MemorySimulationModel assert_Inputs_Simulation_Outputs(
        ConsumerWithException<EnvironmentController> modelCreator,
        boolean cloneInputModel,
        BiConsumerWithException<EnvironmentController, EnvironmentModel> inputAsserter,
        boolean cloneOutputModel,
        BiConsumerWithException<EnvironmentController, EnvironmentModel> outputAsserter,
        MemorySimulationConfig simulationConfig
    ) throws Exception
    {
        // Create the controller that we are testing:
        EnvironmentController inputController = new EnvironmentController(new EnvironmentModel());

        // Create the model:
        modelCreator.accept(inputController);

        // Make sure that we have a simulation config:
        if (simulationConfig == null) simulationConfig = new MemorySimulationConfig();

        // Create the simulation handler that we will be testing:
        MemorySimulationHandler simulationHandler = new MemorySimulationHandler();

        // Get the input model:
        EnvironmentModel inputEnvironmentModel = inputController.getEnvironmentModel();

        // Check whether we need to assert the input:
        if (inputAsserter != null)
        {
            // We want to assert the input.

            // Clone the input model if necessary (useful for masking and other changes before asserting):
            EnvironmentModel inputEnvironmentModelToUse;
            if (cloneInputModel)
            {
                // We must clone the input model before asserting.

                // Clone the model according to the algorithm used for simulation:
                EnvironmentModel clonedInputEnvironmentModel = simulationHandler.cloneEnvironmentModel(simulationConfig, inputEnvironmentModel);

                // Use the cloned model for the assertion:
                inputEnvironmentModelToUse = clonedInputEnvironmentModel;
            }
            else
            {
                // We must use the actual input model to the simulation.
                inputEnvironmentModelToUse = inputEnvironmentModel;
            }

            // Create a controller for the input environment model:
            EnvironmentController inputControllerToUse = new EnvironmentController(inputEnvironmentModelToUse);

            // Make sure the model is as expected:
            inputAsserter.accept(inputControllerToUse, inputEnvironmentModelToUse);
        }
        // Now we know that the input is as expected.

        // Run the agent simulation:
        // NOTE: We send the original input model to the simulation (not the potentially modified one that was used for assertion).
        MemorySimulationModel simulation = simulationHandler.runSimulation(simulationConfig, inputEnvironmentModel);

        // Check that the simulation ran as expected:
        assertSame(inputEnvironmentModel, simulation.getInputEnvironmentModel());

        // Get the solution with the most iterations:
        Comparator<MemorySimulationSolution> solutionComparator = Comparator.comparing(s -> s.getIterations().size());
        solutionComparator = solutionComparator.reversed();

        // Get the solution:
        MemorySimulationSolution solution = simulation.getSolutions().stream().sorted(solutionComparator).findFirst().get();

        assertNotSame(inputEnvironmentModel, solution.getOutputEnvironmentModel());
        assertEquals(0, solution.getSimulationErrors().size());

        // Check each iteration of the simulation:
        List<MemorySimulationIteration> iterations = solution.getIterations();
        assertNotNull(iterations);
        assertTrue(iterations.size() > 0);


        // Get the output environment model:
        EnvironmentModel outputEnvironmentModel = solution.getOutputEnvironmentModel();

        // Check whether we need to assert the output:
        if (outputAsserter != null)
        {
            // We want to assert the output.

            // Clone the output model if necessary (useful for masking and other changes before asserting):
            EnvironmentModel outputEnvironmentModelToUse;
            if (cloneOutputModel)
            {
                // We must clone the output model before asserting.

                // Clone the model according to the algorithm used for simulation:
                EnvironmentModel clonedOutputEnvironmentModel = simulationHandler.cloneEnvironmentModel(simulationConfig, outputEnvironmentModel);

                // Use the cloned model for the assertion:
                outputEnvironmentModelToUse = clonedOutputEnvironmentModel;
            }
            else
            {
                // We must use the actual output model to the simulation.
                outputEnvironmentModelToUse = outputEnvironmentModel;
            }

            // Create a controller for the output environment model:
            EnvironmentController outputControllerToUse = new EnvironmentController(outputEnvironmentModelToUse);

            // Make sure the model is as expected:
            outputAsserter.accept(outputControllerToUse, outputEnvironmentModelToUse);
        }
        // Now we know that the input is as expected.

        return simulation;
    }

    /**
     * A lambda for a method that takes two arguments and returns no outputs but may throw an exception.
     *
     * @param <T> The type of the first argument.
     * @param <U> The type of the second argument.
     */
    @FunctionalInterface
    public interface BiConsumerWithException<T, U>
    {
        /**
         * Performs this operation on the given arguments.
         *
         * @param t the first input argument
         * @param u the second input argument
         */
        void accept(T t, U u) throws Exception;
    }

    /**
     * A lambda for a method that takes two arguments and returns no outputs but may throw an exception.
     *
     * @param <T> The type of the first argument.
     */
    @FunctionalInterface
    public interface ConsumerWithException<T>
    {
        /**
         * Performs this operation on the given arguments.
         *
         * @param t the first input argument
         */
        void accept(T t) throws Exception;
    }

}
