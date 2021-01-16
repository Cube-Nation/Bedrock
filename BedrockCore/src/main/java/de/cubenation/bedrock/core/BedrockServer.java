package de.cubenation.bedrock.core;

import de.cubenation.bedrock.core.wrapper.BedrockPlayer;

import java.util.Collection;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public interface BedrockServer {

    /**
     * Gets the player for a given username
     * @param username
     * @return corresponding BedrockPlayer
     */
    BedrockPlayer getPlayer(String username);

    /**
     * Gets all currently active players
     * @return all active BedrockPlayers
     */
    Collection<? extends BedrockPlayer> getOnlinePlayers();
}
