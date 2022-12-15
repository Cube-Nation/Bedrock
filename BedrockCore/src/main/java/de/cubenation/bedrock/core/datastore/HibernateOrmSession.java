package de.cubenation.bedrock.core.datastore;


import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateOrmSession implements DatastoreSession {

    @Getter
    private final Session hibernateSession;

    public HibernateOrmSession(SessionFactory sessionFactory) {
        hibernateSession = sessionFactory.openSession();
    }

    @Override
    public void beginTransaction() {
        hibernateSession.getTransaction().begin();
    }

    @Override
    public void commitTransaction() {
        hibernateSession.getTransaction().commit();
    }

    @Override
    public void persist(Object entity) {
        hibernateSession.persist(entity);
    }

    @Override
    public void close() {
        hibernateSession.close();
    }
}
