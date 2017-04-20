package de.cubenation.api.bedrock.exception;

/**
 * @author Cube-Nation
 * @version 1.0
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
