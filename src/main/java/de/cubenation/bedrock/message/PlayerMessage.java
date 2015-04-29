package de.cubenation.bedrock.message;

import com.google.gson.stream.JsonWriter;
import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created by B1acksheep on 07.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.message
 */
public class PlayerMessage {

    private ArrayList<MessagePart> parts = new ArrayList<>();

    public PlayerMessage PlayerMessage(MessagePart parts) {
        this.parts.add(parts);
        return this;
    }

    public PlayerMessage PlayerMessage(ArrayList<MessagePart> parts) {
        this.parts = parts;
        return this;
    }


    public PlayerMessage addComponent(MessagePart component) {
        parts.add(component);
        return this;
    }

    public void send(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(toJSONString())));
    }

    private String toJSONString() {
        StringWriter stringWriter = new StringWriter();
        JsonWriter json = new JsonWriter(stringWriter);

        try {
            if (parts.size() == 1) {
                parts.get(0).toJSON(json);
            } else {
                json.beginObject().name("text").value("").name("extra").beginArray();
                for (MessagePart component : parts) {
                    component.toJSON(json);
                }
                json.endArray().endObject();
            }

        } catch (IOException e) {
            throw new RuntimeException("invalid message");
        }
        return stringWriter.toString();
    }

}
