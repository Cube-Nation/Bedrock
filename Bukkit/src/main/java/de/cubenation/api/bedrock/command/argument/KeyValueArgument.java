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

package de.cubenation.api.bedrock.command.argument;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.service.permission.Permission;

import java.util.HashMap;

/**
 * @author Cube-Nation
 * @version 1.0
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
