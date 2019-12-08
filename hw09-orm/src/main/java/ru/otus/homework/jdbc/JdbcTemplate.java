package ru.otus.homework.jdbc;

public interface JdbcTemplate<T> {
    void create(T objectData);

    void update(T objectData);

    void createOrUpdate(T objectData);

    <T> T load(long id, Class<T> clazz);
}
