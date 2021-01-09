package io.nanovc.meh;

/**
 * This defines the known {@link MEHConcepts} patterns of architecture.
 */
public enum MEHPatterns
{
    /**
     * <li>Poor extensibility</li>
     * <li>Tight coupling</li>
     * <li>Used when you want a simple and intuitive design.</li>
     * <li>What is interesting is that you can loop architecture 3 ({@link #MODEL_ENGINE_HANDLER}) back to architecture 1 ({@link #OOP}) as a convenience sugar.
     * You subclass the xModelAPI or xModelBase, you implement the xHandlerAPI and you delegate the implementation to an xEngineAPI.</li>
     * <p>
     * For a detailed diagram and description, see: {@link MEHConcepts#OOP}.
     *
     * @see MEHConcepts#OOP
     */
    OOP(1, "Object Oriented Programming"),


    /**
     * <li>This represents the "Functional Programming Approach" if the engine never mutates the models but always returns new ones.</li>
     * <li>Decoupled structure and behaviour.</li>
     * <li>Improved parallelism?</li>
     * <li>Used for unit tests, ...</li>
     * <li>Engine ~ "Strategy"</li>
     * <p>
     * For a detailed diagram and description, see: {@link MEHConcepts#MODEL_ENGINE}.
     *
     * @see MEHConcepts#MODEL_ENGINE
     */
    MODEL_ENGINE(2, "Model + Engine"),

    /**
     * <li>Highly extensible.</li>
     * <li>Handlers have Engines injected.</li>
     * <li>Decouples the context-variables from the data-variables (why is this significant?)</li>
     * <li>At first, there will likelycould be a 1:1 relationship between Handlers and Engines.</li>
     * <li>This option is great when there are MANY ways to achieve the same thing.</li>
     * <li>This design is the most onerous for the library writer but it gives us the most flexibility to model any arbitrary approach in the private API's (Engines).</li>
     * <li>The naming convention is to have xModelAPI, xModelBase, xHandlerAPI, xHandlerBase, xEngineAPI, xEngineBase for each implementation option. That's a lot of overhead but it's worth it for the extensibility.</li>
     * <p>
     * For a detailed diagram and description, see: {@link MEHConcepts#MODEL_ENGINE_HANDLER}.
     *
     * @see MEHConcepts#MODEL_ENGINE_HANDLER
     */
    MODEL_ENGINE_HANDLER(3, "Model + Engine + Handler"),


    /**
     * <li>The Handler and Engine is combined.</li>
     * <li>Seen most often in frameworks like Angular as the Controller.</li>
     * <li>Model might be the View Model in the UI Context, data would then be the Model.</li>
     * <li>This option is good when there is ONLY ONE implementation required but we want to keep the model clean and simple. The context in the controller is where the "working set" is placed for the model.</li>
     * <li>You don't need interfaces and base classes with this option because there is only one implementation that makes sense at a time.</li>
     * <p>
     * For a detailed diagram and description, see: {@link MEHConcepts#MODEL_CONTROLLER}.
     *
     * @see MEHConcepts#MODEL_CONTROLLER
     */
    MODEL_CONTROLLER(4, "Model + Controller"),
    ;

    /**
     * The pattern number that we refer to in relation to the other patterns.
     */
    public final int patternNumber;

    /**
     * The full name of this pattern.
     */
    public final String patternName;

    /**
     * Defines a new pattern for the {@link MEHConcepts}.
     *
     * @param patternNumber The pattern number that we refer to in relation to the other patterns.
     * @param patternName   The full name of this pattern.
     */
    MEHPatterns(
        int patternNumber,
        String patternName
    )
    {
        this.patternNumber = patternNumber;
        this.patternName = patternName;
    }
}
