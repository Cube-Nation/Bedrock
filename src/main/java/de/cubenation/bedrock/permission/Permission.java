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

    //region Properties
    private final PermissionService permissionService;
    private final BasePlugin plugin;
    private String name;
    //endregion


    //region Constructors

    public Permission(String name, BasePlugin plugin) {
        this.name = name;
        this.plugin = plugin;
        this.permissionService = plugin.getPermissionService();

        setupPermission();
    }

    //endregion

    private void setupPermission() {
        if (permissionService != null) {
            permissionService.registerPermission(getName());
        }
    }

    public boolean userHasPermission(CommandSender sender) {
        if (sender.isOp()) {
            return true;
        }

        if (getName() == null) {
            return true;
        }

        if (getName().equalsIgnoreCase("")) {
            return true;
        }

        if (permissionService != null) {
            if (sender.hasPermission(getServicePermission())) {
                return true;
            }
        } else {
            if (plugin.usePermissionService()) {
                if (sender.hasPermission(getDefaultPermission())) {
                    return true;
                }
            } else {
                if (sender.hasPermission(getName())) {
                    return true;
                }
            }

        }
        return false;
    }

    //region Getter

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    private String getServicePermission() {
        return permissionService.getPermissionWithRole(getName());
    }

    public String getDefaultPermission() {
        return plugin.getExplicitPermissionPrefix() + "." + getName();
    }
    //endregion


}
