package de.cubenation.bedrock.command.predefined;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.Command;
import de.cubenation.bedrock.command.CommandRole;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.permission.Permission;
import de.cubenation.bedrock.translation.Translation;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class VersionCommand extends Command {

    public VersionCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void setPermissions(ArrayList<Permission> permissions) {
        permissions.add(new Permission("version", CommandRole.MODERATOR.getType()));
    }

    @Override
    public void setSubCommands(ArrayList<String[]> subcommands) {
        subcommands.add(new String[] { "v", "version" } );
    }

    @Override
    public void setDescription(StringBuilder description) {
        description.append("help.version");
    }

    @Override
    public void setArguments(ArrayList<Argument> permissions) {
    }


    @Override
    public void execute(CommandSender sender, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException {
        MessageHelper.send(this.plugin, sender, new Translation(
                this.getCommandManager().getPlugin(),
                "version",
                new String[] { "version", this.getCommandManager().getPlugin().getDescription().getVersion() }
        ).getTranslation());
    }

}