package de.cubenation.bedrock.bungee.wrapper;

import de.cubenation.bedrock.core.model.MappedModel;
import de.cubenation.bedrock.core.model.wrapper.BedrockDimension;
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
public class BungeeDimension extends MappedModel implements BedrockDimension {

    @Getter
    private final String server;
    @Getter
    private final String world;

    @SuppressWarnings("unused")
    public static BungeeDimension wrap(String server, String world) {
        return new BungeeDimension(server, world);
    }

    @Override
    public String getName() {
        return server+"-"+world;
    }

    @Override
    public String toPrintableString() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BungeeDimension that)) return false;
        return server.equals(that.server) && world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, world);
    }
}
