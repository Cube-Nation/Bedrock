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

package de.cubenation.bedrock.bukkit.wrapper;

import de.cubenation.bedrock.core.model.MappedModel;
import de.cubenation.bedrock.core.model.wrapper.BedrockChatSender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings("unused")
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BukkitChatSender extends MappedModel implements BedrockChatSender {

    @Getter
    private final org.bukkit.command.CommandSender commandSender;

    public static BukkitChatSender wrap(CommandSender sender) {
        if(sender instanceof Player)
            return new BukkitPlayer((Player) sender);
        return new BukkitChatSender(sender);
    }

    @Override
    public boolean isOp() {
        return commandSender.isOp();
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
        commandSender.sendMessage(messages);
    }

    @Override
    public String getName() {
        return commandSender.getName();
    }

    @Override
    public void sendMessage(BaseComponent component) {
        commandSender.spigot().sendMessage(component);
    }

    @Override
    public void sendMessage(BaseComponent... components) {
        commandSender.spigot().sendMessage(components);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BukkitChatSender that)) return false;
        return commandSender.equals(that.commandSender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandSender);
    }
}
