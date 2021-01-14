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

package de.cubenation.api.bedrock.translation.parts;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@SuppressWarnings("unused")
public enum JsonColor {
    /**
     * Represents black.
     */
    BLACK("black"),
    /**
     * Represents dark blue.
     */
    DARK_BLUE("dark_blue"),
    /**
     * Represents dark green.
     */
    DARK_GREEN("dark_green"),
    /**
     * Represents dark blue (aqua).
     */
    DARK_AQUA("dark_aqua"),
    /**
     * Represents dark red.
     */
    DARK_RED("dark_red"),
    /**
     * Represents dark purple.
     */
    DARK_PURPLE("dark_purple"),
    /**
     * Represents gold.
     */
    GOLD("gold"),
    /**
     * Represents gray.
     */
    GRAY("gray"),
    /**
     * Represents dark gray.
     */
    DARK_GRAY("dark_gray"),
    /**
     * Represents blue.
     */
    BLUE("blue"),
    /**
     * Represents green.
     */
    GREEN("green"),
    /**
     * Represents aqua.
     */
    AQUA("aqua"),
    /**
     * Represents red.
     */
    RED("red"),
    /**
     * Represents light purple.
     */
    LIGHT_PURPLE("light_purple"),
    /**
     * Represents yellow.
     */
    YELLOW("yellow"),
    /**
     * Represents white.
     */
    WHITE("white"),

    /**
     * Represents the primary color.
     */
    PRIMARY("&PRIMARY&"),
    /**
     * Represents the secondary color.
     */
    SECONDARY("&SECONDARY&"),
    /**
     * Represents the flag color.
     */
    FLAG("&FLAG&"),
    /**
     * Represents the text color.
     */
    TEXT("&TEXT&");


    private final String code;

    JsonColor(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
