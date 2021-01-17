package de.cubenation.bedrock.bungee.wrapper;

import de.cubenation.bedrock.core.wrapper.BedrockPosition;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public class BungeePosition implements BedrockPosition {

    private BungeeDimension dimension;
    private double x = 0.0d, y = 0.0d, z = 0.0d;
    private float yaw = 0.0f, pitch = 0.0f;

    public BungeePosition(BungeeDimension dimension, double x, double y, double z, float yaw, float pitch) {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public BungeeDimension getDimension() {
        return dimension;
    }

    @Override
    public String getPrettyToString() {
        return dimension.getName()+", "+
                x+", "+
                y+", "+
                z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
