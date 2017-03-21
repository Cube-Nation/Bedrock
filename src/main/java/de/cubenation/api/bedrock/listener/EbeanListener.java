package de.cubenation.api.bedrock.listener;

import de.cubenation.api.bedrock.BedrockPlugin;
import de.cubenation.api.bedrock.event.PlayerChangesNameEvent;
import de.cubenation.api.bedrock.helper.BedrockEbeanHelper;
import de.cubenation.api.bedrock.ebean.BedrockPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;

/**
 * Created by Tristan Cebulla <equinox@lichtspiele.org> on 03.08.2015.
 */
@SuppressWarnings({"unused", "DefaultFileTemplate"})
public class EbeanListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
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
                    bp = new BedrockPlayer(uuid, event.getPlayer().getName(), new Date());
                    bp.save();
                } else {
                    // check if username changed
                    if (!bp.getUsername().equals(event.getPlayer().getName())) {
                        BedrockPlugin.getInstance().getLogger().info("BedrockPlayer: " + bp.getUsername() + " " +
                                "changed name to " + event.getPlayer().getName());
                        // fire name change event
                        PlayerChangesNameEvent playerChangesNameEvent = new PlayerChangesNameEvent(
                                event.getPlayer(),
                                bp.getUsername(),
                                event.getPlayer().getName());
                        BedrockPlugin.getInstance().getServer().getPluginManager().callEvent(playerChangesNameEvent);

                        // update username
                        bp.setUsername(event.getPlayer().getName());
                    }
                    // update timestamp
                    bp.setLastlogin(new Date());
                    bp.update();
                }


            }

        }.runTaskAsynchronously(BedrockPlugin.getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldInit(final WorldInitEvent event) {
        // run this as async task
        new BukkitRunnable() {

            @Override
            public void run() {
                BedrockEbeanHelper.createBedrockWorld(event.getWorld());
            }

        }.runTaskAsynchronously(BedrockPlugin.getInstance());
    }

}