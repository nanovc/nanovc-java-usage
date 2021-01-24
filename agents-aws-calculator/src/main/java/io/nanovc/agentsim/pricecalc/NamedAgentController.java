package io.nanovc.agentsim.pricecalc;

import io.nanovc.agentsim.EnvironmentController;
import io.nanovc.agentsim.EnvironmentModel;
import io.nanovc.agentsim.ModelAPI;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This is used for interacting with all the {@link NamedAgentConfigAPI named agents} in an {@link io.nanovc.agentsim.EnvironmentController environment}.
 */
public class NamedAgentController
{
    public final EnvironmentController environmentController;

    public final Map<String, NamedAgentConfigAPI> namedAgentConfigs = new LinkedHashMap<>();

    public NamedAgentController(EnvironmentController controller)
    {
        environmentController = controller;
    }

    /**
     * This indexes the named agents that exist in the {@link EnvironmentModel environment}.
     */
    public void indexNamedAgents()
    {
        // Clear the existing map of named agents:
        this.namedAgentConfigs.clear();

        // Index each of the agent configs that have a name:
        this.environmentController.forEachTypeOfAgentConfigOrSubClass(
            NamedAgentConfigAPI.class,
            namedAgentConfig ->
            {
                // Index this named agent:
                this.namedAgentConfigs.put(namedAgentConfig.getAgentName(), namedAgentConfig);
            }
        );
        // Now we have any named agents if there are any.

        // Get all the agents that have registered a period of interest for the time simulation:
        Map<NamedAgentConfigAPI, PeriodOfInterestForAgent> periodOfInterestForAgentMap = new LinkedHashMap<>();
        this.environmentController.forEachTypeOfModelOrSubClass(
            PeriodOfInterestForAgent.class,
            periodOfInterestForAgent ->
            {
                // Get the agent config that this period of interest is for:
                NamedAgentConfigAPI namedAgentConfig = namedAgentConfigs.get(periodOfInterestForAgent.agentName);
                if (namedAgentConfig != null)
                {
                    // We have resolved an agent with the given name.

                    // Add this pair to our map:
                    periodOfInterestForAgentMap.put(namedAgentConfig, periodOfInterestForAgent);
                }
            }
        );
        // Now we have all the named agents and the periods of interest that they have registered.
        // We also have a flattened timeline of all the start and end points of interest. This helps us determine what to set the clock to next.

    }

    /**
     * This finds the {@link ModelAPI models} that reference {@link NamedAgentConfigAPI named agents} by name.
     *
     * @param modelType                   The specific type of model that we want to match up to named agents.
     * @param referencedAgentNameSupplier The lambda to extract the referenced agent name from the specific type of {@link ModelAPI model} to find.
     * @param <T>                         The specific type of {@link ModelAPI model} to map to the {@link NamedAgentConfigAPI named agent}.
     * @return The map of all {@link NamedAgentConfigAPI named agents} and the corresponding {@link ModelAPI models} of the specific type that referenced them.
     */
    public <T extends ModelAPI> Map<NamedAgentConfigAPI, T> findModelsForNamedAgents(Class<T> modelType, Function<T, String> referencedAgentNameSupplier)
    {
        // Get all the agents that have registered a period of interest for the time simulation:
        Map<NamedAgentConfigAPI, T> result = new LinkedHashMap<>();
        this.environmentController.forEachTypeOfModelOrSubClass(
            modelType,
            model ->
            {
                // Get the referenced agent name from the model:
                String agentName = referencedAgentNameSupplier.apply(model);

                // Get the agent config that is being referenced by name:
                NamedAgentConfigAPI namedAgentConfig = namedAgentConfigs.get(agentName);
                if (namedAgentConfig != null)
                {
                    // We have resolved an agent with the given name.

                    // Add this pair to our map:
                    result.put(namedAgentConfig, model);
                }
            }
        );
        // Now we have all the named agents and models that they have referenced.
        return result;
    }
}
