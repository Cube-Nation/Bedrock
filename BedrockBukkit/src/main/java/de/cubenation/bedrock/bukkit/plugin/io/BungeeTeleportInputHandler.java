package de.cubenation.bedrock.bukkit.plugin.io;

import de.cubenation.bedrock.bukkit.plugin.BedrockPlugin;
import de.cubenation.bedrock.bukkit.plugin.manager.BungeeTeleportManager;
import de.cubenation.bedrock.bukkit.wrapper.BukkitPlayer;
import de.cubenation.bedrock.bukkit.wrapper.BukkitPosition;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class BungeeTeleportInputHandler implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (!channel.equals(IOVerbs.CHANNEL))
            return;

        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(bytes));

        try {
            byte type = stream.readByte();
            switch (type) {
                case IOVerbs.TP_TO_LOCATION:
                    processLocationTeleportRequest(stream);
                    break;
                case IOVerbs.TP_TO_PLAYER:
                    processPlayerTeleportRequest(stream);
                    break;
            }
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processLocationTeleportRequest(DataInputStream data) throws IOException {
        UUID playerUuid = UUID.fromString(data.readUTF());

        String world = data.readUTF();
        double x = data.readDouble();
        double y = data.readDouble();
        double z = data.readDouble();
        float yaw = data.readFloat();
        float pitch = data.readFloat();

        BukkitPosition loc = new BukkitPosition(world, x, y, z, yaw, pitch);
        BungeeTeleportManager.getInstance().scheduleTeleport(playerUuid, loc);
    }

    private void processPlayerTeleportRequest(DataInputStream data) throws IOException {
        UUID playerUuid = UUID.fromString(data.readUTF());
        UUID targetUuid = UUID.fromString(data.readUTF());

        BukkitPlayer target = (BukkitPlayer) BedrockPlugin.getInstance().getBedrockServer().getPlayer(targetUuid);
        if (target == null)
            return;

        BukkitPosition loc = (BukkitPosition) target.getPosition();
        BungeeTeleportManager.getInstance().scheduleTeleport(playerUuid, loc);
    }

}
