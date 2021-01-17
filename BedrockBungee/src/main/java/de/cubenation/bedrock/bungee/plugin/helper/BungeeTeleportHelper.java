package de.cubenation.bedrock.bungee.plugin.helper;

import de.cubenation.bedrock.bungee.plugin.io.IOVerbs;
import de.cubenation.bedrock.bungee.plugin.io.OutgoingPluginMessage;
import de.cubenation.bedrock.bungee.wrapper.BungeePlayer;
import de.cubenation.bedrock.bungee.wrapper.BungeePosition;
import de.cubenation.bedrock.core.wrapper.BedrockPosition;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author Cube-Nation
 * @version 2.0
 */
public class BungeeTeleportHelper {

    private static void silentServerSwitch(ProxiedPlayer player, ServerInfo server) {
        if (!player.getServer().getInfo().equals(server)) {
            player.connect(server);
        }
    }

    public static void executeTeleport(BungeePlayer player, BungeePosition to) {
        silentServerSwitch(player.getPlayer(), ProxyServer.getInstance().getServerInfo(to.getDimension().getServer()));

        OutgoingPluginMessage msg = new OutgoingPluginMessage(IOVerbs.TP_TO_LOCATION, ProxyServer.getInstance().getServerInfo(to.getDimension().getServer()));
        msg.write(player.getName());
        msg.write(to.getDimension().getServer()).write(to.getDimension().getWorld()).write(to.getX()).write(to.getY()).write(to.getZ());
        msg.write(to.getYaw()).write(to.getPitch());
        msg.send();
    }

    public static void executeTeleport(BungeePlayer player, BungeePlayer to) {
        ServerInfo targetServerInfo = to.getPlayer().getServer().getInfo();
        silentServerSwitch(player.getPlayer(), targetServerInfo);

        OutgoingPluginMessage msg = new OutgoingPluginMessage(IOVerbs.TP_TO_PLAYER, targetServerInfo);
        msg.write(player.getUniqueId().toString());
        msg.write(to.getUniqueId().toString());
        msg.send();
    }

}
