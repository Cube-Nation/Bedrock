package de.cubenation.api.bedrock.helper;

import org.bukkit.Location;

/**
 * Created by BenediktHr on 28.01.16.
 * Project: Bedrock
 */

@SuppressWarnings("unused")
public enum FacingDirection {
    SOUTH(0),
    SOUTHWEST(45),
    WEST(90),
    NORTHWEST(135),
    NORTH(180),
    NORTHEAST(225),
    EAST(270),
    SOUTHEAST(315);

    private int value;

    FacingDirection(int value) {
        this.value = value;
    }

    public static FacingDirection directionForLocation(Location location) {
        return directionForYaw(location.getYaw());
    }

    public static FacingDirection directionForYaw(float yaw) {
        float value = yaw % 360;
        if (yaw < 0) {
            value += 360;
        }

        for (FacingDirection facingDirection : FacingDirection.values()) {
            if (facingDirection == SOUTH) {
                if ((value >= 360 - (45f / 2) && value <= 360) ||
                        (value >= (45f / 2) && value <= facingDirection.value + (45f / 2))) {
                    return facingDirection;
                }
            }

            if (value >= facingDirection.value - (45f / 2) && value <= facingDirection.value + (45f / 2)) {
                return facingDirection;
            }
        }

        // Shouldn't happen.
        throw new RuntimeException("Can't calulate direction for yaw: " + yaw);
    }

}
