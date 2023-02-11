package de.cubenation.bedrock.core.exception;

public class InjectionException extends Exception {

    public InjectionException(String message) {
        super(message);
    }

    public InjectionException(String message, Throwable e) {
        super(message, e);
    }
}
