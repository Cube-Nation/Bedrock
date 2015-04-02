package de.cubenation.bedrock.exception;

/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.exception
 */

public class CommandException extends Exception {

    private static final long serialVersionUID = 1L;

    public CommandException(String message) {
        super(message);
    }
}