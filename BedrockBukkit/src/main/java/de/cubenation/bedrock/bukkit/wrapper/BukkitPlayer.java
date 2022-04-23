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

import de.cubenation.bedrock.core.model.wrapper.BedrockPlayer;
import de.cubenation.bedrock.core.model.wrapper.BedrockPosition;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings("unused")
@ToString
public class BukkitPlayer extends BukkitChatSender implements BedrockPlayer {

    @Getter
    private final Player player;

    protected BukkitPlayer(Player player) {
        super(player);
        this.player = player;
    }

    public static BukkitPlayer wrap(Player player) {
        return new BukkitPlayer(player);
    }

    @Override
    public String getDisplayName() {
        return player.getDisplayName();
    }

    @Override
    public void setDisplayName(String name) {
        player.setDisplayName(name);
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public void chat(String msg) {
        player.chat(msg);
    }

    @Override
    public void sendMessage(ChatMessageType position, BaseComponent component) {
        player.spigot().sendMessage(position, component);
    }

    @Override
    public void sendMessage(ChatMessageType chatMessageType, BaseComponent[] components) {
        player.spigot().sendMessage(chatMessageType, components);
    }

    @Override
    public void teleport(BedrockPosition pos) {
        System.out.println("teleport");
        player.teleport(((BukkitPosition) pos).getLocation());
    }

    @Override
    public void teleport(BedrockPlayer target) {
        player.teleport(((BukkitPlayer)target).getPlayer());
    }

    @Override
    public BedrockPosition getPosition() {
        return new BukkitPosition(player.getLocation());
    }
}
