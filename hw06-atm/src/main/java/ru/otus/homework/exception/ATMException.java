package ru.otus.homework.exception;

/**
 * Class reflects exception which can appear during ATM's work
 */
public class ATMException extends RuntimeException {

    public ATMException(String s) {
        super(s);
    }
}
