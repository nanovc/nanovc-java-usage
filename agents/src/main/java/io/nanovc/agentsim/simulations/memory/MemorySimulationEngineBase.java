package io.nanovc.agentsim.simulations.memory;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.nanovc.*;
import io.nanovc.agentsim.*;
import io.nanovc.agentsim.utils.Activator;
import io.nanovc.areas.ByteArrayHashMapArea;
import io.nanovc.content.ByteArrayContent;
import io.nanovc.meh.MEHConcepts;
import io.nanovc.meh.MEHPatterns;
import io.nanovc.memory.MemoryCommit;
import io.nanovc.memory.bytes.ByteArrayNanoRepo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A base class for an in-memory simulation engine.
 * <p>
 * This captures the private API between the {@link MEHConcepts#HANDLER handler} and the {@link MEHConcepts#ENGINE engine} to run the actual simulations.
 * This follows {@link MEHPatterns#MODEL_ENGINE_HANDLER architecture 3}  of the {@link MEHPatterns MEH Pattern}.
 *
 * @param <TConfig>     The specific type of configuration that the simulation takes.
 * @param <TIteration>  The specific type of iteration for the simulation.
 * @param <TSolution>   The specific type of solution for the simulation.
 * @param <TSimulation> The specific type of simulation that we are running.
 */
public abstract class MemorySimulationEngineBase<
    TConfig extends MemorySimulationConfigAPI,
    TIteration extends MemorySimulationIterationAPI,
    TSolution extends MemorySimulationSolutionAPI<TIteration>,
    TSimulation extends MemorySimulationModelAPI<TIteration, TSolution>
    >
    extends SimulationEngineBase<TConfig, TIteration, TSolution, TSimulation>
    implements MemorySimulationEngineAPI<TConfig, TIteration, TSolution, TSimulation>
{

    /**
     * The name of the master branch that we commit to.
     */
    public static final String MASTER_BRANCH_NAME = "master";

    /**
     * The root path in the content area where models that have names are written.
     */
    public static final String MODELS_ROOT_PATH = "models";

    /**
     * The root path in the content area where models without names are written.
     */
    public static final String MODELS_WITH_NO_NAMES_ROOT_PATH = "modelsWithNoNames";

    /**
     * The root path in the content area where agent configs are written.
     */
    public static final String AGENTS_ROOT_PATH = "agents";

    /**
     * Runs a new simulation.
     *
     * @param config                The configuration to run for the simulation.
     * @param inputEnvironmentModel The environment model to use as input for the simulation.
     * @return The new simulation that was started.
     * @throws SimulationException If an error occurred during the simulation.
     */
    @Override public TSimulation runSimulation(TConfig config, EnvironmentModel inputEnvironmentModel) throws SimulationException
    {
        // Create the simulation model:
        TSimulation simulation = createSimulationModel();

        // Set the input environment model on the simulation for reference:
        simulation.setInputEnvironmentModel(inputEnvironmentModel);

        // Create the repo for keeping track of the simulation between iterations:
        ByteArrayNanoRepo simulationRepo = createSimulationRepo();

        // Create the JSON Mapper that helps with serialization:
        JsonMapper jsonMapper = createJsonMapper();

        // Configure the Json Mapper for the way we want to serialize the content:
        configureJsonMapper(config, jsonMapper);

        // Commit the starting point of the simulation:
        MemoryCommit startingCommit = commitEnvironmentToMaster(config, inputEnvironmentModel, simulationRepo, jsonMapper);

        // Create the solution for the model:
        TSolution solution = startSolution(simulation, simulationRepo, inputEnvironmentModel, startingCommit);

        // Begin the simulation by running iterations:
        executeIterationsUntilActivityStops(config, simulation, simulationRepo, jsonMapper);

        return simulation;
    }

    /**
     * Starts a new solution for the simulation.
     *
     * @param simulationToAddTo     The simulation that we should add the solution to.
     * @param simulationRepoToAddTo The repo to use for the simulation.
     * @param inputEnvironmentModel The input environment model to start the solution with.
     * @param startingCommit        The starting commit to start the solution from.
     * @return The solution that was started.
     */
    public TSolution startSolution(TSimulation simulationToAddTo, ByteArrayNanoRepo simulationRepoToAddTo, EnvironmentModel inputEnvironmentModel, MemoryCommit startingCommit)
    {
        // Create the solution by using the core logic:
        TSolution solution = startSolutionCore(simulationToAddTo);

        // Save the input environment model for this solution:
        solution.setInputEnvironmentModel(inputEnvironmentModel);

        // Save the input environment model as the last environment model for the solution so far:
        solution.setOutputEnvironmentModel(inputEnvironmentModel);

        // Create a branch from the starting commit for this solution:
        simulationRepoToAddTo.createBranchAtCommit(startingCommit, solution.getBranchName());

        // Save this commit as the last commit for this solution:
        solution.setLastCommit(startingCommit);

        return solution;
    }

    /**
     * Spawns a new solution for the simulation from the given solution.
     * It keeps the history
     *
     * @param simulationToAddTo     The simulation that we should add the solution to.
     * @param simulationRepoToAddTo The repo to use for the simulation.
     * @param solutionToSpawnFrom   The solution to spawn from.
     * @return The solution that was started.
     */
    public TSolution spawnSolution(TSimulation simulationToAddTo, ByteArrayNanoRepo simulationRepoToAddTo, TSolution solutionToSpawnFrom)
    {
        // Create the solution by using the core logic:
        TSolution solution = startSolutionCore(simulationToAddTo);

        // Save the input environment model for this solution:
        EnvironmentModel inputEnvironmentModel = solutionToSpawnFrom.getInputEnvironmentModel();
        solution.setInputEnvironmentModel(inputEnvironmentModel);

        // Save the output environment model for this solution:
        EnvironmentModel outputEnvironmentModel = solutionToSpawnFrom.getOutputEnvironmentModel();
        solution.setOutputEnvironmentModel(outputEnvironmentModel);

        // Create a branch from the starting commit for this solution:
        MemoryCommit lastCommit = solutionToSpawnFrom.getLastCommit();
        simulationRepoToAddTo.createBranchAtCommit(lastCommit, solution.getBranchName());

        // Save this commit as the last commit for this solution:
        solution.setLastCommit(lastCommit);

        // Copy all of the iterations across:
        List<TIteration> iterations = solution.getIterations();
        iterations.addAll(solutionToSpawnFrom.getIterations());

        return solution;
    }

    /**
     * The core logic to start a new solution for the simulation.
     * It is used by {@link #startSolution(MemorySimulationModelAPI, ByteArrayNanoRepo, EnvironmentModel, MemoryCommit)}
     * and {@link #spawnSolution(MemorySimulationModelAPI, ByteArrayNanoRepo, MemorySimulationSolutionAPI)}.
     * to make sure that we have consistent implementations.
     *
     * @param simulationToAddTo The simulation that we should add the solution to.
     * @return The solution that was started.
     */
    public TSolution startSolutionCore(TSimulation simulationToAddTo)
    {
        // Create the solution:
        TSolution solution = createSimulationSolution();

        // Get the tracker that tracks the next solution index for the simulation:
        AtomicInteger nextSolutionIndexTracker = simulationToAddTo.getNextSolutionIndex();

        // Get the index of the solution in the simulation, for reference:
        int solutionIndex = nextSolutionIndexTracker.getAndIncrement();

        // Flag that the solution is not completed yet:
        solution.setCompleted(false);

        // Add the solution to the simulation:
        simulationToAddTo.getSolutions().add(solution);

        // Save the solution index for easy reference:
        solution.setSolutionIndex(solutionIndex);

        // Determine a name for this solution, so it's easy to see and also because we use it as the branch name:
        String solutionName = String.format("Solution %d", solutionIndex);

        // Save the solution name for easy reference and also as the branch name:
        solution.setSolutionName(solutionName);
        solution.setBranchName(solutionName);

        return solution;
    }

    /**
     * A factory method to create a new JsonMapper that helps with serialization.
     *
     * @return A new JsonMapper that helps with serialization.
     */
    protected JsonMapper createJsonMapper()
    {
        return new JsonMapper();
    }

    /**
     * Configures the JsonMapper for the way that we want to serialize the content.
     *
     * @param config     The simulation configuration to use.
     * @param jsonMapper The json mapper to configure.
     */
    protected void configureJsonMapper(TConfig config, JsonMapper jsonMapper) throws SimulationException
    {
        // Make sure that we have neat indenting to help with debugging:
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Register any additional modules:
        List<Class<? extends Module>> moduleClasses = config.getSerializationModules();
        for (Class<? extends Module> moduleClass : moduleClasses)
        {
            try
            {
                // Create an instance of the module:
                Module module = Activator.createInstanceOfClass(moduleClass);

                // Register the module:
                jsonMapper.registerModule(module);
            }
            catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e)
            {
                throw new SimulationException("Could not create serialization module: " + moduleClass.getName(), e);
            }
        }

        // Use mixins to provide annotations for types that we don't control directly:
        // https://github.com/FasterXML/jackson-docs/wiki/JacksonMixInAnnotations
        jsonMapper.addMixIn(AgentConfigAPI.class, AgentConfigMixin.class);
        jsonMapper.addMixIn(ModelAPI.class, ModelMixin.class);

        // Make sure that we don't use type information for maps:
        jsonMapper.addMixIn(Map.class, MapMixin.class);

        // Register additional mixins from the simulation config:
        Map<Class<?>, Class<?>> mixins = config.getSerializationMixins();
        if (mixins != null)
        {
            for (Map.Entry<Class<?>, Class<?>> entry : mixins.entrySet())
            {
                jsonMapper.addMixIn(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * This creates and executes iterations for the simulation and exits when there is no more activity in the iterations.
     *
     * @param config         The configuration for the simulation.
     * @param simulation     The simulation that we are running.
     * @param simulationRepo The in-memory repo for keeping track of the simulation between iterations.
     * @param jsonMapper     The JsonMapper that helps with serialization.
     * @return The final iteration that was simulated.
     */
    protected void executeIterationsUntilActivityStops(TConfig config, TSimulation simulation, ByteArrayNanoRepo simulationRepo, JsonMapper jsonMapper) throws SimulationException
    {
        // Keep track of whether there is activity in the simulation:
        boolean simulationHasActivity;

        // Start running iterations:
        do
        {
            // Reset the simulation activity flag to false by default. If there is any activity then each iteration will set this to true.
            simulationHasActivity = false;

            // Keep track of solutions that get spawned during this iteration:
            List<TSolution> spawnedSolutions = new ArrayList<>();

            // Keep track of solutions that we need to remove for the next iteration:
            List<TSolution> solutionsThatWereRemoved = new ArrayList<>();

            // Get a copy of the solutions for this round (so that we can modify the simulation solutions if we spawn new solutions):
            List<TSolution> solutionsForThisRound = new ArrayList<>(simulation.getSolutions());

            // Go through each solution that is running and perform one iteration:
            for (TSolution solution : solutionsForThisRound)
            {
                // Check whether this solution is already completed so that we can skip over it:
                if (solution.isCompleted()) continue;
                // Now we know that the solution is not completed yet.

                // Get the iterations for this solution so far:
                List<TIteration> iterations = solution.getIterations();

                // Get the information from the previous iteration for this solution if there was one:
                EnvironmentModel previousEnvironmentModel;
                MemoryCommit previousCommit;
                if (iterations.size() == 0)
                {
                    // This is the first iteration.

                    // Use the starting information for the solution:
                    previousEnvironmentModel = solution.getInputEnvironmentModel();
                    previousCommit = solution.getLastCommit();
                }
                else
                {
                    // This is not the first iteration anymore.

                    // Get the previous iteration:
                    TIteration previousIteration = iterations.get(iterations.size() - 1);

                    // Use the information from the previous iteration:
                    previousEnvironmentModel = previousIteration.getOutputEnvironmentModel();
                    previousCommit = previousIteration.getCommit();
                }
                // Now we have information about the previous iteration if there was one.

                // Create the first iteration for the simulation:
                TIteration currentIteration = createSimulationIteration();

                // Add it to the list of iterations for the solution:
                iterations.add(currentIteration);

                // Set the input environment for this iteration:
                currentIteration.setInputEnvironmentModel(previousEnvironmentModel);

                // Run the iteration:
                executeIteration(config, previousCommit, simulationRepo, simulation, solution, currentIteration, previousEnvironmentModel, jsonMapper);

                // Get the output model from the iteration:
                EnvironmentModel outputEnvironmentModel = currentIteration.getOutputEnvironmentModel();

                // Set the output for the solution:
                solution.setOutputEnvironmentModel(outputEnvironmentModel);

                // Get the final commit for the iteration:
                MemoryCommit currentCommit = currentIteration.getCommit();

                // Set this as the final commit for the solution:
                solution.setLastCommit(currentCommit);

                // Get any differences in the environment:
                DifferenceAPI differences = simulationRepo.computeDifferenceBetweenCommits(previousCommit, currentCommit);

                // Check whether there is activity in this iteration:
                boolean iterationHasActivity = differences.hasDifferences();

                // Update our activity status for simulation if we have activity:
                if (iterationHasActivity)
                {
                    // The iteration has activity.
                    // So flag that the overall simulation still has activity:
                    simulationHasActivity = true;
                }
                else
                {
                    // This solution no longer has activity to flag it as being done:
                    solution.setCompleted(true);
                }

                // Get the content permutator that detected clashing content for this iteration:
                ContentPermutator<ByteArrayContent> contentPermutator = currentIteration.getContentPermutator();

                // Check whether there were permutations:
                if (contentPermutator.hasClashingContent())
                {
                    // We have clashing content permutations.

                    // Get the permutations:
                    // NOTE: We do this so that we can propagate transactions to the calling method. With the stream API we don't propagate the exception.
                    List<List<AreaEntry<ByteArrayContent>>> permutations = contentPermutator.streamOfPermutations().collect(Collectors.toList());


                    // Go through each permutation and spawn a new solution:
                    for (List<AreaEntry<ByteArrayContent>> permutation : permutations)
                    {
                        // Spawn a new solution from the current solution:
                        TSolution spawnedSolution = spawnSolution(simulation, simulationRepo, solution);

                        // Add the spawned solution to our list for tracking purposes:
                        spawnedSolutions.add(spawnedSolution);

                        // Check out the content area for the spawned solution so that we can modify it appropriately:
                        ByteArrayHashMapArea spawnedAreaToModify = simulationRepo.checkout(spawnedSolution.getLastCommit());

                        // Go through each piece of content that needs to be different for the spawned solution:
                        for (AreaEntry<ByteArrayContent> areaEntry : permutation)
                        {
                            // Get the content for this path:
                            ByteArrayContent content = areaEntry.content;

                            // Check whether we must modify or delete the content for this permutation:
                            if (content == null)
                            {
                                // We need to delete the content at this path for the permutation.
                                spawnedAreaToModify.removeBytes(areaEntry.path);
                            }
                            else
                            {
                                // Modify the spawned area:
                                spawnedAreaToModify.putBytes(areaEntry.path, content.getEfficientByteArray());
                            }
                        }
                        // Now we have updated the spawned area.

                        // Commit the updated area to the spawned solution branch:
                        MemoryCommit spawnedCommit = simulationRepo.commitToBranch(spawnedAreaToModify, spawnedSolution.getBranchName(), "Spawning " + spawnedSolution.getSolutionName(), CommitTags.none());

                        // Get the iterations for the spawned solution because we are going to modify them:
                        List<TIteration> spawnedIterations = spawnedSolution.getIterations();

                        // Remove the existing iteration because we want to replace it with the spawned one:
                        spawnedIterations.remove(spawnedIterations.size() - 1);

                        // Create an iteration to reflect the spawning:
                        TIteration spawnedIteration = createSimulationIteration();

                        // Add it to the list of iterations for the solution:
                        spawnedIterations.add(spawnedIteration);

                        // Set the input environment model for this iteration:
                        spawnedIteration.setInputEnvironmentModel(solution.getOutputEnvironmentModel());

                        // Update the spawned solution with the last commit:
                        spawnedIteration.setCommit(spawnedCommit);
                        spawnedSolution.setLastCommit(spawnedCommit);

                        try
                        {
                            // Get the environment model for the spawned area:
                            EnvironmentModel spawnedOutputEnvironmentModel = readEnvironmentFromContentArea(config, spawnedAreaToModify, jsonMapper);

                            // Set the output environment model for the spawned iteration to the  spawned output model:
                            spawnedIteration.setOutputEnvironmentModel(spawnedOutputEnvironmentModel);

                            // Save the spawned environment model:
                            spawnedSolution.setOutputEnvironmentModel(spawnedOutputEnvironmentModel);
                        }
                        catch (IOException e)
                        {
                            throw new SimulationException("Error checking out spawned commit for " + spawnedSolution.getSolutionName());
                        }

                    }
                    // Now we have spawned new solutions for all agent clash permutations.

                    // Remove the current solution from the next round:
                    simulation.getSolutions().remove(solution);

                    // Flag the current solution as being removed in this round because it has been replaced with spawned solutions:
                    solutionsThatWereRemoved.add(solution);
                }
                // Now we have spawned any necessary solutions.

            }
            // Now we have finished this round of simulation.
        }
        while (simulationHasActivity);
    }

    /**
     * Runs an iteration of the simulation.
     *
     * @param config                The configuration for the simulation.
     * @param previousCommit        The commit for the previous iteration. If this is the first iteration then this must be the starting commit.
     * @param simulationRepo        The repo that tracks the history of the simulation.
     * @param simulation            The simulation that is running.
     * @param solution              The solution that is running.
     * @param currentIteration      The current iteration that is running.
     * @param inputEnvironmentModel The input environment for the simulation.
     * @param jsonMapper            The JsonMapper that helps with serialization.
     */
    protected void executeIteration(TConfig config, MemoryCommit previousCommit, ByteArrayNanoRepo simulationRepo, TSimulation simulation, TSolution solution, TIteration currentIteration, EnvironmentModel inputEnvironmentModel, JsonMapper jsonMapper) throws SimulationException
    {
        // Create a read-only environment controller for the input environment model:
        ReadOnlyEnvironmentController inputEnvironmentController = createReadOnlyEnvironmentController(inputEnvironmentModel);

        // Keep a map of each commit that is created for each agent that runs:
        Map<AgentAPI<AgentConfigAPI>, MemoryCommit> commitsPerExecutedAgent = new LinkedHashMap<>();

        // Go through each agent config:
        for (AgentConfigAPI agentConfig : inputEnvironmentModel.agentConfigs)
        {
            // Make sure that the agent is enabled:
            if (!agentConfig.isEnabled()) continue;
            // Now we know that the agent is enabled.

            try
            {
                // Create an instance of the agent:
                AgentAPI<AgentConfigAPI> agent = (AgentAPI<AgentConfigAPI>) Activator.createInstanceOfSimilarType(agentConfig, "Config", "");

                // Set the config on the agent:
                agent.setConfig(agentConfig);

                // Create a clone of the environment model for the iteration to mutate:
                // NOTE: We do this so that each agent thinks that it is the first agent to run.
                //       We then merge the results together to decide the final output for this iteration.
                //       The benefit of this approach is that we can parallelize or offload the execution of the agent for this iteration.
                EnvironmentModel outputEnvironmentModel = checkoutEnvironment(config, previousCommit, simulationRepo, jsonMapper);

                // Create a normal environment controllers for the output environment that is meant to be mutated by the agents for this iteration:
                EnvironmentController outputEnvironmentController = createEnvironmentController(outputEnvironmentModel);

                // Create a simulation controller for this agent so that it is able to introspect on the simulation, solution and iteration:
                SimulationController simulationController = createSimulationController(simulation, solution, currentIteration);

                // Modify the environment:
                agent.modifyEnvironment(inputEnvironmentController, outputEnvironmentController, currentIteration, simulationController, agentConfig);

                // Commit the change that the agent made as a branch in the simulation repo:
                MemoryCommit agentCommit = commitEnvironmentWithNoBranch("Agent commit", config, outputEnvironmentModel, previousCommit, simulationRepo, jsonMapper);

                // Save the mapping from this agent to the commit so that we can merge it later:
                commitsPerExecutedAgent.put(agent, agentCommit);
            }
            catch (Exception e)
            {
                throw new SimulationException("Error running agent during simulation iteration.", e);
            }
        }
        // Now we have allowed each active agent to run so there might be several agent branches (unnamed) that need to be merged.

        // Create a content permutator so that we can spawn solutions for every merge conflict:
        ContentPermutator<ByteArrayContent> contentPermutator = new ContentPermutator<>();

        // Create a merge handler that will add all conflicts to our content permutator:
        MemorySimulationMergerWithContentPermutator<ByteArrayContent> merger = new MemorySimulationMergerWithContentPermutator<>(contentPermutator);

        // Merge all the agent branches that manipulated the model:
        MemoryCommit finalCommit = previousCommit;
        for (Map.Entry<AgentAPI<AgentConfigAPI>, MemoryCommit> entry : commitsPerExecutedAgent.entrySet())
        {
            // Get the agent:
            AgentAPI<AgentConfigAPI> agent = entry.getKey();

            // Get the commit that was created for this agent:
            MemoryCommit agentCommit = entry.getValue();

            // Use this merger when dealing with conflicts:
            simulationRepo.setMergeHandler(merger);

            // Merge the agent branch into the solution branch and deal with any conflicts using the merge handler in the repo:
            // NOTE: We use a merge handler which will allow the last agent to touch the model to win.
            MemoryCommit mergeCommit = simulationRepo.mergeIntoBranchFromCommit(solution.getBranchName(), agentCommit, "Agent merge", null);

            // Keep this as the final commit:
            finalCommit = mergeCommit;
        }
        // Now we have all merge conflicts captured by the content permutator.

        // Disconnect the merge handler because we are done:
        simulationRepo.setMergeHandler(ByteArrayNanoRepo.COMMON_MERGE_HANDLER);

        // Save the content permutator for this iteration so that we can spawn new solutions if necessary:
        currentIteration.setContentPermutator(contentPermutator);

        try
        {
            // Get the final model at the end of all the merges:
            EnvironmentModel outputEnvironmentModel = checkoutEnvironment(config, finalCommit, simulationRepo, jsonMapper);

            // Update the iteration output:
            currentIteration.setOutputEnvironmentModel(outputEnvironmentModel);
            currentIteration.setCommit(finalCommit);
        }
        catch (IOException e)
        {
            throw new SimulationException("Could not extract final output model after merging agents.", e);
        }
    }

    /**
     * Creates a new simulation controller that allows an agent to introspect the simulation, solution and iteration.
     *
     * @param simulation The simulation that is currently being run.
     * @param solution   The solution this is currently being run.
     * @param iteration  The iteration that is currently being run.
     * @return The simulation controller that allows an agent to introspect the simulation, solution and iteration.
     */
    protected SimulationController createSimulationController(TSimulation simulation, TSolution solution, TIteration iteration)
    {
        return new SimulationController(simulation, solution, iteration);
    }

    /**
     * Commits the given environment with no branch.
     * This is useful when we are not interested in tracking the history with a branch name.
     *
     * @param commitMessage    The commit message to use.
     * @param config           The configuration for the simulation.
     * @param environmentModel The input environment for the simulation.
     * @param parentCommit     The commit to use as the parent for this commit. This must not be null. For this first iteration this should be the starting commit.
     * @param simulationRepo   The in-memory repo for keeping track of the simulation between iterations.
     * @param jsonMapper       The JsonMapper that helps with serialization.
     * @return The commit that was created.
     */
    protected MemoryCommit commitEnvironmentWithNoBranch(String commitMessage, TConfig config, EnvironmentModel environmentModel, MemoryCommit parentCommit, ByteArrayNanoRepo simulationRepo, JsonMapper jsonMapper) throws JsonProcessingException
    {
        // Create a content area for this commit:
        ByteArrayHashMapArea contentArea = simulationRepo.createArea();

        // Serialize the environment to the content area:
        writeEnvironmentToContentArea(config, environmentModel, contentArea, jsonMapper);

        // Commit the environment:
        MemoryCommit commit = simulationRepo.commit(contentArea, commitMessage, null, parentCommit);

        return commit;
    }

    /**
     * Commits the given environment to the repository.
     *
     * @param config           The configuration for the simulation.
     * @param environmentModel The input environment for the simulation.
     * @param simulationRepo   The in-memory repo for keeping track of the simulation between iterations.
     * @param jsonMapper       The JsonMapper that helps with serialization.
     * @return The commit that was created.
     * @throws SimulationException If there was an error with serialization.
     */
    protected MemoryCommit commitEnvironmentToMaster(TConfig config, EnvironmentModel environmentModel, ByteArrayNanoRepo simulationRepo, JsonMapper jsonMapper) throws SimulationException
    {
        try
        {
            // Create a content area for this commit:
            ByteArrayHashMapArea contentArea = simulationRepo.createArea();

            // Serialize the environment to the content area:
            writeEnvironmentToContentArea(config, environmentModel, contentArea, jsonMapper);

            // Commit the environment:
            MemoryCommit commit = simulationRepo.commitToBranch(contentArea, MASTER_BRANCH_NAME, "Input Environment", null);

            return commit;
        }
        catch (JsonProcessingException ex)
        {
            throw new SimulationException("Could not commit environment to version control.", ex);
        }
    }

    /**
     * This writes the environment to the given content area.
     *
     * @param config           The configuration to use for writing the content to the content area.
     * @param environmentModel The environment to write to the content area.
     * @param contentArea      The content area to write to.
     * @param jsonMapper       The JsonMapper that helps with serialization.
     */
    protected void writeEnvironmentToContentArea(TConfig config, EnvironmentModel environmentModel, ByteArrayHashMapArea contentArea, JsonMapper jsonMapper) throws JsonProcessingException
    {
        RepoPath root = RepoPath.atRoot();
        contentArea.putBytes(root.resolve("name"), toBytes(environmentModel.name, jsonMapper));
        contentArea.putBytes(root.resolve("description"), toBytes(environmentModel.description, jsonMapper));

        // Write the models that have names:
        RepoPath modelsRootPath = root.resolve(MODELS_ROOT_PATH);
        for (int i = 0; i < environmentModel.models.size(); i++)
        {
            // Get the model:
            ModelAPI model = environmentModel.models.get(i);

            // Make sure that the model has a name:
            String modelName = model.getName();
            if (modelName == null || modelName.isEmpty())
            {
                // The model doesn't have a name so skip over it.
                continue;
            }
            // Now we know that the model has a name.

            // Get the path for this model:
            RepoPath modelPath = modelsRootPath.resolve(modelName);

            // Write the content for the model:
            contentArea.putBytes(modelPath, toBytes(model, jsonMapper));
        }

        // Write the models that have no names but we need to keep anyway:
        RepoPath modelsWithNoNamesRootPath = root.resolve(MODELS_WITH_NO_NAMES_ROOT_PATH);
        for (int i = 0, pathIndex = 0; i < environmentModel.models.size(); i++)
        {
            // Get the model:
            ModelAPI model = environmentModel.models.get(i);

            // Check whether it has a name:
            String modelName = model.getName();
            if (modelName != null && !modelName.isEmpty())
            {
                // This model has a name so skip over it.
                continue;
            }
            // Now we know that the model doesn't have a name.

            // Get the path for this model:
            RepoPath modelPath = modelsWithNoNamesRootPath.resolve(Integer.toString(pathIndex++));

            // Write the content for the model:
            contentArea.putBytes(modelPath, toBytes(model, jsonMapper));
        }

        // Write the agent configs:
        RepoPath agentsRootPath = root.resolve(AGENTS_ROOT_PATH);
        for (int i = 0; i < environmentModel.agentConfigs.size(); i++)
        {
            // Get the agent config:
            AgentConfigAPI agentConfig = environmentModel.agentConfigs.get(i);

            // Get the path for this agent config:
            RepoPath agentConfigPath = agentsRootPath.resolve(Integer.toString(i));

            // Write the content for the agent config:
            contentArea.putBytes(agentConfigPath, toBytes(agentConfig, jsonMapper));
        }
    }

    /**
     * Converts the given value to a byte array.
     * It returns an empty array if the value was null.
     *
     * @param value      The value to convert to bytes.
     * @param jsonMapper The mapper to use for the conversion.
     * @return An empty byte array if the value was null or the serialized version of the value.
     */
    protected byte[] toBytes(Object value, JsonMapper jsonMapper) throws JsonProcessingException
    {
        if (value == null)
        {
            return new byte[0];
        }
        else
        {
            return jsonMapper.writeValueAsBytes(value);
        }
    }

    /**
     * Checks out the environment from the given commit in the repository.
     *
     * @param config         The configuration for the simulation.
     * @param commit         The commit to check out.
     * @param simulationRepo The in-memory repo for keeping track of the simulation between iterations.
     * @param jsonMapper     The JsonMapper that helps with serialization.
     * @return The environment for the given commit.
     * @throws IOException If there is an error deserializing the environment from the commit.
     */
    protected EnvironmentModel checkoutEnvironment(TConfig config, MemoryCommit commit, ByteArrayNanoRepo simulationRepo, JsonMapper jsonMapper) throws IOException
    {
        // Checkout the given commit:
        ByteArrayHashMapArea contentArea = simulationRepo.checkout(commit);

        // Deserialize the environment from the content area:
        EnvironmentModel environmentModel = readEnvironmentFromContentArea(config, contentArea, jsonMapper);

        return environmentModel;
    }

    /**
     * This reads the environment from the given content area.
     *
     * @param config      The configuration to use for reading the content from the content area.
     * @param contentArea The content area to read from.
     * @param jsonMapper  The JsonMapper that helps with serialization.
     * @return The environment model from the given content area.
     */
    protected EnvironmentModel readEnvironmentFromContentArea(TConfig config, ByteArrayHashMapArea contentArea, JsonMapper jsonMapper) throws IOException
    {
        // Create the environment model:
        EnvironmentModel environmentModel = createEnvironmentModel();

        RepoPath root = RepoPath.atRoot();
        environmentModel.name = fromBytes(String.class, contentArea.getBytes(root.resolve("name")), jsonMapper);
        environmentModel.description = fromBytes(String.class, contentArea.getBytes(root.resolve("description")), jsonMapper);

        // Read out the list of models that have names:
        populateListFromNamedStream(
            root.resolve(MODELS_ROOT_PATH),
            contentArea,
            ModelAPI.class,
            environmentModel.models,
            jsonMapper
        );

        // Read out the list of model that have no names:
        populateListFromIndexedStream(
            root.resolve(MODELS_WITH_NO_NAMES_ROOT_PATH),
            contentArea,
            ModelAPI.class,
            environmentModel.models,
            jsonMapper
        );

        // Read out the list of agents:
        populateListFromIndexedStream(
            root.resolve(AGENTS_ROOT_PATH),
            contentArea,
            AgentConfigAPI.class,
            environmentModel.agentConfigs,
            jsonMapper
        );

        return environmentModel;
    }

    /**
     * Populates the given list by finding the indexed items (eg: model/1, model/2) under the given base path.
     *
     * @param <T>               The specific type of item to deserialize from the content area.
     * @param basePath          The base path to search for. eg: /model/
     * @param contentArea       The content area to inspect.
     * @param itemClass         The specific type of item to deserialize from the content area.
     * @param collectionToAddTo The collection to add the items to.
     * @param jsonMapper        The JSON mapper used for deserializing the content.
     */
    protected <T> void populateListFromIndexedStream(RepoPath basePath, ByteArrayHashMapArea contentArea, Class<T> itemClass, List<T> collectionToAddTo, JsonMapper jsonMapper) throws IOException
    {
        // Define the pattern for getting all the items:
        RepoPattern itemPattern = RepoPattern.matching(basePath.ensureEndsWithDelimiter().toAbsolutePath().toString() + "(?<index>\\d+)");
        Pattern itemRegex = itemPattern.asRegex();

        // Get the stream that matches each of the items:
        Stream<AreaEntry<ByteArrayContent>> itemStream = itemPattern.matchStream(contentArea.getTypedContentStream());

        // Create a sorted map where we have the index of the items in the map:
        TreeMap<Integer, ByteArrayContent> sortedItems = new TreeMap<>();
        itemStream.forEachOrdered(
            entry ->
            {
                // Get the index of this item:
                Matcher matcher = itemRegex.matcher(entry.path.toString());
                if (matcher.matches())
                {
                    // Get the index:
                    String indexString = matcher.group("index");
                    Integer index = Integer.valueOf(indexString);

                    // Add the item to our sorted list:
                    sortedItems.put(index, entry.content);
                }
            });

        // Check whether we have items:
        if (sortedItems.size() > 0)
        {
            // Get the highest index:
            int lastIndex = sortedItems.lastKey();

            // Create the list:
            // NOTE: We skip out missing numbers in the sequence and also ones with no content.
            for (Map.Entry<Integer, ByteArrayContent> entry : sortedItems.entrySet())
            {
                // Get the content:
                ByteArrayContent content = entry.getValue();

                // Check whether we have content and only add it to the result if we do:
                if (content != null)
                {
                    // We have content.

                    // Get the value:
                    T item = fromBytes(itemClass, content.getEfficientByteArray(), jsonMapper);

                    // Add the item to the destination list:
                    collectionToAddTo.add(item);
                }
            }
        }
    }

    /**
     * Populates the given list by finding the named items under the given base path.
     *
     * @param <T>               The specific type of item to deserialize from the content area.
     * @param basePath          The base path to search for. eg: /model/
     * @param contentArea       The content area to inspect.
     * @param itemClass         The specific type of item to deserialize from the content area.
     * @param collectionToAddTo The collection to add the items to.
     * @param jsonMapper        The JSON mapper used for deserializing the content.
     */
    protected <T> void populateListFromNamedStream(RepoPath basePath, ByteArrayHashMapArea contentArea, Class<T> itemClass, List<T> collectionToAddTo, JsonMapper jsonMapper) throws IOException
    {
        // Define the pattern for getting all the models:
        RepoPattern itemPattern = RepoPattern.matching(basePath.ensureEndsWithDelimiter().toAbsolutePath().toString() + "**");

        // Get the stream that matches each of the items:
        for (AreaEntry<ByteArrayContent> areaEntry : contentArea)
        {
            // Check whether this area matches the pattern:
            if (itemPattern.matches(areaEntry.path))
            {
                // This content matched our pattern.
                // Add it to the output:
                // Get the content:
                ByteArrayContent content = areaEntry.getContent();

                // Get the value:
                T item = fromBytes(itemClass, content.getEfficientByteArray(), jsonMapper);

                // Add the item to the destination list:
                collectionToAddTo.add(item);
            }
        }
    }

    /**
     * Converts the given byte array to a strongly typed value.
     * If the byte array is null or empty then null is returned.
     *
     * @param objectClass The specific type of object to deserialize from the bytes.
     * @param bytes       The bytes that contain the serialized value.
     * @param jsonMapper  The mapper to use for the conversion.
     * @param <T>         The specific type of value to deserialize.
     * @return The strongly typed value from the byte array. If the byte array is null or empty then null is returned.
     * @throws JsonProcessingException If there is an error  with the JSON.
     */
    protected <T> T fromBytes(Class<T> objectClass, byte[] bytes, JsonMapper jsonMapper) throws IOException
    {
        if (bytes == null || bytes.length == 0)
        {
            return null;
        }
        else
        {
            return jsonMapper.readValue(bytes, objectClass);
        }
    }

    /**
     * A factory that creates a new environment model.
     *
     * @return A new environment model.
     */
    protected EnvironmentModel createEnvironmentModel()
    {
        return new EnvironmentModel();
    }

    /**
     * A factory that creates a new environment controller.
     *
     * @param environmentModel The environment model to wrap with the controller.
     * @return A new environment controller.
     */
    protected EnvironmentController createEnvironmentController(EnvironmentModel environmentModel)
    {
        return new EnvironmentController(environmentModel);
    }

    /**
     * A factory that creates a new environment controller that is read-only.
     *
     * @param environmentModel The environment model to wrap with the controller.
     * @return A new environment controller that is read-only.
     */
    protected ReadOnlyEnvironmentController createReadOnlyEnvironmentController(EnvironmentModel environmentModel)
    {
        return new ReadOnlyEnvironmentController(environmentModel);
    }

    /**
     * A factory that creates a new strongly typed simulation iteration.
     *
     * @return A new simulation iteration.
     */
    protected abstract TIteration createSimulationIteration();

    /**
     * A factory that creates a new strongly typed simulation solution.
     *
     * @return A new simulation solution.
     */
    protected abstract TSolution createSimulationSolution();

    /**
     * A factory that creates a new strongly typed simulation model.
     *
     * @return A new simulation model.
     */
    protected abstract TSimulation createSimulationModel();


    /**
     * A factory that creates a new repo for keeping track of the simulation between iterations.
     *
     * @return A new repo for keeping track of the simulation between iterations.
     */
    protected ByteArrayNanoRepo createSimulationRepo()
    {
        // Create the repo:
        return new ByteArrayNanoRepo();
    }


    /**
     * Clones the given environment model according to the algorithm used during simulation.
     * This is useful for duplicating inputs or outputs for unit testing or other higher level operations.
     *
     * @param config           The configuration to use when cloning.
     * @param environmentModel The environment model to clone.
     * @return A new clone of the environment model according to the algorithm used during simulation.
     * @throws SimulationException If an error occurred during the simulation.
     */
    @Override public EnvironmentModel cloneEnvironmentModel(TConfig config, EnvironmentModel environmentModel) throws SimulationException
    {
        try
        {
            // Create the repo for keeping track of the simulation between iterations:
            ByteArrayNanoRepo repo = createSimulationRepo();

            // Create the JSON Mapper that helps with serialization:
            JsonMapper jsonMapper = createJsonMapper();

            // Configure the Json Mapper for the way we want to serialize the content:
            configureJsonMapper(config, jsonMapper);

            // Commit the environment model so we can make a snapshot from it:
            MemoryCommit commit = commitEnvironmentToMaster(config, environmentModel, repo, jsonMapper);

            // Check out the commit to get the clone:
            EnvironmentModel clonedEnvironmentModel = checkoutEnvironment(config, commit, repo, jsonMapper);

            return clonedEnvironmentModel;
        }
        catch (IOException e)
        {
            throw new SimulationException(e);
        }
    }

    /**
     * This is a mixin class for Jackson which allows us to configure how we want these classes serialized without polluting the models.
     * https://github.com/FasterXML/jackson-docs/wiki/JacksonMixInAnnotations
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
    public static abstract class TypedMixin
    {
    }

    public static abstract class AgentConfigMixin extends TypedMixin
    {
    }

    public static abstract class ModelMixin extends TypedMixin
    {
    }

    // NOTE: Do not add type info to maps.
    public static abstract class MapMixin
    {
    }
}
