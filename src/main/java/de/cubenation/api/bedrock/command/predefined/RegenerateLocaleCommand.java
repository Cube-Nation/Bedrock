package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.CommandDescription;
import de.cubenation.api.bedrock.annotation.CommandPermission;
import de.cubenation.api.bedrock.annotation.CommandSubCommand;
import de.cubenation.api.bedrock.annotation.CommandSubCommands;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.api.bedrock.exception.ServiceReloadException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.service.localization.LocalizationService;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.logging.Level;

public class RegenerateLocaleCommand extends Command {

    public RegenerateLocaleCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @CommandDescription(Ident = "command.bedrock.regeneratelocale.desc")
    @CommandPermission(Name = "regeneratelocale", Role = CommandRole.ADMIN)
    @CommandSubCommands(SubCommands = {
            @CommandSubCommand(Commands = { "regenerate" }),
            @CommandSubCommand(Commands = { "locale" })
    })
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
