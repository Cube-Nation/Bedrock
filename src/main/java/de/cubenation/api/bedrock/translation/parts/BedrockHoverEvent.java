package de.cubenation.api.bedrock.translation.parts;

import org.json.simple.JSONObject;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class BedrockHoverEvent {

    private final BedrockHoverEvent.Action action;
    private final BedrockJson value;

    public BedrockHoverEvent(Action action, BedrockJson value) {
        this.action = action;
        this.value = value;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", getAction().getKey());
        jsonObject.put("value", getValue());
        return jsonObject;
    }


    public BedrockHoverEvent.Action getAction() {
        return this.action;
    }

    public BedrockJson getValue() {
        return this.value;
    }

    public String toString() {
        return "HoverEvent(action=" + this.getAction() + ", value=" + this.getValue() + ")";
    }


    public enum Action {
        SHOW_TEXT("show_text"),
        SHOW_ACHIEVEMENT("show_achievement"),
        SHOW_ITEM("show_item"),
        SHOW_ENTITY("show_entity");

        private final String key;

        Action(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}
