package de.cubenation.bedrock.core.injection;

import de.cubenation.bedrock.core.FoundationPlugin;
import lombok.AllArgsConstructor;

import java.lang.reflect.Field;

@AllArgsConstructor
public abstract class InstanceSupplier<Entity> {

    protected final FoundationPlugin plugin;

    abstract public Entity getInstance(Field field);
}
