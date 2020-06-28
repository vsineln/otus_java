package ru.otus.homework.demo;

public class TestObject {
    private String s;
    private int i;

    public TestObject(String s, int i) {
        this.s = s;
        this.i = i;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "s='" + s + '\'' +
                ", i=" + i +
                '}';
    }
}
