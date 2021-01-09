package io.nanovc.agentsim;

import io.nanovc.meh.MEHConcepts;

/**
 * A read-only controller for environments.
 * This is useful so that we can interrogate an environment but we can't change it.
 * NOTE: If you reference and modify the model outside of this controller,
 *       then we can't stop you from modifying it by reference.
 * The {@link MEHConcepts#CONTROLLER controller} for {@link EnvironmentModel}'s.
 * A {@link MEHConcepts#CONTROLLER controller} is the {@link MEHConcepts#HANDLER handler} and {@link MEHConcepts#ENGINE engine} combined.
 * This follows {@link io.nanovc.meh.MEHPatterns#MODEL_CONTROLLER architecture 4 } of the {@link io.nanovc.meh.MEHPatterns MEH Pattern}.
 */
public class ReadOnlyEnvironmentController extends EnvironmentController
{
    /**
     * Creates a new environment controller that doesn't have a environment model to control.
     * You need to call {@link #setEnvironmentModel(EnvironmentModel)} separately.
     */
    public ReadOnlyEnvironmentController()
    {
    }

    /**
     * Creates a new environment controller for the given environment model.
     *
     * @param environmentModel The environment model to control.
     */
    public ReadOnlyEnvironmentController(EnvironmentModel environmentModel)
    {
        super(environmentModel);
    }

    /**
     * Adds the given model to the environment.
     * This throws an exception if it is called because this environment is read-only.
     *
     * @param model The model to add to the environment.
     */
    @Override
    public void addModel(ModelAPI model)
    {
        throw new ReadOnlyException("Cannot add models to a read-only environment.");
    }

    /**
     * Throws an exception because the environment is read-only.
     *
     * @throws ReadOnlyException Cannot add agent configs to a read-only environment.
     */
    @Override
    public void addAgentConfig(AgentConfigAPI agentConfig)
    {
        throw new ReadOnlyException("Cannot add agent configs to a read-only environment.");
    }

    /**
     * An exception that is thrown when an attempt is made to modify a read-only environment model.
     */
    public static class ReadOnlyException extends RuntimeException
    {
        /**
         * Constructs a new runtime exception with {@code null} as its
         * detail message.  The cause is not initialized, and may subsequently be
         * initialized by a call to {@link #initCause}.
         */
        public ReadOnlyException()
        {
        }

        /**
         * Constructs a new runtime exception with the specified detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         *
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public ReadOnlyException(String message)
        {
            super(message);
        }

        /**
         * Constructs a new runtime exception with the specified detail message and
         * cause.  <p>Note that the detail message associated with
         * {@code cause} is <i>not</i> automatically incorporated in
         * this runtime exception's detail message.
         *
         * @param message the detail message (which is saved for later retrieval
         *                by the {@link #getMessage()} method).
         * @param cause   the cause (which is saved for later retrieval by the
         *                {@link #getCause()} method).  (A {@code null} value is
         *                permitted, and indicates that the cause is nonexistent or
         *                unknown.)
         * @since 1.4
         */
        public ReadOnlyException(String message, Throwable cause)
        {
            super(message, cause);
        }

        /**
         * Constructs a new runtime exception with the specified cause and a
         * detail message of {@code (cause==null ? null : cause.toString())}
         * (which typically contains the class and detail message of
         * {@code cause}).  This constructor is useful for runtime exceptions
         * that are little more than wrappers for other throwables.
         *
         * @param cause the cause (which is saved for later retrieval by the
         *              {@link #getCause()} method).  (A {@code null} value is
         *              permitted, and indicates that the cause is nonexistent or
         *              unknown.)
         * @since 1.4
         */
        public ReadOnlyException(Throwable cause)
        {
            super(cause);
        }

        /**
         * Constructs a new runtime exception with the specified detail
         * message, cause, suppression enabled or disabled, and writable
         * stack trace enabled or disabled.
         *
         * @param message            the detail message.
         * @param cause              the cause.  (A {@code null} value is permitted,
         *                           and indicates that the cause is nonexistent or unknown.)
         * @param enableSuppression  whether or not suppression is enabled
         *                           or disabled
         * @param writableStackTrace whether or not the stack trace should
         *                           be writable
         * @since 1.7
         */
        public ReadOnlyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
        {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
