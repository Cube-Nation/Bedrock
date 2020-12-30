package de.cubenation.bedrock.implementation.bukkit.wrapper;

import de.cubenation.bedrock.core.exception.WrongBedrockImplementationException;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import de.cubenation.bedrock.core.wrapper.BedrockPosition;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BukkitPlayer implements BedrockPlayer {

    private Player player;

    public BukkitPlayer(Player player) {
        this.player = player;
    }

    public static BukkitPlayer wrap(Player player) {
        return new BukkitPlayer(player);
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public BukkitLocation getPosition() {
        return new BukkitLocation(player.getLocation());
    }

    @Override
    public void teleport(BedrockPosition pos) throws WrongBedrockImplementationException {
        if(!(pos instanceof BukkitLocation))
            throw new WrongBedrockImplementationException();
        player.teleport((Location) pos);
    }

    @Override
    public String getDisplayName() {
        return player.getDisplayName();
    }

    @Override
    public void sendMessage(BaseComponent component) {
        player.spigot().sendMessage(component);
    }

    @Override
    public void sendMessage(ChatMessageType type, BaseComponent[] components) {
        player.spigot().sendMessage(type, components);
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    @Override
    public boolean isOp() {
        return player.isOp();
    }

    @Override
    public void sendMessage(String msg) {
        player.sendMessage(msg);
    }
}
