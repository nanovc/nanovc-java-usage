package io.nanovc.agentsim;

import java.util.ArrayList;

/**
 * A list of {@link ValidationResult}'s.
 */
public class ValidationResultCollection extends ArrayList<ValidationResult>
{
    /**
     * Creates and adds a new validation result to the collection.
     * This is a convenience method for creating a {@link ValidationResult} and {@link #add(Object)}'ing it in one step.
     * @param validationName The name of the validation to add. This is used to identify the type of validation result that was generated.
     * @param message The validation message.
     * @return The validation result that was created.
     */
    public ValidationResult addValidationResult(String validationName, String message)
    {
        // Create the validation result:
        ValidationResult validationResult = new ValidationResult();
        validationResult.validationName = validationName;
        validationResult.message = message;

        // Add the validation result:
        this.add(validationResult);

        return validationResult;
    }
}
