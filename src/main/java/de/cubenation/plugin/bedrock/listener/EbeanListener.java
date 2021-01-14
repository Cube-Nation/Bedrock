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

package de.cubenation.plugin.bedrock.listener;

import de.cubenation.api.bedrock.ebean.BedrockPlayer;
import de.cubenation.api.bedrock.helper.BedrockEbeanHelper;
import de.cubenation.plugin.bedrock.BedrockPlugin;
import de.cubenation.plugin.bedrock.event.PlayerChangesNameEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;

/**
 * @author Cube-Nation
 * @version 1.0
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

                        /**
                        PlayerChangesNameEvent playerChangesNameEvent = new PlayerChangesNameEvent(
                                event.getPlayer(),
                                bp.getUsername(),
                                event.getPlayer().getName());
                        BedrockPlugin.getInstance().getServer().getPluginManager().callEvent(playerChangesNameEvent);
                         */

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