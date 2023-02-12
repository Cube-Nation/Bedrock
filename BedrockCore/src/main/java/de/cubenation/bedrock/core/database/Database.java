package de.cubenation.bedrock.core.database;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.StorageInitException;
import de.cubenation.bedrock.core.injection.Component;
import lombok.AllArgsConstructor;
import org.hibernate.Session;

import java.io.Closeable;

public abstract class Database extends Component implements Closeable {

    protected final String identifier;

    public Database(FoundationPlugin plugin, String identifier) {
        super(plugin);
        this.identifier = identifier;
    }

    abstract public void init(Class<?>... entities) throws StorageInitException;

    abstract public Session openSession();
}
