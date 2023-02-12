package de.cubenation.bedrock.bungee.plugin.listener;

import de.cubenation.bedrock.bungee.api.BasePlugin;
import de.cubenation.bedrock.bungee.plugin.event.MultiAccountJoinEvent;
import de.cubenation.bedrock.bungee.plugin.event.PlayerChangeNameEvent;
import de.cubenation.bedrock.core.annotation.injection.Inject;
import de.cubenation.bedrock.core.database.Database;
import de.cubenation.bedrock.core.injection.Component;
import de.cubenation.bedrock.core.model.BedrockOfflinePlayer;
import de.cubenation.bedrock.core.service.database.DatabaseService;
import jakarta.persistence.NoResultException;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.hibernate.Session;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class PlayerListener extends Component implements Listener {

    private final BasePlugin basePlugin;

    @Inject
    private Database database;

    public PlayerListener(BasePlugin plugin) {
        super(plugin);
        basePlugin = plugin;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        basePlugin.getProxy().getScheduler().runAsync(basePlugin, () -> {
            BedrockOfflinePlayer bp;
            try (Session hibernateSession = database.openSession()) {
                String uuid = event.getPlayer().getUniqueId().toString();
                String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
                try {
                    bp = hibernateSession.createQuery(
                            "select p from BedrockOfflinePlayer p where p.uuid = :uuid",
                            BedrockOfflinePlayer.class
                    ).setParameter("uuid", uuid).getSingleResult();

                    // check if username changed
                    if (!bp.getUsername().equals(event.getPlayer().getName())) {
                        plugin.log(Level.INFO, String.format("BedrockPlayer: %s changed name to %s",
                                bp.getUsername(), event.getPlayer().getName()));
                        // fire name change event
                        PlayerChangeNameEvent changeNameEvent = new PlayerChangeNameEvent(
                                event.getPlayer().getUniqueId(),
                                bp.getUsername(),
                                event.getPlayer().getName());
                        basePlugin.getProxy().getPluginManager().callEvent(changeNameEvent);

                        // update username
                        bp.setUsername(event.getPlayer().getName());
                    }

                    // update ip
                    bp.setIp(ip);

                    // update timestamp
                    bp.setLastlogin(new Date());
                    try {
                        bp.persist(plugin);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (NoResultException e) {
                    bp = new BedrockOfflinePlayer(uuid, event.getPlayer().getName(), ip, new Date());
                }

                // Check for multi accounts
                List<BedrockOfflinePlayer> bedrockPlayers = hibernateSession.createQuery(
                        "select p from BedrockOfflinePlayer p where p.ip like :ip",
                        BedrockOfflinePlayer.class
                ).setParameter("ip", ip).getResultList();
                if (bedrockPlayers != null) {
                    // Remove self
                    bedrockPlayers = bedrockPlayers.stream()
                            .filter(player -> !player.getUuid().equals(event.getPlayer().getUniqueId().toString()))
                            .collect(Collectors.toList());

                    if (bedrockPlayers.size() > 0) {
                        MultiAccountJoinEvent joinEvent = new MultiAccountJoinEvent(ip, bedrockPlayers);
                        basePlugin.getProxy().getPluginManager().callEvent(joinEvent);
                    }
                }
            }

            // persist BedrockOfflinePlayer object
            try {
                bp.persist(plugin);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    }

}
