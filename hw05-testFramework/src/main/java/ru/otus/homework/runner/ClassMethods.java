package ru.otus.homework.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for holding methods separated by annotations
 */
public class ClassMethods {

    private List<Method> afterMethods = new ArrayList<>();
    private List<Method> beforeMethods = new ArrayList<>();
    private List<TestMethod> testMethods = new ArrayList<>();

    public ClassMethods() {
    }

    public List<Method> getAfterMethods() {
        return afterMethods;
    }

    public List<Method> getBeforeMethods() {
        return beforeMethods;
    }

    public List<TestMethod> getTestMethods() {
        return testMethods;
    }
}
