package de.cubenation.api.bedrock.command.player;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.command.argument.Argument;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.ebean.BedrockPlayer;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.api.bedrock.helper.BedrockEbeanHelper;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.permission.Permission;
import de.cubenation.api.bedrock.helper.UUIDUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;

/**
 * Created by BenediktHr on 17.08.16.
 * Project: Bedrock
 */
public class BedrockPlayerInfoCommand extends Command {

    public BedrockPlayerInfoCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void setPermissions(ArrayList<Permission> permissions) {
        permissions.add(new Permission("info.other", CommandRole.ADMIN.getType()));
    }

    @Override
    public void setSubCommands(ArrayList<String[]> subcommands) {
        subcommands.add(new String[]{"info", "i"});
    }

    @Override
    public void setDescription(StringBuilder description) {
        description.append("command.bedrockplayer.info.desc");
    }

    @Override
    public void setArguments(ArrayList<Argument> arguments) {
        arguments.add(new Argument("command.bedrock.username_uuid.desc", "command.bedrock.username_uuid.ph", true));
    }

    @Override
    public void execute(final CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {

        // check args length
        if (args.length > 1) {
            throw new IllegalCommandArgumentException();
        }

        if (args.length == 0 && sender instanceof ConsoleCommandSender) {
            MessageHelper.mustBePlayer(plugin, sender);
            return;
        }

        final String player = (args.length == 0) ? sender.getName() : args[0];

        if (UUIDUtil.isUUID(player)) {
            BedrockEbeanHelper.requestBedrockPlayer(player, bedrockPlayer -> {
                sender.sendMessage("Results: (1)");
                sender.sendMessage(bedrockPlayer.getUsername() + ": " + bedrockPlayer.getLastlogin());
            }, e -> sender.sendMessage("No player found for: " + player));
        } else {
            BedrockEbeanHelper.requestBedrockPlayerForLastKnownName(player, false, bedrockPlayers -> {
                sender.sendMessage("Results: (" + bedrockPlayers.size() + ")");
                for (BedrockPlayer bedrockPlayer : bedrockPlayers) {
                    sender.sendMessage(bedrockPlayer.getUsername() + ": " + bedrockPlayer.getLastlogin());
                }
            }, e -> sender.sendMessage("No player found for: " + player));
        }
    }
}
