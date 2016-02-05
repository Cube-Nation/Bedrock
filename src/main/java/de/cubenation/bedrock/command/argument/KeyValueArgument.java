package de.cubenation.bedrock.command.argument;

import de.cubenation.bedrock.permission.Permission;
import de.cubenation.bedrock.translation.Translation;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command.argument
 */
public class KeyValueArgument extends Argument {

    private final String key;

    @SuppressWarnings(value = "unused")
    public KeyValueArgument(String key, String description, String placeholder, boolean optional, Permission permission) {
        super(description, placeholder, optional, permission);
        this.key = key;

    }
    @SuppressWarnings(value = "unused")
    public KeyValueArgument(String key, String description, String placeholder, boolean optional) {
        super(description, placeholder, optional);
        this.key = key;
    }

    @SuppressWarnings(value = "unused")
    public KeyValueArgument(String key, String description, String placeholder) {
        super(description, placeholder, false);
        this.key = key;
    }

    public String getRuntimeKey() {
        return new Translation(getPlugin(), this.key).getTranslation();
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "KeyValueArgument{" +
                "key='" + key + '\'' +
                ", description='" + getRuntimeDescription() + '\'' +
                ", placeholder=" + getRuntimePlaceholder() +
                ", optional=" + isOptional() +
                '}';
    }
}
