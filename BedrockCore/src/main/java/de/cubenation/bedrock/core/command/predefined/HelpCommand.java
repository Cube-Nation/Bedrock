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
import de.cubenation.bedrock.core.annotation.Option;
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

import javax.swing.tree.TreePath;
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
            Integer page,
            @Option(Description = "command.bedrock.filter.desc", Placeholder = "command.bedrock.filter.ph", Key = "f", Hidden = true)
            String filter
    ) {
        CommandTreePath parentTreePath = treePath.getSequence(0, treePath.size()-1);
        List<JsonMessage> commandComponents = getHelpJsonMessages(sender, parentTreePath, filter);
        printHelp(sender, parentTreePath, commandComponents, page, filter);
    }

    public List<JsonMessage> getHelpJsonMessages(BedrockChatSender sender, CommandTreePath treePath, String filter) {
        List<JsonMessage> jsonList = treePath.getHead().getNode().getJsonHelp(sender, treePath);
        if (filter == null) {
            return jsonList;
        }
        // TODO: HelpPriority: helpCommands.sort(Comparator.comparing(CommandPath::getHelpPriority));
        return jsonList.stream().filter(j -> j.getPlainText().toLowerCase().contains(filter.toLowerCase())).toList();
    }

    @SuppressWarnings("unchecked")
    public void printHelp(BedrockChatSender sender, CommandTreePath treePath, List<JsonMessage> commandComponents, Integer page, String filter) {
        HelpPageableListService helpPageableListService = new HelpPageableListService(getPlugin());

        // Preparation for Pagination
        for (JsonMessage commandComponent : commandComponents) {
            PageableListStorable<?> msgStoreable = new PageableListStorable<>();
            msgStoreable.set(commandComponent);
            helpPageableListService.store(msgStoreable);
        }

        // Validate page input
        if (page == null || page < 1 || page > helpPageableListService.getPages()) {
            page = 1;
        }

        // Create paginate command
        String managerLabel = treePath.getCommandAsString();
        StringBuilder command = new StringBuilder();
        command.append("/");
        command.append(managerLabel);
        command.append(" help %page%");
        if (filter != null) {
            command.append(" -");
            command.append(new Translation(plugin, "command.bedrock.filter.key").getTranslation());
            command.append(" ");
            command.append(filter);
        }

        // Print current page
        helpPageableListService.paginate(sender, command.toString(), getHeader(managerLabel), page);
    }

    private String getHeader(String label) {
        String commandHeaderName = Character.toUpperCase(label.charAt(0)) + label.substring(1);

        return new Translation(
                plugin,
                "help.header",
                new String[]{"plugin", commandHeaderName}
        ).getTranslation();
    }
}

