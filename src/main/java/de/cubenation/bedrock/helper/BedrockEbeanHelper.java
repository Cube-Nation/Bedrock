package de.cubenation.bedrock.helper;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.callback.MultipleBedrockPlayerCallback;
import de.cubenation.bedrock.callback.SingleBedrockPlayerCallback;
import de.cubenation.bedrock.callback.SingleBedrockWorldCallback;
import de.cubenation.bedrock.ebean.BedrockPlayer;
import de.cubenation.bedrock.ebean.BedrockWorld;
import de.cubenation.bedrock.exception.BedrockEbeanEntityAlreadyExistsException;
import de.cubenation.bedrock.exception.BedrockEbeanEntityNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by Tristan Cebulla <equinox@lichtspiele.org> on 03.08.2015.
 */
@SuppressWarnings({"unused", "DefaultFileTemplate"})
public class BedrockEbeanHelper {

    /**
     * Returns a BedrockPlayer object for the given org.bukkit.entity.Player
     *
     * @param player A org.bukkit.entity.Player object
     * @return BedrockPlayer
     * @throws BedrockEbeanEntityNotFoundException
     */
    @Deprecated
    public static BedrockPlayer getBedrockPlayer(Player player) throws BedrockEbeanEntityNotFoundException {
        return getBedrockPlayer(player.getUniqueId());
    }

    /**
     * Returns a BedrockPlayer object for the given org.bukkit.entity.Player.
     *
     * @param player A org.bukkit.entity.Player object.
     * @param callback The callback for the result of the request.
     */
    public static void requestBedrockPlayer(final Player player, SingleBedrockPlayerCallback callback){
        requestBedrockPlayer(player.getUniqueId(), callback);
    }

    /**
     * Returns a BedrockPlayer object for the given java.util.UUID
     *
     * @param uuid A UUID
     * @return BedrockPlayer
     * @throws BedrockEbeanEntityNotFoundException
     */
    @Deprecated
    public static BedrockPlayer getBedrockPlayer(UUID uuid) throws BedrockEbeanEntityNotFoundException {
        return getBedrockPlayer(uuid.toString());
    }

    /**
     * Returns a BedrockPlayer object for the given java.util.UUID
     *
     * @param uuid The UUID of a player.
     * @param callback The callback for the result of the request.
     */
    public static void requestBedrockPlayer(UUID uuid, SingleBedrockPlayerCallback callback) {
        requestBedrockPlayer(uuid.toString(), callback);
    }

    /**
     * Returns a BedrockPlayer object for the given uuid String
     *
     * @param uuid A string representing a UUID
     * @return BedrockPlayer
     * @throws BedrockEbeanEntityNotFoundException
     */
    @Deprecated
    public static BedrockPlayer getBedrockPlayer(String uuid) throws BedrockEbeanEntityNotFoundException {
        BedrockPlayer player = BedrockPlugin.getInstance().getDatabase()
                .find(BedrockPlayer.class)
                .where()
                .eq("uuid", uuid)
                .findUnique();

        if (player == null)
            throw new BedrockEbeanEntityNotFoundException(BedrockPlayer.class, uuid);

        return player;
    }

