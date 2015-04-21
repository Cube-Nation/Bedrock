package de.cubenation.bedrock.message;

import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by B1acksheep on 21.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.message
 */
public class JsonTitle {

    private JsonMessage title = null;
    private JsonMessage subtitle = null;

    public JsonTitle(JsonMessage title) {
        this.title = title;
    }

    public JsonTitle(JsonMessage title, JsonMessage subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public JsonTitle(String title, String subTitle) {
        this.title = new JsonMessage(title);
        this.subtitle = new JsonMessage(subTitle);
    }

    public void send(Player player) {
        if (this.subtitle != null) {
            PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(
                    PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
                    IChatBaseComponent.ChatSerializer.a(subtitle.toJSONString()));

            ((CraftPlayer) player).getHandle().playerConnection
                    .sendPacket(subtitlePacket);
        }

        JsonMessage title = new JsonMessage("");
        if (this.title != null) {
            title = this.title;
        }

        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(
                PacketPlayOutTitle.EnumTitleAction.TITLE,
                IChatBaseComponent.ChatSerializer.a(title.toJSONString()));

        ((CraftPlayer) player).getHandle().playerConnection
                .sendPacket(titlePacket);

    }
}
