package de.cubenation.bedrock.listener;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.ebean.BedrockPlayer;
import de.cubenation.bedrock.ebean.BedrockWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Tristan Cebulla <equinox@lichtspiele.org> on 03.08.2015.
 */
@SuppressWarnings({"unused", "DefaultFileTemplate"})
public class EbeanListener implements Listener {

    @EventHandler(priority= EventPriority.MONITOR)
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        // run this as async task
        new BukkitRunnable() {

            @Override
            public void run() {

                String uuid = event.getPlayer().getUniqueId().toString();
                BedrockPlayer bp = BedrockPlugin.getInstance().getDatabase()
                        .find(BedrockPlayer.class)
                        .where()
                        .eq("uuid", uuid)
                        .findUnique();

                if (bp == null) {
                    bp = new BedrockPlayer();
                    bp.setUuid(uuid);
                }

                bp.setUsername(event.getPlayer().getName());
                bp.save();
            }

        }.runTaskAsynchronously(BedrockPlugin.getInstance());
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onWorldInit(final WorldInitEvent event) {
        // run this as async task
        new BukkitRunnable() {

            @Override
            public void run() {

                String uuid = event.getWorld().getUID().toString();
                BedrockWorld bw = BedrockPlugin.getInstance().getDatabase()
                        .find(BedrockWorld.class)
                        .where()
                        .eq("uuid", uuid)
                        .findUnique();

                if (bw == null) {
                    // save to table
                    bw = new BedrockWorld();
                    bw.setUuid(uuid);
                    bw.save();
                }
            }

        }.runTaskAsynchronously(BedrockPlugin.getInstance());
    }

}