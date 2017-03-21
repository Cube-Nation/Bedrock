package de.cubenation.api.bedrock.exception;

import java.io.IOException;

public class NoSuchRegisterableException extends IOException {

    public NoSuchRegisterableException(String message) {
        super(message);
    }
}
