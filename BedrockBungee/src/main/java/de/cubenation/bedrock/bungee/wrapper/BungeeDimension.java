package de.cubenation.bedrock.bungee.wrapper;

import de.cubenation.bedrock.core.wrapper.BedrockDimension;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public class BungeeDimension implements BedrockDimension {

    private String server, world;

    public BungeeDimension(String server, String world) {
        this.server = server;
        this.world = world;
    }

    @Override
    public String getName() {
        return server+"-"+world;
    }

    public String getServer() {
        return server;
    }

    public String getWorld() {
        return world;
    }
}
