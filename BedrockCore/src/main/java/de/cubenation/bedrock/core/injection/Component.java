package de.cubenation.bedrock.core.injection;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.InjectionException;

public abstract class Component {

    protected final FoundationPlugin plugin;

    public Component(FoundationPlugin plugin) {
        this.plugin = plugin;
        try {
            InstanceInjector.performInjection(plugin, this);
        } catch (InjectionException e) {
            // TODO: Is this exception handling okay? investigate or something, idk
            throw new RuntimeException(e);
        }
    }
}
