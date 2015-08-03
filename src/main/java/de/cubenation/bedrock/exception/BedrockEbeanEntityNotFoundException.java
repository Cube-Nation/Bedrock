package de.cubenation.bedrock.exception;

import java.io.IOException;

/**
 * Created by Tristan Cebulla <equinox@lichtspiele.org> on 03.08.2015.
 */
@SuppressWarnings("DefaultFileTemplate")
public class BedrockEbeanEntityNotFoundException extends IOException {

    public BedrockEbeanEntityNotFoundException(Class clazz, String uuid) {
        super(String.format("Could not find bedrock ebean entity %s for UUID %s", clazz.toString(), uuid));
    }

}
