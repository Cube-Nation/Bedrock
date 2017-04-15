package de.cubenation.api.bedrock.command.argument;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.permission.Permission;
import de.cubenation.api.bedrock.translation.Translation;
import org.bukkit.command.CommandSender;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 */
public class Argument {

    private BasePlugin plugin;

    private String description;

    private String placeholder;

    private boolean optional = false;

    private Permission permission = null;

    public Argument(BasePlugin plugin, String description, String placeholder, boolean optional, Permission permission) {
        this.setPlugin(plugin);
        this.setDescription(description);
        this.setPlaceholder(placeholder);
        this.setOptional(optional);
        this.setPermission(permission);
    }

    // plugin
    public void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }

    public BasePlugin getPlugin() {
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

    public boolean userHasPermission(CommandSender sender) {
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
