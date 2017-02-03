package de.cubenation.bedrock.callback;

import de.cubenation.bedrock.ebean.BedrockPlayer;

/**
 * Created by BenediktHr on 15.04.16.
 * Project: Bedrock
 */

public interface SingleBedrockPlayerCallback {

    void didFinished(BedrockPlayer player);

    void didFailed(Exception e);

}
