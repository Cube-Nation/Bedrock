package de.cubenation.bedrock.exception;

import java.io.IOException;

public class NoSuchRegisterableException extends IOException {

    public NoSuchRegisterableException(String message) {
        super(message);
    }
}
