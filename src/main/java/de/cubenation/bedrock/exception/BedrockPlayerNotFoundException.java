package de.cubenation.bedrock.exception;

import java.io.IOException;

/**
 * Created by Tristan Cebulla <equinox@lichtspiele.org> on 03.08.2015.
 */
@SuppressWarnings("DefaultFileTemplate")
public class BedrockPlayerNotFoundException extends IOException {

    public BedrockPlayerNotFoundException(String uuid) {
        super(String.format("Could not find player data for UUID %s. This seems very unlikely", uuid));
    }

}
