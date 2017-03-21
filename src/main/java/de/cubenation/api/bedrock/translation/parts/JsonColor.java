package de.cubenation.api.bedrock.translation.parts;

/**
 * Created by BenediktHr on 04.03.16.
 * Project: Bedrock
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
