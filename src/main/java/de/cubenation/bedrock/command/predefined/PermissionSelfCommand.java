package de.cubenation.bedrock.command.predefined;

import de.cubenation.bedrock.command.Command;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.exception.PlayerNotFoundException;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.service.permission.PermissionService;
import de.cubenation.bedrock.translation.Translation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by B1acksheep on 28.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command.permission
 */
public class PermissionSelfCommand extends Command {

    public PermissionSelfCommand() {
        super(
                new String[] {"mypermissions", "myperms"},
                "help.permission.self",
                "permission.self"
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(CommandSender sender, String label, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException {

        // must be player
        if (!(sender instanceof Player)) {
            MessageHelper.mustBePlayer(this.plugin, sender);
            return;
        }

        Player player = (Player) sender;

        PermissionService permissionService = getCommandManager().getPlugin().getPermissionService();
        if (permissionService != null) {

            MessageHelper.send(
                    getCommandManager().getPlugin(),
                    sender,
                    new Translation(
                            this.getCommandManager().getPlugin(),
                            "permission.list.header"
                    ).getTranslation()
            );

            HashMap<String, ArrayList<String>> permissionDump;
            try {
                permissionDump = permissionService.getPermissionRoleDump(player);
            } catch (PlayerNotFoundException e) {
                // this should not happen normally, because the command issuer is the player himself
                MessageHelper.commandExecutionError(this.plugin, sender, e);
                e.printStackTrace();
                return;
            }

            for (Map.Entry entry : permissionDump.entrySet()) {

                // send role
                MessageHelper.send(
                        this.getCommandManager().getPlugin(),
                        sender,
                        new Translation(
                                this.getCommandManager().getPlugin(),
                                "permission.list.role",
                                new String[]{"role", entry.getKey().toString()}
                        ).getTranslation()
                );

                for (String perm : (ArrayList<String>) entry.getValue()) {
                    MessageHelper.send(
                            this.getCommandManager().getPlugin(),
                            sender,
                            new Translation(
                                    this.getCommandManager().getPlugin(),
                                    "permission.list.permission",
                                    new String[]{"permission", perm}
                            ).getTranslation()
                    );

                } // for (permission)

            } // for (permissionDump)

            // no permissions
        } else {
            MessageHelper.send(
                    this.getCommandManager().getPlugin(),
                    sender,
                    new Translation(
                            this.getCommandManager().getPlugin(),
                            "permission.no_permissions"
                    ).getTranslation()
            );

        } // if
    }

}
