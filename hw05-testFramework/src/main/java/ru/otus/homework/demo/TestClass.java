package ru.otus.homework.demo;

import ru.otus.homework.annotations.After;
import ru.otus.homework.annotations.Before;
import ru.otus.homework.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Class for testing DemoClass methods
 */
public class TestClass {
    private DemoClass demo1;
    private DemoClass demo2;

    @Before
    public void initCatalog() {
        demo1 = new DemoClass();
        demo2 = new DemoClass(new ArrayList<>(Arrays.asList("Film1", "Film2")));
    }

    @Test
    public void whenAddItemInEmptyCatalog_thenSuccess() {
        demo1.addItem("NewFilm");
    }

    @Test
    public void whenAddItemInNotEmptyCatalog_thenSuccess() {
        demo2.addItem("NewFilm");
        demo2.addItem("Film3");
        demo2.addItem("Film4");
    }

    @Test(expected = RuntimeException.class)
    public void whenAddItemWhichExists_thenException() {
        demo2.addItem("Film2");
    }

    @Test(expected = RuntimeException.class)
    public void whenRemoveItemFromEmptyCatalog_thenException() {
        demo1.removeItem("");
    }

    @Test(expected = RuntimeException.class)
    public void whenRemoveItemWhichNotExist_thenException() {
        demo2.removeItem("NewBook");
    }

    @Test
    public void whenRemoveExistingItem_thenSuccess() {
        demo2.removeItem("Film1");
    }

    @Test
    public void testException() {
        DemoClass demo3 = new DemoClass(Arrays.asList("Film1"));
        demo3.addItem("UnknownItem");
    }

    @After
    public void cleanCatalog() {
        demo1 = new DemoClass();
        demo2 = new DemoClass(new ArrayList<>());
    }
}
