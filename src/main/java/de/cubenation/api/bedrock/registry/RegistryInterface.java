package de.cubenation.api.bedrock.registry;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.exception.NoSuchRegisterableException;
import org.bukkit.command.CommandSender;

public interface RegistryInterface {

    @SuppressWarnings("unused")
    void _register(BasePlugin plugin, String ident, CommandSender sender, Registerable storable);

    @SuppressWarnings("unused")
    boolean _exists(BasePlugin plugin, String ident, CommandSender sender);

    @SuppressWarnings("unused")
    Registerable _get(BasePlugin plugin, String ident, CommandSender sender) throws NoSuchRegisterableException;

    @SuppressWarnings("unused")
    void _remove(BasePlugin plugin, String ident, CommandSender sender);

}
