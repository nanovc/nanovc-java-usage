package io.nanovc.agentsim.aws;

/**
 * An {@link AWSConceptBase} with a name so that it can be identified within the {@link AWSCloud} model.
 */
public interface NamedAWSConcept
{
    /**
     * The name of the {@link AWSConceptBase}.
     * This is used to identify the concept within the {@link AWSCloud} model.
     * @return The name of the {@link AWSConceptBase}.
     */
    String getName();

    /**
     * The name of the {@link AWSConceptBase}.
     * This is used to identify the concept within the {@link AWSCloud} model.
     *
     * @param name The name of the {@link AWSConceptBase}.
     */
    void setName(String name);
}
