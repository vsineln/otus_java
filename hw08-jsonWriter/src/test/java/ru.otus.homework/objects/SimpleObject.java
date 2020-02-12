package ru.otus.homework.objects;

import java.util.Objects;

public class SimpleObject {
    private int n = 0;
    private String str = "string";
    private Double d = 0.0033;
    private Integer integer = 2;
    private boolean bool = true;
    private char ch = 'q';

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleObject that = (SimpleObject) o;
        return n == that.n &&
                bool == that.bool &&
                ch == that.ch &&
                Objects.equals(str, that.str) &&
                Objects.equals(d, that.d) &&
                Objects.equals(integer, that.integer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, str, d, integer, bool, ch);
    }
}
