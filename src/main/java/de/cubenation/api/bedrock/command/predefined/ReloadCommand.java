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
import de.cubenation.api.bedrock.exception.ServiceReloadException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.reloadable.Reloadable;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class ReloadCommand extends Command {

    public ReloadCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @CommandDescription(Ident = "command.bedrock.reload.desc")
    @CommandPermission(Name = "reload", Role = CommandRole.ADMIN)
    @CommandSubCommand(Commands = { "reload", "r" })
    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException {
        try {

            this.getPlugin().log(Level.INFO, "Reloading plugin services");

            // DO NOT MODIFY THIS ORDER!
            this.plugin.getConfigService().reload();
            this.plugin.getColorSchemeService().reload();
            this.plugin.getLocalizationService().reload();
            this.plugin.getCommandService().reload();
            this.plugin.getPermissionService().reload();
            this.plugin.getSettingService().reload();

            if (this.plugin.getReloadable() != null) {
                for (Reloadable reloadable : this.plugin.getReloadable()) {
                    reloadable.reload();
                }
            }

            MessageHelper.reloadComplete(this.getCommandManager().getPlugin(),sender);
        } catch (ServiceReloadException e) {
            MessageHelper.reloadFailed(this.getCommandManager().getPlugin(),sender);
            e.printStackTrace();
        }
    }

}
