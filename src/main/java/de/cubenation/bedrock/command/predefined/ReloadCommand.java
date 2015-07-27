package de.cubenation.bedrock.command.predefined;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.command.SubCommand;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.translation.Translation;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;

public class ReloadCommand extends SubCommand {

    public ReloadCommand() {
        super(
                new String[] { "reload" , "r"},
                new String[] { "help.reload" },
                "reload"
        );
    }

    @Override
    public void execute(CommandSender sender, String label, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException {
        try {
            this.plugin.reloadConfig();
            
            this.plugin.getColorSchemeService().reload();
            this.plugin.getPermissionService().reload();
            this.plugin.getCustomConfigurationFileService().reload();
            this.plugin.getLocalizationService().reload();

            MessageHelper.send(
                    getCommandManager().getPlugin(),
                    sender,
                    getCommandManager().getPlugin().getMessagePrefix() + " " + new Translation(
                            BedrockPlugin.getInstance(),
                            "reload.complete"
                    ).getTranslation()
            );
        } catch (ServiceReloadException e) {

            MessageHelper.send(
                    getCommandManager().getPlugin(),
                    sender,
                    getCommandManager().getPlugin().getMessagePrefix() + " " + new Translation(
                            BedrockPlugin.getInstance(),
                            "reload.failed"
                    ).getTranslation()
            );
            e.printStackTrace();
        }
    }

    @Override
    public LinkedHashMap<String, String> getArguments() {
        return null;
    }
}
