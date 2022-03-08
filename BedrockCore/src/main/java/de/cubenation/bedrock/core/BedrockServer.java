package de.cubenation.bedrock.core;

import de.cubenation.bedrock.core.model.BedrockOfflinePlayer;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Cube-Nation
 * @version 2.0
 */

public abstract class BedrockServer {

    /**
     * Gets the player for a given username
     * @param username Name of the BedrockPlayers.
     * @return corresponding BedrockPlayer
     */
    @SuppressWarnings("unused")
    public abstract BedrockPlayer getPlayer(String username);

    /**
     * Gets the player for a given UUID
     * @param uuid UUID of the BedrockPlayers.
     * @return corresponding BedrockPlayer
     */
    @SuppressWarnings("unused")
    public abstract BedrockPlayer getPlayer(UUID uuid);

    /**
     * Gets all currently active players
     * @return all active BedrockPlayers
     */
    @SuppressWarnings("unused")
    public abstract Collection<? extends BedrockPlayer> getPlayers();

    /**
     * Gets the BedrockOfflinePlayer for a given UUID
     * @param  uuid UUID of the BedrockPlayers.
     * @return Future of corresponding BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public abstract CompletableFuture<BedrockOfflinePlayer> getOfflinePlayer(UUID uuid);

    /**
     * Gets a list of BedrockOfflinePlayer for a list of uuids.
     * @param uuids UUIDs of the BedrockPlayers.
     * @return Future of corresponding HashMap of requested UUIDs and BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public abstract CompletableFuture<Map<UUID, BedrockOfflinePlayer>> getOfflinePlayers(final Collection<UUID> uuids);

    /**
     * Gets the BedrockOfflinePlayer for a given UUID
     * @param uuid UUID of the BedrockPlayers to request as a String.
     * @return Future of corresponding BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public abstract CompletableFuture<BedrockOfflinePlayer> getOfflinePlayerForUuidString(String uuid);

    /**
     * Gets a list of BedrockOfflinePlayer for a list of uuids as Strings.
     * @param uuids UUIDs of the BedrockPlayers as Strings.
     * @return Future of corresponding HashMap of requested UUIDs and BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public abstract CompletableFuture<Map<String, BedrockOfflinePlayer>> getOfflinePlayersForUuidStrings(final Collection<String> uuids);

    /**
     * Gets the BedrockOfflinePlayer for a given UUID
     * @param usernameOrUuid Username or UUID (as a String) of the BedrockPlayers.
     * @param exact Mach all or an exact player.
     * @return Future of corresponding BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public abstract CompletableFuture<BedrockOfflinePlayer> getOfflinePlayer(final String usernameOrUuid, boolean exact);

    /**
     * Gets the BedrockOfflinePlayer for a given last known name
     * @param username Username of the BedrockPlayers.
     * @param exact Mach all or an exact player.
     * @return Future of corresponding BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public abstract CompletableFuture<BedrockOfflinePlayer> getOfflinePlayerForLastKnownUsername(final String username, final boolean exact);

    /**
     * Gets a list of BedrockOfflinePlayer for the given name Strings.
     * @param usernamesOrUuids Username or UUID (as a String) of the BedrockPlayers.
     * @param exact Mach all or an exact player.
     * @return Future of corresponding BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public abstract CompletableFuture<Map<String, BedrockOfflinePlayer>> getOfflinePlayers(final Collection<String> usernamesOrUuids, final boolean exact);

    /**
     * Gets a BedrockOfflinePlayer for the given ID.
     * @param id The ID of the BedrockOfflinePlayer.
     * @return Future of corresponding BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public abstract CompletableFuture<BedrockOfflinePlayer> getOfflinePlayerForId(int id);

    /**
     * Gets a list of BedrockOfflinePlayer for a list of IDs.
     * @param ids IDs of the BedrockPlayers.
     * @return Future of corresponding HashMap of requested IDs and BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public abstract CompletableFuture<Map<String, BedrockOfflinePlayer>> getOfflinePlayersForIds(final Collection<Integer> ids);
}
