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

package de.cubenation.bedrock.bukkit.api.service.command;

import de.cubenation.bedrock.bukkit.api.command.sender.CommandSender;
import de.cubenation.bedrock.core.FoundationPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.List;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class CommandManager
        extends de.cubenation.bedrock.core.service.command.CommandManager
        implements CommandExecutor, TabCompleter {

    public CommandManager(FoundationPlugin plugin, String label, String helpPrefix) {
        super(plugin, label, helpPrefix);
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, Command command, String label, String[] args) {
        return onCommand(new CommandSender(sender), args);
    }

    @Override
    public List<String> onTabComplete(org.bukkit.command.CommandSender sender, Command command, String alias, String[] args) {
        return onTabComplete(new CommandSender(sender), args);
    }
}