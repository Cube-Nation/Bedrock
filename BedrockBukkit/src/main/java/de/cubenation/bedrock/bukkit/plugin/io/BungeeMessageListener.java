package de.cubenation.bedrock.bukkit.plugin.io;

import de.cubenation.bedrock.bukkit.plugin.io.handler.PluginMessageHandler;
import de.cubenation.bedrock.bukkit.plugin.io.handler.TpToLocationHandler;
import de.cubenation.bedrock.bukkit.plugin.io.handler.TpToPlayerHandler;
import de.cubenation.bedrock.core.exception.IllegalPluginMessageException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

public class BungeeMessageListener implements PluginMessageListener {

    HashMap<Byte, PluginMessageHandler> handlers = new HashMap<>(){{
        put(PluginMessageVerbs.TP_TO_LOCATION, new TpToLocationHandler());
        put(PluginMessageVerbs.TP_TO_PLAYER, new TpToPlayerHandler());
    }};

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (!channel.equals(PluginMessageVerbs.CHANNEL))
            return;

        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(bytes));

        try {
            byte type = stream.readByte();

            PluginMessageHandler handler = handlers.get(type);
            if (handler == null) {
                throw new IllegalPluginMessageException("No such plugin message handler");
            }
            handler.handle(stream);

            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
