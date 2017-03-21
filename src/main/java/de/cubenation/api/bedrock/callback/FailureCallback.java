package de.cubenation.api.bedrock.callback;

/**
 * Created by bhruschka on 12.02.17.
 * Project: Bedrock
 */
public interface FailureCallback<T> {
    void didFailed(T object);
}

