package de.cubenation.bedrock.implementation.bukkit.wrapper;

import de.cubenation.bedrock.core.wrapper.BedrockChatSender;
import org.bukkit.command.CommandSender;

public class BukkitCommandSender implements BedrockChatSender {

    private CommandSender sender;

    public BukkitCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    public static BukkitCommandSender wrap(CommandSender sender) {
        return new BukkitCommandSender(sender);
    }

    public CommandSender getSender() {
        return sender;
    }

    @Override
    public String getDisplayName() {
        return sender.getName();
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isOp() {
        return sender.isOp();
    }

    @Override
    public void sendMessage(String msg) {
        sender.sendMessage(msg);
    }
}
