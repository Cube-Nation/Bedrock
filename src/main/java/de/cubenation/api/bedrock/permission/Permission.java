package de.cubenation.api.bedrock.permission;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.CommandRole;
import org.bukkit.command.CommandSender;

/**
 * @author Cube-Nation
 * @version 1.0
 *
 * The Permission class
 */
@SuppressWarnings("unused")
public class Permission {

    private BasePlugin plugin;

    private String name;

    private CommandRole role = CommandRole.NO_ROLE;

    private String descriptionLocaleIdent;

    public Permission(String name) {
        this(name, CommandRole.NO_ROLE, null);
    }

    public Permission(String name, CommandRole role) {
        this(name, role, null);
    }

    public Permission(String name, CommandRole role, String descriptionLocaleIdent) {
        this.setName(name);
        this.setRole(role);
        this.setDescriptionLocaleIdent(descriptionLocaleIdent);
    }

    public static CommandRole getCommandRole(String roleName) {
        CommandRole role = CommandRole.NO_ROLE;

        for (CommandRole commandRole : CommandRole.values()) {
            if (commandRole.getType().equals(roleName.toUpperCase())) {
                role = CommandRole.valueOf(roleName.toUpperCase());
            }
        }

        return role;
    }

    public boolean userHasPermission(CommandSender sender) {
        return
                getPlugin() != null &&
                sender != null &&
                plugin.getPermissionService().hasPermission(sender, this);
    }

    public BasePlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommandRole getRole() {
        return role;
    }

    public void setRole(CommandRole role) {
        this.role = role;
    }

    public String getDescriptionLocaleIdent() {
        // By default the descriptive locale ident is null and will be auto-generated.
        // In case it's not null return it's value.
        if (this.descriptionLocaleIdent != null) {
            return this.descriptionLocaleIdent;
        }

        // auto-generate
        return String.format("help.permission.%s", this.getName());
    }

    public void setDescriptionLocaleIdent(String descriptionLocaleIdent) {
        this.descriptionLocaleIdent = descriptionLocaleIdent;
    }

    public String getPermissionNode() {
        if (this.getPlugin() == null) {
            if (this.getRole().equals(CommandRole.NO_ROLE)) {
                return this.getName();
            } else {
                return String.format("%s.%s",
                        this.getRole().getType().toLowerCase(),
                        this.getName()
                );
            }
        }

        String permissionPrefix = this.getPlugin().getPermissionService().getPermissionPrefix();
        if (this.getRole().equals(CommandRole.NO_ROLE)) {
            return String.format("%s.%s", permissionPrefix, this.getName());
        }

        return String.format("%s.%s.%s",
                permissionPrefix,
                this.getRole().getType().toLowerCase(),
                this.getName()
        );
    }

    @Override
    public String toString() {
        return "Permission{" +
                "name='" + name + '\'' +
                ", role=" + role +
                ", descriptionLocaleIdent='" + descriptionLocaleIdent + '\'' +
                '}';
    }

}
