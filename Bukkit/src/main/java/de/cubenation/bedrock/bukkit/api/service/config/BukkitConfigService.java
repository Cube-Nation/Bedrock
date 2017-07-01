package de.cubenation.bedrock.bukkit.api.service.config;

import de.cubenation.bedrock.bukkit.api.configuration.BukkitBedrockYaml;
import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.config.BedrockDefaults;
import de.cubenation.bedrock.core.service.config.ConfigService;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class BukkitConfigService extends ConfigService {

    public BukkitConfigService(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public BukkitBedrockYaml getReadOnlyConfig() {
        return getReadOnlyConfig(BedrockDefaults.getFilename());
    }

    @Override
    public BukkitBedrockYaml getReadOnlyConfig(String name) {
        File file = new File(this.getPlugin().getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + name);
        YamlConfiguration configuration = (file.exists()) ? YamlConfiguration.loadConfiguration(file) : null;

        if (configuration == null) {
            return null;
        } else {
            return new BukkitBedrockYaml(configuration);
        }
    }


}
