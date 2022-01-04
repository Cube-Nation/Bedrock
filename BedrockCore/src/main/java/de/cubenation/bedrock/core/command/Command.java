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
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;

import java.util.ArrayList;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public abstract class Command extends AbstractCommand {

    public Command(FoundationPlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public ArrayList<String> getTabCompletion(String[] args, BedrockChatSender sender) {
        ArrayList<String> tabCompletion = new ArrayList<>();

        // not a valid command yet? autocomplete subcommands.
        if (!isValidTrigger(args)) {
            ArrayList<String> tabCompletionFromCommands = getTabCompletionFromCommands(args);
            if (tabCompletionFromCommands != null) {
                tabCompletion.addAll(tabCompletionFromCommands);
            }
            return tabCompletion;
        }

        // autocomplete arguments from ArgumentType
        ArrayList<String> tabCompletionFromArguments = getTabCompletionFromArguments(sender, args);
        if (tabCompletionFromArguments != null) {
            tabCompletion.addAll(tabCompletionFromArguments);
        }

        return tabCompletion;
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
}
