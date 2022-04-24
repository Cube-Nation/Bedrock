package de.cubenation.bedrock.bungee.api;

import de.cubenation.bedrock.bungee.wrapper.BungeePlayer;
import de.cubenation.bedrock.core.model.BedrockServer;
import de.cubenation.bedrock.core.model.wrapper.BedrockPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class BungeeServer implements BedrockServer {

    private final BasePlugin plugin;

    public BungeeServer(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BedrockPlayer getPlayer(String username) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(username);
        if (player == null) {
            return null;
        }
        return BungeePlayer.wrap(player);
    }

    @Override
    public BedrockPlayer getPlayer(UUID uuid) {
        return BungeePlayer.wrap(plugin.getProxy().getPlayer(uuid));
    }

    @Override
    public Collection<? extends BedrockPlayer> getOnlinePlayers() {
        return plugin.getProxy().getPlayers().stream().map(BungeePlayer::wrap).collect(Collectors.toList());
    }
}
