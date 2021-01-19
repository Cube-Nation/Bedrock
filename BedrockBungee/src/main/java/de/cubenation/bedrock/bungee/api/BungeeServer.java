package de.cubenation.bedrock.bungee.api;

import de.cubenation.bedrock.bungee.wrapper.BungeePlayer;
import de.cubenation.bedrock.core.BedrockServer;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class BungeeServer implements BedrockServer {

    private BasePlugin plugin;

    public BungeeServer(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BedrockPlayer getPlayer(String username) {
        return BungeePlayer.wrap(plugin.getProxy().getPlayer(username));
    }

    @Override
    public BedrockPlayer getPlayer(UUID uuid) {
        return BungeePlayer.wrap(plugin.getProxy().getPlayer(uuid));
    }

    @Override
    public Collection<? extends BedrockPlayer> getOnlinePlayers() {
        return plugin.getProxy().getPlayers().stream().map(o -> BungeePlayer.wrap(o)).collect(Collectors.toList());
    }
}
