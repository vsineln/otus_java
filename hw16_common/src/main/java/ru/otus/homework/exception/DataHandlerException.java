package ru.otus.homework.exception;

public class DataHandlerException extends RuntimeException {

    public DataHandlerException(String message) {
        super(message);
    }

    public DataHandlerException(String message, Exception e) {
        super(message, e);
    }
}
