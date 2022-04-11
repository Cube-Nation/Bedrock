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
import de.cubenation.bedrock.core.command.Command;
import de.cubenation.bedrock.core.command.tree.CommandTreeNestedNode;
import de.cubenation.bedrock.core.command.tree.CommandTreePath;
import de.cubenation.bedrock.core.command.tree.CommandTreePathItem;
import de.cubenation.bedrock.core.helper.HelpPageableListService;
import de.cubenation.bedrock.core.service.pageablelist.PageableListStorable;
import de.cubenation.bedrock.core.translation.JsonMessage;
import de.cubenation.bedrock.core.translation.Translation;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@Description("command.bedrock.help.desc")
public class HelpCommand extends Command {

    public HelpCommand(FoundationPlugin plugin) {
        super(plugin);
    }

    public void execute(
            BedrockChatSender sender,
            CommandTreePath treePath,
            @Argument(Description = "command.bedrock.page.desc", Placeholder = "command.bedrock.page.ph", Optional = true)
            String[] args
    ) {

        if (args.length == 0 || StringUtils.isNumeric(args[0])) {
            // Display help for all commands

            List<JsonMessage> commandComponents = getFullHelpList(sender, treePath);
            printHelp(sender, treePath, args, commandComponents);

        } else {
            // Send help for special command
            List<CommandTreePathItem> helpList = getHelpCommands(treePath.getSequence(0, treePath.size()-2));

//            for (CommandTreePathItem commandPath : getHelpCommands(treePath.getSequence(0, treePath.size()-2))) {
//                // TODO: HelpTrigger
//                if (!(commandPath.getCommand() instanceof HelpCommand) && commandPath.isValidHelpTrigger(args)) {
//                    helpList.add(commandPath);
//                }
//            }

            ArrayList<JsonMessage> jsonList = getHelpJsonMessages(sender, treePath, helpList);
//            if (helpList.isEmpty()) {
//                // If no command is valid, show help for all
//                jsonList = getFullHelpList(sender);
//            } else {
//                jsonList = getHelpJsonMessages(sender, helpList);
//            }

            printHelp(sender, treePath, args, jsonList);
        }
    }

    public List<CommandTreePathItem> getHelpCommands(CommandTreePath treePath) {
        ArrayList<CommandTreePathItem> helpCommands = new ArrayList<>();
        CommandTreePathItem parent = treePath.getParent();
        helpCommands.add(parent);

        // Sorting
        // TODO: HelpPriority: helpCommands.sort(Comparator.comparing(CommandPath::getHelpPriority));

        return helpCommands;
    }

    public List<JsonMessage> getFullHelpList(BedrockChatSender sender, CommandTreePath treePath) {
        return treePath.getHead().getNode().getJsonHelp(sender, treePath);
    }

    public ArrayList<JsonMessage> getHelpJsonMessages(BedrockChatSender sender, CommandTreePath treePath, List<CommandTreePathItem> helpList) {
        ArrayList<JsonMessage> jsonList = new ArrayList<>();

        for (CommandTreePathItem helpItem : helpList) {
            List<JsonMessage> jsonHelp = helpItem.getNode().getJsonHelp(sender, treePath);
            if (jsonHelp != null) {
                jsonList.addAll(jsonHelp);
            }
        }
        return jsonList;
    }

    public void printHelp(BedrockChatSender sender, CommandTreePath treePath, String[] args, List<JsonMessage> commandComponents) {
        HelpPageableListService helpPageableListService = new HelpPageableListService(getPlugin());

        // Preparation for Pagination
        for (JsonMessage commandComponent : commandComponents) {
            PageableListStorable<?> msgStoreable = new PageableListStorable<>();
            msgStoreable.set(commandComponent);
            helpPageableListService.store(msgStoreable);
        }

        String managerLabel = treePath.getCommandAsString(); // TODO: treePath.getSequence(0, treePath.size()-2).getCommandAsString();

        int number = 1;
        if (args.length > 0 && StringUtils.isNumeric(args[0])) {
            int n = Integer.parseInt(args[0]);
            if(n > 0 && n <= helpPageableListService.getPages())
                number = n;
        }
        helpPageableListService.paginate(sender, "/" + managerLabel + " help %page%", getHeader(managerLabel), number);
    }

    private String getHeader(String label) {
        String commandHeaderName = Character.toUpperCase(label.charAt(0)) + label.substring(1);

        return new Translation(
                plugin,
                "help.header",
                new String[]{"plugin", commandHeaderName}
        ).getTranslation();
    }

//    @Override
//    public List<String> getAutoCompletion(String[] args, BedrockChatSender sender) {
//
//        if (args.length > 1) {
//            // Tab Completion for each command to display special help
//            // like
//            // /plugin help version
//            // /plugin help reload
//
//            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(args));
//            arrayList.remove("help");
//
//            ArrayList<String> list = new ArrayList<>();
//            for (CommandPath commandPath : this.commandManager.getCommandPaths()) {
//                //Ignore Help Command
//                if (commandPath.getCommand() instanceof HelpCommand) {
//                    continue;
//                }
//
//                if (!commandPath.getCommand().hasPermission(sender)) {
//                    continue;
//                }
//
//                if (!commandPath.displayInHelp()) {
//                    continue;
//                }
//
//                if (!commandPath.displayInCompletion()) {
//                    continue;
//                }
//
//                CollectionUtil.addAllIfNotNull(list, commandPath.getAutoCompletion(arrayList.toArray(new String[0]), sender));
//            }
//
//            // Remove duplicates.
//            Set<String> set = new HashSet<>(list);
//            return set.isEmpty() ? null : new ArrayList<>(set);
//        } else {
//            return super.getAutoCompletion(args, sender);
//        }
//    }
}

