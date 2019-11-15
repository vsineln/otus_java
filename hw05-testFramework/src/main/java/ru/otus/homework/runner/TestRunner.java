package ru.otus.homework.runner;

import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/*
 * Class which performs testing. Static run(..) method should be called with name of the class
 * which has methods marked with @Test, @After, @Before
 */
public class TestRunner {

    public static void run(String className) {
        Class cl;
        try {
            cl = Class.forName(className);
            ClassMethods classMethods = new ClassMethods(getTestMethods(cl));
            executeTestMethods(cl, classMethods);
        } catch (ClassNotFoundException e) {
            throw new TestExecutionException(String.format("Class %s is not found", className), e.getCause());
        } catch (TestExecutionException e) {
            System.out.println("Error appeared during tests execution: " + e.getMessage());
        }
    }

    private static Method[] getTestMethods(Class cl) {
        Method[] methods = cl.getDeclaredMethods();
        if (methods.length == 0) {
            throw new TestExecutionException(String.format("Class %s does not have methods", cl.getName()));
        }
        return methods;
    }

    private static void executeTestMethods(Class cl, ClassMethods classMethods) {
        List<TestMethod> testMethods = classMethods.getTestMethods();
        if (CollectionUtils.isEmpty(testMethods)) {
            throw new TestExecutionException("No test methods were found");
        }

        int passed = 0;

        for (TestMethod testMethod : testMethods) {
            try {
                Object testClassInstance = cl.getConstructor().newInstance();
                boolean result = runTest(testClassInstance, testMethod, classMethods.getBeforeMethods(), classMethods.getAfterMethods());
                if (result) {
                    passed += 1;
                }
            } catch (NoSuchMethodException e) {
                throw new TestExecutionException(String.format("Class %s should not have explicit constructor", cl.getName()), e.getCause());
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new TestExecutionException(String.format("Instance of the class %s can not be created", cl.getName()), e.getCause());
            }
        }

        printStatistic(testMethods.size(), passed);
    }

    private static boolean runTest(Object testClassInstance, TestMethod testMethod, List<Method> beforeMethods, List<Method> afterMethods) {
        try {
            for (Method m : beforeMethods) {
                m.invoke(testClassInstance);
            }

            testMethod.getMethod().invoke(testClassInstance);

        } catch (IllegalAccessException | InvocationTargetException e) {
            if ((testMethod.getExpected()).equals(e.getCause().getClass())) {
                return true;
            } else {
                return false;
            }
        }

        runAfterMethods(testClassInstance, afterMethods);

        return true;
    }

    private static void runAfterMethods(Object testClassInstance, List<Method> afterMethods) {
        for (Method m : afterMethods) {
            try {
                m.invoke(testClassInstance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.out.println(String.format("Failed to execute method %s", m.getName()));
            }
        }
    }

    private static void printStatistic(int sum, int passed) {
        System.out.println(String.format("Number of tests to execute: %d\n" +
                        "Number of passed tests: %d\n" +
                        "Number of failed tests: %d\n",
                sum, passed, (sum - passed)));
    }
}
