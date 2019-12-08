package ru.otus.homework.api.dao;

import ru.otus.homework.api.sessionmanager.SessionManager;

import java.util.Optional;

public interface ObjectDao {
    Optional<Object> findById(long id, Class cl);

    void saveObject(Object object);

    void updateObject(Object object);

    void createOrUpdateObject(Object object);

    SessionManager getSessionManager();
}
