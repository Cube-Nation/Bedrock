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
                "version",
                new String[]{ "help.version" },
                "version"
        );
    }

    @Override
    public void execute(CommandSender sender, String label, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException {
        String t = this.plugin.getMessagePrefix() + " " +
                new Translation(
                        BedrockPlugin.getInstance(),
                        "version",
                        new String[]{ "version", this.plugin.getDescription().getVersion() }
                ).getTranslation();

        sender.sendMessage(t);
    }

    @Override
    public LinkedHashMap<String, String> getArguments() {
        return null;
    }

}