package io.nanovc.agentsim;

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
