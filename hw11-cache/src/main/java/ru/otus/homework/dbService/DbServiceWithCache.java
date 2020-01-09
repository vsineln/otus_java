package ru.otus.homework.dbService;

import ru.otus.homework.api.dao.ObjectDao;
import ru.otus.homework.api.service.DbService;
import ru.otus.homework.api.service.DbServiceException;
import ru.otus.homework.api.sessionmanager.SessionManager;
import ru.otus.homework.cache.HwCache;
import ru.otus.homework.jdbc.reflection.ReflectionHelper;

import java.util.Optional;

public class DbServiceWithCache implements DbService {
    private ObjectDao objectDao;
    private HwCache<String, Object> cache;
    private ReflectionHelper reflectionHelper = new ReflectionHelper();

    public DbServiceWithCache(ObjectDao objectDao, HwCache<String, Object> cache) {
        this.objectDao = objectDao;
        this.cache = cache;
    }

    @Override
    public long saveObject(Object object) {
        try (SessionManager sessionManager = objectDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                long objectId = objectDao.saveObject(object);
                sessionManager.commitSession();
                reflectionHelper.setIdField(object, objectId);
                cache.put(String.valueOf(objectId), object);
                return objectId;
            } catch (Exception e) {
                sessionManager.rollbackSession();
                throw new DbServiceException(e);
            }
        }
    }

    @Override
    public Optional<Object> getObject(long id, Class cl) {
        Object cachedObject = cache.get(String.valueOf(id));
        if (cachedObject != null) {
            return Optional.of(cachedObject);
        }
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
                cache.put(reflectionHelper.getIdFieldValue(object), object);
            } catch (Exception e) {
                sessionManager.rollbackSession();
            }
        }
    }

    @Override
    public long createOrUpdate(Object object) {
        try (SessionManager sessionManager = objectDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                long objectId = objectDao.createOrUpdateObject(object);
                sessionManager.commitSession();
                cache.put(String.valueOf(objectId), object);
                return objectId;
            } catch (Exception e) {
                sessionManager.rollbackSession();
                throw new DbServiceException(e);
            }
        }
    }
}
