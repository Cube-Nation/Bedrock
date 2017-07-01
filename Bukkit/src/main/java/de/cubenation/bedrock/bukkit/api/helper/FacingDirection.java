/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.bedrock.bukkit.api.helper;

import org.bukkit.Location;

/**
 * @author Cube-Nation
 * @version 1.0
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
