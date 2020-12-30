package de.cubenation.bedrock.implementation.bukkit.wrapper;

import de.cubenation.bedrock.core.wrapper.BedrockDimension;
import org.bukkit.World;

public class BukkitWorld implements BedrockDimension {

    private World world;

    public BukkitWorld(World world) {
        this.world = world;
    }

    public static BukkitWorld wrap(World world) {
        return new BukkitWorld(world);
    }

    public World getWorld() {
        return world;
    }

    @Override
    public String getName() {
        return world.getName();
    }
}
