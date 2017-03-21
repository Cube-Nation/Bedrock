package de.cubenation.bedrock.translation.parts;

import org.json.simple.JSONObject;

/**
 * Created by BenediktHr on 04.03.16.
 * Project: Bedrock
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
