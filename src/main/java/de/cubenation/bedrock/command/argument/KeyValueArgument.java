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
    private Boolean keyOnly = false;

    @Deprecated
    @SuppressWarnings(value = "unused")
    public KeyValueArgument(String key, String description, String placeholder, boolean optional, Permission permission) {
        super(description, placeholder, optional, permission);
        this.key = key;

    }
    @Deprecated
    @SuppressWarnings(value = "unused")
    public KeyValueArgument(String key, String description, String placeholder, boolean optional) {
        super(description, placeholder, optional);
        this.key = key;
    }

    public KeyValueArgument(String key, String description, String placeholder) {
        super(description, placeholder);
        this.key = key;
    }

    public KeyValueArgument(String key, String description) {
        super(description, null);
        this.key = key;
    }

    public String getRuntimeKey() {
        return new Translation(getPlugin(), this.key).getTranslation();
    }

    @Override
    public String getRuntimePlaceholder() {
        if (getPlaceholder() == null) {
            return null;
        }
        return super.getRuntimePlaceholder();
    }

    public String getKey() {
        return key;
    }

    public Boolean getKeyOnly() {
        return keyOnly;
    }

    public void setKeyOnly(Boolean keyOnly) {
        this.keyOnly = keyOnly;
    }

    @Override
    public String toString() {
//        return getRuntimeKey();
        return "KeyValueArgument{" +
                "key='" + key + '\'' +
                ", description='" + getRuntimeDescription() + '\'' +
//                ", placeholder=" + (getRuntimePlaceholder() != null ? getRuntimePlaceholder()  : "null") +
                ", optional=" + isOptional() +
                '}';
    }

    public static class Builder {

        private final KeyValueArgument keyValueArgument;

        public Builder(String key, String description, String placeholder) {
            keyValueArgument = new KeyValueArgument(key, description, placeholder);
        }

        public Builder(String key, String description) {
            keyValueArgument = new KeyValueArgument(key, description);
            keyValueArgument.setKeyOnly(true);
        }

        public Builder setOptional(Boolean optional) {
            keyValueArgument.setOptional(optional);
            return this;
        }

        public Builder setPermission(Permission permission) {
            keyValueArgument.setPermission(permission);
            return this;
        }

        public Builder setKeyOnly(Boolean value) {
            keyValueArgument.setKeyOnly(value);
            return this;
        }

        public KeyValueArgument build() {
            return keyValueArgument;
        }
    }
}
