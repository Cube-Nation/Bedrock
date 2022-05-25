package de.cubenation.bedrock.core.exception;

import java.io.IOException;

public class IllegalPluginMessageException extends IOException {

    public IllegalPluginMessageException() {
    }

    public IllegalPluginMessageException(String message) {
        super(message);
    }

    public IllegalPluginMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalPluginMessageException(Throwable cause) {
        super(cause);
    }
}
