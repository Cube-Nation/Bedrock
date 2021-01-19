package de.cubenation.bedrock.bukkit.wrapper;

import de.cubenation.bedrock.core.wrapper.BedrockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public class BukkitPosition implements BedrockPosition {

    private final Location location;

    protected BukkitPosition(Location location) {
        this.location = location;
    }

    protected BukkitPosition(BukkitDimension dimension, double x, double y, double z, float yaw, float pitch) {
        this.location = new Location(Bukkit.getWorld(dimension.getName()), x, y, z, yaw, pitch);
    }

    protected BukkitPosition(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    public static BukkitPosition wrap(Location location) {
        return new BukkitPosition(location);
    }

    public static BukkitPosition wrap(BukkitDimension dimension, double x, double y, double z, float yaw, float pitch) {
        return new BukkitPosition(dimension, x, y, z, yaw, pitch);
    }

    public static BukkitPosition wrap(String worldName, double x, double y, double z, float yaw, float pitch) {
        return new BukkitPosition(worldName, x, y, z, yaw, pitch);
    }

    public Location getLocation() {
        return location;
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
