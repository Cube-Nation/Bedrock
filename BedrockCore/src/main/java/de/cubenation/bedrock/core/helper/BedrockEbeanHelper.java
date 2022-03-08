/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.bedrock.core.helper;

import de.cubenation.bedrock.core.exception.BedrockEbeanEntityNotFoundException;
import de.cubenation.bedrock.core.model.BedrockOfflinePlayer;
import de.cubenation.bedrock.core.model.query.QBedrockOfflinePlayer;
import io.ebean.RawSql;
import io.ebean.RawSqlBuilder;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Cube-Nation
 * @version 2.0
 */

public class BedrockEbeanHelper {

//    /**
//     * Returns a BedrockOfflinePlayer object for the given org.bukkit.entity.Player.
//     *
//     * @param player          A org.bukkit.entity.Player object.
//     * @param successCallback The success callback for the result of the request.
//     * @param failureCallback The failure callback for the result of the request.
//     */
//    public static void requestBedrockPlayer(final BedrockPlayer player, SuccessCallback<BedrockOfflinePlayer> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
//        requestBedrockPlayer(player.getUniqueId(), successCallback, failureCallback);
//    }
//
//    /**
//     * Request a list of BedrockOfflinePlayer for a list of players.
//     *
//     * @param players           Player of the BedrockPlayers to request.
//     * @param successCallback The success callback for the result of the request.
//     * @param failureCallback The failure callback for the result of the request.
//     */
//    public static void bulkRequestBedrockPlayerForPlayers(final ArrayList<BedrockPlayer> players, final SuccessCallback<HashMap<BedrockPlayer, BedrockOfflinePlayer>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
//        if (successCallback == null) {
//            return;
//        }
//
//        if (players == null || players.isEmpty()) {
//            successCallback.didFinished(new HashMap<>());
//            return;
//        }
//
//        List<UUID> collect = players.stream().map(player -> {
//            return player.getUniqueId();
//        }).collect(Collectors.toList());
//        bulkRequestBedrockPlayerForUuids((ArrayList<UUID>) collect, bedrockPlayers -> {
//            HashMap<BedrockPlayer, BedrockOfflinePlayer> result = new HashMap<>();
//            for (Map.Entry<UUID, BedrockOfflinePlayer> entry : bedrockPlayers.entrySet()) {
//                result.put(BukkitPlayer.wrap(BedrockPlugin.getInstance().getServer().getPlayer(entry.getKey())), entry.getValue());
//            }
//            successCallback.didFinished(result);
//        }, failureCallback);
//    }

