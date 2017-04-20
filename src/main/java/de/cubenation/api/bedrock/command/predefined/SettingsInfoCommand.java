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

package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.Argument;
import de.cubenation.api.bedrock.annotation.Description;
import de.cubenation.api.bedrock.annotation.Permission;
import de.cubenation.api.bedrock.annotation.SubCommand;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.api.bedrock.exception.NoSuchPlayerException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.service.command.CommandManager;
import de.cubenation.api.bedrock.service.settings.CustomSettingsFile;
import de.cubenation.api.bedrock.service.settings.SettingsManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Description("command.bedrock.info.desc")
@Permission(Name = "settings.list", Role = CommandRole.ADMIN)
@SubCommand({ "settings" })
@SubCommand({ "info", "i" })
@Argument(Description = "command.bedrock.key.desc", Placeholder = "command.bedrock.key.ph")
@Argument(Description = "command.bedrock.username_uuid.desc", Placeholder = "command.bedrock.username_uuid.ph", Optional = true)
public class SettingsInfoCommand extends Command {

    public SettingsInfoCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {

        String settingsKey = args[0];
        SettingsManager settingsManager = plugin.getSettingService().getSettingsManager(settingsKey);
        if (settingsManager == null) {
            MessageHelper.Error.SettingsNotFound(plugin, sender, settingsKey);
            return;
        }

        // TODO: Make it fancy!
        if (args.length > 1) {
            String user = args[1];
            try {
                CustomSettingsFile settings = settingsManager.getSettings(user);
                if (settings != null) {
                    sender.sendMessage(settings.info());
                } else {
                    sender.sendMessage("User " + user + " uses the default settings:");
                    sender.sendMessage(settingsManager.getDefaultFile().info());
                }
            } catch (NoSuchPlayerException e) {
                MessageHelper.noSuchPlayer(plugin, sender, user);
            }

        } else {
            sender.sendMessage("Default:");
            sender.sendMessage(settingsManager.getDefaultFile().info());
            sender.sendMessage(settingsManager.getUserSettings().size() + " User Settings");
        }


        ///TODO - B1acksheep
    }

    @Override
    public ArrayList<String> getTabArgumentCompletion(CommandSender sender, int argumentIndex, String[] args) {
        if (argumentIndex == 0) {
            return new ArrayList<String>() {{
                addAll(plugin.getSettingService().getSettingsMap().keySet());
            }};

        }
        return null;
    }
}
