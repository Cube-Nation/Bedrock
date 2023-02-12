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
import de.cubenation.bedrock.core.annotation.Permission;
import de.cubenation.bedrock.core.annotation.injection.Inject;
import de.cubenation.bedrock.core.authorization.Role;
import de.cubenation.bedrock.core.command.Command;
import de.cubenation.bedrock.core.exception.PlayerNotFoundException;
import de.cubenation.bedrock.core.service.permission.PermissionService;
import de.cubenation.bedrock.core.model.wrapper.BedrockChatSender;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@Description("command.bedrock.permissions.desc")
@Permission(Name = "permissions.other", Role = Role.MODERATOR)
@Permission(Name = "permissions.self", Role = Role.USER)
public class PermissionOtherCommand extends Command {

    @Inject
    private PermissionService permissionService;

    public PermissionOtherCommand(FoundationPlugin plugin) {
        super(plugin);
    }

    public void execute(
            BedrockChatSender sender,
            @Argument(Description = "command.bedrock.username_uuid.desc", Placeholder = "command.bedrock.username_uuid.ph")
            @Permission(Name = "permissions.other", Role = Role.MODERATOR)
            String name
    ) {

        String player = name != null ? name : sender.getName();

        if (permissionService == null) {
            plugin.messages().noPermission(sender);
            return;
        }

        try {
            BedrockChatSender bedrockChatSender = plugin.getBedrockServer().getPlayer(player);
            plugin.messages().displayPermissions(sender, permissionService.getPermissions(bedrockChatSender));

        } catch (PlayerNotFoundException e) {
            plugin.messages().noSuchPlayer(sender, player);
        }
    }

}
