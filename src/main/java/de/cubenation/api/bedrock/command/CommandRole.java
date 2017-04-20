package de.cubenation.api.bedrock.command;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@SuppressWarnings("unused")
public enum CommandRole {
    // all permissions with no role are in this role by default
    NO_ROLE("no_role"),

    // assign permissions to this role if you don't want to use them
    UNUSED("unused"),

    // guest users
    GUEST("guest"),

    // default users
    USER("user"),

    // more privileged users
    SUPPORTER("supporter"),

    // much more privileged users
    MODERATOR("moderator"),

    // administrators
    ADMIN("admin"),

    // root
    OWNER("owner");

    private final String type;

    CommandRole(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
