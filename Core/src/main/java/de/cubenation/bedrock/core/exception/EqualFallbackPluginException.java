package de.cubenation.bedrock.core.exception;

public class EqualFallbackPluginException extends Exception {

    public EqualFallbackPluginException() {
    }

    public EqualFallbackPluginException(String message) {
        super(message);
    }

    public EqualFallbackPluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public EqualFallbackPluginException(Throwable cause) {
        super(cause);
    }

    public EqualFallbackPluginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
