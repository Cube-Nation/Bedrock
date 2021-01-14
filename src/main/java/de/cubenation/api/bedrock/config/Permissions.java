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

package de.cubenation.api.bedrock.config;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.service.config.CustomConfigurationFile;
import de.cubenation.api.bedrock.service.permission.Permission;
import net.cubespace.Yamler.Config.Path;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Permissions extends CustomConfigurationFile {

    public static String getFilename() {
        return "permissions.yaml";
    }

    public Permissions(BasePlugin plugin) throws IOException {
        CONFIG_FILE = new File(plugin.getDataFolder(), getFilename());
    }

    @Path("permissions")
    private HashMap<String, List<String>> permissions = new HashMap<String, List<String>>() {{
    }};


    // Roles

    /**
     * Get a map of all permissions per role
     *
     * @return A map of all permissions per role
     */
    public HashMap<CommandRole, ArrayList<String>> getAll() {
        HashMap<CommandRole, ArrayList<String>> map = new HashMap<>();

        this.permissions.forEach((role, permissions) ->
                map.put(Permission.getCommandRole(role), (ArrayList<String>) permissions)
        );

        return map;
    }

    public boolean hasRoles() {
        return (this.permissions.size() != 0);
    }

    private boolean roleExists(CommandRole role) {
        return this.permissions.containsKey(role.getType().toLowerCase());
    }

    private void createRole(CommandRole role) {
        if (!this.roleExists(role)) this.permissions.put(role.getType().toLowerCase(), new ArrayList<>());
    }

    public void removeRole(CommandRole role) {
        if (this.roleExists(role)) this.permissions.remove(role.getType());
    }

    public Set<CommandRole> getRoles() {
        return this.permissions.keySet().stream()
                .map(String::toUpperCase)
                .map(CommandRole::valueOf)
                .collect(Collectors.toSet());
    }


    // Permissions (role based)

    public void addPermission(String permission, CommandRole role) {
        if (permission == null) return;
        if (!this.roleExists(role)) this.createRole(role);
        if (this.getPermissionsFor(role).contains(permission)) return;

        this.permissions.get(role.getType().toLowerCase()).add(permission);
    }

    public void addPermission(String permission) {
        this.addPermission(permission, CommandRole.NO_ROLE);
    }

    public void addPermission(Permission permission) {
        this.addPermission(permission.getName(), permission.getRole());
    }

    public void addPermissions(List<String> permissions, CommandRole role) {
        if (permissions == null) return;
        permissions.forEach(permission -> this.addPermission(permission, role));
    }

    public void addPermissions(List<Permission> permissions) {
        if (permissions == null) return;
        permissions.forEach(permission -> this.addPermission(permission.getName(), permission.getRole()));
    }

    public void removePermission(String permission, CommandRole role) {
        if (!this.roleExists(role)) return;
        this.permissions.get(role.getType().toLowerCase()).remove(permission);
    }

    public void removePermissions(List<String> permissions, CommandRole role) {
        if (permissions == null) return;
        permissions.forEach(permission -> this.removePermission(permission, role));
    }

    public void removePermissions(List<Permission> permissions) {
        if (permissions == null) return;
        permissions.forEach(permission -> this.removePermission(permission.getName(), permission.getRole()));
    }

    public boolean permissionExists(String permission, CommandRole role) {
        return this.roleExists(role) && this.permissions.get(role.getType().toLowerCase()).contains(permission);
    }

    public CommandRole getRoleForPermission(String permission) {
        if (!this.hasRoles()) return null;

        for (CommandRole role : this.getRoles()) {
            if (this.permissions.get(role.getType().toLowerCase()).contains(permission)) return role;
        }

        return null;
    }

    public List<String> getPermissionsFor(CommandRole role) {
        if (!this.roleExists(role)) return new ArrayList<>();
        return this.permissions.get(role.getType().toLowerCase());
    }

}
