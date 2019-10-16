package ru.otus.homework;

import com.google.common.collect.Ordering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DIYarrayListTest {

    private int SIZE = 100;
    private int DOUBLE_SIZE = 200;
    private List<Integer> list1;
    private List<Integer> list2;

    @BeforeEach
    public void createLists() {
        list1 = new DIYarrayList<>();
        list2 = new DIYarrayList<>();
        for (int i = 0; i < SIZE; i++) {
            list1.add(i);
            list2.add(i * 2);
        }
    }

    @Test
    public void addAll() {
        assertEquals(SIZE, list1.size());
        Integer[] array = list2.toArray(new Integer[list2.size()]);

        Collections.addAll(list1, array);

        assertEquals(DOUBLE_SIZE, list1.size());
    }

    @Test
    public void copy() {
        assertFalse(Arrays.equals(list1.toArray(), list2.toArray()));

        Collections.copy(list1, list2);

        assertArrayEquals(list1.toArray(), list2.toArray());
    }

    @Test
    public void sort() {
        assertTrue(Ordering.natural().isOrdered(list2));

        Collections.sort(list2, (x, y) -> y - x);

        assertTrue(Ordering.natural().reverse().isOrdered(list2));
    }

}
