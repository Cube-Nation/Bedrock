package de.cubenation.bedrock.bungee.api.command;

import de.cubenation.bedrock.bungee.wrapper.BungeeChatSender;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.command.tree.CommandTreePath;
import de.cubenation.bedrock.core.command.tree.CommandTreePathItem;
import de.cubenation.bedrock.core.command.tree.CommandTreeRoot;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;

public class BungeeCommand extends Command implements TabExecutor {

    @Getter
    private final FoundationPlugin plugin;

    private final CommandTreeRoot root;

    public BungeeCommand(FoundationPlugin plugin, CommandTreeRoot root, String name) {
        super(name);
        this.plugin = plugin;
        this.root = root;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            root.onCommand(BungeeChatSender.wrap(sender), new CommandTreePath(root.getEntrypoint()), args);
        } catch (IllegalCommandArgumentException e) {
            e.printStackTrace();
        } catch (InsufficientPermissionException e) {
            e.printStackTrace();
        } catch (CommandException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
