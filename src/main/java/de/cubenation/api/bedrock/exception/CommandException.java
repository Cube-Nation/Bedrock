package de.cubenation.api.bedrock.exception;

/**
 * @author Cube-Nation
 * @version 1.0
 */

public class CommandException extends Exception {

    private static final long serialVersionUID = 1L;

    public CommandException(String message) {
        super(message);
    }
}