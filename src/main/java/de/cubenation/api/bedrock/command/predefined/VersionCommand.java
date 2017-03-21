package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.command.argument.Argument;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.permission.Permission;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class VersionCommand extends Command {

    public VersionCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void setPermissions(ArrayList<Permission> permissions) {
        permissions.add(new Permission("version", CommandRole.MODERATOR));
    }

    @Override
    public void setSubCommands(ArrayList<String[]> subcommands) {
        subcommands.add(new String[] { "v", "version" } );
    }

    @Override
    public void setDescription(StringBuilder description) {
        description.append("command.bedrock.version.desc");
    }

    @Override
    public void setArguments(ArrayList<Argument> permissions) {
    }


    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException {
        MessageHelper.version(this.getCommandManager().getPlugin(), sender);
    }

}