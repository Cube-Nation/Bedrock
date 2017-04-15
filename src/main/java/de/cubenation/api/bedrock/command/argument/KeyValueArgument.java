package de.cubenation.api.bedrock.command.argument;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.permission.Permission;

import java.util.HashMap;

/**
 * Created by BenediktHr on 27.07.15.
 * Project: Bedrock
 */
public class KeyValueArgument extends Argument {

    private String key = "";

    private Boolean keyOnly = false;

    public KeyValueArgument(BasePlugin plugin, String key, String description, String placeholder, boolean optional, Permission permission) {
        super(plugin, description, placeholder, optional, permission);
        this.setKey(key);
    }

    // key
    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getValue(HashMap<String, String> args) {
        if (args.containsKey(this.getKey())) {
            return args.get(this.getKey());
        }

        return null;
    }


    // key only
    public void setKeyOnly(Boolean keyOnly) {
        this.keyOnly = keyOnly;
    }

    public Boolean getKeyOnly() {
        return keyOnly;
    }

    @Override
    public String toString() {
        return "KeyValueArgument{" +
                "key='" + key + '\'' +
                ", keyOnly=" + keyOnly +
                "  description='" + this.getDescription() + '\'' +
                ", runTimeDescription='" + this.getRuntimeDescription() + '\'' +
                ", placeholder='" + this.getPlaceholder() + '\'' +
                ", runTimePlaceholder='" + this.getRuntimePlaceholder() + '\'' +
                ", optional=" + this.isOptional() +
                ", permission=" + this.getPermission() +
                '}';
    }

}
