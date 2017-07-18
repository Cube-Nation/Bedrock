package de.cubenation.bedrock.bungee.plugin.event;

import de.cubenation.bedrock.core.model.BedrockPlayer;
import net.md_5.bungee.api.plugin.Event;

import java.util.List;
import java.util.UUID;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class MultiAccountJoinEvent extends Event {

    private String ip;

    private List<BedrockPlayer> bedrockPlayers;

    public MultiAccountJoinEvent(String ip, List<BedrockPlayer> bedrockPlayers) {
        this.ip = ip;
        this.bedrockPlayers = bedrockPlayers;
    }

    public String getIp() {
        return ip;
    }

    public List<BedrockPlayer> getBedrockPlayers() {
        return bedrockPlayers;
    }
}

