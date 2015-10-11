package de.cubenation.bedrock.command.predefined;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.Command;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.permission.Permission;
import de.cubenation.bedrock.translation.Translation;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.logging.Level;

public class ReloadCommand extends Command {

    public ReloadCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void setPermissions(ArrayList<Permission> permissions) {
        permissions.add(new Permission("reload"));
    }

    @Override
    public void setSubCommands(ArrayList<String[]> subcommands) {
        subcommands.add(new String[] { "r", "reload" } );
    }

    @Override
    public void setDescription(StringBuilder description) {
        description.append("help.reload");
    }

    @Override
    public void setArguments(ArrayList<Argument> arguments) {
    }

    @Override
    public void execute(CommandSender sender, String[] subcommands, String[] args) throws CommandException, IllegalCommandArgumentException {
        try {

            this.getPlugin().log(Level.INFO, "Reloading plugin services");

            // DO NOT MODIFY THIS ORDER!
            this.plugin.getConfigService().reload();
            this.plugin.getColorSchemeService().reload();
            this.plugin.getLocalizationService().reload();
            this.plugin.getCommandService().reload();
            this.plugin.getPermissionService().reload();

            MessageHelper.send(
                    this.getCommandManager().getPlugin(),
                    sender,
                    new Translation(
                            this.getCommandManager().getPlugin(),
                            "reload.complete"
                    ).getTranslation()
            );
        } catch (ServiceReloadException e) {

            MessageHelper.send(
                    this.getCommandManager().getPlugin(),
                    sender,
                    new Translation(
                            this.getCommandManager().getPlugin(),
                            "reload.failed"
                    ).getTranslation()
            );
            e.printStackTrace();
        }
    }

}
