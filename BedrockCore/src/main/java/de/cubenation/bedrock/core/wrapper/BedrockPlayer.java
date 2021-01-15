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

package de.cubenation.bedrock.core.wrapper;

import de.cubenation.bedrock.core.exception.WrongBedrockImplementationException;

import java.util.UUID;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public interface BedrockPlayer extends BedrockChatSender {

    /**
     * Gets the "friendly" name to display of this player. This may include
     * color.
     * <p>
     * Note that this name will not be displayed in game, only in chat and
     * places defined by plugins.
     *
     * @return the friendly name
     */
    String getDisplayName();

    /**
     * Sets the "friendly" name to display of this player. This may include
     * color.
     * <p>
     * Note that this name will not be displayed in game, only in chat and
     * places defined by plugins.
     *
     * @param name The new display name.
     */
    void setDisplayName(String name);

    /**
     * Gets the players unique ID
     * @return UUID of the Player
     */
    UUID getUniqueId();

    /**
     * Says a message (or runs a command).
     *
     * @param msg message to print
     */
    void chat(String msg);

    /**
     * Sends the component to the specified screen position of this player
     *
     * @param position  the screen position
     * @param component the components to send
     */
    void sendMessage(net.md_5.bungee.api.ChatMessageType position, net.md_5.bungee.api.chat.BaseComponent component);

    /**
     * Sends an array of components as a single message to the specified screen position of this player
     *
     * @param position   the screen position
     * @param components the components to send
     */
    void sendMessage(net.md_5.bungee.api.ChatMessageType position, net.md_5.bungee.api.chat.BaseComponent... components);

    /**
     * Teleports the player to a given position
     *
     * @param pos   the desired position
     */
    void teleport(BedrockPosition pos) throws WrongBedrockImplementationException;
}
