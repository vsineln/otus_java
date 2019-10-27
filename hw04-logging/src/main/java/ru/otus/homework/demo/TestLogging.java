package ru.otus.homework.demo;

import ru.otus.homework.annotation.Log;

/**
 * Test class
 */
public class TestLogging {

    @Log
    public void testInt(int param1, int param2, int param3) {
        System.out.println("Multiple int");
    }

    @Log
    public void testIntegerFloatDouble(Integer i, Float f, Double d) {
        System.out.println("Test integer, float");
    }

    @Log
    public void testBooleanShortByte(boolean bool, short s, byte b) {
        System.out.println("Test boolean, short, byte");
    }

    @Log
    public void testLongDouble(long l, double d){
        System.out.println("Test long, double");
    }

    public void testNoLog() {
        System.out.println("Log annotation is absent");
    }

    @Log
    public void testNoArgs() {
        System.out.println("No arguments");
    }

    @Log
    public void testObject(TestObject o) {
        System.out.println("Test object");
    }

    @Log
    public String testStringChar(String s, char a) {
        System.out.println("Test string and char");
        return s;
    }
}
