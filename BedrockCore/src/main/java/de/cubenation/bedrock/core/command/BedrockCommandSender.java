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

package de.cubenation.bedrock.core.command;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public interface BedrockCommandSender {

    /**
     * Checks if this object is a server operator
     *
     * @return true if this is an operator, otherwise false
     */
    boolean isOp();

    /**
     * Gets the value of the specified permission, if set.
     * <p>
     * If a permission override is not set on this object, the default value
     * of the permission will be returned.
     *
     * @param permission Name of the permission
     * @return Value of the permission
     */
    boolean hasPermission(String permission);

    /**
     * Sends this sender a message
     *
     * @param message Message to be displayed
     */
    void sendMessage(String message);

    /**
     * Sends this sender multiple messages
     *
     * @param messages An array of messages to be displayed
     */
    void sendMessage(String[] messages);

    /**
     * Gets the name of this command sender
     *
     * @return Name of the sender
     */
    String getName();

    /**
     * Sends this sender a chat component.
     *
     * @param component the components to send
     */
    void sendMessage(net.md_5.bungee.api.chat.BaseComponent component);

    /**
     * Sends an array of components as a single message to the sender.
     *
     * @param components the components to send
     */
    void sendMessage(net.md_5.bungee.api.chat.BaseComponent... components);

}
