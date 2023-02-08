package de.cubenation.bedrock.core.database;

import de.cubenation.bedrock.core.exception.DatastoreInitException;
import org.hibernate.Session;

import java.io.Closeable;

public interface Database extends Closeable {

    void init(Class<?>... entities) throws DatastoreInitException;

    Session openSession();

}
