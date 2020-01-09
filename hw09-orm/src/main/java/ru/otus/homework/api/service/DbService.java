package ru.otus.homework.api.service;

import java.util.Optional;

public interface DbService {

    long saveObject(Object object);

    Optional<Object> getObject(long id, Class cl);

    void updateObject(Object object);

    long createOrUpdate(Object o);
}
