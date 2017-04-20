package de.cubenation.api.bedrock.exception;

import java.io.IOException;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class NoSuchRegisterableException extends IOException {

    public NoSuchRegisterableException(String message) {
        super(message);
    }
}
