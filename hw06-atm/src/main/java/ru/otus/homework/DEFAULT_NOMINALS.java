package ru.otus.homework;

public enum DEFAULT_NOMINALS {
    ONE_HUNDRED(100),
    TWO_HUNDRED(200),
    THREE_HUNDRED(300),
    FIVE_HUNDRED(500),
    ONE_THOUSAND(1000),
    FIVE_THOUSAND(5000);

    private int value;

    DEFAULT_NOMINALS(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
