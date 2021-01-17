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

import de.cubenation.bedrock.bungee.plugin.helper.BungeeTeleportHelper;
import de.cubenation.bedrock.core.exception.WrongBedrockImplementationException;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import de.cubenation.bedrock.core.wrapper.BedrockPosition;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang.NotImplementedException;

import java.util.UUID;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public class BungeePlayer extends BungeeChatSender implements BedrockPlayer {

    private final ProxiedPlayer player;

    public BungeePlayer(ProxiedPlayer player) {
        super(player);
        this.player = player;
    }

    public ProxiedPlayer getPlayer() {
        return player;
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
        player.sendMessage(position, component);
    }

    @Override
    public void sendMessage(ChatMessageType position, BaseComponent... components) {
        player.sendMessage(position, components);
    }

    @Override
    public void teleport(BedrockPosition pos) {
        BungeeTeleportHelper.executeTeleport(this, (BungeePosition) pos);
    }

    @Override
    public void teleport(BedrockPlayer target) {
        BungeeTeleportHelper.executeTeleport(this, (BungeePlayer) target);
    }

    @Override
    public BedrockPosition getPosition() {
        // TODO: implement BungeePosition callback
        throw new NotImplementedException();
    }
}

