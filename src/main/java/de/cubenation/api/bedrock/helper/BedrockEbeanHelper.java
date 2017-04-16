package de.cubenation.api.bedrock.helper;

import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import de.cubenation.api.bedrock.BedrockPlugin;
import de.cubenation.api.bedrock.callback.FailureCallback;
import de.cubenation.api.bedrock.callback.SuccessCallback;
import de.cubenation.api.bedrock.ebean.BedrockPlayer;
import de.cubenation.api.bedrock.ebean.BedrockWorld;
import de.cubenation.api.bedrock.exception.BedrockEbeanEntityAlreadyExistsException;
import de.cubenation.api.bedrock.exception.BedrockEbeanEntityNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author Cube-Nation
 * @version {$project.version}
 */
@SuppressWarnings("ALL")
public class BedrockEbeanHelper {

    /**
     * Returns a BedrockPlayer object for the given org.bukkit.entity.Player.
     *
     * @param player          A org.bukkit.entity.Player object.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void requestBedrockPlayer(final Player player, SuccessCallback<BedrockPlayer> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        requestBedrockPlayer(player.getUniqueId(), successCallback, failureCallback);
    }

    /**
     * Request a list of BedrockPlayer for a list of players.
     *
     * @param players           Player of the BedrockPlayers to request.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void bulkRequestBedrockPlayerForPlayers(final ArrayList<Player> players, final SuccessCallback<HashMap<Player, BedrockPlayer>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
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
            HashMap<Player, BedrockPlayer> result = new HashMap<>();
            for (Map.Entry<UUID, BedrockPlayer> entry : bedrockPlayers.entrySet()) {
                result.put(entry.getValue().getPlayer(), entry.getValue());
            }
            successCallback.didFinished(result);
        }, failureCallback);
    }

    /**
     * Returns a BedrockPlayer object for the given java.util.UUID
     *
     * @param uuid            The UUID of a player.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void requestBedrockPlayer(UUID uuid, SuccessCallback<BedrockPlayer> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        requestBedrockPlayer(uuid.toString(), successCallback, failureCallback);
    }

    /**
     * Request a list of BedrockPlayer for a list of uuids.
     *
     * @param uuids           UUIDs of the BedrockPlayers to request.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void bulkRequestBedrockPlayerForUuids(final ArrayList<UUID> uuids, final SuccessCallback<HashMap<UUID, BedrockPlayer>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
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
            HashMap<UUID, BedrockPlayer> result = new HashMap<>();
            for (Map.Entry<String, BedrockPlayer> entry : bedrockPlayers.entrySet()) {
                result.put(entry.getValue().getUUID(), entry.getValue());
            }
            successCallback.didFinished(result);
        }, failureCallback);
    }

    /**
     * Returns a BedrockPlayer object for the given uuid String.
     *
     * @param uuid            A string representing a UUID.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void requestBedrockPlayer(final String uuid, final SuccessCallback<BedrockPlayer> successCallback, final FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        if (successCallback == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedrockPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {

                final BedrockPlayer player = BedrockPlugin.getInstance().getDatabase()
                        .find(BedrockPlayer.class)
                        .where()
                        .eq("uuid", uuid)
                        .findUnique();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                    if (player == null) {
                        if (failureCallback != null) {
                            failureCallback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockPlayer.class, uuid));
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
     * Request a list of BedrockPlayer for a list of uuids.
     *
     * @param uuids           UUIDs of the BedrockPlayers to request.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void bulkRequestBedrockPlayerForUuidStrings(final ArrayList<String> uuids, final SuccessCallback<HashMap<String, BedrockPlayer>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
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
                List<BedrockPlayer> list = BedrockPlugin.getInstance().getDatabase().find(BedrockPlayer.class).setRawSql(rawSql).findList();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                    if (list == null || list.isEmpty()) {
                        failureCallback.didFailed(new BedrockEbeanEntityNotFoundException(uuids, BedrockPlayer.class));
                    } else {
                        HashMap<String, BedrockPlayer> result = new HashMap<>();
                        for (BedrockPlayer bedrockPlayer : list) {
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
     * Returns a BedrockPlayer object for the given name String.
     *
     * @param username        A string representing a username.
     * @param exact           Mach all or an exact player.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void requestBedrockPlayerForLastKnownName(final String username, final boolean exact, final SuccessCallback<ArrayList<BedrockPlayer>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        if (successCallback == null) {
            failureCallback.didFailed(null);
            return;
        }

        if (UUIDUtil.isUUID(username)) {
            requestBedrockPlayer(username,
                    player -> successCallback.didFinished(new ArrayList<BedrockPlayer>() {{add(player);}}),
                    failureCallback);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedrockPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {

                final List<BedrockPlayer> foundPlayers = BedrockPlugin.getInstance().getDatabase().find(BedrockPlayer.class)
                        .where()
                        .like("username", username + (exact ? "" : "%"))
                        .orderBy().desc("lastlogin")
                        .findList();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                    if (foundPlayers != null && !foundPlayers.isEmpty()) {
                        ArrayList<BedrockPlayer> bedrockPlayers = new ArrayList<BedrockPlayer>();
                        bedrockPlayers.addAll(foundPlayers);

                        successCallback.didFinished(bedrockPlayers);
                        return null;
                    }

                    failureCallback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockPlayer.class, username));
                    return null;
                });
            }
        });
    }

    /**
     * Returns a list of BedrockPlayer for the given name Strings.
     *
     * @param usernames       A list of usernames.
     * @param exact           Mach all or an exact player.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void bulkRequestBedrockPlayerForLastKnownNames(final ArrayList<String> usernames, final boolean exact, final SuccessCallback<HashMap<String, ArrayList<BedrockPlayer>>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
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
                HashMap<String, ArrayList<BedrockPlayer>> result = new HashMap<String, ArrayList<BedrockPlayer>>() {{
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
                    List<BedrockPlayer> list = BedrockPlugin.getInstance().getDatabase().find(BedrockPlayer.class).setRawSql(rawSql).findList();

                    for (String name : usernames) {
                        for (BedrockPlayer bedrockPlayer : list) {
                            if (bedrockPlayer.getUsername().toLowerCase().startsWith(name.toLowerCase())) {
                                result.get(name).add(bedrockPlayer);
                            }
                        }
                    }

                    bulkRequestBedrockPlayerForUuidStrings(uuids, bedrockPlayers -> {
                        for (BedrockPlayer bedrockPlayer : bedrockPlayers.values()) {
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
                        for (BedrockPlayer bedrockPlayer : bedrockPlayers.values()) {
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
     * Returns a BedrockPlayer object for the given id
     *
     * @param id              The id of the BedrockPlayer.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void requestBedrockPlayer(final int id, final SuccessCallback<BedrockPlayer> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        if (successCallback == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedrockPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                final BedrockPlayer player = BedrockPlugin.getInstance().getDatabase()
                        .find(BedrockPlayer.class)
                        .where()
                        .eq("id", id)
                        .findUnique();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                    if (player == null) {
                        if (failureCallback != null) {
                            failureCallback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockPlayer.class, id));
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
     * Request a list of BedrockPlayer for a list of ids.
     *
     * @param ids             Ids of the BedrockPlayers to request.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void bulkRequestBedrockPlayerForIds(final ArrayList<Integer> ids, final SuccessCallback<HashMap<Integer, BedrockPlayer>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
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
                List<BedrockPlayer> list = BedrockPlugin.getInstance().getDatabase().find(BedrockPlayer.class).setRawSql(rawSql).findList();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                    if (list == null || list.isEmpty()) {
                        failureCallback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockPlayer.class, ids));
                    } else {
                        HashMap<Integer, BedrockPlayer> result = new HashMap<>();
                        for (BedrockPlayer bedrockPlayer : list) {
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
     * @return the new BedrockPlayer instance.
     * @throws BedrockEbeanEntityAlreadyExistsException if the player already exists.
     */
    public static BedrockPlayer createBedrockPlayer(UUID uuid, String playername) throws BedrockEbeanEntityAlreadyExistsException {
        BedrockPlayer player = BedrockPlugin.getInstance().getDatabase()
                .find(BedrockPlayer.class)
                .where()
                .eq("uuid", uuid.toString())
                .findUnique();

        if (player != null) {
            throw new BedrockEbeanEntityAlreadyExistsException(BedrockPlayer.class, uuid.toString());
        }

        BedrockPlayer bedrockPlayer = new BedrockPlayer(uuid.toString(), playername, null);
        bedrockPlayer.save();

        return bedrockPlayer;
    }

