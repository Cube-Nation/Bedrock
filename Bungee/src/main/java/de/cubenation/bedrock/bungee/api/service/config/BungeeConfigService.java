package de.cubenation.bedrock.bungee.api.service.config;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.bungee.api.configuration.BungeeBedrockYaml;
import de.cubenation.bedrock.core.config.BedrockDefaults;
import de.cubenation.bedrock.core.service.config.ConfigService;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class BungeeConfigService extends ConfigService {

    public BungeeConfigService(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public BungeeBedrockYaml getReadOnlyConfig() {
        return getReadOnlyConfig(BedrockDefaults.getFilename());
    }

    @Override
    public BungeeBedrockYaml getReadOnlyConfig(String name) {
        File file = new File(this.getPlugin().getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + name);

        try {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            return new BungeeBedrockYaml(configuration);
        } catch (IOException e) {
            return null;
        }
    }



}
