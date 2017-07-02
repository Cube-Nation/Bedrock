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

package de.cubenation.bedrock.bukkit.api.message;

import de.cubenation.bedrock.bukkit.api.helper.FacingDirection;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.translation.Translation;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class Messages extends de.cubenation.bedrock.core.message.Messages {

    public Messages(FoundationPlugin foundationPlugin) {
        super(foundationPlugin);
    }


    public String direction(FoundationPlugin plugin, FacingDirection facingDirection) {
        String string = "";
        switch (facingDirection) {
            case SOUTH:
                string = new Translation(plugin, "direction.south").getTranslation();
                break;
            case SOUTHWEST:
                string = new Translation(plugin, "direction.southwest").getTranslation();
                break;
            case WEST:
                string = new Translation(plugin, "direction.west").getTranslation();
                break;
            case NORTHWEST:
                string = new Translation(plugin, "direction.nothwest").getTranslation();
                break;
            case NORTH:
                string = new Translation(plugin, "direction.north").getTranslation();
                break;
            case NORTHEAST:
                string = new Translation(plugin, "direction.northeast").getTranslation();
                break;
            case EAST:
                string = new Translation(plugin, "direction.east").getTranslation();
                break;
            case SOUTHEAST:
                string = new Translation(plugin, "direction.southeast").getTranslation();
                break;
        }
        return string;
    }

}
