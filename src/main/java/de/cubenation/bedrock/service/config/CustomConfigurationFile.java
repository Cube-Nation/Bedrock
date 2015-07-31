package de.cubenation.bedrock.service.config;

import de.cubenation.bedrock.BasePlugin;
import net.cubespace.Yamler.Config.Config;

public abstract class CustomConfigurationFile extends Config {

    protected String filename  = null;

    public abstract void setFilename(BasePlugin plugin);

    public final String getFilename() {
        return this.filename;
    }

}
