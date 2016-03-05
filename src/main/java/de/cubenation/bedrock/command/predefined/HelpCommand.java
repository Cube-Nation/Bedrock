package de.cubenation.bedrock.command.predefined;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.command.Command;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.exception.CommandException;
import de.cubenation.bedrock.helper.HelpPageableListService;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.helper.TextHolder;
import de.cubenation.bedrock.helper.design.PageDesignHelper;
import de.cubenation.bedrock.permission.Permission;
import de.cubenation.bedrock.service.pageablelist.PageableListRegistry;
import de.cubenation.bedrock.service.pageablelist.PageableListStorable;
import de.cubenation.bedrock.translation.Translation;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.command
 */
public class HelpCommand extends Command {

    public HelpCommand(BasePlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void setPermissions(ArrayList<Permission> permissions) {
    }

    @Override
    public void setSubCommands(ArrayList<String[]> subcommands) {
        subcommands.add(new String[]{"help"});
    }

    @Override
    public void setDescription(StringBuilder description) {
        description.append("command.bedrock.help.desc");
    }

    @Override
    public void setArguments(ArrayList<Argument> arguments) {
    }

    @Override
    public void execute(CommandSender sender, String[] subCommands, String[] args) throws CommandException {
        if (args.length == 0 || StringUtils.isNumeric(args[0])) {
            // Display help for all commands

            // create help for each subcommand
            ArrayList<TextComponent> commandComponents = new ArrayList<>();
            for (AbstractCommand command : commandManager.getHelpCommands()) {
                TextComponent component_help = command.getBeautifulHelp(sender);
                if (component_help != null) {
                    commandComponents.add(component_help);
                }
            }

            HelpPageableListService helpPageableListService = new HelpPageableListService(getPlugin());

            // Preparation for Pagination
            for (TextComponent component : commandComponents) {
                PageableListStorable msgStoreable = new PageableListStorable();
                msgStoreable.set(new TextHolder(component, component.getHoverEvent(), component.getClickEvent()));
                helpPageableListService.store(msgStoreable);
            }

            PageableListRegistry.getInstance()._register(
                    getPlugin(),
                    "help",
                    sender,
                    helpPageableListService);

            int number = 1;
            if (args.length > 0 && StringUtils.isNumeric(args[0])) {
                number = Integer.parseInt(args[0]);
            }

            PageDesignHelper.pagination(getPlugin(),
                    helpPageableListService,
                    number,
                    "/" + getCommandManager().getPluginCommand().getLabel() + " help %page%",
                    sender,
                    getHeader(getCommandManager().getPluginCommand().getLabel()));


        } else {

            // Send help for special command
            ArrayList<AbstractCommand> helpList = new ArrayList<>();
            for (AbstractCommand command : getCommandManager().getHelpCommands()) {
                if (command instanceof HelpCommand) {
                    continue;
                }
                if (isValidHelpTrigger(command, args)) {
                    helpList.add(command);
                }
            }

            MessageHelper.send(getPlugin(), sender, getHeader(getCommandManager().getPluginCommand().getLabel()));
            if (helpList.isEmpty()) {
                // If no command is valid, show help for all
                for (AbstractCommand command : commandManager.getHelpCommands()) {
                    MessageHelper.send(
                            this.getPlugin(),
                            sender,
                            command.getBeautifulHelp(sender)
                    );
                }
            } else {
                for (AbstractCommand command : helpList) {
                    MessageHelper.send(
                            this.getPlugin(),
                            sender,
                            command.getBeautifulHelp(sender));
                }
            }
        }
    }

    private String getHeader(String label) {
        String commandHeaderName = Character.toUpperCase(label.charAt(0)) + label.substring(1);

        return new Translation(
                this.getCommandManager().getPlugin(),
                "help.header",
                new String[]{"plugin", commandHeaderName}
        ).getTranslation();
    }

    private boolean isValidHelpTrigger(AbstractCommand command, String[] args) {
        for (int i = 0; i < args.length; i++) {
            for (String cmd : command.getSubcommands().get(i)) {
                if (cmd.startsWith(args[i])) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<String> getTabCompletion(String[] args, CommandSender sender) {

        if (args.length > 1) {
            // Tab Completion for each command to display special help
            // like
            // /plugin help version
            // /plugin help reload

            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(args));
            if (arrayList.contains("help")) {
                arrayList.remove("help");
            }

            ArrayList<String> list = new ArrayList<>();
            for (AbstractCommand cmd : getCommandManager().getCommands()) {
                //Ignore Help Command
                if (cmd instanceof HelpCommand) {
                    continue;
                }

                if (!cmd.hasPermission(sender)) {
                    continue;
                }

                if (!cmd.displayInHelp()) {
                    continue;
                }

                if (!cmd.displayInCompletion()) {
                    continue;
                }

                ArrayList tabCom = cmd.getTabCompletion(arrayList.toArray(new String[arrayList.size()]), sender);
                if (tabCom != null) {
                    list.addAll(tabCom);
                }
            }

            // Remove duplicates.
            Set<String> set = new HashSet<>(list);
            return set.isEmpty() ? null : new ArrayList<>(set);

        } else {
            return super.getTabCompletion(args, sender);
        }
    }
}
