package de.cubenation.bedrock.command;

import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;

public class ReloadCommand extends SubCommand {

    public ReloadCommand() {
        super(
                new String[] { "reload" , "r"},
                new String[] { "Reload the plugin" },
                "reload"
        );
    }

    @Override
    public void execute(CommandSender sender, String label, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException {
        try {
            this.plugin.getPermissionService().reload();
            this.plugin.getCustomConfigurationFileService().reload();
            this.plugin.getLocalizationService().reload();

            sender.sendMessage(this.plugin.getMessagePrefix() + " Reload complete");
        } catch (ServiceReloadException e) {
            sender.sendMessage(this.plugin.getMessagePrefix() + " Reload failed");
        }
    }

    @Override
    public LinkedHashMap<String, String> getArguments() {
        return null;
    }
}
