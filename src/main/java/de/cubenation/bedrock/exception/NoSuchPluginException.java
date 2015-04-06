package de.cubenation.bedrock.exception;

/**
 * Created by B1acksheep on 06.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.exception
 */
public class NoSuchPluginException extends Exception {

    private static final long serialVersionUID = 1L;

    public NoSuchPluginException(String name) {
        super(name);
    }
}
