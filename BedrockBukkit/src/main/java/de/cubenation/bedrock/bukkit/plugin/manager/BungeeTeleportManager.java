package de.cubenation.bedrock.bukkit.plugin.manager;

import de.cubenation.bedrock.bukkit.wrapper.BukkitPlayer;
import de.cubenation.bedrock.bukkit.wrapper.BukkitPosition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public class BungeeTeleportManager {

    private static BungeeTeleportManager instance;

    public static BungeeTeleportManager getInstance() {
        if(instance == null)
            instance = new BungeeTeleportManager();
        return instance;
    }

    private final Map<UUID, BukkitPosition> scheduledTeleports = new HashMap<>();

    public void scheduleTeleport(UUID uuid, BukkitPosition pos) {
        // directly teleport if player is online
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            BukkitPlayer.wrap(player).teleport(pos);
            return;
        }

        // otherwise schedule
        scheduledTeleports.put(uuid, pos);
    }

    public void tryExecuteTeleport(BukkitPlayer player) {
        if (scheduledTeleports.containsKey(player.getUniqueId())) {
            try {
                BukkitPosition pos = scheduledTeleports.get(player.getUniqueId());
                player.teleport(pos);
            } catch (Exception ex) {
                // occurs if the target World is null
            }
            scheduledTeleports.remove(player.getUniqueId());
        }
    }

}
