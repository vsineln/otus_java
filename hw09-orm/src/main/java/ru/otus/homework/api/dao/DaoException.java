package ru.otus.homework.api.dao;

public class DaoException extends RuntimeException {
    public DaoException(Exception ex) {
        super(ex);
    }
}
