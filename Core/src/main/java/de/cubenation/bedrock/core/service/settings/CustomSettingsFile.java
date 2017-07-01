package de.cubenation.bedrock.core.service.settings;

import de.cubenation.bedrock.core.FoundationPlugin;
import net.cubespace.Yamler.Config.YamlConfig;

import java.io.File;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored", "unused"})
public abstract class CustomSettingsFile extends YamlConfig {

    private final FoundationPlugin plugin;

    public CustomSettingsFile(FoundationPlugin plugin) {
        this(plugin, getFilename());
    }

    public CustomSettingsFile(FoundationPlugin plugin, String name) {
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
