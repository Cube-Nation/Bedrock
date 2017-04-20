package de.cubenation.api.bedrock.exception;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@SuppressWarnings("unused")
public class LocalizationFileNotFoundException extends Exception {

    private static final long serialVersionUID = 7143550159081144119L;

    public LocalizationFileNotFoundException(String message) {
        super(message);
    }

}
