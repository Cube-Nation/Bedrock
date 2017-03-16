package de.cubenation.bedrock.callback;

import de.cubenation.bedrock.ebean.BedrockWorld;

/**
 * Created by BenediktHr on 15.04.16.
 * Project: Bedrock
 */

@Deprecated
public interface SingleBedrockWorldCallback {

    void didFinished(BedrockWorld world);

    void didFailed(Exception e);

}
