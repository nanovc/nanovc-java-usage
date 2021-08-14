package io.nanovc.agentsim.aws;

/**
 * The base class for AWS Concepts with a name.
 */
public abstract class NamedAWSConceptBase extends AWSConceptBase implements NamedAWSConcept
{
    /**
     * The name of the {@link NamedAWSConcept AWS concept}.
     */
    public String name;

    /**
     * Gets the name of the {@link NamedAWSConcept AWS concept}.
     * @return The name of the {@link NamedAWSConcept AWS concept}.
     */
    @Override public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the {@link NamedAWSConcept AWS concept}.
     * @param name The name of the {@link NamedAWSConcept AWS concept}.
     */
    @Override public void setName(String name)
    {
        this.name = name;
    }
}
