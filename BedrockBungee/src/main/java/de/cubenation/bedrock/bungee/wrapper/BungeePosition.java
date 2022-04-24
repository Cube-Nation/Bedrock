package de.cubenation.bedrock.bungee.wrapper;

import de.cubenation.bedrock.core.model.MappedModel;
import de.cubenation.bedrock.core.model.wrapper.BedrockPosition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BungeePosition extends MappedModel implements BedrockPosition {

    @Getter
    private final BungeeDimension dimension;
    @Getter
    private final double x;
    @Getter
    private final double y;
    @Getter
    private final double z;
    @Getter
    private final float yaw;
    @Getter
    private final float pitch;

    @SuppressWarnings("unused")
    public static BungeePosition wrap(BungeeDimension dimension, double x, double y, double z, float yaw, float pitch) {
        return new BungeePosition(dimension, x, y, z, yaw, pitch);
    }

    @Override
    public String toPrintableString() {
        return String.format("%s (%s, %s, %s)", dimension.toPrintableString(), Math.floor(x), Math.floor(y), Math.floor(z));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BungeePosition that)) return false;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0 && Float.compare(that.yaw, yaw) == 0 && Float.compare(that.pitch, pitch) == 0 && dimension.equals(that.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimension, x, y, z, yaw, pitch);
    }
}
