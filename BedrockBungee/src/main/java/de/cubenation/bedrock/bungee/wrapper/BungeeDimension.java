package de.cubenation.bedrock.bungee.wrapper;

import de.cubenation.bedrock.core.wrapper.BedrockDimension;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public class BungeeDimension implements BedrockDimension {

    private String server, world;

    protected BungeeDimension(String server, String world) {
        this.server = server;
        this.world = world;
    }

    public static BungeeDimension wrap(String server, String world) {
        return new BungeeDimension(server, world);
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
