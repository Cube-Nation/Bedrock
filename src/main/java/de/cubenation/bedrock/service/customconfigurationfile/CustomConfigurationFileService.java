package de.cubenation.bedrock.service.customconfigurationfile;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.NoSuchRegisterableException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.ServiceInterface;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class CustomConfigurationFileService implements ServiceInterface {

    private BasePlugin plugin;

    public CustomConfigurationFileService(BasePlugin plugin) {
        this.setPlugin(plugin);
    }

    public CustomConfigurationFileService(BasePlugin plugin, List<CustomConfigurationFile> list) {
        // FIXME: tdb (init)
    }

    @Override
    public void init() throws ServiceInitException {

    }

    @Override
    public void reload() throws ServiceReloadException {

    }

    public void register(CustomConfigurationFile file) {
        CustomConfigurationRegistry.register(this.getPlugin(), file.getFilename(), null, file);
    }

    public YamlConfiguration get(String filename) throws NoSuchRegisterableException {
        try {
            return CustomConfigurationRegistry.get(this.getPlugin(), filename, null).load();
        } catch (NoSuchRegisterableException e) {
            e.printStackTrace();
        }
        return null;
    }

    // FIXME: more tdb (Reload)

    /*
     * Plugin Getter/Setter
     */
    public BasePlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }
}
