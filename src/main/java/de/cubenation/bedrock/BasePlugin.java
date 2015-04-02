package de.cubenation.bedrock;

import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by B1acksheep on 30.03.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock
 */
public class BasePlugin extends JavaPlugin {

    public static BasePlugin instance;

    public static BasePlugin getInstance() {
        return BasePlugin.instance;
    }

    private void setInstance(JavaPlugin instance) {
        BasePlugin.instance = (BasePlugin) instance;
    }

    @Override
    public void onEnable() {
        this.setInstance(this);
    }
}
