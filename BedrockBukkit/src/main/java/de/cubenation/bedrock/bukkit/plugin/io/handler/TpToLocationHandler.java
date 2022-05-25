package de.cubenation.bedrock.bukkit.plugin.io.handler;

import de.cubenation.bedrock.bukkit.plugin.manager.BungeeTeleportManager;
import de.cubenation.bedrock.bukkit.wrapper.BukkitPosition;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class TpToLocationHandler implements PluginMessageHandler {

    @Override
    public void handle(DataInputStream data) throws IOException {
        UUID playerUuid = UUID.fromString(data.readUTF());

        String world = data.readUTF();
        double x = data.readDouble();
        double y = data.readDouble();
        double z = data.readDouble();
        float yaw = data.readFloat();
        float pitch = data.readFloat();

        BukkitPosition loc = BukkitPosition.wrap(world, x, y, z, yaw, pitch);
        BungeeTeleportManager.getInstance().scheduleTeleport(playerUuid, loc);
    }
}