    /**
     * Returns a BedrockPlayer object for the given uuid String.
     *
     * @param uuid A string representing a UUID.
     * @param callback The callback for the result of the request.
     */
    public static void requestBedrockPlayer(final String uuid, final SingleBedrockPlayerCallback callback) {
        if (callback == null) {
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

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {

                        if (player == null) {
                            callback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockPlayer.class, uuid));
                        } else {
                            callback.didFinished(player);
                        }

                        return null;
                    }
                });
            }
        });
    }

    /**
     * Returns a BedrockPlayer object for the given name String
     *
     * @param username A string representing a username
     * @return BedrockPlayer
     * @throws BedrockEbeanEntityNotFoundException
     */
    @Deprecated
    public static List<BedrockPlayer> getBedrockPlayerForLastKnownName(final String username) throws BedrockEbeanEntityNotFoundException {
        return getBedrockPlayerForLastKnownName(username, false);
    }

    /**
     * Returns a BedrockPlayer object for the given name String
     *
     * @param username A string representing a username
     * @param exact Mach all or an exact player
     * @return BedrockPlayer
     * @throws BedrockEbeanEntityNotFoundException
     */
    @Deprecated
    public static List<BedrockPlayer> getBedrockPlayerForLastKnownName(final String username, boolean exact) throws BedrockEbeanEntityNotFoundException {

        if (UUIDUtil.isUUID(username)) {
            return new ArrayList<BedrockPlayer>() {{
                getBedrockPlayer(username);
            }};
        }

        List<BedrockPlayer> players = BedrockPlugin.getInstance().getDatabase().find(BedrockPlayer.class)
                .where()
                .like("username", username + (exact ? "" : "%"))
                .orderBy().desc("lastlogin")
                .findList();

        if (players == null) {
            System.out.println("players == null");
            throw new BedrockEbeanEntityNotFoundException(BedrockPlayer.class, username);
        }

        return players;
    }

    /**
     * Returns a BedrockPlayer object for the given name String.
     *
     * @param username A string representing a username.
     * @param exact Mach all or an exact player.
     * @param callback The callback for the result of the request.
     */
    public static void requestBedrockPlayerForLastKnownName(final String username, final boolean exact, final MultipleBedrockPlayerCallback callback) {
        if (callback == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedrockPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (UUIDUtil.isUUID(username)) {
                    requestBedrockPlayer(username, new SingleBedrockPlayerCallback() {
                        @Override
                        public void didFinished(final BedrockPlayer player) {
                            callback.didFinished(new ArrayList<BedrockPlayer>(){{
                                add(player);
                            }});
                        }

                        @Override
                        public void didFailed(Exception e) {
                            callback.didFailed(e);
                        }
                    });
                }


                final List<BedrockPlayer> players = BedrockPlugin.getInstance().getDatabase().find(BedrockPlayer.class)
                        .where()
                        .like("username", username + (exact ? "" : "%"))
                        .orderBy().desc("lastlogin")
                        .findList();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {

                        if (players == null) {
                            callback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockPlayer.class, username));
                        } else {
                            callback.didFinished(players);
                        }


                        return null;
                    }
                });
            }
        });
    }


    /**
     * Returns a BedrockPlayer object for the given id
     *
     * @param id The id of the BedrockPlayer
     * @return BedrockPlayer
     * @throws BedrockEbeanEntityNotFoundException
     */
    @Deprecated
    public static BedrockPlayer getBedrockPlayer(int id) throws BedrockEbeanEntityNotFoundException {
        BedrockPlayer player = BedrockPlugin.getInstance().getDatabase()
                .find(BedrockPlayer.class)
                .where()
                .eq("id", id)
                .findUnique();

        if (player == null)
            throw new BedrockEbeanEntityNotFoundException(BedrockPlayer.class, id);

        return player;
    }

    /**
     * Returns a BedrockPlayer object for the given id
     *
     * @param id The id of the BedrockPlayer.
     * @param callback The callback for the result of the request.
     */
    public static void requestBedrockPlayer(final int id, final SingleBedrockPlayerCallback callback) {
        if (callback == null) {
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

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), new Callable<Void>() {

                    @Override
                    public Void call() throws Exception {
                        if (player == null) {
                            callback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockPlayer.class, id));
                        } else {
                            callback.didFinished(player);
                        }
                        return null;
                    }
                });
            }
        });
    }

    /**
     * CAUTION!
     * Use this method only to add players, who played on the server before installing Bedrock!
     *
     * @param uuid       The unique id of the player
     * @param playername The name of the player
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
     * Returns a BedrockWorld for the given org.bukkit.World
     *
     * @param world A org.bukkit.World object
     * @return BedrockWorld
     * @throws BedrockEbeanEntityNotFoundException
     */
    @Deprecated
    public static BedrockWorld getBedrockWorld(World world) throws BedrockEbeanEntityNotFoundException {
        return getBedrockWorld(world.getUID());
    }

    /**
     * Returns a BedrockWorld for the given org.bukkit.World.
     *
     * @param world A org.bukkit.World object.
     * @param callback The callback for the result of the request.
     */
    public static void requestBedrockWorld(World world, SingleBedrockWorldCallback callback) {
        requestBedrockWorld(world.getUID(), callback);
    }

    /**
     * Returns a BedrockWorld for the given java.util.UUID
     *
     * @param uuid A UUID
     * @return BedrockWorld
     * @throws BedrockEbeanEntityNotFoundException
     */
    @Deprecated
    public static BedrockWorld getBedrockWorld(UUID uuid) throws BedrockEbeanEntityNotFoundException {
        return getBedrockWorld(uuid.toString());
    }

    /**
     * Returns a BedrockWorld for the given java.util.UUID
     *
     * @param uuid A UUID.
     * @param callback The callback for the result of the request.
     */
    public static void requestBedrockWorld(UUID uuid, SingleBedrockWorldCallback callback) {
        requestBedrockWorld(uuid.toString(), callback);
    }

    /**
     * Returns a BedrockWorld for the given uuid String
     *
     * @param uuid A string representing a UUID
     * @return BedrockWorld
     * @throws BedrockEbeanEntityNotFoundException
     */
    @Deprecated
    public static BedrockWorld getBedrockWorld(String uuid) throws BedrockEbeanEntityNotFoundException {
        BedrockWorld world = BedrockPlugin.getInstance().getDatabase()
                .find(BedrockWorld.class)
                .where()
                .eq("uuid", uuid)
                .findUnique();

        if (world == null)
            throw new BedrockEbeanEntityNotFoundException(BedrockWorld.class, uuid);

        return world;
    }

    /**
     * Returns a BedrockWorld for the given uuid String.
     *
     * @param uuid A string representing a UUID.
     * @param callback The callback for the result of the request.
     */
    public static void requestBedrockWorld(final String uuid, final SingleBedrockWorldCallback callback) {
        if (callback == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedrockPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                final BedrockWorld world = BedrockPlugin.getInstance().getDatabase()
                        .find(BedrockWorld.class)
                        .where()
                        .eq("uuid", uuid)
                        .findUnique();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        if (world == null) {
                            callback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockWorld.class, uuid));
                        } else {
                            callback.didFinished(world);
                        }
                        return null;
                    }
                });

            }
        });

    }

    /**
     * Returns a BedrockWorld for the given id
     *
     * @param id The id of the BedrockPlayer
     * @return BedrockWorld
     * @throws BedrockEbeanEntityNotFoundException
     */
    @Deprecated
    public static BedrockWorld getBedrockWorld(int id) throws BedrockEbeanEntityNotFoundException {
        BedrockWorld world = BedrockPlugin.getInstance().getDatabase()
                .find(BedrockWorld.class)
                .where()
                .eq("id", id)
                .findUnique();

        if (world == null)
            throw new BedrockEbeanEntityNotFoundException(BedrockWorld.class, id);

        return world;
    }

    public static void requestBedrockWorld(final int id, final SingleBedrockWorldCallback callback) {
        if (callback == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(BedrockPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                final BedrockWorld world = BedrockPlugin.getInstance().getDatabase()
                        .find(BedrockWorld.class)
                        .where()
                        .eq("id", id)
                        .findUnique();

                Bukkit.getScheduler().callSyncMethod(BedrockPlugin.getInstance(), new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        if (world == null) {
                            callback.didFailed(new BedrockEbeanEntityNotFoundException(BedrockWorld.class, id));
                        } else {
                            callback.didFinished(world);
                        }

                        return null;
                    }
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