package de.cubenation.bedrock.bungee.plugin;

import de.cubenation.bedrock.bungee.api.BasePlugin;

public class BedrockPlugin extends BasePlugin {

    public static BedrockPlugin instance;

    public static BedrockPlugin getInstance() {
        return instance;
    }

    @Override
    public void onPreEnable() throws Exception {
        instance = this;
        getLogger().info("Yay! It loads!");
    }

    @Override
    public void onPostEnable() throws Exception {
        getLogger().info("Yay! It loads again!");
    }

}
