package io.nanovc.agentsim;

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
