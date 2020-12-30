package de.cubenation.bedrock.implementation.bukkit.wrapper;

import de.cubenation.bedrock.core.wrapper.BedrockDimension;
import de.cubenation.bedrock.core.wrapper.BedrockPosition;
import org.bukkit.Location;

public class BukkitLocation implements BedrockPosition {

    private Location location;

    public BukkitLocation(Location location) {
        this.location = location;
    }

    public static BukkitLocation wrap(Location location) {
        return new BukkitLocation(location);
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public BedrockDimension getDimension() {
        return new BukkitWorld(location.getWorld());
    }

    @Override
    public String getPrettyToString() {
        return location.getWorld().getName()+", "+
                location.getBlockX()+", "+
                location.getBlockY()+", "+
                location.getBlockZ();
    }
}
