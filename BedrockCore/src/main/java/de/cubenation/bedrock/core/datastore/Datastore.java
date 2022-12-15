package de.cubenation.bedrock.core.datastore;

import de.cubenation.bedrock.core.exception.DatastoreInitException;

import java.io.Closeable;

public interface Datastore extends Closeable {

    void init(Class<?>... entities) throws DatastoreInitException;

    DatastoreType getType();

    DatastoreSession openSession();

}
