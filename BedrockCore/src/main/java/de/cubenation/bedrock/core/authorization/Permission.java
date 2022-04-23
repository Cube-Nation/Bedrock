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

package de.cubenation.bedrock.core.authorization;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.model.wrapper.BedrockChatSender;

/**
 * The Permission class
 *
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings("unused")
public class Permission {

    private FoundationPlugin plugin;

    private String name;

    private Role role = Role.NO_ROLE;

    private String descriptionLocaleIdent;

    public Permission(String name) {
        this(name, Role.NO_ROLE, null);
    }

    public Permission(String name, Role role) {
        this(name, role, null);
    }

    public Permission(String name, Role role, String descriptionLocaleIdent) {
        this.setName(name);
        this.setRole(role);
        this.setDescriptionLocaleIdent(descriptionLocaleIdent);
    }

    public static Role getCommandRole(String roleName) {
        Role role = Role.NO_ROLE;

        for (Role commandRole : Role.values()) {
            if (commandRole.getType().toLowerCase().equals(roleName.toLowerCase())) {
                role = Role.valueOf(roleName.toUpperCase());
            }
        }

        return role;
    }

    public boolean userHasPermission(BedrockChatSender sender) {
        return getPlugin() != null &&
                        sender != null &&
                        plugin.getPermissionService().hasPermission(sender, this);
    }

    public FoundationPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(FoundationPlugin plugin) {
        this.plugin = plugin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
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
            if (this.getRole().equals(Role.NO_ROLE)) {
                return this.getName();
            } else {
                return String.format("%s.%s",
                        this.getRole().getType().toLowerCase(),
                        this.getName()
                );
            }
        }

        String permissionPrefix = this.getPlugin().getPermissionService().getPermissionPrefix();
        if (this.getRole().equals(Role.NO_ROLE)) {
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

