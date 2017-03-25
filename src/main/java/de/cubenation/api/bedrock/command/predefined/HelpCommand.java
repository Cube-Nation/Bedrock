package de.cubenation.api.bedrock.command.predefined;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.AbstractCommand;
import de.cubenation.api.bedrock.command.Command;
import de.cubenation.api.bedrock.command.argument.Argument;
import de.cubenation.api.bedrock.command.manager.CommandManager;
import de.cubenation.api.bedrock.exception.CommandException;
import de.cubenation.api.bedrock.helper.HelpPageableListService;
import de.cubenation.api.bedrock.permission.Permission;
import de.cubenation.api.bedrock.service.pageablelist.PageableListStorable;
import de.cubenation.api.bedrock.translation.JsonMessage;
import de.cubenation.api.bedrock.translation.Translation;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by B1acksheep on 02.04.15.
 * Project: Bedrock
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
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0 || StringUtils.isNumeric(args[0])) {
            // Display help for all commands

            ArrayList<JsonMessage> commandComponents = getFullHelpList(sender);
            printHelp(sender, args, commandComponents);

        } else {
            // Send help for special command
            ArrayList<AbstractCommand> helpList = new ArrayList<>();
            for (AbstractCommand command : getCommandManager().getHelpCommands()) {
                if (!(command instanceof HelpCommand) && command.isValidHelpTrigger(args)) {
                    helpList.add(command);
                }
            }

            ArrayList<JsonMessage> jsonList;
            if (helpList.isEmpty()) {
                // If no command is valid, show help for all
                jsonList = getFullHelpList(sender);
            } else {
                jsonList = getHelpJsonMessages(sender, helpList);
            }

            printHelp(sender, args, jsonList);
        }
    }

    private ArrayList<JsonMessage> getFullHelpList(CommandSender sender) {
        // create help for each subcommand
        ArrayList<AbstractCommand> commands = new ArrayList<AbstractCommand>() {{
            addAll(commandManager.getHelpCommands());
        }};

        return getHelpJsonMessages(sender, commands);
    }

    public ArrayList<JsonMessage> getHelpJsonMessages(CommandSender sender, ArrayList<AbstractCommand> helpList) {
        ArrayList<JsonMessage> jsonList = new ArrayList<>();

        for (AbstractCommand abstractCommand : helpList) {
            JsonMessage jsonHelp = abstractCommand.getJsonHelp(sender);
            if (jsonHelp != null) {
                jsonList.add(jsonHelp);
            }
        }
        return jsonList;
    }

    public void printHelp(CommandSender sender, String[] args, ArrayList<JsonMessage> commandComponents) {
        HelpPageableListService helpPageableListService = new HelpPageableListService(getPlugin());

        // Preparation for Pagination
        for (JsonMessage commandComponent : commandComponents) {
            PageableListStorable msgStoreable = new PageableListStorable();
            msgStoreable.set(commandComponent);
            helpPageableListService.store(msgStoreable);
        }
        String header = getHeader(getCommandManager().getPluginCommand().getLabel());

        int number = 1;
        if (args.length > 0 && StringUtils.isNumeric(args[0])) {
            number = Integer.parseInt(args[0]);
        }
        helpPageableListService.paginate(sender, "/" + getCommandManager().getPluginCommand().getLabel() + " help %page%", header, number);
    }

    private String getHeader(String label) {
        String commandHeaderName = Character.toUpperCase(label.charAt(0)) + label.substring(1);

        return new Translation(
                this.getCommandManager().getPlugin(),
                "help.header",
                new String[]{"plugin", commandHeaderName}
        ).getTranslation();
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
