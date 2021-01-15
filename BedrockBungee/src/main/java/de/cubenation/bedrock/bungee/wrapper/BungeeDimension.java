package de.cubenation.bedrock.bungee.wrapper;

import de.cubenation.bedrock.bukkit.wrapper.BukkitDimension;
import de.cubenation.bedrock.core.wrapper.BedrockDimension;

public class BungeeDimension implements BedrockDimension {

    private String server;
    private BukkitDimension dimension;

    public BungeeDimension(String server, BukkitDimension dimension) {
        this.server = server;
        this.dimension = dimension;
    }

    @Override
    public String getName() {
        return server+"-"+dimension.getName();
    }
}
