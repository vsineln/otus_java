package ru.otus.homework.objects;

import java.util.Objects;

public class TestObject {
    private int n;
    private String s;

    public TestObject(int n, String s) {
        this.n = n;
        this.s = s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestObject that = (TestObject) o;
        return n == that.n &&
                Objects.equals(s, that.s);
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, s);
    }
}
