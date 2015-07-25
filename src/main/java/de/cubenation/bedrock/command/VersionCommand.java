package de.cubenation.bedrock.command;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.translation.Translation;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;

public class VersionCommand extends SubCommand {

    public VersionCommand() {
        super(
                new String[]{ "version", "v" },
                new String[]{ "help.version" },
                "version"
        );
    }

    @Override
    public void execute(CommandSender sender, String label, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException {

        String plugin_name      = this.plugin.getDescription().getName();
        String plugin_version   = this.plugin.getDescription().getVersion();

        String t = new Translation(
                BedrockPlugin.getInstance(),
                "version",
                new String[]{"plugin", plugin_name, "version", plugin_version}
        ).getTranslation();

        sender.sendMessage(t);
    }

    @Override
    public LinkedHashMap<String, String> getArguments() {
        return null;
    }

}