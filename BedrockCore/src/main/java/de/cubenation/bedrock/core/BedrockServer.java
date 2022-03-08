package de.cubenation.bedrock.core;

import de.cubenation.bedrock.core.wrapper.BedrockPlayer;

import java.util.Collection;
import java.util.UUID;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public abstract class BedrockServer {

    /**
     * Gets the player for a given username
     * @param username
     * @return corresponding BedrockPlayer
     */
    public abstract BedrockPlayer getPlayer(String username);

    /**
     * Gets the player for a given UUID
     * @param uuid
     * @return corresponding BedrockPlayer
     */
    public abstract BedrockPlayer getPlayer(UUID uuid);

    /**
     * Gets all currently active players
     * @return all active BedrockPlayers
     */
    public abstract Collection<? extends BedrockPlayer> getPlayers();
}
