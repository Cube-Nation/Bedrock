package de.cubenation.api.bedrock.exception;

import java.io.IOException;

/**
 * Created by BenediktHr on 30.11.15.
 * Project: Bedrock
 */
public class BedrockEbeanEntityAlreadyExistsException extends IOException {

    public BedrockEbeanEntityAlreadyExistsException(Class clazz, String uuid) {
        super(String.format("Could not create bedrock ebean entity %s for UUID %s", clazz.toString(), uuid));
    }

}
