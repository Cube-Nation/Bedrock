package de.cubenation.api.bedrock.service.settings;

import de.cubenation.api.bedrock.BasePlugin;
import net.cubespace.Yamler.Config.YamlConfig;

import java.io.File;

/**
 * Created by bhruschka on 25.12.16.
 * Project: Bedrock
 */
@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored", "unused"})
public abstract class CustomSettingsFile extends YamlConfig {

    private final BasePlugin plugin;

    public CustomSettingsFile(BasePlugin plugin) {
        this(plugin, getFilename());
    }

    public CustomSettingsFile(BasePlugin plugin, String name) {
        this.plugin = plugin;
        setConfigFile(name);
    }

    private void setConfigFile(String name) {
        File settingsDir = new File(this.plugin.getDataFolder(), SettingsService.SETTINGSDIR);
        if (!settingsDir.exists()) {
            settingsDir.mkdir();
        }

        File file = new File(settingsDir, getSettingsName());
        if (!file.exists()) {
            file.mkdir();
        }

        CONFIG_FILE = new File(file, name);
    }

    private static String getFilename() {
        return "_default.yml";
    }

    public abstract String getSettingsName();

    public abstract String info();

}