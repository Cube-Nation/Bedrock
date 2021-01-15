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

/**
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings("unused")
public enum Role {
    // all permissions with no role are in this role by default
    NO_ROLE("no_role"),

    // assign permissions to this role if you don't want to use them
    UNUSED("unused"),

    // guest users
    GUEST("guest"),

    // default users
    USER("user"),

    // more privileged users
    SUPPORTER("supporter"),

    // much more privileged users
    MODERATOR("moderator"),

    // administrators
    ADMIN("admin"),

    // root
    OWNER("owner");

    private final String type;

    Role(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
