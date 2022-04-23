package de.cubenation.bedrock.bukkit.wrapper;

import de.cubenation.bedrock.core.model.wrapper.BedrockDimension;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.World;

import java.util.Objects;

@SuppressWarnings("unused")
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BukkitDimension implements BedrockDimension {

    @Getter
    private final World world;

    public static BukkitDimension wrap(World world) {
        return new BukkitDimension(world);
    }

    @Override
    public String getName() {
        return world.getName();
    }

    @Override
    public String toPrintableString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BukkitDimension that)) return false;
        return world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world);
    }
}
