package ru.otus.homework.runner;

/*
 * Custom exception which will be thrown in case of any error during tests' execution
 */
public class TestExecutionException extends RuntimeException {

    public TestExecutionException(String message) {
        super(message);
    }

    public TestExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
