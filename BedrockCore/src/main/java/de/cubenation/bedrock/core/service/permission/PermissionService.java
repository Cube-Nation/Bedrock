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

package de.cubenation.bedrock.core.service.permission;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.injection.Inject;
import de.cubenation.bedrock.core.authorization.Permission;
import de.cubenation.bedrock.core.model.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.authorization.Role;
import de.cubenation.bedrock.core.config.PermissionsConfig;
import de.cubenation.bedrock.core.exception.PlayerNotFoundException;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;
import de.cubenation.bedrock.core.service.config.ConfigService;
import lombok.ToString;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@ToString
public class PermissionService extends AbstractService {

    @Inject
    private ConfigService configService;

    private final ArrayList<Permission> externalPermissions = new ArrayList<>();

    private ArrayList<Permission> localPermissionCache = new ArrayList<>();

    public PermissionService(FoundationPlugin plugin) {
        super(plugin);
    }

    public String getPermissionPrefix() {
        return (String) getConfigurationValue(
                "service.permission.prefix",
                plugin.getPluginDescription().getName().toLowerCase()
        );
    }

    @Override
    public void init() throws ServiceInitException {
        //this.getPlugin().log(Level.INFO, "  permission service: setting up " + this.toString());
        try {
            configService.registerFile(
                    PermissionsConfig.class,
                    new PermissionsConfig(plugin)
            );
        } catch (Exception e) {
            throw new ServiceInitException(e.getMessage());
        }

        initializePermissions();
    }

    @Override
    public void reload() throws ServiceReloadException {
        initializePermissions();
    }

    @Deprecated
    public void registerPermission(String role, String permission) throws NullPointerException {
        registerPermission(permission, Role.valueOf(role));
    }

    @SuppressWarnings("unused")
    public void registerPermission(String permission) {
        registerPermission(new Permission(permission));
    }

    @SuppressWarnings("WeakerAccess")
    public void registerPermission(String permission, Role role) {
        registerPermission(new Permission(permission, role));
    }

    @SuppressWarnings("WeakerAccess")
    public void registerPermission(Permission permission) {
        List<Permission> exists = this.externalPermissions.stream()
                .filter(permissionObject ->
                        permissionObject.getName().equals(permission.getName()) && permissionObject.getRole().equals(permission.getRole()))
                .toList();

        if (exists.size() == 0) {
            externalPermissions.add(permission);
            initializePermissions();
        }
    }

    private void addPermission(Permission permission) {
        if (permission.getName() == null) return;

        permission.setPlugin(plugin);

        List<Permission> exists = localPermissionCache.stream()
                .filter(cachedPermission ->
                        cachedPermission.getName().equals(permission.getName())
                ).toList();

        if (exists.size() == 0) {
            localPermissionCache.add(permission);
        }
    }

    @SuppressWarnings("unused")
    public Permission getPermission(String rawPermission) {
        if (rawPermission == null) {
            return null;
        }

        return getPermissions().stream()
                .filter(permission -> permission.getName().equalsIgnoreCase(rawPermission))
                .findFirst()
                .orElse(null);
    }

    private void initializePermissions() {
        // re-initialize local permission cache
        localPermissionCache = new ArrayList<>();

        /*
        We need to reload the permission file first to make sure the Permissions class knows its current
        content.
        This must be done for the case a permission has been moved to another role or removed from a role.
        The YamlConfiguration object would not know about the move/remove and would still have the permission
        in it's cache, so the move/remove would not be recognized.
        */
        PermissionsConfig permissions = (PermissionsConfig) configService.getConfig(PermissionsConfig.class);
        try {
            permissions.reload();

            permissions.getAll().forEach(
                    (commandRole, permissionList) -> permissionList.forEach(permission -> {

                        // To self-repair NO_ROLE permissions we ignore them here.
                        // They will be added later to their approriate role (or NO_ROLE) again
                        if (commandRole.equals(Role.NO_ROLE)) {
                            return;
                        }

                        addPermission(new Permission(permission, commandRole));
                    }));
        } catch (InvalidConfigurationException e) {
            plugin.log(Level.SEVERE, "While reloading permissions: " + e.getCause(), e);
            return;
        }

        // collect permissions from commands (and arguments)
        // TODO: Permissions
//        this.getPlugin().getCommandService().getCommandManagers().forEach(commandManager ->
//                commandManager.getCommandPaths().forEach(abstractCommand -> {
//
//                    abstractCommand.getRuntimePermissions().forEach(this::addPermission);
//
//                    abstractCommand.getArguments().forEach(argument -> {
//                        if (argument.getPermission() != null) {
//                            this.addPermission(argument.getPermission());
//                        }
//                    });
//
//                }));

        // collect externally registered permissions from userland
        externalPermissions.forEach(this::addPermission);

        savePermissions(permissions);
    }

    private void savePermissions(PermissionsConfig permissions) {
        // clean up default role - will be restored if necessary
        permissions.removeRole(Role.NO_ROLE);

        // restore missing permissions
        localPermissionCache.forEach(permission -> {
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
            plugin.log(Level.SEVERE, "  permission service: Could not save permission file", e);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public boolean hasPermission(BedrockChatSender sender, Permission permission) {
        // op check
        if (sender.isOp() && (Boolean) getConfigurationValue("service.permission.grant_all_permissions_to_op", true)) {
            return true;
        }

        // let the Permission object do the decision
        // this can fail in case a permission is in the NO_ROLE role by default or the plugin is not known yet
        if (sender.hasPermission(permission.getPermissionNode())) {
            return true;
        }

        // check again with full permission node (including permission prefix for role)
        Role role = ((PermissionsConfig) configService.getConfig(PermissionsConfig.class)).getRoleForPermission(permission.getName());
        if (role != null) {
            return sender.hasPermission(String.format("%s.%s.%s", getPermissionPrefix(), role.getType().toLowerCase(), permission.getName()));
        }

        return false;
    }

    @SuppressWarnings("unused")
    public boolean hasPermission(BedrockChatSender sender, String permission) {
        List<Permission> filtered = localPermissionCache.stream()
                .filter(cachedPermission -> cachedPermission.getName().equals(permission))
                .toList();

        for (Permission filteredPermission : filtered) {
            if (hasPermission(sender, filteredPermission)) {
                return true;
            }
        }

        return false;
    }

    public List<Permission> getPermissions() {
        return localPermissionCache;
    }

    public List<Permission> getPermissions(BedrockChatSender player) throws PlayerNotFoundException {
        if (player == null) {
            throw new PlayerNotFoundException();
        }

        return localPermissionCache.stream()
                .filter(permission -> permission.userHasPermission(player))
                .collect(Collectors.toList());
    }
}
