package de.cubenation.api.bedrock.permission;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.service.permission.PermissionService;
import org.bukkit.command.CommandSender;

/**
 * Created by B1acksheep on 04.04.15.
 * Project: Bedrock
 */
public class Permission {

    private BasePlugin plugin;

    private String name;
    private String roleName;
    private CommandRole role;


    public Permission(String name) {
        this(name, PermissionService.no_role);
    }

    public Permission(String name, String role) {
        this.name = name;
        this.roleName = role;
        this.role = null;
    }

    public Permission(String name, CommandRole role) {
        this.name = name;
        this.roleName = role.getType();
        this.role = role;

    }

    public boolean userHasPermission(CommandSender sender) {
        if (getPlugin() == null) {
            // Error -> no Permission
            return false;
        }

        return plugin.getPermissionService().hasPermission(sender, this.getName());
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String role) {
        this.roleName = role;
    }

    public CommandRole getRole() {
        return role;
    }

    public void setRole(CommandRole role) {
        this.role = role;
    }
}
