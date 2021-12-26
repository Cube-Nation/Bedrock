package de.cubenation.bedrock.bungee.plugin.event;

import de.cubenation.bedrock.core.model.BedrockOfflinePlayer;
import lombok.Getter;
import lombok.ToString;
import net.md_5.bungee.api.plugin.Event;

import java.util.List;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@ToString
public class MultiAccountJoinEvent extends Event {

    @Getter
    private String ip;

    @Getter
    private List<BedrockOfflinePlayer> bedrockPlayers;

    public MultiAccountJoinEvent(String ip, List<BedrockOfflinePlayer> bedrockPlayers) {
        this.ip = ip;
        this.bedrockPlayers = bedrockPlayers;
    }
}

