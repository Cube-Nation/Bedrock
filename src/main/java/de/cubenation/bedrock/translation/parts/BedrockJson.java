package de.cubenation.bedrock.translation.parts;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by BenediktHr on 04.03.16.
 * Project: Bedrock
 */

public class BedrockJson extends JSONObject {

    public static final String TEXT = "text";
    public static final String COLOR = "color";

    public static final String INSERTION = "insertion";

    public static final String BOLD = "bold";
    public static final String UNDERLINED = "underlined";
    public static final String ITALIC = "italic";
    public static final String STRIKETHROUGH = "strikethrough";
    public static final String OBFUSCATED = "obfuscated";

    public static final String CLICKEVENT = "clickEvent";
    public static final String HOVEREVENT = "hoverEvent";

    public static final String EXTRA = "extra";


    public BedrockJson() {
        this.bold(false);
        this.underlined(false);
        this.italic(false);
        this.strikethrough(false);
        this.obfuscated(false);
        this.color(JsonColor.WHITE);
    }

    public static BedrockJson JsonWithText(String text) {
        BedrockJson json = new BedrockJson();
        json.text(text);
        return json;
    }


    public BedrockJson text(String text) {
        this.put(TEXT, text);
        return this;
    }

    public BedrockJson color(JsonColor color) {
        this.put(COLOR, color.getCode());
        return this;
    }

    public BedrockJson insertion(String text) {
        this.put(INSERTION, text);
        return this;
    }


    public BedrockJson bold(Boolean state) {
        this.put(BOLD, state);
        return this;
    }

    public BedrockJson underlined(Boolean state) {
        this.put(UNDERLINED, state);
        return this;
    }

    public BedrockJson italic(Boolean state) {
        this.put(ITALIC, state);
        return this;
    }

    public BedrockJson strikethrough(Boolean state) {
        this.put(STRIKETHROUGH, state);
        return this;
    }

    public BedrockJson obfuscated(Boolean state) {
        this.put(OBFUSCATED, state);
        return this;
    }

    public BedrockJson clickAction(BedrockClickEvent.Action action, String value) {
        this.put(CLICKEVENT, new BedrockClickEvent(action, value).toJson());
        return this;
    }

    public BedrockJson hoverAction(BedrockHoverEvent.Action action, BedrockJson value) {
        this.put(HOVEREVENT, new BedrockHoverEvent(action, value).toJson());
        return this;
    }

    public BedrockJson extra(ArrayList<BedrockJson> extra) {
        this.put(EXTRA, extra);
        return this;
    }

    public BedrockJson addExtra(BedrockJson json) {
        ArrayList<BedrockJson> extras;

        if (this.containsKey(EXTRA)) {
            extras = (ArrayList<BedrockJson>) this.get(EXTRA);
        } else {
            extras = new ArrayList<>();
        }

        extras.add(json);

        this.put(EXTRA, extras);

        return this;
    }

    public ArrayList<BedrockJson> getExtras() {
        if (this.containsKey(EXTRA)) {
            return (ArrayList<BedrockJson>) this.get(EXTRA);
        } else {
            return new ArrayList<>();
        }
    }


    public static BedrockJson Space() {
        return BedrockJson.JsonWithText(" ");
    }

    public static BedrockJson NewLine() {
        return BedrockJson.JsonWithText("\n");
    }

}
