package de.cubenation.bedrock.bukkit.api.command;

import de.cubenation.bedrock.bukkit.wrapper.BukkitChatSender;
import de.cubenation.bedrock.bukkit.wrapper.BukkitPlayer;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.tree.CommandTreeNode;
import de.cubenation.bedrock.core.command.tree.CommandTreeRoot;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class BukkitCommandTreeRoot extends CommandTreeRoot implements CommandExecutor, TabCompleter {

    public BukkitCommandTreeRoot(FoundationPlugin plugin, CommandTreeNode entrypoint) {
        super(plugin, null);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            return onCommand(BukkitChatSender.wrap(sender), args);
        } catch (IllegalCommandArgumentException e) {
            e.printStackTrace();
        } catch (InsufficientPermissionException e) {
            e.printStackTrace();
        } catch (CommandException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // TODO: TabComplete
        return null;
    }
}
