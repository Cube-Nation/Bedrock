package de.cubenation.bedrock.listener;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.ebean.BedrockPlayer;
import de.cubenation.bedrock.event.PlayerChangesNameEvent;
import de.cubenation.bedrock.helper.BedrockEbeanHelper;
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
                    System.out.println("bp is null, creating new for uuid " + uuid);
                    bp = new BedrockPlayer();
                    bp.setUuid(uuid);
                    bp.setUsername(event.getPlayer().getName());
                }

                // check if username changed
                if (!bp.getUsername().equals(event.getPlayer().getName())) {

                    // fire name change event
                    PlayerChangesNameEvent playerChangesNameEvent = new PlayerChangesNameEvent(
                            event.getPlayer(),
                            bp.getUsername(),
                            event.getPlayer().getName());
                    BedrockPlugin.getInstance().getServer().getPluginManager().callEvent(playerChangesNameEvent);

                    // update username
                    bp.setUsername(event.getPlayer().getName());
                }

                // save record
                bp.save();
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