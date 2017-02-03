package de.cubenation.bedrock.command.player;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.callback.MultipleBedrockPlayerCallback;
import de.cubenation.bedrock.callback.SingleBedrockPlayerCallback;
import de.cubenation.bedrock.command.Command;
import de.cubenation.bedrock.command.CommandRole;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.ebean.BedrockPlayer;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.exception.InsufficientPermissionException;
import de.cubenation.bedrock.helper.BedrockEbeanHelper;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.helper.UUIDUtil;
import de.cubenation.bedrock.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.List;

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
    public void execute(final CommandSender sender, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {

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
            BedrockEbeanHelper.requestBedrockPlayer(player, new SingleBedrockPlayerCallback() {
                @Override
                public void didFinished(BedrockPlayer player) {
                    sender.sendMessage("Results: (1)");
                    sender.sendMessage(player.getUsername() + ": " + player.getLastlogin());
                }

                @Override
                public void didFailed(Exception e) {
                    sender.sendMessage("No player found for: " + player);
                }
            });
        } else {
            BedrockEbeanHelper.requestBedrockPlayerForLastKnownName(player, false, new MultipleBedrockPlayerCallback() {
                @Override
                public void didFinished(List<BedrockPlayer> players) {
                    sender.sendMessage("Results: (" + players.size() + ")");
                    for (BedrockPlayer player : players) {
                        sender.sendMessage(player.getUsername() + ": " + player.getLastlogin());
                    }
                }

                @Override
                public void didFailed(Exception e) {
                    sender.sendMessage("No player found for: " + player);
                }
            });
        }

    }
}
