package de.cubenation.bedrock.callback;

import de.cubenation.bedrock.ebean.BedrockPlayer;

import java.util.List;

/**
 * Created by BenediktHr on 15.04.16.
 * Project: Bedrock
 */

public interface MultipleBedrockPlayerCallback {

    void didFinished(List<BedrockPlayer> players);

    void didFailed(Exception e);

}
