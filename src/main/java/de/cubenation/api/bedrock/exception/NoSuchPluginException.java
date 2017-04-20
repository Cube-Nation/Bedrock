package de.cubenation.api.bedrock.exception;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class NoSuchPluginException extends Exception {

    private static final long serialVersionUID = 1L;

    public NoSuchPluginException(String name) {
        super(name);
    }
}
