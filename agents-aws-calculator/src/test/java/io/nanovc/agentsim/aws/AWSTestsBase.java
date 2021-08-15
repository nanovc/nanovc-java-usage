package io.nanovc.agentsim.aws;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.nanovc.agentsim.EnvironmentController;
import io.nanovc.agentsim.ModelAPI;
import io.nanovc.agentsim.SimulationException;
import io.nanovc.agentsim.aws.organizations.OrganizationalUnit;
import io.nanovc.agentsim.simulations.memory.MemorySimulationConfig;
import io.nanovc.agentsim.simulations.memory.MemorySimulationHandlerTestsBase;
import io.nanovc.agentsim.simulations.memory.MemorySimulationModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Comparator;

/**
 * The base class for AWS tests.
 */
public class AWSTestsBase extends MemorySimulationHandlerTestsBase
{
    /**
     * The memory simulation configuration to use for AWS tests.
     */
    public MemorySimulationConfig memorySimulationConfig;

    @BeforeEach
    public void configureMemorySimulation()
    {
        this.memorySimulationConfig = new MemorySimulationConfig();
        this.memorySimulationConfig.serializationModules.add(JavaTimeModule.class);
    }

    @BeforeAll
    public static void configureJsonMapper()
    {
        // Register the Java Date and Time serialization module:
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);

        // Use mixins to provide annotations for types that we don't control directly:
        // https://github.com/FasterXML/jackson-docs/wiki/JacksonMixInAnnotations
        jsonMapper.addMixIn(AWSConceptBase.class, AWSConceptMixin.class);
        jsonMapper.addMixIn(OrganizationalUnit.class, OrganizationalUnitMixin.class);
    }

    @JsonPropertyOrder({"name"})
    public static abstract class AWSConceptMixin extends TypedMixin
    {
    }

    @JsonPropertyOrder({"organizationalUnitName"})
    public static abstract class OrganizationalUnitMixin extends TypedMixin
    {
    }

    /**
     * Runs the simulation for the test and makes sure that the inputs and outputs are as expected.
     * It uses the default AWS simulation configuration.
     *
     * @param modelCreator       The logic to create the specific input model for the simulation for this test.
     * @param expectedInputJSON  The JSON representation of the input model that we expect.
     * @param expectedOutputJSON The JSON representation of the output model that we expect after the simulation runs.
     * @return The simulation that was run so that additional assertions can be done.
     * @throws JsonProcessingException If the JSON could not be created for the environment model.
     * @throws SimulationException     If we had errors while running the simulation.
     */
    public MemorySimulationModel assert_AWS_InputJSON_Simulation_OutputJSON(ConsumerWithException<EnvironmentController> modelCreator, String expectedInputJSON, String expectedOutputJSON) throws Exception
    {
        return assert_MaskedInputJSON_Simulation_MaskedOutputJSON(
            modelCreator,
            (controller, model) ->
            {
                // Sort the models by name:
                model.models.sort(Comparator.comparing(ModelAPI::getName));
            },
            expectedInputJSON,
            (controller, model) ->
            {
                // Sort the models by name:
                model.models.sort(Comparator.comparing(ModelAPI::getName));
            },
            expectedOutputJSON,
            this.memorySimulationConfig
        );
    }

    /**
     * Runs the simulation for the test and makes sure that the inputs and outputs are as expected.
     * It uses the default AWS simulation configuration.
     *
     * @param modelCreator       The logic to create the specific input model for the simulation for this test.
     * @param expectedOutputJSON The JSON representation of the output model that we expect after the simulation runs.
     * @return The simulation that was run so that additional assertions can be done.
     * @throws JsonProcessingException If the JSON could not be created for the environment model.
     * @throws SimulationException     If we had errors while running the simulation.
     */
    public MemorySimulationModel assert_AWS_Simulation_OutputJSONSolutions(ConsumerWithException<EnvironmentController> modelCreator, String expectedOutputJSON) throws Exception
    {
        return assert_Simulation_OutputJSONSolutions(
            modelCreator,
            expectedOutputJSON,
            this.memorySimulationConfig
        );
    }
}
