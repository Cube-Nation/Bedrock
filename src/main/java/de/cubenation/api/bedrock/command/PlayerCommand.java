package de.cubenation.api.bedrock.command;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by BenediktHr on 12.03.16.
 * Project: Bedrock
 */

public abstract class PlayerCommand extends Command {

    public PlayerCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void execute(CommandSender sender, String[] args)
            throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {
        if (sender instanceof Player) {
            execute((Player) sender, args);
        } else {
            MessageHelper.mustBePlayer(plugin, sender);
        }
    }

    public abstract void execute(Player player, String[] args) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException;

}
