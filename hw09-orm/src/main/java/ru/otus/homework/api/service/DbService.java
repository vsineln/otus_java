package ru.otus.homework.api.service;

import java.util.Optional;

public interface DbService {

    void saveObject(Object object);

    Optional<Object> getObject(long id, Class cl);

    void updateObject(Object object);

    void createOrUpdate(Object o);
}
