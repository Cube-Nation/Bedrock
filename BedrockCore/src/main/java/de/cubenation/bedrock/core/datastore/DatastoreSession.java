package de.cubenation.bedrock.core.datastore;

import java.io.Closeable;

public interface DatastoreSession extends Closeable {

    void beginTransaction();

    void commitTransaction();

    void persist(Object entity);
}
