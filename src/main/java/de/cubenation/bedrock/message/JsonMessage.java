package de.cubenation.bedrock.message;

import com.google.gson.stream.JsonWriter;
import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created by B1acksheep on 21.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.message
 */
public class JsonMessage {

    private ArrayList<MessagePart> parts = new ArrayList<>();


    public JsonMessage(String text) {
        parts.add(new MessagePart(text));
    }

    public JsonMessage text(String text) {
        parts.add(new MessagePart(text));
        return this;
    }

    public JsonMessage chatcolor(ChatColor chatColor) {
        MessagePart part = latestPart();
        part.chatcolor(chatColor);
        return this;
    }

    public JsonMessage chatcolors(ChatColor... chatColors) {
        MessagePart part = latestPart();
        part.chatcolors(chatColors);
        return this;
    }

    public JsonMessage tooltip(String text) {
        MessagePart part = latestPart();
        part.tooltip(text);
        return this;
    }

    public JsonMessage file(String path) {
        MessagePart part = latestPart();
        part.action(MessageComponentAction.FILE, path);
        return this;
    }

    public JsonMessage link(String url) {
        MessagePart part = latestPart();
        part.action(MessageComponentAction.LINK, url);
        return this;
    }

    public JsonMessage suggest(String command) {
        MessagePart part = latestPart();
        part.action(MessageComponentAction.SUGGEST_COMMAND, command);
        return this;
    }

    public JsonMessage command(String command) {
        MessagePart part = latestPart();
        part.action(MessageComponentAction.RUN_COMMAND, command);
        return this;
    }




    private MessagePart latestPart() {
        return parts.get(parts.size() -1);
    }

    public void send(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(toJSONString())));
    }

    public String toJSONString() {
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
