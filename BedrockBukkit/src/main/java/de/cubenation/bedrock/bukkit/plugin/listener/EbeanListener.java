/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.bedrock.bukkit.plugin.listener;

import de.cubenation.bedrock.bukkit.plugin.BedrockPlugin;
import de.cubenation.bedrock.bukkit.plugin.event.MultiAccountJoinEvent;
import de.cubenation.bedrock.bukkit.plugin.event.PlayerChangeNameEvent;
import de.cubenation.bedrock.core.model.BedrockOfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings({"unused", "DefaultFileTemplate"})
public class EbeanListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        // run this as async task
        new BukkitRunnable() {

            @Override
            public void run() {

                BedrockPlugin plugin = BedrockPlugin.getInstance();

                String uuid = event.getPlayer().getUniqueId().toString();
                BedrockOfflinePlayer bp = BedrockPlugin.getInstance().getDatabase()
                        .find(BedrockOfflinePlayer.class)
                        .where()
                        .eq("uuid", uuid)
                        .findUnique();

                String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
                if (bp == null) {
                    bp = new BedrockOfflinePlayer(uuid, event.getPlayer().getName(), ip, new Date());
                    bp.save(plugin.getDatabase());
                } else {
                    // check if username changed
                    if (!bp.getUsername().equals(event.getPlayer().getName())) {
                        plugin.log(Level.INFO, String.format("BedrockPlayer: %s changed name to %s",
                                bp.getUsername(), event.getPlayer().getName()));
                        // fire name change event
                        PlayerChangeNameEvent changeNameEvent = new PlayerChangeNameEvent(
                                event.getPlayer(),
                                bp.getUsername(),
                                event.getPlayer().getName());
                        Bukkit.getPluginManager().callEvent(changeNameEvent);

                        // update username
                        bp.setUsername(event.getPlayer().getName());
                    }

                    List<BedrockOfflinePlayer> bedrockPlayers = plugin.getDatabase().find(BedrockOfflinePlayer.class).where()
                            .like("ip", ip)
                            .findList();

                    if (bedrockPlayers != null) {
                        // Remove self
                        bedrockPlayers = bedrockPlayers.stream()
                                .filter(player -> !player.getUuid().equals(event.getPlayer().getUniqueId().toString()))
                                .collect(Collectors.toList());

                        if (bedrockPlayers.size() > 0) {
                            MultiAccountJoinEvent joinEvent = new MultiAccountJoinEvent(ip, bedrockPlayers);
                            Bukkit.getPluginManager().callEvent(joinEvent);
                        }
                    }

                    // update ip
                    bp.setIp(ip);

                    // update timestamp
                    bp.setLastlogin(new Date());
                    bp.update(plugin.getDatabase());
                }
            }

        }.runTaskAsynchronously(BedrockPlugin.getInstance());
    }
}
