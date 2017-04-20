package de.cubenation.api.bedrock.exception;

import java.io.IOException;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class BedrockEbeanEntityAlreadyExistsException extends IOException {

    public BedrockEbeanEntityAlreadyExistsException(Class clazz, String uuid) {
        super(String.format("Could not create bedrock ebean entity %s for UUID %s", clazz.toString(), uuid));
    }

}
