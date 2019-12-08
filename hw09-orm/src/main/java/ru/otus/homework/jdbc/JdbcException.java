package ru.otus.homework.jdbc;

public class JdbcException extends RuntimeException {
    public JdbcException(Exception e) {
        super(e);
    }
}
