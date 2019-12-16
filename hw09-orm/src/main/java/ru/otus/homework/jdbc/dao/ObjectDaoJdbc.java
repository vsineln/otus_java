package ru.otus.homework.jdbc.dao;

import ru.otus.homework.api.dao.DaoException;
import ru.otus.homework.api.dao.ObjectDao;
import ru.otus.homework.api.sessionmanager.SessionManager;
import ru.otus.homework.jdbc.JdbcTemplate;
import ru.otus.homework.jdbc.JdbcTemplateImpl;
import ru.otus.homework.jdbc.sessionmanager.SessionManagerJdbc;

import java.util.Optional;

public class ObjectDaoJdbc implements ObjectDao {

    private final SessionManagerJdbc sessionManager;
    private final JdbcTemplate<Object> jdbcTemplate;

    public ObjectDaoJdbc(SessionManagerJdbc sessionManager) {
        this.sessionManager = sessionManager;
        this.jdbcTemplate = new JdbcTemplateImpl(sessionManager);
    }

    @Override
    public Optional<Object> findById(long id, Class cl) {
        try {
            return Optional.of(jdbcTemplate.load(id, cl));
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public long saveObject(Object object) {
        try {
            return jdbcTemplate.create(object);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void updateObject(Object object) {
        jdbcTemplate.update(object);
    }

    @Override
    public void createOrUpdateObject(Object object) {
        jdbcTemplate.createOrUpdate(object);
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
