package de.cubenation.bedrock.service.customconfigurationfile;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.NoSuchRegisterableException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.logging.Level;

public class CustomConfigurationFileService extends AbstractService implements ServiceInterface {

    public CustomConfigurationFileService(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        try {
            // avoid NullPointerException
            if (this.getPlugin().getCustomConfigurationFiles() == null)
                return;

            for (CustomConfigurationFile file : this.getPlugin().getCustomConfigurationFiles())
                this.register(file);

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
        this.getPlugin().log(Level.INFO, "Registering file: " + file.getFilename());
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

}
