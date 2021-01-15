package de.cubenation.bedrock.bukkit.wrapper;

import de.cubenation.bedrock.core.wrapper.BedrockDimension;
import de.cubenation.bedrock.core.wrapper.BedrockPosition;
import org.bukkit.Location;

public class BukkitPosition implements BedrockPosition {

    private Location location;

    public BukkitPosition(Location location) {
        this.location = location;
    }

    @Override
    public BukkitDimension getDimension() {
        return new BukkitDimension(location.getWorld());
    }

    @Override
    public String getPrettyToString() {
        return location.getWorld().getName()+", "+
                location.getBlockX()+", "+
                location.getBlockY()+", "+
                location.getBlockZ();
    }
}
