package io.nanovc.agentsim;

/**
 * A validation result that indicates information about validation results in the model.
 */
public class ValidationResult
{
    /**
     * The name of the validation.
     * This is used to identify the type of validation result that was generated.
     */
    public String validationName;

    /**
     * This is the message for the validation result.
     */
    public String message;
}
