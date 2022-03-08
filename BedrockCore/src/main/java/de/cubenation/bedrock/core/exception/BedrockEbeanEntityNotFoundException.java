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

package de.cubenation.bedrock.core.exception;

import de.cubenation.bedrock.core.model.BedrockOfflinePlayer;

import java.util.Collection;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings("DefaultFileTemplate")
public class BedrockEbeanEntityNotFoundException extends RuntimeException {

    public BedrockEbeanEntityNotFoundException(Class clazz, String uuid) {
        super(String.format("Could not find bedrock ebean entity %s for UUID %s", clazz.toString(), uuid));
    }

    public BedrockEbeanEntityNotFoundException(Class clazz, int id) {
        super(String.format("Could not find bedrock ebean entity %s for id %s", clazz.toString(), id));
    }

    public BedrockEbeanEntityNotFoundException(Class<BedrockOfflinePlayer> clazz, Collection<Integer> ids) {
        super(String.format("Could not find bedrock ebean entity %s for ids %s", clazz.toString(), ids));
    }

    public BedrockEbeanEntityNotFoundException(Collection<String> uuids, Class<BedrockOfflinePlayer> clazz) {
        super(String.format("Could not find bedrock ebean entity %s for ids %s", clazz.toString(), uuids));
    }
}
