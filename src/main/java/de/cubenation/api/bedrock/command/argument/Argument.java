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

    private final String description;
    private final String placeholder;
    private boolean optional = false;
    private Permission permission = null;

    private String runtimeDescription = null;
    private String runtimePlaceholder = null;
    private BasePlugin plugin;

    @SuppressWarnings(value = "unused")
    public Argument(String description, String placeholder, boolean optional, Permission permission) {
        this.description = description;
        this.placeholder = placeholder;
        this.optional = optional;
        this.permission = permission;
    }

    @SuppressWarnings(value = "unused")
    public Argument(String description, String placeholder, boolean optional) {
        this(description, placeholder, optional, null);
    }

    @SuppressWarnings(value = "unused")
    public Argument(String description, String placeholder, Permission permission) {
        this(description, placeholder, false, permission);
    }

    @SuppressWarnings(value = "unused")
    public Argument(String description, String placeholder) {
        this.description = description;
        this.placeholder = placeholder;
    }

    public String getRuntimeDescription() {
        return new Translation(this.plugin, this.description).getTranslation();
    }

    public String getRuntimePlaceholder() {
        return new Translation(this.plugin, this.placeholder).getTranslation();
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public Permission getPermission() {
        return permission;
    }

    public BasePlugin getPlugin() {
        return plugin;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
        if (permission != null) {
            permission.setPlugin(plugin);
        }
    }

    @Override
    public String toString() {
        return "Argument{" +
                "description='" + runtimeDescription + '\'' +
                ", placeholder=" + runtimePlaceholder +
                ", optional=" + optional +
                '}';
    }

    public boolean userHasPermission(CommandSender sender) {
        return permission == null || permission.userHasPermission(sender);

    }
}
