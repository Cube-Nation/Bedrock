package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.Description;
import de.cubenation.api.bedrock.annotation.Permission;
import de.cubenation.api.bedrock.annotation.SubCommand;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.ServiceReloadException;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.service.command.CommandManager;
import org.bukkit.command.CommandSender;

@Description("command.bedrock.reload.desc")
@Permission(Name = "reload", Role = CommandRole.ADMIN)
@SubCommand({ "reload", "r" })
public class ReloadCommand extends Command {

    public ReloadCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException {
        try {
            this.getPlugin().getServiceManager().reload();
            MessageHelper.reloadComplete(this.getCommandManager().getPlugin(),sender);

        } catch (ServiceReloadException e) {
            MessageHelper.reloadFailed(this.getCommandManager().getPlugin(),sender);
            e.printStackTrace();

        }
    }

}
