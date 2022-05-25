package de.cubenation.bedrock.bukkit.plugin.io.handler;

import java.io.DataInputStream;
import java.io.IOException;

public interface PluginMessageHandler {

    void handle(DataInputStream data) throws IOException;
}
