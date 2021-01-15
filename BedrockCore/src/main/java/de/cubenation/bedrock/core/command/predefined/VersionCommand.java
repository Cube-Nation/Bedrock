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
import de.cubenation.bedrock.core.annotation.Description;
import de.cubenation.bedrock.core.annotation.Permission;
import de.cubenation.bedrock.core.annotation.SubCommand;
import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import de.cubenation.bedrock.core.command.Command;
import de.cubenation.bedrock.core.authorization.Role;
import de.cubenation.bedrock.core.exception.CommandException;
import de.cubenation.bedrock.core.exception.IllegalCommandArgumentException;
import de.cubenation.bedrock.core.service.command.CommandManager;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Description("command.bedrock.version.desc")
@Permission(Name = "version", Role = Role.MODERATOR)
@SubCommand({ "version", "v" })
public class VersionCommand extends Command {

    public VersionCommand(FoundationPlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void execute(BedrockChatSender sender, String[] args) throws CommandException, IllegalCommandArgumentException {
        getPlugin().messages().version(sender);
    }

}