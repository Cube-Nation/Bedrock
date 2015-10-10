package de.cubenation.bedrock.command.argument;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.translation.Translation;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class Argument {

    private final String description;
    private final String placeholder;
    private final boolean optional;

    private String runtimeDescription;
    private String runtimePlaceholder;
    private BasePlugin plugin;

    @SuppressWarnings(value = "unused")
    public Argument(String description, String placeholder, boolean optional) {
        this.description = description;
        this.placeholder = placeholder;
        this.optional = optional;
    }

    @SuppressWarnings(value = "unused")
    public Argument(String description, String placeholder) {
        this(description, placeholder, false);
    }

    public String getRuntimeDescription() {
        return new Translation(this.plugin, this.description).getTranslation();
    }

    public String getRuntimePlaceholder() {
        return new Translation(this.plugin, this.placeholder).getTranslation();
    }

    public boolean isOptional() {
        return optional;
    }


    public void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String toString() {
        return "Argument{" +
                "description='" + runtimeDescription + '\'' +
                ", placeholder=" + runtimePlaceholder +
                ", optional=" + optional +
                '}';
    }

}
