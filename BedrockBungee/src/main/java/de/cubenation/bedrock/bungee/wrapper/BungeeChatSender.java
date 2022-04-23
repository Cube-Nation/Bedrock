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

import de.cubenation.bedrock.core.model.MappedModel;
import de.cubenation.bedrock.core.model.wrapper.BedrockChatSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Objects;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public class BungeeChatSender extends MappedModel implements BedrockChatSender {

    private final net.md_5.bungee.api.CommandSender commandSender;

    protected BungeeChatSender(net.md_5.bungee.api.CommandSender original) {
        this.commandSender = original;
    }

    @SuppressWarnings("unused")
    public net.md_5.bungee.api.CommandSender getCommandSender() {
        return commandSender;
    }

    public static BungeeChatSender wrap(CommandSender sender) {
        if(sender instanceof ProxiedPlayer)
            return new BungeePlayer((ProxiedPlayer) sender);
        return new BungeeChatSender(sender);
    }

    @Deprecated
    @Override
    public boolean isOp() {
        return false; // There is nothing like op on Bungee
    }

    @Override
    public boolean hasPermission(String permission) {
        // allow star wildcard with BungeeBedrock permissions
        String[] parts = permission.split("\\.");
        StringBuilder currentNode = new StringBuilder();
        for (int i = 0; i < parts.length-1; i++) {
            currentNode.append(parts[i]).append(".");
            if(commandSender.hasPermission(currentNode+"*"))
                return true;
        }

        // default permission validation
        return commandSender.hasPermission(permission);
    }

    @Deprecated
    @Override
    public void sendMessage(String legacyText) {
        commandSender.sendMessage(legacyText);
    }

    @Deprecated
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BungeeChatSender that)) return false;
        return commandSender.equals(that.commandSender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandSender);
    }
}

