package de.cubenation.bedrock.core.exception;

import de.cubenation.bedrock.core.translation.JsonMessage;
import lombok.Getter;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public class ArgumentTypeCastException extends Exception {

    @Getter
    private final JsonMessage failureMessage;

    public ArgumentTypeCastException(JsonMessage failureMessage) {
        this.failureMessage = failureMessage;
    }
}
