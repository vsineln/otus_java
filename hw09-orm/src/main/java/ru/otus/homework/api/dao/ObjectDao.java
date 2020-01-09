package ru.otus.homework.api.dao;

import ru.otus.homework.api.sessionmanager.SessionManager;

import java.util.Optional;

public interface ObjectDao<T> {
    Optional<T> findById(long id, Class cl);

    long saveObject(T object);

    void updateObject(T object);

    long createOrUpdateObject(T object);

    SessionManager getSessionManager();
}