    /**
     * Returns a BedrockWorld for the given org.bukkit.World.
     *
     * @param world           A org.bukkit.World object.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void requestBedrockWorld(World world, SuccessCallback<BedrockWorld> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        requestBedrockWorld(world.getUID(), successCallback, failureCallback);
    }

    /**
     * Returns a BedrockWorld for the given java.util.UUID
     *
     * @param uuid            A UUID.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void requestBedrockWorld(UUID uuid, SuccessCallback<BedrockWorld> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        requestBedrockWorld(uuid.toString(), successCallback, failureCallback);
    }

    /**
     * Returns a BedrockWorld for the given uuid String.
     *
     * @param uuids           A list of UUIDs.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void bulkRequestBedrockWorldForUuids(final ArrayList<UUID> uuids, final SuccessCallback<HashMap<UUID, BedrockWorld>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
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
        bulkRequestBedrockWorldForUuidStrings((ArrayList<String>) collect, bedrockWorldHashMap -> {
            HashMap<UUID, BedrockWorld> result = new HashMap<>();
            for (Map.Entry<String, BedrockWorld> entry : bedrockWorldHashMap.entrySet()) {
                result.put(UUID.fromString(entry.getValue().getUuid()), entry.getValue());
            }
            successCallback.didFinished(result);
        }, failureCallback);
    }

    /**
     * Returns a BedrockWorld for the given uuid String.
     *
     * @param uuid            A string representing a UUID.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void requestBedrockWorld(final String uuid, final SuccessCallback<BedrockWorld> successCallback, final FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        if (successCallback == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedrockPlugin.getInstance(), () -> {
            final BedrockWorld world = BedrockPlugin.getInstance().getDatabase()
                    .find(BedrockWorld.class)
                    .where()
                    .eq("uuid", uuid)
                    .findUnique();

            Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                if (world == null) {
                    if (failureCallback != null) {
                        failureCallback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockWorld.class, uuid));
                    }
                } else {
                    successCallback.didFinished(world);
                }
                return null;
            });

        });

    }

    /**
     * Request a list of BedrockWorld for a list of uuids.
     *
     * @param uuids           UUIDs of the BedrockWorld to request.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void bulkRequestBedrockWorldForUuidStrings(final ArrayList<String> uuids, final SuccessCallback<HashMap<String, BedrockWorld>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
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

                String oql = "SELECT id, uuid " +
                        "FROM `bedrock_worlds` " +
                        "WHERE uuid IN ('" + StringUtils.join(uuids, "','") + "');";

                RawSql rawSql = RawSqlBuilder.parse(oql).create();
                List<BedrockWorld> list = BedrockPlugin.getInstance().getDatabase().find(BedrockWorld.class).setRawSql(rawSql).findList();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                    if (list == null || list.isEmpty()) {
                        failureCallback.didFailed(new BedrockEbeanEntityNotFoundException(uuids, BedrockPlayer.class));
                    } else {
                        HashMap<String, BedrockWorld> result = new HashMap<>();
                        for (BedrockWorld bedrockWorld : list) {
                            result.put(bedrockWorld.getUuid(), bedrockWorld);
                        }
                        successCallback.didFinished(result);
                    }
                    return null;
                });
            }
        });
    }

    /**
     * Returns a BedrockWorld for the given id.
     *
     * @param id              The id of the world.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void requestBedrockWorld(final int id, final SuccessCallback<BedrockWorld> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
        if (successCallback == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedrockPlugin.getInstance(), () -> {
            final BedrockWorld world = BedrockPlugin.getInstance().getDatabase()
                    .find(BedrockWorld.class)
                    .where()
                    .eq("id", id)
                    .findUnique();

            Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                if (world == null) {
                    if (failureCallback != null) {
                        failureCallback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockWorld.class, id));
                    }
                } else {
                    successCallback.didFinished(world);
                }

                return null;
            });
        });
    }

    /**
     * Request a list of BedrockWorld for a list of ids.
     *
     * @param ids             Ids of the BedrockWorlds to request.
     * @param successCallback The success callback for the result of the request.
     * @param failureCallback The failure callback for the result of the request.
     */
    public static void bulkRequestBedrockWorldForIds(final ArrayList<Integer> ids, final SuccessCallback<HashMap<Integer, BedrockWorld>> successCallback, FailureCallback<BedrockEbeanEntityNotFoundException> failureCallback) {
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

                String oql = "SELECT id, uuid " +
                        "FROM `bedrock_worlds` " +
                        "WHERE id IN ('" + StringUtils.join(ids, "','") + "');";

                RawSql rawSql = RawSqlBuilder.parse(oql).create();
                List<BedrockWorld> list = BedrockPlugin.getInstance().getDatabase().find(BedrockWorld.class).setRawSql(rawSql).findList();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), (Callable<Void>) () -> {
                    if (list == null || list.isEmpty()) {
                        failureCallback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockPlayer.class, ids));
                    } else {
                        HashMap<Integer, BedrockWorld> result = new HashMap<>();
                        for (BedrockWorld bedrockWorld : list) {
                            result.put(bedrockWorld.getId(), bedrockWorld);
                        }
                        successCallback.didFinished(result);
                    }
                    return null;
                });
            }
        });
    }


    public static BedrockWorld createBedrockWorld(World world) {

        BedrockWorld bw = BedrockPlugin.getInstance().getDatabase()
                .find(BedrockWorld.class)
                .where()
                .eq("uuid", world.getUID())
                .findUnique();

        if (bw == null) {
            // save to table
            bw = new BedrockWorld();
            bw.setUuid(world.getUID().toString());
            bw.save();
        }
        return bw;
    }

}