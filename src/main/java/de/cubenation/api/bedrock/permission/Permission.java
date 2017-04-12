package de.cubenation.api.bedrock.permission;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.CommandRole;
import org.bukkit.command.CommandSender;

/**
 * Created by B1acksheep on 04.04.15.
 * Project: Bedrock
 */
public class Permission {

    private BasePlugin plugin;

    private String name;

    private CommandRole role = CommandRole.NO_ROLE;

    private String roleName;

    public Permission(String name) {
        this(name, CommandRole.NO_ROLE);
    }

    @Deprecated
    public Permission(String name, String role) {
        this.name = name;
        this.role = CommandRole.valueOf(role.toUpperCase());
        this.roleName = role;
    }

    public Permission(String name, CommandRole role) {
        this.name = name;
        this.role = role;
        this.roleName = role.getType();
    }

    public boolean userHasPermission(CommandSender sender) {
        return getPlugin() != null && plugin.getPermissionService().hasPermission(sender, this.getName());
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

    @SuppressWarnings("unused")
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String role) {
        this.roleName = role;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "name='" + name + '\'' +
                ", role=" + role +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
