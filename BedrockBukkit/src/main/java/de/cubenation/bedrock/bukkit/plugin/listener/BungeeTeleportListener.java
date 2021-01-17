package de.cubenation.bedrock.bukkit.plugin.listener;

import de.cubenation.bedrock.bukkit.plugin.manager.BungeeTeleportManager;
import de.cubenation.bedrock.bukkit.wrapper.BukkitPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public class BungeeTeleportListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        BukkitPlayer player = new BukkitPlayer(event.getPlayer());
        BungeeTeleportManager.getInstance().tryExecuteTeleport(player);
    }

}
