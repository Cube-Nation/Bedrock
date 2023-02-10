package de.cubenation.bedrock.core.database;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.StorageInitException;
import lombok.AllArgsConstructor;
import org.hibernate.Session;

import java.io.Closeable;

@AllArgsConstructor
public abstract class Database implements Closeable {

    protected final FoundationPlugin plugin;

    protected final String identifier;

    abstract public void init(Class<?>... entities) throws StorageInitException;

    abstract public Session openSession();
}
