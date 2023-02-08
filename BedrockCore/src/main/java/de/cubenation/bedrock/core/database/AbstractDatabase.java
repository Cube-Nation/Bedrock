package de.cubenation.bedrock.core.database;

import de.cubenation.bedrock.core.FoundationPlugin;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractDatabase implements Database {

    protected final FoundationPlugin plugin;

    protected final String identifier;
}
