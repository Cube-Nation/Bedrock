package de.cubenation.api.bedrock.service.confirm;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public interface ConfirmInterface {

    void call();

    void abort();

    void invalidate();

}