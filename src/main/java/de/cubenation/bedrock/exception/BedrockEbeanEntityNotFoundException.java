package de.cubenation.bedrock.exception;

import de.cubenation.bedrock.ebean.BedrockPlayer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tristan Cebulla <equinox@lichtspiele.org> on 03.08.2015.
 */
@SuppressWarnings("DefaultFileTemplate")
public class BedrockEbeanEntityNotFoundException extends IOException {

    public BedrockEbeanEntityNotFoundException(Class clazz, String uuid) {
        super(String.format("Could not find bedrock ebean entity %s for UUID %s", clazz.toString(), uuid));
    }

    public BedrockEbeanEntityNotFoundException(Class clazz, int id) {
        super(String.format("Could not find bedrock ebean entity %s for id %s", clazz.toString(), id));
    }

    public BedrockEbeanEntityNotFoundException(Class<BedrockPlayer> clazz, ArrayList<Integer> ids) {
        super(String.format("Could not find bedrock ebean entity %s for ids %s", clazz.toString(), ids));
    }

    public BedrockEbeanEntityNotFoundException(ArrayList<String> uuids, Class<BedrockPlayer> clazz) {
        super(String.format("Could not find bedrock ebean entity %s for ids %s", clazz.toString(), uuids));
    }
}
