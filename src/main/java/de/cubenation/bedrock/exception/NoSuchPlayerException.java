package de.cubenation.bedrock.exception;

/**
 * Created by bhruschka on 30.12.16.
 * Project: Bedrock
 */
public class NoSuchPlayerException extends Exception {

    private static final long serialVersionUID = 1L;

    public NoSuchPlayerException(String name) {
        super(name);
    }

    public NoSuchPlayerException() {
        super();
    }
}
