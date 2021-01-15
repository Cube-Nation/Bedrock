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

package de.cubenation.bedrock.bungee.wrapper;

import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class BungeeChatSender implements BedrockChatSender {

    private final net.md_5.bungee.api.CommandSender commandSender;

    public BungeeChatSender(net.md_5.bungee.api.CommandSender original) {
        this.commandSender = original;
    }

    public net.md_5.bungee.api.CommandSender getCommandSender() {
        return commandSender;
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public boolean hasPermission(String permission) {
        return commandSender.hasPermission(permission);
    }

    @Override
    public void sendMessage(String legacyText) {
        commandSender.sendMessage(legacyText);
    }

    @Override
    public void sendMessage(String[] messages) {
        commandSender.sendMessages(messages);
    }

    @Override
    public String getName() {
        return commandSender.getName();
    }

    @Override
    public void sendMessage(BaseComponent component) {
        commandSender.sendMessage(component);
    }

    @Override
    public void sendMessage(BaseComponent... components) {
        commandSender.sendMessage(components);
    }
}

