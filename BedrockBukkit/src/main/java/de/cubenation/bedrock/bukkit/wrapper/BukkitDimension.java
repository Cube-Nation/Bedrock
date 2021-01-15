package de.cubenation.bedrock.bukkit.wrapper;

import de.cubenation.bedrock.core.wrapper.BedrockDimension;
import org.bukkit.World;

public class BukkitDimension implements BedrockDimension {

    private World world;

    public BukkitDimension(World world) {
        this.world = world;
    }

    @Override
    public String getName() {
        return world.getName();
    }
}
