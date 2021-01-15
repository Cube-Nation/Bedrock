package de.cubenation.bedrock.bungee.wrapper;

import de.cubenation.bedrock.bukkit.wrapper.BukkitPosition;
import de.cubenation.bedrock.core.wrapper.BedrockPosition;

public class BungeePosition implements BedrockPosition {

    private String server;
    private BukkitPosition position;

    public BungeePosition(String server, BukkitPosition position) {
        this.server = server;
        this.position = position;
    }

    @Override
    public BungeeDimension getDimension() {
        return new BungeeDimension(server, position.getDimension());
    }

    @Override
    public String getPrettyToString() {
        return null;
    }
}
