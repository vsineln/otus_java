package ru.otus.homework.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Nominal {
    ONE_HUNDRED(100),
    TWO_HUNDRED(200),
    FIVE_HUNDRED(500),
    ONE_THOUSAND(1000),
    TWO_THOUSAND(2000),
    FIVE_THOUSAND(5000);

    private int value;

    private final static Map<Integer, Nominal> map =
            Arrays.stream(Nominal.values()).collect(Collectors.toMap(nominal -> nominal.value, nominal -> nominal));

    Nominal(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Nominal valueOf(int value) {
        return map.get(value);
    }
}
