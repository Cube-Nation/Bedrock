package de.cubenation.bedrock.exception;

/**
 * Created by B1acksheep on 06.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.exception
 */
public class IllegalCommandArgumentException extends Exception {

    private static final long serialVersionUID = 1L;

    public IllegalCommandArgumentException(String message) {
        super(message);
    }
}
