package de.cubenation.bedrock.core.datastore;

import de.cubenation.bedrock.core.FoundationPlugin;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractDatastore implements Datastore {

    protected final FoundationPlugin plugin;

    protected final String identifier;
}
