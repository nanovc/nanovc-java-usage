package io.nanovc.agentsim;

import io.nanovc.meh.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The {@link MEHConcepts#CONTROLLER controller} for {@link EnvironmentModel}'s.
 * <p>
 * A {@link MEHConcepts#CONTROLLER controller} is the {@link MEHConcepts#HANDLER handler} and {@link MEHConcepts#ENGINE engine} combined.
 * This follows {@link MEHPatterns#MODEL_CONTROLLER architecture 4 } of the {@link MEHPatterns MEH Pattern}.
 */
public class EnvironmentController
{
    /**
     * The environment model that is being controlled.
     */
    private EnvironmentModel environmentModel;

    /**
     * An index of all the models in the environment, indexed by their name.
     * The key is the {@link ModelAPI#getName() model name}.
     */
    private Map<String, ModelAPI> modelsByName;

    /**
     * This is a list of all validation errors that were created when indexing the {@link #environmentModel}.
     */
    private ValidationResultCollection indexingErrors;

    /**
     * Creates a new environment controller that doesn't have an environment model to control.
     * You need to call {@link #setEnvironmentModel(EnvironmentModel)} separately.
     */
    public EnvironmentController()
    {
    }

    /**
     * Creates a new controller for the given environment model.
     *
     * @param environmentModel The environment model to control. This gets indexed immediately and any validation errors are reported.
     */
    public EnvironmentController(EnvironmentModel environmentModel)
    {
        setEnvironmentModel(environmentModel);
        indexEnvironmentModel();
    }

    /**
     * Adds the given model to the environment.
     * This indexes the model immediately and updates {@link #indexingErrors} if there are any errors.
     *
     * @param model The model to add to the environment.
     */
    public void addModel(ModelAPI model)
    {
        // Add the model to the environment:
        this.environmentModel.models.add(model);

        // Index the model:
        indexModel(model);
    }

    /**
     * Removes the given model from the environment.
     * This indexes the model immediately and updates {@link #indexingErrors} if there are any errors.
     *
     * @param model The model to remove from the environment.
     */
    public void removeModel(ModelAPI model)
    {
        // Remove the model from the environment:
        if (this.environmentModel.models.remove(model))
        {
            // We removed the model.

            // Remove the model from the index:
            String modelName = model.getName();
            if (modelName != null)
            {
                this.modelsByName.remove(modelName);
            }
        }
    }

    /**
     * Indexes the given model and updates the {@link #indexingErrors} if an error occurs.
     *
     * @param model The model to index.
     */
    private void indexModel(ModelAPI model)
    {
        // Check whether we already have an model with the given name:
        this.modelsByName.merge(model.getName(), model, (existingModel, newModel) ->
        {
            // If we get here then we already have a model with the given name.

            // Add a validation error:
            this.indexingErrors.addValidationResult("Duplicate model name", "A model called '" + existingModel.getName() + "' already exists. The first occurrence used and all the rest are ignored in the environment model when searching by name. Give each model a distinct name.");

            // Use the existing model:
            return existingModel;
        });
    }

    /**
     * Adds the given agent configuration to the environment.
     * This indexes the agent configuration immediately and updates {@link #indexingErrors} if there are any errors.
     *
     * @param agentConfig The agent configuration to add to the environment.
     */
    public void addAgentConfig(AgentConfigAPI agentConfig)
    {
        // Add the agent config to the model:
        this.environmentModel.agentConfigs.add(agentConfig);

        // Index the agent config:
        indexAgentConfig(agentConfig);
    }

    /**
     * Indexes the given agent configuration and updates the {@link #indexingErrors} if an error occurs.
     *
     * @param agentConfig The agent config to index.
     */
    private void indexAgentConfig(AgentConfigAPI agentConfig)
    {
        // Do nothing for now.
    }

    /**
     * Gets the model with the given name.
     *
     * @param modelName The name of the model to get.
     */
    public <T extends ModelAPI> T getModelByName(String modelName)
    {
        return (T) modelsByName.get(modelName);
    }

    /**
     * Adds the given model if it doesn't exist or
     * replaces an existing model with the given name if it already exists.
     * If it does exist then the given replacement is given the same name and used instead.
     * This is useful when replacing a model from an input environment
     * to another model in the output environment, during simulations.
     *
     * @param modelToAddOrReplace The model to add or replace.
     */
    public void addOrReplaceModel(ModelAPI modelToAddOrReplace)
    {
        addOrReplaceModel(modelToAddOrReplace.getName(), modelToAddOrReplace);
    }

    /**
     * Adds the given model if it doesn't exist or
     * replaces an existing model with the given name if it already exists.
     * If it does exist then the given replacement is given the same name and used instead.
     * This is useful when replacing a model from an input environment
     * to another model in the output environment, during simulations.
     *
     * @param modelName           The name of the model to add or replace.
     * @param modelToAddOrReplace The model to add or replace.
     */
    public void addOrReplaceModel(String modelName, ModelAPI modelToAddOrReplace)
    {
        // Check whether we already have an existing model:
        ModelAPI existingModel = this.getModelByName(modelName);
        if (existingModel != null)
        {
            // We have an existing model with the given name.

            // Remove the existing model:
            removeModel(existingModel);
        }

        // Make sure the new model has the same name:
        modelToAddOrReplace.setName(modelName);

        // Add the new model:
        addModel(modelToAddOrReplace);
    }

    /**
     * Replaces an existing model with the given name only if it already exists.
     * If it does exist then the given replacement is given the same name and used instead.
     * This is useful when replacing a model from an input environment
     * to another model in the output environment, during simulations.
     *
     * @param modelName        The name of the model to replace.
     * @param replacementModel The model to replace the existing one with. The name will be set to the given name to be sure that it has the replacement name.
     */
    public void replaceModelIfExists(String modelName, ModelAPI replacementModel)
    {
        // Check whether we already have an existing model:
        ModelAPI existingModel = this.getModelByName(modelName);
        if (existingModel != null)
        {
            // We have an existing model with the given name.

            // Make sure the replacement model has the same name:
            replacementModel.setName(modelName);

            // Remove the existing model:
            removeModel(existingModel);

            // Add the replacement model:
            addModel(replacementModel);
        }
    }

    /**
     * Gets the environment model that is being controlled.
     *
     * @return The environment model that is being controlled.
     */
    public EnvironmentModel getEnvironmentModel()
    {
        return environmentModel;
    }

    /**
     * Sets the environment model that is being controlled.
     * After changing the {@link #environmentModel},
     * you should call {@link #indexEnvironmentModel()} to make sure that the controller is up to date with the new environment model.
     * We keep this setter light weight intentionally.
     *
     * @param environmentModel The environment model that is being controlled.
     */
    public void setEnvironmentModel(EnvironmentModel environmentModel)
    {
        this.environmentModel = environmentModel;
    }

    /**
     * This builds up the indexes for the environment model.
     * This is necessary so that any internal data structures for the environment model are updated.
     * This should be called whenever the {@link #environmentModel} is changed outside of this controller.
     * We make this indexing step explicit so that the {@link #setEnvironmentModel(EnvironmentModel)} method remains light-weight.
     */
    public void indexEnvironmentModel()
    {
        // Get the environment model:
        EnvironmentModel environmentModel = getEnvironmentModel();

        // Check whether we have an environment:
        if (environmentModel == null)
        {
            // We don't have en environment model.
            // Clear the indexes:
            clearIndexes();
        }
        else
        {
            // We have an environment model.
            // Re-index the environment model:
            reIndexEnvironmentModel(environmentModel);
        }

    }

    /**
     * This re-indexes the given environment model.
     *
     * @param environmentModel The environment model to re-index.
     */
    private void reIndexEnvironmentModel(EnvironmentModel environmentModel)
    {
        // Create the indexing structures:
        createIndexes();

        // Index each of the models:
        for (ModelAPI model : environmentModel.models)
        {
            // Index the model and add any errors if there are any:
            indexModel(model);
        }

        // Index each of the agent configs:
        for (AgentConfigAPI agentConfig : environmentModel.agentConfigs)
        {
            // Index the agent config and add any errors if there are any:
            indexAgentConfig(agentConfig);
        }
    }

    /**
     * This creates the indexing structures (but does not populate them).
     */
    private void createIndexes()
    {
        // Create new indexing structures:
        this.indexingErrors = new ValidationResultCollection();
        this.modelsByName = new HashMap<>();
    }

    /**
     * This clears the indexes when there is no environment model.
     */
    private void clearIndexes()
    {
        // Clear the indexing structures:
        this.indexingErrors = null;
        this.modelsByName = null;
    }

    /**
     * Gets the validation errors that we got during indexing.
     *
     * @return The validation errors that we got during indexing.
     */
    public ValidationResultCollection getIndexingErrors()
    {
        return this.indexingErrors;
    }


    /**
     * A convenience method for going through each {@link ModelAPI model} in the {@link EnvironmentModel environment}.
     *
     * @param modelConsumer The logic that consumes each {@link ModelAPI model} in the {@link EnvironmentModel environment}.
     */
    public void forEachModel(Consumer<ModelAPI> modelConsumer)
    {
        this.getEnvironmentModel().models.forEach(modelConsumer);
    }

    /**
     * This iterates through each specific type of {@link ModelAPI model} or a sub class of it.
     *
     * @param modelTypeOrSubClass The specific type of {@link ModelAPI model} (or sub class) to iterate over in the {@link EnvironmentModel environment}.
     * @param modelConsumer       The lambda that is called with each matching {@link ModelAPI model}.
     * @param <T>                 The specific type of {@link ModelAPI model} to get or a sub class of it.
     */
    public <T extends ModelAPI> void forEachTypeOfModelOrSubClass(Class<T> modelTypeOrSubClass, Consumer<T> modelConsumer)
    {
        // Go through each model in the environment:
        for (ModelAPI model : getEnvironmentModel().models)
        {
            // Get the specific class of the model so that we can check whether it is the type we wanted or a sub class:
            Class<? extends ModelAPI> modelClass = model.getClass();

            // Check whether it is the right type that was requested or a sub class:
            if (modelClass == modelTypeOrSubClass || modelTypeOrSubClass.isAssignableFrom(modelClass))
            {
                // Now we know that this is the right type or a sub class that was requested.

                // Get the strongly typed model that the call back wants:
                T specificModel = (T) model;

                // Pass this model to the callback:
                modelConsumer.accept(specificModel);
            }
        }
    }

    /**
     * This iterates through each specific type of {@link ModelAPI model}
     * and only matches {@link ModelAPI models} that are EXACTLY that type (not sub classes).
     *
     * @param exactModelType The exact type of {@link ModelAPI model} to iterate over in the {@link EnvironmentModel environment}.
     * @param modelConsumer  The lambda that is called with each matching {@link ModelAPI model}.
     * @param <T>            The specific type of {@link ModelAPI model} to get (exactly).
     */
    public <T extends ModelAPI> void forEachTypeOfModelExactly(Class<T> exactModelType, Consumer<T> modelConsumer)
    {
        // Go through each model of the model
        for (ModelAPI model : getEnvironmentModel().models)
        {
            // Get the specific class of the model so that we can check whether it is the type we wanted:
            Class<? extends ModelAPI> modelClass = model.getClass();

            // Check whether it is EXACTLY the right type that was requested:
            if (modelClass == exactModelType)
            {
                // Now we know that this is EXACTLY the right type that was requested.

                // Get the strongly typed model that the call back wants:
                T specificModel = (T) model;

                // Pass this model to the callback:
                modelConsumer.accept(specificModel);
            }
        }
    }

    /**
     * A convenience method for going through each {@link AgentConfigAPI agent config} in the {@link EnvironmentModel environment}.
     *
     * @param agentConfigConsumer The logic that consumes each {@link AgentConfigAPI agent config} in the {@link EnvironmentModel environment}.
     */
    public void forEachAgentConfig(Consumer<AgentConfigAPI> agentConfigConsumer)
    {
        this.getEnvironmentModel().agentConfigs.forEach(agentConfigConsumer);
    }

    /**
     * This iterates through each specific type of {@link AgentConfigAPI agent config} or a sub class of it.
     *
     * @param agentConfigTypeOrSubClass The specific type of {@link AgentConfigAPI agent config} (or sub class) to iterate over in the {@link EnvironmentModel environment}.
     * @param agentConfigConsumer       The lambda that is called with each matching {@link AgentConfigAPI agent config}.
     * @param <T>                       The specific type of {@link AgentConfigAPI agent config} to get or a sub class of it.
     */
    public <T extends AgentConfigAPI> void forEachTypeOfAgentConfigOrSubClass(Class<T> agentConfigTypeOrSubClass, Consumer<T> agentConfigConsumer)
    {
        // Go through each agent config in the environment:
        for (AgentConfigAPI agentConfig : getEnvironmentModel().agentConfigs)
        {
            // Get the specific class of the agent config so that we can check whether it is the type we wanted or a sub class:
            Class<? extends AgentConfigAPI> agentConfigClass = agentConfig.getClass();

            // Check whether it is the right type that was requested or a sub class:
            if (agentConfigClass == agentConfigTypeOrSubClass || agentConfigTypeOrSubClass.isAssignableFrom(agentConfigClass))
            {
                // Now we know that this is the right type or a sub class that was requested.

                // Get the strongly typed agent config that the call back wants:
                T specificAgentConfig = (T) agentConfig;

                // Pass this agent config to the callback:
                agentConfigConsumer.accept(specificAgentConfig);
            }
        }
    }

    /**
     * This iterates through each specific type of {@link AgentConfigAPI agent config}
     * and only matches {@link AgentConfigAPI agent configs} that are EXACTLY that type (not sub classes).
     *
     * @param exactAgentConfigType The exact type of {@link AgentConfigAPI agent config} to iterate over in the {@link EnvironmentModel environment}.
     * @param agentConfigConsumer  The lambda that is called with each matching {@link AgentConfigAPI agent config}.
     * @param <T>                  The specific type of {@link AgentConfigAPI agent config} to get (exactly).
     */
    public <T extends AgentConfigAPI> void forEachTypeOfAgentConfigExactly(Class<T> exactAgentConfigType, Consumer<T> agentConfigConsumer)
    {
        // Go through each agent config in the environment:
        for (AgentConfigAPI agentConfig : getEnvironmentModel().agentConfigs)
        {
            // Get the specific class of the agent config so that we can check whether it is the type we wanted:
            Class<? extends AgentConfigAPI> agentConfigClass = agentConfig.getClass();

            // Check whether it is EXACTLY the right type that was requested:
            if (agentConfigClass == exactAgentConfigType)
            {
                // Now we know that this is EXACTLY the right type that was requested.

                // Get the strongly typed agent config that the call back wants:
                T specificAgentConfig = (T) agentConfig;

                // Pass this agent config to the callback:
                agentConfigConsumer.accept(specificAgentConfig);
            }
        }
    }
}
