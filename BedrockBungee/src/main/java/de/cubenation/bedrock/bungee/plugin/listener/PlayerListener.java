package de.cubenation.bedrock.bungee.plugin.listener;

import de.cubenation.bedrock.bungee.api.BasePlugin;
import de.cubenation.bedrock.bungee.plugin.BedrockPlugin;
import de.cubenation.bedrock.bungee.plugin.event.PlayerChangeNameEvent;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.model.BedrockPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Date;
import java.util.logging.Level;

@SuppressWarnings("Duplicates")
public class PlayerListener implements Listener {

    private final BasePlugin plugin;

    public PlayerListener(BasePlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {

            String uuid = event.getPlayer().getUniqueId().toString();
            BedrockPlayer bp = BedrockPlugin.getInstance().getDatabase()
                    .find(BedrockPlayer.class)
                    .where()
                    .eq("uuid", uuid)
                    .findUnique();

            if (bp == null) {
                bp = new BedrockPlayer(uuid, event.getPlayer().getName(), new Date());
                bp.save(plugin.getDatabase());
            } else {
                // check if username changed
                if (!bp.getUsername().equals(event.getPlayer().getName())) {
                    plugin.log(Level.INFO, String.format("BedrockPlayer: %s changed name to %s",
                            bp.getUsername(), event.getPlayer().getName()));
                    // fire name change event
                    PlayerChangeNameEvent changeNameEvent = new PlayerChangeNameEvent(
                            event.getPlayer().getUniqueId(),
                            bp.getUsername(),
                            event.getPlayer().getName());
                    plugin.getProxy().getPluginManager().callEvent(changeNameEvent);

                    // update username
                    bp.setUsername(event.getPlayer().getName());
                }
                // update timestamp
                bp.setLastlogin(new Date());
                bp.update(plugin.getDatabase());
            }
        });


    }

}
