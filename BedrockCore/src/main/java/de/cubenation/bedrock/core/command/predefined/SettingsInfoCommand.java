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

package de.cubenation.bedrock.core.command.predefined;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.Argument;
import de.cubenation.bedrock.core.annotation.Description;
import de.cubenation.bedrock.core.annotation.Permission;
import de.cubenation.bedrock.core.annotation.SubCommand;
import de.cubenation.bedrock.core.authorization.Role;
import de.cubenation.bedrock.core.command.Command;
import de.cubenation.bedrock.core.command.CommandManager;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.exception.NoSuchPlayerException;
import de.cubenation.bedrock.core.helper.UUIDUtil;
import de.cubenation.bedrock.core.service.settings.CustomSettingsFile;
import de.cubenation.bedrock.core.service.settings.SettingsManager;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@Description("command.bedrock.info.desc")
@Permission(Name = "settings.list", Role = Role.ADMIN)
@SubCommand({ "settings" })
@SubCommand({ "info", "i" })
public class SettingsInfoCommand extends Command {

    public SettingsInfoCommand(FoundationPlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    public void execute(
            BedrockChatSender sender,
            @Argument(Description = "command.bedrock.key.desc", Placeholder = "command.bedrock.key.ph")
            String settingsKey,
            @Argument(Description = "command.bedrock.username_uuid.desc", Placeholder = "command.bedrock.username_uuid.ph")
            String user
    ) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {

        SettingsManager settingsManager = plugin.getSettingService().getSettingsManager(settingsKey);
        if (settingsManager == null) {
            plugin.messages().error().SettingsNotFound(sender, settingsKey);
            return;
        }

        // TODO: Make it fancy!
        if (user != null) {

            try {
                UUID uuid = null;
                if (UUIDUtil.isUUID(user)) {
                    uuid = UUID.fromString(user);
                } else {
                    BedrockPlayer player = plugin.getBedrockServer().getPlayer(user);
                    if (player == null) {
                        throw new NoSuchPlayerException(user);
                    }
                    uuid = player.getUniqueId();
                }

                if (uuid == null) {
                    throw new NoSuchPlayerException(user);
                }

                CustomSettingsFile settings = settingsManager.getSettings(uuid);
                if (settings != null) {
                    sender.sendMessage(settings.info());
                } else {
                    sender.sendMessage("User " + user + " uses the default settings:");
                    sender.sendMessage(settingsManager.getDefaultFile().info());
                }
            } catch (NoSuchPlayerException e) {
                plugin.messages().noSuchPlayer(sender, user);
            }

        } else {
            sender.sendMessage("Default:");
            sender.sendMessage(settingsManager.getDefaultFile().info());
            sender.sendMessage(settingsManager.getUserSettings().size() + " User Settings");
        }


        ///TODO - B1acksheep
    }

    // TODO: needs ArgumentType instead
    public ArrayList<String> getTabArgumentCompletion(BedrockChatSender sender, int argumentIndex, String[] args) {
        if (argumentIndex == 0) {
            return new ArrayList<String>() {{
                addAll(plugin.getSettingService().getSettingsMap().keySet());
            }};

        }
        return null;
    }
}
