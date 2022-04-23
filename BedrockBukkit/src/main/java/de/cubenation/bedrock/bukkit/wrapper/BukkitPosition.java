package de.cubenation.bedrock.bukkit.wrapper;

import de.cubenation.bedrock.core.model.wrapper.BedrockPosition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings("unused")
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BukkitPosition implements BedrockPosition {

    @Getter
    private final Location location;

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

    @Override
    public BukkitDimension getDimension() {
        return new BukkitDimension(location.getWorld());
    }

    @Override
    public String toPrintableString() {
        return String.format("%s (%s, %s, %s)", getDimension().toPrintableString(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BukkitPosition that)) return false;
        return location.equals(that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }
}
