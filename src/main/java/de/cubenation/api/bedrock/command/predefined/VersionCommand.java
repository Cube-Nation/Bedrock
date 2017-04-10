package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.CommandDescription;
import de.cubenation.api.bedrock.annotation.CommandPermission;
import de.cubenation.api.bedrock.annotation.CommandSubCommand;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import org.bukkit.command.CommandSender;

public class VersionCommand extends Command {

    public VersionCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @CommandDescription("command.bedrock.version.desc")
    @CommandPermission(Name = "version", Role = CommandRole.MODERATOR)
    @CommandSubCommand({ "version", "v" })
    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException {
        MessageHelper.version(this.getCommandManager().getPlugin(), sender);
    }

}