package de.cubenation.bedrock.core.model;

import de.cubenation.bedrock.core.model.wrapper.BedrockPlayer;

import java.util.Collection;
import java.util.UUID;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings("unused")
public interface BedrockServer {

    /**
     * Gets the player for a given username
     * @param username name to be looked up
     * @return corresponding BedrockPlayer
     */
    BedrockPlayer getPlayer(String username);

    /**
     * Gets the player for a given UUID
     * @param uuid UUID to be looked up
     * @return corresponding BedrockPlayer
     */
    BedrockPlayer getPlayer(UUID uuid);

    /**
     * Gets all currently active players
     * @return all active BedrockPlayers
     */
    Collection<? extends BedrockPlayer> getOnlinePlayers();
}
