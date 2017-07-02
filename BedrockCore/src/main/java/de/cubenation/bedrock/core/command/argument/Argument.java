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

package de.cubenation.bedrock.core.command.argument;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.BedrockCommandSender;
import de.cubenation.bedrock.core.service.permission.Permission;
import de.cubenation.bedrock.core.translation.Translation;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class Argument {

    private FoundationPlugin plugin;

    private String description;

    private String placeholder;

    private boolean optional = false;

    private Permission permission = null;

    public Argument(FoundationPlugin plugin, String description, String placeholder, boolean optional, Permission permission) {
        this.setPlugin(plugin);
        this.setDescription(description);
        this.setPlaceholder(placeholder);
        this.setOptional(optional);
        this.setPermission(permission);
    }

    // plugin
    public void setPlugin(FoundationPlugin plugin) {
        this.plugin = plugin;
    }

    public FoundationPlugin getPlugin() {
        return plugin;
    }

    // description
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public String getRuntimeDescription() {
        return new Translation(this.plugin, this.description).getTranslation();
    }

    // placeholder
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getRuntimePlaceholder() {
        if (this.placeholder == null)
            return null;

        return new Translation(this.plugin, this.placeholder).getTranslation();
    }

    // optional
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isOptional() {
        return optional;
    }

    // permission
    public void setPermission(Permission permission) {
        if (this.permission != null) {
            permission.setPlugin(this.plugin);
        }
        this.permission = permission;
    }

    public Permission getPermission() {
        return permission;
    }

    public boolean userHasPermission(BedrockCommandSender sender) {
        return this.permission == null || this.permission.userHasPermission(sender);
    }

    @Override
    public String toString() {
        return "Argument{" +
                "description='" + description + '\'' +
                ", runTimeDescription='" + this.getRuntimeDescription() + '\'' +
                ", placeholder='" + placeholder + '\'' +
                ", runTimePlaceholder='" + this.getRuntimePlaceholder() + '\'' +
                ", optional=" + optional +
                ", permission=" + permission.toString() +
                '}';
    }

}

