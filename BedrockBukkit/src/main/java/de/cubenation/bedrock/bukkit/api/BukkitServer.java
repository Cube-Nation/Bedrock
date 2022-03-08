package de.cubenation.bedrock.bukkit.api;

import de.cubenation.bedrock.bukkit.wrapper.BukkitPlayer;
import de.cubenation.bedrock.core.BedrockServer;
import de.cubenation.bedrock.core.exception.BedrockEbeanEntityNotFoundException;
import de.cubenation.bedrock.core.helper.UUIDUtil;
import de.cubenation.bedrock.core.model.BedrockOfflinePlayer;
import de.cubenation.bedrock.core.model.query.QBedrockOfflinePlayer;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import io.ebean.RawSql;
import io.ebean.RawSqlBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BukkitServer extends BedrockServer {

    @Override
    public BedrockPlayer getPlayer(String username) {
        Player player = Bukkit.getPlayer(username);
        if(player == null)
            return null;
        return BukkitPlayer.wrap(player);
    }

    @Override
    public BukkitPlayer getPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null)
            return null;
        return BukkitPlayer.wrap(player);
    }

    @Override
    public Collection<? extends BedrockPlayer> getPlayers() {
        return Bukkit.getServer().getOnlinePlayers().stream().map(BukkitPlayer::wrap).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unused")
    public CompletableFuture<BedrockOfflinePlayer> getOfflinePlayer(UUID uuid) {
        return getOfflinePlayerForUuidString(uuid.toString());
    }

    @Override
    @SuppressWarnings("unused")
    public CompletableFuture<Map<UUID, BedrockOfflinePlayer>> getOfflinePlayers(final Collection<UUID> uuids) {
        List<String> collect = uuids.stream().map(UUID::toString).collect(Collectors.toList());

        return getOfflinePlayersForUuidStrings(collect).thenApply(bedrockPlayers -> {
            HashMap<UUID, BedrockOfflinePlayer> result = new HashMap<>();
            for (Map.Entry<String, BedrockOfflinePlayer> entry : bedrockPlayers.entrySet()) {
                result.put(entry.getValue().getUUID(), entry.getValue());
            }
            return result;
        });
    }

    @Override
    @SuppressWarnings("unused")
    public CompletableFuture<BedrockOfflinePlayer> getOfflinePlayerForUuidString(String uuid) {
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

    @Override
    @SuppressWarnings("unused")
    public CompletableFuture<Map<String, BedrockOfflinePlayer>> getOfflinePlayersForUuidStrings(final Collection<String> uuids) {
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

    @Override
    @SuppressWarnings("unused")
    public CompletableFuture<BedrockOfflinePlayer> getOfflinePlayer(final String usernameOrUuid, boolean exact) {
        if (UUIDUtil.isUUID(usernameOrUuid)) {
            return getOfflinePlayerForUuidString(usernameOrUuid);
        }

        return getOfflinePlayerForLastKnownUsername(usernameOrUuid, exact);
    }

    @Override
    @SuppressWarnings("unused")
    public CompletableFuture<BedrockOfflinePlayer> getOfflinePlayerForLastKnownUsername(final String username, final boolean exact) {
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

    @Override
    @SuppressWarnings("unused")
    public CompletableFuture<Map<String, BedrockOfflinePlayer>> getOfflinePlayers(final Collection<String> usernamesOrUuids, final boolean exact) {
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

    @Override
    @SuppressWarnings("unused")
    public CompletableFuture<BedrockOfflinePlayer> getOfflinePlayerForId(int id) {
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

    @Override
    @SuppressWarnings("unused")
    public CompletableFuture<Map<String, BedrockOfflinePlayer>> getOfflinePlayersForIds(final Collection<Integer> ids) {
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
}
