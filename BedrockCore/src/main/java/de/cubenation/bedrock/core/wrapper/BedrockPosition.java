package de.cubenation.bedrock.core.wrapper;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public interface BedrockPosition {

    /**
     * Gets the dimension of this position
     *
     * @return Name of the dimension
     */
    BedrockDimension getDimension();

    /**
     * Gets a printable stringified version for Use in chat etc.
     * @return stringified BedrockPosition
     */
    String getPrettyToString();

}
