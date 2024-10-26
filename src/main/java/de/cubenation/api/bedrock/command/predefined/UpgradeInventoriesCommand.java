package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.annotation.Description;
import de.cubenation.api.bedrock.annotation.Permission;
import de.cubenation.api.bedrock.annotation.SubCommand;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.exception.IllegalCommandArgumentException;
import de.cubenation.api.bedrock.exception.InsufficientPermissionException;
import de.cubenation.api.bedrock.service.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Description("command.bedrock.upgradeinventories.desc")
@Permission(Name = "upgradeinventories", Role = CommandRole.ADMIN)
@SubCommand({ "upgrade" })
@SubCommand({ "inventories" })
public class UpgradeInventoriesCommand extends Command {

    public UpgradeInventoriesCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException, IllegalCommandArgumentException, InsufficientPermissionException {
        if (sender instanceof Player) {
            sender.sendMessage("This command can only be executed as console.");
            return;
        }

        plugin.getInventoryService().upgrade();
    }
}
