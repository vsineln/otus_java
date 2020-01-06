package ru.otus.homework.dbService;

import ru.otus.homework.api.dao.ObjectDao;
import ru.otus.homework.api.service.DbService;
import ru.otus.homework.api.service.DbServiceException;
import ru.otus.homework.api.sessionmanager.SessionManager;
import ru.otus.homework.cache.HwCache;

import java.util.Optional;

public class DbServiceWithCache implements DbService {
    private final ObjectDao objectDao;

    public DbServiceWithCache(ObjectDao objectDao) {
        this.objectDao = objectDao;
    }

    @Override
    public long saveObject(Object object) {
        try (SessionManager sessionManager = objectDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                long objectId = objectDao.saveObject(object);
                sessionManager.commitSession();
                return objectId;
            } catch (Exception e) {
                sessionManager.rollbackSession();
                throw new DbServiceException(e);
            }
        }
    }

    @Override
    public Optional<Object> getObject(long id, Class cl) {
        try (SessionManager sessionManager = objectDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                return objectDao.findById(id, cl);
            } catch (Exception e) {
                sessionManager.rollbackSession();
            }
            return Optional.empty();
        }
    }

    @Override
    public void updateObject(Object object) {
        try (SessionManager sessionManager = objectDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                objectDao.updateObject(object);
                sessionManager.commitSession();
            } catch (Exception e) {
                sessionManager.rollbackSession();
            }
        }
    }

    @Override
    public void createOrUpdate(Object object) {
        try (SessionManager sessionManager = objectDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                objectDao.createOrUpdateObject(object);
                sessionManager.commitSession();
            } catch (Exception e) {
                sessionManager.rollbackSession();
            }
        }
    }
}
