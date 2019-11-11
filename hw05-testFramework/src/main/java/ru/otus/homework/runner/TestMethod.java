package ru.otus.homework.runner;

import java.lang.reflect.Method;

/**
 * Class for holding test method together with its annotation's value for expected field
 */
public class TestMethod {
    private Method method;
    private Class <? extends Throwable> expected;

    public TestMethod(Method method, Class<? extends Throwable> expected) {
        this.method = method;
        this.expected = expected;
    }

    public Method getMethod() {
        return method;
    }

    public Class<? extends Throwable> getExpected() {
        return expected;
    }
}
