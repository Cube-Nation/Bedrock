package de.cubenation.bedrock;

import de.cubenation.bedrock.command.CommandManager;
import de.cubenation.bedrock.exception.CustomConfigurationFileNotFoundException;

import java.util.ArrayList;

/**
 * Created by B1acksheep on 25.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock
 */
public class BedrockPlugin extends BasePlugin {

    @Override
    public void loadCustomConfiguration() throws CustomConfigurationFileNotFoundException {
    }

    @Override
    public ArrayList<CommandManager> getCommandManager() {
        return null;
    }

    @Override
    public Boolean usePermissionService() {
        return false;
    }
}
