package de.cubenation.bedrock.bukkit.plugin.io.handler;

import de.cubenation.bedrock.bukkit.plugin.BedrockPlugin;
import de.cubenation.bedrock.bukkit.plugin.manager.BungeeTeleportManager;
import de.cubenation.bedrock.bukkit.wrapper.BukkitPlayer;
import de.cubenation.bedrock.bukkit.wrapper.BukkitPosition;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class TpToPlayerHandler implements PluginMessageHandler {

    @Override
    public void handle(DataInputStream data) throws IOException {
        UUID playerUuid = UUID.fromString(data.readUTF());
        UUID targetUuid = UUID.fromString(data.readUTF());

        BukkitPlayer target = (BukkitPlayer) BedrockPlugin.getInstance().getBedrockServer().getPlayer(targetUuid);
        if (target == null)
            return;

        BukkitPosition loc = (BukkitPosition) target.getPosition();
        BungeeTeleportManager.getInstance().scheduleTeleport(playerUuid, loc);
    }
}
