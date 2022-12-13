package de.cubenation.bedrock.bungee.plugin.listener;

import de.cubenation.bedrock.bungee.api.BasePlugin;
import de.cubenation.bedrock.bungee.plugin.BedrockPlugin;
import de.cubenation.bedrock.bungee.plugin.event.MultiAccountJoinEvent;
import de.cubenation.bedrock.bungee.plugin.event.PlayerChangeNameEvent;
import de.cubenation.bedrock.core.model.BedrockOfflinePlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
            BedrockOfflinePlayer bp = BedrockPlugin.getInstance().getEbeanService().getDatabase()
                    .find(BedrockOfflinePlayer.class)
                    .where()
                    .eq("uuid", uuid)
                    .findOne();

            String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
            if (bp == null) {
                bp = new BedrockOfflinePlayer(uuid, event.getPlayer().getName(), ip, new Date());
                bp.save(plugin.getEbeanService().getDatabase());
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

                List<BedrockOfflinePlayer> bedrockPlayers = plugin.getEbeanService().getDatabase().find(BedrockOfflinePlayer.class).where()
                        .like("ip", ip)
                        .findList();

                if (bedrockPlayers != null) {
                    // Remove self
                    bedrockPlayers = bedrockPlayers.stream()
                            .filter(player -> !player.getUuid().equals(event.getPlayer().getUniqueId().toString()))
                            .collect(Collectors.toList());

                    if (bedrockPlayers.size() > 0) {
                        MultiAccountJoinEvent joinEvent = new MultiAccountJoinEvent(ip, bedrockPlayers);
                        plugin.getProxy().getPluginManager().callEvent(joinEvent);
                    }
                }

                // update ip
                bp.setIp(ip);

                // update timestamp
                bp.setLastlogin(new Date());
                bp.update(plugin.getEbeanService().getDatabase());
            }
        });


    }

}
