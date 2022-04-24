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

package de.cubenation.bedrock.bukkit.api.helper;

import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import de.cubenation.bedrock.bukkit.api.exception.BedrockEbeanEntityAlreadyExistsException;
import de.cubenation.bedrock.bukkit.api.exception.BedrockEbeanEntityNotFoundException;
import de.cubenation.bedrock.bukkit.plugin.BedrockPlugin;
import de.cubenation.bedrock.bukkit.wrapper.BukkitPlayer;
import de.cubenation.bedrock.core.callback.FailureCallback;
import de.cubenation.bedrock.core.callback.SuccessCallback;
import de.cubenation.bedrock.core.helper.UUIDUtil;
import de.cubenation.bedrock.core.model.BedrockOfflinePlayer;
import de.cubenation.bedrock.core.model.wrapper.BedrockPlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings("ALL")
public class BedrockEbeanHelper {

    /**
     * Returns a BedrockOfflinePlayer object for the given org.bukkit.entity.Player.
     *
     * @param player          A org.bukkit.entity.Player object.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void requestBedrockPlayer(final BedrockPlayer player, SuccessCallback<BedrockOfflinePlayer> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        requestBedrockPlayer(player.getUniqueId(), successCallback, failureCallback);
    }

    /**
     * Request a list of BedrockOfflinePlayer for a list of players.
     *
     * @param players           Player of the BedrockPlayers to request.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void bulkRequestBedrockPlayerForPlayers(final ArrayList<BedrockPlayer> players, final SuccessCallback<HashMap<BedrockPlayer, BedrockOfflinePlayer>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        if (successCallback == null) {
            return;
        }

        if (players == null || players.isEmpty()) {
            successCallback.didFinished(new HashMap<>());
            return;
        }

        List<UUID> collect = players.stream().map(player -> {
            return player.getUniqueId();
        }).collect(Collectors.toList());
        bulkRequestBedrockPlayerForUuids((ArrayList<UUID>) collect, bedrockPlayers -> {
            HashMap<BedrockPlayer, BedrockOfflinePlayer> result = new HashMap<>();
            for (Map.Entry<UUID, BedrockOfflinePlayer> entry : bedrockPlayers.entrySet()) {
                result.put(BukkitPlayer.wrap(BedrockPlugin.getInstance().getServer().getPlayer(entry.getKey())), entry.getValue());
            }
            successCallback.didFinished(result);
        }, failureCallback);
    }

    /**
     * Returns a BedrockOfflinePlayer object for the given java.util.UUID
     *
     * @param uuid            The UUID of a player.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void requestBedrockPlayer(UUID uuid, SuccessCallback<BedrockOfflinePlayer> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        requestBedrockPlayer(uuid.toString(), successCallback, failureCallback);
    }

    /**
     * Request a list of BedrockOfflinePlayer for a list of uuids.
     *
     * @param uuids           UUIDs of the BedrockPlayers to request.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void bulkRequestBedrockPlayerForUuids(final ArrayList<UUID> uuids, final SuccessCallback<HashMap<UUID, BedrockOfflinePlayer>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        if (successCallback == null) {
            return;
        }

        if (uuids == null || uuids.isEmpty()) {
            successCallback.didFinished(new HashMap<>());
            return;
        }

        List<String> collect = uuids.stream().map(uuid -> {
            return uuid.toString();
        }).collect(Collectors.toList());
        bulkRequestBedrockPlayerForUuidStrings((ArrayList<String>) collect, bedrockPlayers -> {
            HashMap<UUID, BedrockOfflinePlayer> result = new HashMap<>();
            for (Map.Entry<String, BedrockOfflinePlayer> entry : bedrockPlayers.entrySet()) {
                result.put(entry.getValue().getUUID(), entry.getValue());
            }
            successCallback.didFinished(result);
        }, failureCallback);
    }

    /**
     * Returns a BedrockOfflinePlayer object for the given uuid String.
     *
     * @param uuid            A string representing a UUID.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void requestBedrockPlayer(final String uuid, final SuccessCallback<BedrockOfflinePlayer> successCallback, final FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        if (successCallback == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedrockPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {

                final BedrockOfflinePlayer player = BedrockPlugin.getInstance().getDatabase()
                        .find(BedrockOfflinePlayer.class)
                        .where()
                        .eq("uuid", uuid)
                        .findUnique();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                    if (player == null) {
                        if (failureCallback != null) {
                            failureCallback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockOfflinePlayer.class, uuid));
                        }
                    } else {
                        successCallback.didFinished(player);
                    }
                    return null;
                });
            }
        });
    }

    /**
     * Request a list of BedrockOfflinePlayer for a list of uuids.
     *
     * @param uuids           UUIDs of the BedrockPlayers to request.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void bulkRequestBedrockPlayerForUuidStrings(final ArrayList<String> uuids, final SuccessCallback<HashMap<String, BedrockOfflinePlayer>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        if (successCallback == null) {
            return;
        }

        if (uuids == null || uuids.isEmpty()) {
            successCallback.didFinished(new HashMap<>());
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedrockPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {

                String oql = "SELECT id, uuid, username, lastlogin " +
                        "FROM `bedrock_players` " +
                        "WHERE uuid IN ('" + StringUtils.join(uuids, "','") + "');";

                RawSql rawSql = RawSqlBuilder.parse(oql).create();
                List<BedrockOfflinePlayer> list = BedrockPlugin.getInstance().getDatabase().find(BedrockOfflinePlayer.class).setRawSql(rawSql).findList();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                    if (list == null || list.isEmpty()) {
                        failureCallback.didFailed(new BedrockEbeanEntityNotFoundException(uuids, BedrockOfflinePlayer.class));
                    } else {
                        HashMap<String, BedrockOfflinePlayer> result = new HashMap<>();
                        for (BedrockOfflinePlayer bedrockPlayer : list) {
                            result.put(bedrockPlayer.getUuid(), bedrockPlayer);
                        }
                        successCallback.didFinished(result);
                    }
                    return null;
                });
            }
        });
    }

    /**
     * Returns a BedrockOfflinePlayer object for the given name String.
     *
     * @param username        A string representing a username.
     * @param exact           Mach all or an exact player.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void requestBedrockPlayerForLastKnownName(final String username, final boolean exact, final SuccessCallback<ArrayList<BedrockOfflinePlayer>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        if (successCallback == null) {
            failureCallback.didFailed(null);
            return;
        }

        if (UUIDUtil.isUUID(username)) {
            requestBedrockPlayer(username,
                    player -> successCallback.didFinished(new ArrayList<BedrockOfflinePlayer>() {{add(player);}}),
                    failureCallback);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedrockPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {

                final List<BedrockOfflinePlayer> foundPlayers = BedrockPlugin.getInstance().getDatabase().find(BedrockOfflinePlayer.class)
                        .where()
                        .like("username", username + (exact ? "" : "%"))
                        .orderBy().desc("lastlogin")
                        .findList();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                    if (foundPlayers != null && !foundPlayers.isEmpty()) {
                        ArrayList<BedrockOfflinePlayer> bedrockPlayers = new ArrayList<BedrockOfflinePlayer>();
                        bedrockPlayers.addAll(foundPlayers);

                        successCallback.didFinished(bedrockPlayers);
                        return null;
                    }

                    failureCallback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockOfflinePlayer.class, username));
                    return null;
                });
            }
        });
    }

    /**
     * Returns a list of BedrockOfflinePlayer for the given name Strings.
     *
     * @param usernames       A list of usernames.
     * @param exact           Mach all or an exact player.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void bulkRequestBedrockPlayerForLastKnownNames(final ArrayList<String> usernames, final boolean exact, final SuccessCallback<HashMap<String, ArrayList<BedrockOfflinePlayer>>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        if (successCallback == null) {
            return;
        }

        if (usernames == null || usernames.isEmpty()) {
            successCallback.didFinished(new HashMap<>());
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedrockPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {


                ArrayList<String> uuids = usernames.stream().filter(username -> UUIDUtil.isUUID(username)).collect(Collectors.toCollection(ArrayList::new));
                ArrayList<String> names = usernames
                        .stream()
                        .filter(username -> !uuids.contains(username))
                        .map(username -> (username + (exact ? "" : "%")))
                        .collect(Collectors.toCollection(ArrayList::new));

                // Prepare result list
                HashMap<String, ArrayList<BedrockOfflinePlayer>> result = new HashMap<String, ArrayList<BedrockOfflinePlayer>>() {{
                    for (String username : usernames) {
                        put(username, new ArrayList<>());
                    }
                }};

                if (!names.isEmpty()) {
                    names = names.stream().map(name -> "`username` LIKE '" + name + "'").collect(Collectors.toCollection(ArrayList::new));

                    String oql = "SELECT id, uuid, username, lastlogin " +
                            "FROM `bedrock_players` WHERE " +
                            StringUtils.join(names, " OR ");

                    RawSql rawSql = RawSqlBuilder.parse(oql).create();
                    List<BedrockOfflinePlayer> list = BedrockPlugin.getInstance().getDatabase().find(BedrockOfflinePlayer.class).setRawSql(rawSql).findList();

                    for (String name : usernames) {
                        for (BedrockOfflinePlayer bedrockPlayer : list) {
                            if (bedrockPlayer.getUsername().toLowerCase().startsWith(name.toLowerCase())) {
                                result.get(name).add(bedrockPlayer);
                            }
                        }
                    }

                    bulkRequestBedrockPlayerForUuidStrings(uuids, bedrockPlayers -> {
                        for (BedrockOfflinePlayer bedrockPlayer : bedrockPlayers.values()) {
                            result.get(bedrockPlayer.getUuid()).add(bedrockPlayer);
                        }

                        Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                            successCallback.didFinished(result);
                            return null;
                        });

                    }, e -> {
                        Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                            if (result == null || result.isEmpty()) {
                                failureCallback.didFailed(e);
                            } else {
                                successCallback.didFinished(result);
                            }
                            return null;
                        });
                    });

                } else {
                    // Just UUID check

                    bulkRequestBedrockPlayerForUuidStrings(uuids, bedrockPlayers -> {
                        for (BedrockOfflinePlayer bedrockPlayer : bedrockPlayers.values()) {
                            result.get(bedrockPlayer.getUuid()).add(bedrockPlayer);
                        }

                        Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                            successCallback.didFinished(result);
                            return null;
                        });

                    }, e -> {
                        Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                            if (result == null || result.isEmpty()) {
                                failureCallback.didFailed(e);
                            } else {
                                successCallback.didFinished(result);
                            }
                            return null;
                        });
                    });
                }
            }
        });
    }

    /**
     * Returns a BedrockOfflinePlayer object for the given id
     *
     * @param id              The id of the BedrockOfflinePlayer.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void requestBedrockPlayer(final int id, final SuccessCallback<BedrockOfflinePlayer> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        if (successCallback == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedrockPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                final BedrockOfflinePlayer player = BedrockPlugin.getInstance().getDatabase()
                        .find(BedrockOfflinePlayer.class)
                        .where()
                        .eq("id", id)
                        .findUnique();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                    if (player == null) {
                        if (failureCallback != null) {
                            failureCallback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockOfflinePlayer.class, id));
                        }
                    } else {
                        successCallback.didFinished(player);
                    }
                    return null;
                });
            }
        });
    }

    /**
     * Request a list of BedrockOfflinePlayer for a list of ids.
     *
     * @param ids             Ids of the BedrockPlayers to request.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void bulkRequestBedrockPlayerForIds(final ArrayList<Integer> ids, final SuccessCallback<HashMap<Integer, BedrockOfflinePlayer>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        if (successCallback == null) {
            return;
        }

        if (ids == null || ids.isEmpty()) {
            successCallback.didFinished(new HashMap<>());
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedrockPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {

                String oql = "SELECT id, uuid, username, lastlogin " +
                        "FROM `bedrock_players` " +
                        "WHERE id IN ('" + StringUtils.join(ids, "','") + "');";

                RawSql rawSql = RawSqlBuilder.parse(oql).create();
                List<BedrockOfflinePlayer> list = BedrockPlugin.getInstance().getDatabase().find(BedrockOfflinePlayer.class).setRawSql(rawSql).findList();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                    if (list == null || list.isEmpty()) {
                        failureCallback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockOfflinePlayer.class, ids));
                    } else {
                        HashMap<Integer, BedrockOfflinePlayer> result = new HashMap<>();
                        for (BedrockOfflinePlayer bedrockPlayer : list) {
                            result.put(bedrockPlayer.getId(), bedrockPlayer);
                        }
                        successCallback.didFinished(result);
                    }
                    return null;
                });
            }
        });
    }

    /**
     * CAUTION!
     * Use this method only to add players, who played on the server before installing Bedrock!
     *
     * @param uuid          The unique id of the player
     * @param playername    The name of the player
     * @return the new BedrockOfflinePlayer instance.
     * @throws BedrockEbeanEntityAlreadyExistsException if the player already exists.
     */
    public static BedrockOfflinePlayer createBedrockPlayer(UUID uuid, String playername) throws BedrockEbeanEntityAlreadyExistsException {
        BedrockOfflinePlayer player = BedrockPlugin.getInstance().getDatabase()
                .find(BedrockOfflinePlayer.class)
                .where()
                .eq("uuid", uuid.toString())
                .findUnique();

        if (player != null) {
            throw new BedrockEbeanEntityAlreadyExistsException(BedrockOfflinePlayer.class, uuid.toString());
        }

        BedrockOfflinePlayer bedrockPlayer = new BedrockOfflinePlayer(uuid.toString(), playername, null, null);
        bedrockPlayer.save(BedrockPlugin.getInstance().getDatabase());

        return bedrockPlayer;
    }
}
