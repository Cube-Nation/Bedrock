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

package de.cubenation.bedrock.bungee.api.command.sender;

import de.cubenation.bedrock.core.command.BedrockPlayerCommandSender;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class PlayerSender extends CommandSender implements BedrockPlayerCommandSender {

    private final ProxiedPlayer player;

    public PlayerSender(ProxiedPlayer player) {
        super(player);
        this.player = player;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    @Override
    public void sendMessage(TextComponent components) {
        player.sendMessage(components);
    }

    @Override
    public void sendMessage(ChatMessageType chatMessageType, BaseComponent[] components) {
        player.sendMessage(chatMessageType, components);
    }

    @Override
    public void sendMessage(BaseComponent[] baseComponents) {
        player.sendMessage(baseComponents);
    }
}

