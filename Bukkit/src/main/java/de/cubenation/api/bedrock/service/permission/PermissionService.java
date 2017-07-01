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

package de.cubenation.api.bedrock.service.permission;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.config.Permissions;
import de.cubenation.api.bedrock.exception.PlayerNotFoundException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;
import de.cubenation.bedrock.core.service.config.ConfigService;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class PermissionService extends AbstractService {

    private ConfigService configService;

    private ArrayList<Permission> externalPermissions = new ArrayList<>();

    private ArrayList<Permission> localPermissionCache = new ArrayList<>();

    public PermissionService(BasePlugin plugin) {
        super(plugin);
    }

    public String getPermissionPrefix() {
        return (String) this.getConfigurationValue(
                "service.permission.prefix",
                this.getPlugin().getDescription().getName().toLowerCase()
        );
    }

    @Override
    public void init() throws ServiceInitException {
        //this.getPlugin().log(Level.INFO, "  permission service: setting up " + this.toString());
        this.configService = plugin.getConfigService();

        try {
            this.configService.registerFile(
                    Permissions.class,
                    new Permissions(this.getPlugin())
            );
        } catch (Exception e) {
            throw new ServiceInitException(e.getMessage());
        }

        this.initializePermissions();
    }

    @Override
    public void reload() throws ServiceReloadException {
        this.initializePermissions();
    }

    @Deprecated
    public void registerPermission(String role, String permission) throws NullPointerException {
        this.registerPermission(permission, CommandRole.valueOf(role));
    }

    @SuppressWarnings("unused")
    public void registerPermission(String permission) {
        this.registerPermission(new Permission(permission));
    }

    @SuppressWarnings("WeakerAccess")
    public void registerPermission(String permission, CommandRole role) {
        this.registerPermission(new Permission(permission, role));
    }

    @SuppressWarnings("WeakerAccess")
    public void registerPermission(Permission permission) {
        List<Permission> exists = this.externalPermissions.stream()
                .filter(permissionObject ->
                        permissionObject.getName().equals(permission.getName()) && permissionObject.getRole().equals(permission.getRole()))
                .collect(Collectors.toList());

        if (exists.size() == 0) {
            this.externalPermissions.add(permission);
            this.initializePermissions();
        }
    }

    private void addPermission(Permission permission) {
        if (permission.getName() == null) return;

        permission.setPlugin(getPlugin());

        List<Permission> exists = this.localPermissionCache.stream()
                .filter(cachedPermission ->
                        cachedPermission.getName().equals(permission.getName())
                ).collect(Collectors.toList());

        if (exists.size() == 0) {
            this.localPermissionCache.add(permission);
        }
    }

    @SuppressWarnings("unused")
    public Permission getPermission(String rawPermission) {
        if (rawPermission == null)
            return null;

        return getPermissions().stream()
                .filter(permission -> permission.getName().equalsIgnoreCase(rawPermission))
                .findFirst()
                .orElse(null);
    }

    private void initializePermissions() {
        // re-initialize local permission cache
        this.localPermissionCache = new ArrayList<>();

        /*
        We need to reload the permission file first to make sure the Permissions class knows its current
        content.
        This must be done for the case a permission has been moved to another role or removed from a role.
        The YamlConfiguration object would not know about the move/remove and would still have the permission
        in it's cache, so the move/remove would not be recognized.
        */
        Permissions permissions = (Permissions) this.configService.getConfig(Permissions.class);
        try {
            permissions.reload();

            permissions.getAll().forEach(
                    (commandRole, permissionList) -> permissionList.forEach(permission -> {

                // To self-repair NO_ROLE permissions we ignore them here.
                // They will be added later to their approriate role (or NO_ROLE) again
                if (commandRole.equals(CommandRole.NO_ROLE)) {
                    return;
                }

                this.addPermission(new Permission(permission, commandRole));
            }));
        } catch (InvalidConfigurationException e) {
            plugin.log(Level.SEVERE, "While reloading permissions: " + e.getCause(), e);
            return;
        }

        // collect permissions from commands (and arguments)
        this.getPlugin().getCommandService().getCommandManagers().forEach(commandManager ->
                commandManager.getCommands().forEach(abstractCommand -> {

            abstractCommand.getRuntimePermissions().forEach(this::addPermission);

            abstractCommand.getArguments().forEach(argument -> {
                if (argument.getPermission() != null) {
                    this.addPermission(argument.getPermission());
                }
            });

        }));

        // collect externally registered permissions from userland
        this.externalPermissions.forEach(this::addPermission);

        this.savePermissions(permissions);
    }

    private void savePermissions(Permissions permissions) {
        // clean up default role - will be restored if necessary
        permissions.removeRole(CommandRole.NO_ROLE);

        // restore missing permissions
        this.localPermissionCache.forEach(permission -> {
            if (permissions.getRoleForPermission(permission.getName()) == null) {
                permissions.addPermission(permission);
            }
        });

        // save
        try {
            //this.getPlugin().log(Level.INFO, "  permission service: saving permissions");
            permissions.save();
            permissions.reload();
        } catch (InvalidConfigurationException e) {
            this.getPlugin().log(Level.SEVERE, "  permission service: Could not save permission file", e);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public boolean hasPermission(CommandSender sender, Permission permission) {
        // op check
        if (sender.isOp() && (Boolean) this.getConfigurationValue("service.permission.grant_all_permissions_to_op", true)) {
            return true;
        }

        // let the Permission object do the decision
        // this can fail in case a permission is in the NO_ROLE role by default or the plugin is not known yet
        if (sender.hasPermission(permission.getPermissionNode())) {
            return true;
        }

        // check again with full permission node (including permission prefix for role)
        CommandRole role = ((Permissions) plugin.getConfigService().getConfig(Permissions.class)).getRoleForPermission(permission.getName());
        if (role != null) {
            if (sender.hasPermission(String.format("%s.%s.%s", this.getPermissionPrefix(), role.getType().toLowerCase(), permission.getName()))) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unused")
    public boolean hasPermission(CommandSender sender, String permission) {
        List<Permission> filtered = this.localPermissionCache.stream()
                .filter(cachedPermission -> cachedPermission.getName().equals(permission))
                .collect(Collectors.toList());

        for (Permission filteredPermission : filtered) {
            if (this.hasPermission(sender, filteredPermission))
                return true;
        }

        return false;
    }

    public List<Permission> getPermissions() {
        return this.localPermissionCache;
    }

    public List<Permission> getPermissions(Player player) throws PlayerNotFoundException {
        if (player == null)
            throw new PlayerNotFoundException();

        return this.localPermissionCache.stream()
                .filter(permission -> permission.userHasPermission(player))
                .collect(Collectors.toList());
    }

    @Override
    protected BasePlugin getPlugin() {
        return (BasePlugin) super.getPlugin();
    }

    @Override
    public String toString() {
        return "PermissionService{" +
                "configService=" + configService +
                ", localPermissionCache=" + localPermissionCache.stream().map(Permission::toString) +
                ", externalPermissions=" + externalPermissions.stream().map(Permission::toString) +
                '}';
    }
}