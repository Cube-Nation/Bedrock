package de.cubenation.bedrock.permission;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.permission.PermissionService;
import org.bukkit.command.CommandSender;

/**
 * Created by B1acksheep on 04.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.name
 */
public class Permission {

    private BasePlugin plugin;
    private String name;
    private String role;


    public Permission(String name) {
        this(name, PermissionService.no_role);
    }

    public Permission(String name, String role) {
        this.name = name;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
