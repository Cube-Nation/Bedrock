package de.cubenation.bedrock.bukkit.wrapper;

import de.cubenation.bedrock.core.wrapper.BedrockDimension;
import org.bukkit.World;

public class BukkitDimension implements BedrockDimension {

    private World world;

    protected BukkitDimension(World world) {
        this.world = world;
    }

    public static BukkitDimension wrap(World world) {
        return new BukkitDimension(world);
    }

    @Override
    public String getName() {
        return world.getName();
    }
}
