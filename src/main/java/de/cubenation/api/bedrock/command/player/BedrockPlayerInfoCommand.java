package de.cubenation.api.bedrock.command.player;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.CommandArgument;
import de.cubenation.api.bedrock.annotation.CommandDescription;
import de.cubenation.api.bedrock.annotation.CommandPermission;
import de.cubenation.api.bedrock.annotation.CommandSubCommand;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.ebean.BedrockPlayer;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.api.bedrock.helper.BedrockEbeanHelper;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.helper.UUIDUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * Created by BenediktHr on 17.08.16.
 * Project: Bedrock
 */
public class BedrockPlayerInfoCommand extends Command {

    public BedrockPlayerInfoCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @CommandDescription("command.bedrock.playerinfo.desc")
    @CommandPermission(Name = "info.other", Role = CommandRole.ADMIN)
    @CommandSubCommand({ "info", "i" })
    @CommandArgument(
            Description = "command.bedrock.username_uuid.desc",
            Placeholder = "command.bedrock.username_uuid.ph",
            Optional = true
    )
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
