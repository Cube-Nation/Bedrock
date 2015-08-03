package de.cubenation.bedrock.listener;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.ebean.BedrockPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Created by Tristan Cebulla <equinox@lichtspiele.org> on 03.08.2015.
 */
@SuppressWarnings({"unused", "DefaultFileTemplate"})
public class PlayerJoinListener implements Listener {

    @EventHandler(priority= EventPriority.MONITOR)
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {

        // run this as async task
        new BukkitRunnable() {

            @Override
            public void run() {

                String uuid = event.getPlayer().getUniqueId().toString();
                List<BedrockPlayer> list = BedrockPlugin.getInstance().getDatabase()
                        .find(BedrockPlayer.class)
                        .where()
                        .eq("uuid", uuid)
                        .findList();

                if (list.size() > 0)
                    return;

                // save player to table
                BedrockPlayer player = new BedrockPlayer();
                player.setUuid(uuid);
                player.save();
            }

        }.runTaskAsynchronously(BedrockPlugin.getInstance());
    }

}
