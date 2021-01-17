package de.cubenation.bedrock.bukkit.api;

import de.cubenation.bedrock.bukkit.wrapper.BukkitPlayer;
import de.cubenation.bedrock.core.BedrockServer;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class BukkitServer implements BedrockServer {

    @Override
    public BedrockPlayer getPlayer(String username) {
        Player player = Bukkit.getPlayer(username);
        if(player == null)
            return null;
        return new BukkitPlayer(player);
    }

    public BukkitPlayer getPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null)
            return null;
        return new BukkitPlayer(player);
    }

    @Override
    public Collection<? extends BedrockPlayer> getOnlinePlayers() {
        return Bukkit.getServer().getOnlinePlayers().stream().map(o -> new BukkitPlayer(o)).collect(Collectors.toList());
    }
}
