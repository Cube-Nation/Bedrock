package de.cubenation.api.bedrock.command;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.exception.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by BenediktHr on 12.03.16.
 * Project: Bedrock
 */

public abstract class PlayerKeyValueCommand extends KeyValueCommand {

    public PlayerKeyValueCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    public void execute(CommandSender sender, String[] subcommands, HashMap<String, String> arguments)
            throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {
        if (sender instanceof Player) {
            execute((Player) sender, subcommands, arguments);
        } else {
            MessageHelper.mustBePlayer(plugin, sender);
        }
    }

    public abstract void execute(Player player, String[] subcommands, HashMap<String, String> arguments)
            throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException;

}
