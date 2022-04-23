package de.cubenation.bedrock.core.model.wrapper;

import de.cubenation.bedrock.core.model.Printable;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings("unused")
public interface BedrockPosition extends Printable {

    /**
     * Gets the dimension of this position
     *
     * @return Name of the dimension
     */
    BedrockDimension getDimension();
}
