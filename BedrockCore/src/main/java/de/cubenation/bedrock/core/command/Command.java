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

package de.cubenation.bedrock.core.command;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.service.command.CommandManager;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public abstract class Command extends AbstractCommand {

    public Command(FoundationPlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public ArrayList<String> getTabCompletion(String[] args, BedrockCommandSender sender) {
        ArrayList<String> tabCompletionFromCommands = getTabCompletionFromCommands(args);
        if (isValidTrigger(args)) {
            if (args != null && this.getSubcommands()!= null) {
                ArrayList<String> tabArgumentCompletion = getTabArgumentCompletion(sender,args.length - this.getSubcommands().size() - 1, Arrays.copyOfRange(args, this.getSubcommands().size(), args.length));
                if (tabArgumentCompletion != null && !tabArgumentCompletion.isEmpty()) {
                    if (tabCompletionFromCommands == null) {
                        tabCompletionFromCommands = new ArrayList<>();
                    }
                    tabCompletionFromCommands.addAll(tabArgumentCompletion);
                }
            }


            if (tabCompletionFromCommands == null || args == null) {
                return tabCompletionFromCommands;
            }

            ArrayList<String> toRemove = new ArrayList<>();
            String arg = args[args.length - 1];

            for (String completion: tabCompletionFromCommands) {
                if (!completion.toLowerCase().startsWith(arg.toLowerCase())) {
                    toRemove.add(completion);
                }
            }

            tabCompletionFromCommands.removeAll(toRemove);
        }

        return tabCompletionFromCommands;
    }

    /**
     * Returns if the subcommand is a valid trigger.
     *
     * @param args the args
     * @return true if it is a valid trigger, false otherwise
     */
    @Override
    public boolean isValidTrigger(String[] args) {
        return this.isMatchingSubCommands(args);
    }

    public ArrayList<String> getTabArgumentCompletion(BedrockCommandSender sender, int argumentIndex, String[] args) {
        return null;
    }

}
