package ru.otus.homework.runner;

import org.apache.commons.collections4.CollectionUtils;
import ru.otus.homework.annotations.After;
import ru.otus.homework.annotations.Before;
import ru.otus.homework.annotations.Test;
import ru.otus.homework.demo.TestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Class which performs testing. Static run(..) method should be called with name of the class
 * which has methods marked with @Test, @After, @Before
 */
public class TestRunner {
    private static List<Method> afterMethods = new ArrayList<>();
    private static List<Method> beforeMethods = new ArrayList<>();
    private static List<TestMethod> testMethods = new ArrayList<>();
    private static int passed = 0;
    private static int failed = 0;

    public static void run(String className) {
        try {
            Arrays.stream(getTestMethods(className)).forEach(m -> splitMethod(m));
            executeTestMethods(className);
        } catch (TestExecutionException e) {
            System.out.println("Error appeared during tests execution: " + e.getMessage());
        }
        printStatistic();
    }

    private static Method[] getTestMethods(String className) {
        Class cl;
        try {
            cl = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new TestExecutionException(String.format("Class %s is not found", className), e.getCause());
        }
        Method[] methods = cl.getDeclaredMethods();
        if (methods.length == 0) {
            throw new TestExecutionException(String.format("Class %s does not have methods", className));
        }
        return methods;
    }

    private static void splitMethod(Method method) {
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

    private static void executeTestMethods(String className) {
        if (CollectionUtils.isEmpty(testMethods)) {
            throw new TestExecutionException("No test methods were found");
        }

        for (TestMethod testMethod : testMethods) {
            try {
                Object testClass = Class.forName(className).getConstructor().newInstance();
                runTest(testClass, testMethod);
            } catch (ClassNotFoundException e) {
                throw new TestExecutionException(String.format("Class %s is not found", className), e.getCause());
            } catch (NoSuchMethodException e) {
                throw new TestExecutionException(String.format("Class %s should not have explicit constructor", className), e.getCause());
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new TestExecutionException(String.format("Instance of the class %s can not be created", className), e.getCause());
            }
        }
    }

    private static void runTest(Object testClass, TestMethod testMethod) {
        try {
            for (Method m : beforeMethods) {
                m.invoke(testClass);
            }

            testMethod.getMethod().invoke(testClass);
            passed += 1;

            for (Method m : afterMethods) {
                m.invoke(testClass);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            if ((testMethod.getExpected()).equals(e.getCause().getClass())) {
                passed += 1;
            } else {
                failed += 1;
            }
        }
    }

    private static void printStatistic() {
        System.out.println(String.format("Number of tests to execute: %d\n" +
                        "Number of passed tests: %d\n" +
                        "Number of failed tests: %d\n",
                testMethods.size(), passed, failed));
    }
}
