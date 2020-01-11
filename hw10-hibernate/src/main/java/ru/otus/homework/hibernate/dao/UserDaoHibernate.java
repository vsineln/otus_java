package ru.otus.homework.hibernate.dao;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.api.dao.DaoException;
import ru.otus.homework.api.dao.ObjectDao;
import ru.otus.homework.api.model.AddressDataSet;
import ru.otus.homework.api.model.PhoneDataSet;
import ru.otus.homework.api.model.User;
import ru.otus.homework.api.sessionmanager.SessionManager;
import ru.otus.homework.hibernate.sessionmanager.DatabaseSessionHibernate;
import ru.otus.homework.hibernate.sessionmanager.SessionManagerHibernate;

import java.util.List;
import java.util.Optional;

public class UserDaoHibernate<T> implements ObjectDao<T> {
    private static Logger log = LoggerFactory.getLogger(UserDaoHibernate.class);

    private final SessionManagerHibernate sessionManager;

    public UserDaoHibernate(SessionManagerHibernate sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Optional<T> findById(long id, Class cl) {
        DatabaseSessionHibernate currentSession = sessionManager.getCurrentSession();
        try {
            T user = (T) currentSession.getSession().find(cl, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            log.error("User can not be found by id " + e.getMessage());
            throw new DaoException(e);
        }
    }

    @Override
    public long saveObject(T t) {
        DatabaseSessionHibernate currentSession = sessionManager.getCurrentSession();
        User user = (User) t;
        if (user.getId() != null) {
            throw new DaoException("User id should not be provided for save");
        }
        try {
            Session hibernateSession = currentSession.getSession();
            hibernateSession.persist(user);
            return user.getId();
        } catch (Exception e) {
            log.error("User can not be saved " + e.getMessage());
            throw new DaoException(e);
        }
    }

    @Override
    public void updateObject(T t) {
        DatabaseSessionHibernate currentSession = sessionManager.getCurrentSession();
        User user = (User) t;
        if (user.getId() == 0) {
            throw new DaoException("User id should be provided for update");
        }
        try {
            Session hibernateSession = currentSession.getSession();
            persistNewObjects(user, hibernateSession);
            hibernateSession.merge(user);
        } catch (Exception e) {
            log.error("User can not be updated " + e.getMessage());
            throw new DaoException(e);
        }
    }

    @Override
    public long createOrUpdateObject(T t) {
        DatabaseSessionHibernate currentSession = sessionManager.getCurrentSession();
        User user = (User) t;
        try {
            Session hibernateSession = currentSession.getSession();
            if (user.getId() != null) {
                persistNewObjects(user, hibernateSession);
                hibernateSession.merge(user);
            } else {
                hibernateSession.persist(user);
            }
            return user.getId();
        } catch (Exception e) {
            log.error("User can not be saved or updated " + e.getMessage());
            throw new DaoException(e);
        }
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    private void persistNewObjects(User user, Session hibernateSession) {
        List<PhoneDataSet> phones = user.getPhones();
        phones.forEach(phone -> {
            if (phone.getId() == null) {
                hibernateSession.persist(phone);
            }
        });
        AddressDataSet address = user.getAddress();
        if (address.getId() == null) {
            hibernateSession.persist(address);
        }
    }
}
