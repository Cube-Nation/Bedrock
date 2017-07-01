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

package de.cubenation.bedrock.bukkit.api.translation.parts;

import org.json.simple.JSONObject;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class BedrockClickEvent {

    private final BedrockClickEvent.Action action;
    private final String value;

    public BedrockClickEvent(BedrockClickEvent.Action action, String value) {
        this.action = action;
        this.value = value;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", getAction().getKey());
        jsonObject.put("value", getValue());
        return jsonObject;
    }

    public BedrockClickEvent.Action getAction() {
        return this.action;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return "ClickEvent(action=" + this.getAction().getKey() + ", value=" + this.getValue() + ")";
    }


    public enum Action {
        OPEN_URL ("open_url"),
        OPEN_FILE("open_file"),
        RUN_COMMAND("run_command"),
        SUGGEST_COMMAND("suggest_command"),
        CHANGE_PAGE("change_page");

        private final String key;

        Action(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}
