package de.cubenation.bedrock.bungee.api.command;

import de.cubenation.bedrock.bungee.wrapper.BungeeChatSender;
import de.cubenation.bedrock.core.command.tree.CommandTreeRoot;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.exception.InsufficientPermissionException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class BungeeCommand extends Command implements TabExecutor {

    private final CommandTreeRoot root;

    public BungeeCommand(CommandTreeRoot root, String name) {
        super(name);
        this.root = root;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            root.onCommand(BungeeChatSender.wrap(sender), args);
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
        return null;
    }
}
