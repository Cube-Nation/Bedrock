package de.cubenation.bedrock.bukkit.api;

import de.cubenation.bedrock.bukkit.wrapper.BukkitPlayer;
import de.cubenation.bedrock.core.BedrockServer;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.stream.Collectors;

public class BukkitServer implements BedrockServer {

    @Override
    public BedrockPlayer getPlayer(String username) {
        return new BukkitPlayer(Bukkit.getPlayer(username));
    }

    @Override
    public Collection<? extends BedrockPlayer> getOnlinePlayers() {
        return Bukkit.getServer().getOnlinePlayers().stream().map(o -> new BukkitPlayer(o)).collect(Collectors.toList());
    }
}
