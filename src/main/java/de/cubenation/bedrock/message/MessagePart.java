package de.cubenation.bedrock.message;

import com.google.gson.stream.JsonWriter;
import org.apache.logging.log4j.core.helpers.KeyValuePair;
import org.bukkit.ChatColor;

import java.io.IOException;

/**
 * Created by B1acksheep on 07.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.message
 */
public class MessagePart {

    private String message;

    private ChatColor color;
    private ChatColor[] styles;

    private KeyValuePair action;
    private KeyValuePair hover;


    public MessagePart(String message) {
        this.message = message;
    }

    public MessagePart color(ChatColor color) {
        if (!color.isColor()) {

        }
        this.color = color;
        return this;
    }

    public MessagePart style(ChatColor... styles) {
        for (ChatColor style : styles) {
            if (!style.isFormat()) {
                throw new IllegalArgumentException(style.name() + " is not a style");
            }
        }
        this.styles = styles;
        return this;
    }

    public MessagePart action(MessageComponentAction action, String data) {
        String key = "";
        switch (action) {
            case FILE:
                key = "open_file";
                break;
            case LINK:
                key = "open_url";
                break;
            case SUGGEST_COMMAND:
                key = "suggest_command";
                break;
            case RUN_COMMAND:
                key = "run_command";
                break;
        }

        this.action = new KeyValuePair(key, data);

        return this;
    }

    public MessagePart tooltip(String text) {
        this.hover = new KeyValuePair("show_text", text);
        return this;
    }



    public JsonWriter toJSON(final JsonWriter json) throws IOException {
        json.beginObject().name("text").value(message);
        if (color != null) {
            json.name("color").value(color.name().toLowerCase());
        }
        if (styles != null) {
            for (final ChatColor style : styles) {
                json.name(style == ChatColor.UNDERLINE ? "underlined" : style.name().toLowerCase()).value(true);
            }
        }
        if (action != null) {
            json.name("clickEvent")
                    .beginObject()
                    .name("action").value(action.getKey())
                    .name("value").value(action.getValue())
                    .endObject();
        }
        if (hover != null) {
            json.name("hoverEvent")
                    .beginObject()
                    .name("action").value(hover.getKey())
                    .name("value").value(hover.getValue())
                    .endObject();
        }
        return json.endObject();
    }

}
