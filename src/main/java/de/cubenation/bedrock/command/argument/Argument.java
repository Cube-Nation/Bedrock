package de.cubenation.bedrock.command.argument;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.permission.Permission;
import de.cubenation.bedrock.translation.Translation;
import org.bukkit.command.CommandSender;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class Argument {

    private final String description;
    private final String placeholder;
    private final boolean optional;
    private final Permission permission;

    private String runtimeDescription;
    private String runtimePlaceholder;
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
        this(description, placeholder, false, null);
    }

    public String getRuntimeDescription() {
        return new Translation(this.plugin, this.description).getTranslation();
    }

    public String getRuntimePlaceholder() {
        return new Translation(this.plugin, this.placeholder).getTranslation();
    }

    public Permission getPermission() {
        return permission;
    }

    public boolean isOptional() {
        return optional;
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
