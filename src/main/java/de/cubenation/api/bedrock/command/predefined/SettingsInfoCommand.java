package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.*;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.NoSuchPlayerException;
import de.cubenation.api.bedrock.service.settings.SettingsManager;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.service.settings.CustomSettingsFile;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

/**
 * Created by BenediktHr on 15.11.15.
 * Project: Bedrock
 */
public class SettingsInfoCommand extends Command {

    public SettingsInfoCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @CommandDescription(Ident = "command.bedrock.info.desc")
    @CommandPermission(Name = "settings.list", Role = CommandRole.ADMIN)
    @CommandSubCommands(SubCommands = {
            @CommandSubCommand(Commands = { "settings" }),
            @CommandSubCommand(Commands = { "info", "i" })
    })
    @CommandArguments(Arguments = {
            @CommandArgument(
                    Description = "command.bedrock.key.desc",
                    Placeholder = "command.bedrock.key.ph"
            ),
            @CommandArgument(
                    Description = "command.bedrock.username_uuid.desc",
                    Placeholder = "command.bedrock.username_uuid.ph",
                    Optional = true
            )
    })
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
