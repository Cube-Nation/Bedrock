package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.command.argument.Argument;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.api.bedrock.exception.ServiceReloadException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.permission.Permission;
import de.cubenation.api.bedrock.service.localization.LocalizationService;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

public class RegenerateLocaleCommand extends Command {

    public RegenerateLocaleCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void setPermissions(ArrayList<Permission> permissions) {
        permissions.add(new Permission("regeneratelocale", CommandRole.ADMIN));
    }

    @Override
    public void setSubCommands(ArrayList<String[]> subcommands) {
        subcommands.add(new String[] { "regenerate" });
        subcommands.add(new String[] { "locale" });
    }

    @Override
    public void setDescription(StringBuilder description) {
        description.append("command.bedrock.regeneratelocale.desc");
    }

    @Override
    public void setArguments(ArrayList<Argument> arguments) {
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
