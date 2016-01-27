package de.cubenation.bedrock.command;

/**
 * Created by BenediktHr on 27.01.16.
 * Project: CNPlots
 */

public enum CommandRole {
    GUEST("guest"),
    USER("user"),
    SUPPORTER("supporter"),
    MODERATOR("moderator"),
    ADMIN("admin"),
    OWNER("owner");

    private final String type;

    CommandRole(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
