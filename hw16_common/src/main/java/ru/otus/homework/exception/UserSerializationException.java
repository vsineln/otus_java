package ru.otus.homework.exception;

public class UserSerializationException extends RuntimeException {

    public UserSerializationException(String error, Throwable cause) {
        super(error, cause);
    }
}
