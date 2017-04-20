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
import de.cubenation.api.bedrock.annotation.Description;
import de.cubenation.api.bedrock.annotation.Permission;
import de.cubenation.api.bedrock.annotation.SubCommand;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.api.bedrock.exception.ServiceReloadException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.service.command.CommandManager;
import de.cubenation.api.bedrock.service.localization.LocalizationService;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Description("command.bedrock.regeneratelocale.desc")
@Permission(Name = "regeneratelocale", Role = CommandRole.ADMIN)
@SubCommand({ "regenerate" })
@SubCommand({ "locale" })
public class RegenerateLocaleCommand extends Command {

    public RegenerateLocaleCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {
        BasePlugin pluginInstance = this.getPlugin();
        LocalizationService localizationService = pluginInstance.getLocalizationService();

        File localeFile = new File(
                pluginInstance.getDataFolder().getAbsolutePath(),
                localizationService.getRelativeLocaleFile()
        );
        if (localeFile.exists() && !localeFile.delete()) {
            MessageHelper.reloadFailed(this.getPlugin(), sender);
            return;
        }

        try {
            localizationService.reload();
            MessageHelper.reloadComplete(this.getPlugin(), sender);
        } catch (ServiceReloadException e) {
            this.getPlugin().log(
                    Level.SEVERE,
                    String.format("Error while reloading LocalizationService for locale %s", localizationService.getLocale()),
                    e
            );
            MessageHelper.reloadFailed(this.getPlugin(), sender);
        }
    }

}
