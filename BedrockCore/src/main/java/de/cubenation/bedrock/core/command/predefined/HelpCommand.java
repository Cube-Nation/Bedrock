/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.bedrock.core.command.predefined;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.Argument;
import de.cubenation.bedrock.core.annotation.Description;
import de.cubenation.bedrock.core.annotation.SubCommand;
import de.cubenation.bedrock.core.command.AbstractCommand;
import de.cubenation.bedrock.core.command.Command;
import de.cubenation.bedrock.core.command.CommandManager;
import de.cubenation.bedrock.core.helper.HelpPageableListService;
import de.cubenation.bedrock.core.service.pageablelist.PageableListStorable;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.translation.Translation;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Description("command.bedrock.help.desc")
@SubCommand({ "help" })
@Argument(Description = "command.bedrock.page.desc", Placeholder = "command.bedrock.page.ph", Optional = true)
public class HelpCommand extends Command {

    public HelpCommand(FoundationPlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    public void execute(BedrockChatSender sender, Optional<String[]> args) {

        if (args.isEmpty() || StringUtils.isNumeric(args.get()[0])) {
            // Display help for all commands

            ArrayList<JsonMessage> commandComponents = getFullHelpList(sender);
            printHelp(sender, args.orElse(new String[]{"0"}), commandComponents);

        } else {
            // Send help for special command
            ArrayList<AbstractCommand> helpList = new ArrayList<>();
            for (AbstractCommand command : getHelpCommands()) {
                if (!(command instanceof HelpCommand) && command.isValidHelpTrigger(args.get())) {
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

            printHelp(sender, args.get(), jsonList);
        }
    }

    public List<AbstractCommand> getHelpCommands() {
        ArrayList<AbstractCommand> helpCommands = new ArrayList<>();
        for (AbstractCommand command : getCommandManager().getCommands()) {
            if (command.displayInHelp()) {
                helpCommands.add(command);
            }
        }

        // Sorting
        helpCommands.sort(Comparator.comparing(AbstractCommand::getHelpPriority));

        return helpCommands;
    }

    private ArrayList<JsonMessage> getFullHelpList(BedrockChatSender sender) {
        // create help for each subcommand
        ArrayList<AbstractCommand> commands = new ArrayList<AbstractCommand>() {{
            addAll(getHelpCommands());
        }};

        return getHelpJsonMessages(sender, commands);
    }

    public ArrayList<JsonMessage> getHelpJsonMessages(BedrockChatSender sender, ArrayList<AbstractCommand> helpList) {
        ArrayList<JsonMessage> jsonList = new ArrayList<>();

        for (AbstractCommand abstractCommand : helpList) {
            JsonMessage jsonHelp = abstractCommand.getJsonHelp(sender);
            if (jsonHelp != null) {
                jsonList.add(jsonHelp);
            }
        }
        return jsonList;
    }

    public void printHelp(BedrockChatSender sender, String[] args, ArrayList<JsonMessage> commandComponents) {
        HelpPageableListService helpPageableListService = new HelpPageableListService(getPlugin());

        // Preparation for Pagination
        for (JsonMessage commandComponent : commandComponents) {
            PageableListStorable msgStoreable = new PageableListStorable();
            msgStoreable.set(commandComponent);
            helpPageableListService.store(msgStoreable);
        }
        String header = getHeader(getCommandManager().getLabel());

        int number = 1;
        if (args.length > 0 && StringUtils.isNumeric(args[0])) {
            int n = Integer.parseInt(args[0]);
            if(n > 0 && n <= helpPageableListService.getPages())
                number = n;
        }
        helpPageableListService.paginate(sender, "/" + getCommandManager().getLabel() + " help %page%", header, number);
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
    public List<String> getTabCompletion(String[] args, BedrockChatSender sender) {

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

                List tabCom = cmd.getTabCompletion(arrayList.toArray(new String[arrayList.size()]), sender);
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

