package ru.otus.homework.runner;

import ru.otus.homework.annotations.After;
import ru.otus.homework.annotations.Before;
import ru.otus.homework.annotations.Test;

import java.lang.annotation.Annotation;
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

    public ClassMethods(Method[] methods) {
        for (Method method : methods) {
            boolean isPrecondition = false;
            if (method.getParameterCount() > 0 || !method.getReturnType().equals(Void.TYPE)) {
                throw new TestExecutionException(String.format("Test method %s should be void and without parameters", method.getName()));
            }
            for (Annotation annotation : method.getAnnotations()) {
                if (After.class.equals(annotation.annotationType())) {
                    afterMethods.add(method);
                    isPrecondition = true;
                }
                if (Before.class.equals(annotation.annotationType())) {
                    beforeMethods.add(method);
                    isPrecondition = true;
                }
                if (Test.class.equals(annotation.annotationType())) {
                    if (isPrecondition) {
                        throw new TestExecutionException("Annotation @Test can not be used together with @After or @Before");
                    }
                    testMethods.add(new TestMethod(method, ((Test) annotation).expected()));
                }
            }
        }
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