    /**
     * Gets the BedrockOfflinePlayer for a given UUID
     *
     * @param  uuid UUID of the BedrockPlayers to request.
     *
     * @return Future of corresponding BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public static CompletableFuture<BedrockOfflinePlayer> requestOfflinePlayer(UUID uuid) {
        return requestOfflinePlayerForUuidString(uuid.toString());
    }

    /**
     * Request a list of BedrockOfflinePlayer for a list of uuids.
     *
     * @param uuids           UUIDs of the BedrockPlayers to request.
     *
     * @return                Future of corresponding HashMap of requested UUIDs and BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public static CompletableFuture<Map<UUID, BedrockOfflinePlayer>> bulkRequestOfflinePlayer(final Collection<UUID> uuids) {
        List<String> collect = uuids.stream().map(UUID::toString).collect(Collectors.toList());

        return bulkRequestOfflinePlayerForUuidStrings(collect).thenApply(bedrockPlayers -> {
            HashMap<UUID, BedrockOfflinePlayer> result = new HashMap<>();
            for (Map.Entry<String, BedrockOfflinePlayer> entry : bedrockPlayers.entrySet()) {
                result.put(entry.getValue().getUUID(), entry.getValue());
            }
            return result;
        });
    }

    /**
     * Gets the BedrockOfflinePlayer for a given UUID
     *
     * @param uuid UUID of the BedrockPlayers to request as a String.
     *
     * @return     Future of corresponding BedrockOfflinePlayer.
     */
    public static CompletableFuture<BedrockOfflinePlayer> requestOfflinePlayerForUuidString(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            BedrockOfflinePlayer player = new QBedrockOfflinePlayer()
                    .uuid.equalTo(uuid)
                    .findOne();

            if (player == null) {
                throw new BedrockEbeanEntityNotFoundException(BedrockOfflinePlayer.class, uuid);
            }
            return player;
        });
    }

    /**
     * Request a list of BedrockOfflinePlayer for a list of uuids as Strings.
     *
     * @param uuids           UUIDs of the BedrockPlayers to request as Strings.
     *
     * @return                Future of corresponding HashMap of requested UUIDs and BedrockOfflinePlayer.
     */
    public static CompletableFuture<Map<String, BedrockOfflinePlayer>> bulkRequestOfflinePlayerForUuidStrings(final Collection<String> uuids) {
        return CompletableFuture.supplyAsync(() -> {
            if (uuids == null || uuids.isEmpty()) {
                return new HashMap<>();
            }

            String oql = "SELECT id, uuid, username, lastlogin " +
                    "FROM `bedrock_players` " +
                    "WHERE uuid IN ('" + StringUtils.join(uuids, "','") + "');";

            RawSql rawSql = RawSqlBuilder.parse(oql).create();
            List<BedrockOfflinePlayer> list = new QBedrockOfflinePlayer().setRawSql(rawSql).findList();

            if (list.isEmpty()) {
                throw new BedrockEbeanEntityNotFoundException(uuids, BedrockOfflinePlayer.class);
            }

            HashMap<String, BedrockOfflinePlayer> result = new HashMap<>();
            for (BedrockOfflinePlayer bedrockPlayer : list) {
                result.put(bedrockPlayer.getUuid(), bedrockPlayer);
            }
            return result;
        });
    }

    /**
     * Gets the BedrockOfflinePlayer for a given UUID
     *
     * @param usernameOrUuid Username or UUID (as a String) of the BedrockPlayers to request.
     * @param exact          Mach all or an exact player.
     *
     * @return               Future of corresponding BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public static CompletableFuture<BedrockOfflinePlayer> requestOfflinePlayer(final String usernameOrUuid, boolean exact) {
        if (UUIDUtil.isUUID(usernameOrUuid)) {
            return requestOfflinePlayerForUuidString(usernameOrUuid);
        }

        return requestOfflinePlayerForLastKnownUsername(usernameOrUuid, exact);
    }

    /**
     * Gets the BedrockOfflinePlayer for a given last known name
     *
     * @param username Username of the BedrockPlayers to request.
     * @param exact    Mach all or an exact player.
     *
     * @return         Future of corresponding BedrockOfflinePlayer.
     */
    public static CompletableFuture<BedrockOfflinePlayer> requestOfflinePlayerForLastKnownUsername(final String username, final boolean exact) {
        return CompletableFuture.supplyAsync(() -> {
            BedrockOfflinePlayer player = new QBedrockOfflinePlayer()
                    .username.like(username + (exact ? "" : "%"))
                    .orderBy().lastlogin.desc()
                    .findOne();

            if (player == null) {
                throw new BedrockEbeanEntityNotFoundException(BedrockOfflinePlayer.class, username);
            }

            return player;
        });
    }

    /**
     * Returns a list of BedrockOfflinePlayer for the given name Strings.
     *
     * @param usernamesOrUuids Username or UUID (as a String) of the BedrockPlayers to request.
     * @param exact            Mach all or an exact player.
     *
     * @return                 Future of corresponding BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public static CompletableFuture<Map<String, BedrockOfflinePlayer>> bulkRequestOfflinePlayer(final Collection<String> usernamesOrUuids, final boolean exact) {
        if (usernamesOrUuids == null || usernamesOrUuids.isEmpty()) {
            return CompletableFuture.supplyAsync(HashMap::new);
        }

        ArrayList<String> uuids = usernamesOrUuids.stream().filter(UUIDUtil::isUUID).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> names = usernamesOrUuids
                .stream()
                .filter(username -> !uuids.contains(username))
                .map(username -> (username + (exact ? "" : "%")))
                .collect(Collectors.toCollection(ArrayList::new));

        // Create SQL Query
        ArrayList<String> requestParts = names.stream().map(name -> "`username` LIKE '" + name + "'").collect(Collectors.toCollection(ArrayList::new));
        requestParts.add("uuid IN ('" + StringUtils.join(uuids, "','") + "')");
        String oql = "SELECT id, uuid, username, lastlogin " + "FROM `bedrock_players` WHERE " + StringUtils.join(requestParts, " OR ");
        RawSql rawSql = RawSqlBuilder.parse(oql).create();

        return CompletableFuture.supplyAsync(() -> {
            List<BedrockOfflinePlayer> players = new QBedrockOfflinePlayer().setRawSql(rawSql).findList();
            HashMap<String, BedrockOfflinePlayer> map = new HashMap<>();

            // match players to input
            for (String input : usernamesOrUuids) {
                for (BedrockOfflinePlayer bedrockPlayer : players) {
                    if (bedrockPlayer.getUuid().equals(input) || bedrockPlayer.getUsername().toLowerCase().startsWith(input.toLowerCase())) {
                        map.put(input, bedrockPlayer);
                    }
                }
            }

            return map;
        });
    }

    /**
     * Request a BedrockOfflinePlayer for the given ID.
     *
     * @param id The ID of the BedrockOfflinePlayer.
     *
     * @return   Future of corresponding BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public static CompletableFuture<BedrockOfflinePlayer> requestOfflinePlayerForId(int id) {
        return CompletableFuture.supplyAsync(() -> {
            BedrockOfflinePlayer player = new QBedrockOfflinePlayer()
                    .id.equalTo(id)
                    .findOne();

            if (player == null) {
                throw new BedrockEbeanEntityNotFoundException(BedrockOfflinePlayer.class, id);
            }
            return player;
        });
    }

    /**
     * Request a list of BedrockOfflinePlayer for a list of IDs.
     *
     * @param ids           IDs of the BedrockPlayers to request.
     *
     * @return              Future of corresponding HashMap of requested IDs and BedrockOfflinePlayer.
     */
    @SuppressWarnings("unused")
    public static CompletableFuture<Map<String, BedrockOfflinePlayer>> bulkRequestOfflinePlayerForIds(final Collection<Integer> ids) {
        return CompletableFuture.supplyAsync(() -> {
            if (ids == null || ids.isEmpty()) {
                return new HashMap<>();
            }

            String oql = "SELECT id, uuid, username, lastlogin " +
                    "FROM `bedrock_players` " +
                    "WHERE id IN ('" + StringUtils.join(ids, "','") + "');";

            RawSql rawSql = RawSqlBuilder.parse(oql).create();
            List<BedrockOfflinePlayer> list = new QBedrockOfflinePlayer().setRawSql(rawSql).findList();

            if (list.isEmpty()) {
                throw new BedrockEbeanEntityNotFoundException(BedrockOfflinePlayer.class, ids);
            }

            HashMap<String, BedrockOfflinePlayer> result = new HashMap<>();
            for (BedrockOfflinePlayer bedrockPlayer : list) {
                result.put(bedrockPlayer.getUuid(), bedrockPlayer);
            }
            return result;
        });
    }

//    /**
//     * CAUTION!
//     * Use this method only to add players, who played on the server before installing Bedrock!
//     *
//     * @param uuid          The unique id of the player
//     * @param playername    The name of the player
//     * @return the new BedrockOfflinePlayer instance.
//     * @throws BedrockEbeanEntityAlreadyExistsException if the player already exists.
//     */
//    public static BedrockOfflinePlayer createBedrockPlayer(UUID uuid, String playername) throws BedrockEbeanEntityAlreadyExistsException {
//        BedrockOfflinePlayer player = BedrockPlugin.getInstance().getDatabaseService().getDatabase(BedrockDatabase.class)
//                .find(BedrockOfflinePlayer.class)
//                .where()
//                .eq("uuid", uuid.toString())
//                .findOne();
//
//        if (player != null) {
//            throw new BedrockEbeanEntityAlreadyExistsException(BedrockOfflinePlayer.class, uuid.toString());
//        }
//
//        BedrockOfflinePlayer bedrockPlayer = new BedrockOfflinePlayer(uuid.toString(), playername, null, null);
//        bedrockPlayer.save(BedrockPlugin.getInstance().getDatabaseService().getDatabase(BedrockDatabase.class));
//
//        return bedrockPlayer;
//    }
}
