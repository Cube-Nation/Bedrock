package de.cubenation.bedrock.service.customconfigurationfile;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.NoSuchRegisterableException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.ServiceInterface;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.logging.Level;

public class CustomConfigurationFileService implements ServiceInterface {

    private BasePlugin plugin;

    public CustomConfigurationFileService(BasePlugin plugin) {
        this.setPlugin(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        try {
            // avoid NullPointerException
            if (this.getPlugin().getCustomConfigurationFiles() == null)
                return;

            for (CustomConfigurationFile file : this.getPlugin().getCustomConfigurationFiles()) {
                this.getPlugin().log(Level.INFO, "Registering file: " + file.getFilename());
                this.register(file);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceInitException(e.getMessage());

        }
    }

    @Override
    public void reload() throws ServiceReloadException {
        try {
            this.init();
        } catch (ServiceInitException e) {
            throw new ServiceReloadException(e.getMessage());
        }
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
