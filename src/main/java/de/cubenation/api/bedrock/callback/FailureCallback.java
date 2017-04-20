package de.cubenation.api.bedrock.callback;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public interface FailureCallback<T> {
    void didFailed(T object);
}

