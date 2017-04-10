package de.cubenation.api.bedrock.command;

/**
 * Created by BenediktHr on 27.01.16.
 * Project: CNPlots
 */

@SuppressWarnings("unused")
public enum CommandRole {
    NO_ROLE("no_role"),
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
