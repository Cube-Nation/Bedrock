/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.api.bedrock.translation.parts;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * @author Cube-Nation
 * @version 1.0
 */

@SuppressWarnings({"WeakerAccess", "unchecked", "UnusedReturnValue", "unused", "SameParameterValue"})
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

    public boolean containsClickAction() {
        return this.containsKey(CLICKEVENT);
    }

    public BedrockJson hoverAction(BedrockHoverEvent.Action action, BedrockJson value) {
        this.put(HOVEREVENT, new BedrockHoverEvent(action, value).toJson());
        return this;
    }

    public boolean containsHoverAction() {
        return this.containsKey(HOVEREVENT);
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
