package de.cubenation.bedrock.core;

import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import de.cubenation.bedrock.implementation.bukkit.wrapper.BukkitPlayer;
import org.bukkit.Bukkit;

public class Bedrock {

    public static BedrockPlayer getPlayer(String playerName) {
        return BukkitPlayer.wrap(Bukkit.getPlayer(playerName));
    }

    public static BedrockPlayer[] getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(p -> BukkitPlayer.wrap(p)).toArray(BedrockPlayer[]::new);
    }
}
