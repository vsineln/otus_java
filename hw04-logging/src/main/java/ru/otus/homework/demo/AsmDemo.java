package ru.otus.homework.demo;

/*
 * Class with methods which has @Log annotation should be passed as argument for javaagent:
 *
 * java -javaagent:target/asmDemo.jar=ru/otus/homework/demo/TestLogging -jar target/asmDemo.jar
 */
public class AsmDemo {

    public static void main(String[] args) {
        TestLogging testLogging = new TestLogging();
        testLogging.testNoArgs();
        testLogging.testNoLog();
        testLogging.testObject(new TestObject("string", 357));
        testLogging.testInt(3, 2, 1);
        testLogging.testIntegerFloatDouble(1, 2.1f, 3.111111);
        testLogging.testBooleanShortByte(true, (short) 1, (byte) 1);
        testLogging.testLongDouble(5L, 7.8);
        testLogging.testStringChar("asdfg", 't');
    }
}
