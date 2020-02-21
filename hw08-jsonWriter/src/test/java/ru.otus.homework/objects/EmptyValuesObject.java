package ru.otus.homework.objects;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

public class EmptyValuesObject {
    private String str1 = "";
    private Object o = new Object();
    private int[] array1 = new int[]{};
    private String[] array2 = new String[]{};
    private List<TestObject> list1 = new LinkedList<>();
    private Set<Integer> set1 = new HashSet<>();
    private Map<String, Integer> map1 = new TreeMap<>();

    @Override
    public boolean equals(Object o1) {
        if (this == o1) return true;
        if (o1 == null || getClass() != o1.getClass()) return false;
        EmptyValuesObject that = (EmptyValuesObject) o1;
        return Objects.equals(str1, that.str1) &&
                Objects.equals(o, that.o) &&
                Arrays.equals(array1, that.array1) &&
                Arrays.equals(array2, that.array2) &&
                Objects.equals(list1, that.list1) &&
                Objects.equals(set1, that.set1) &&
                Objects.equals(map1, that.map1);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(str1, o, list1, set1, map1);
        result = 31 * result + Arrays.hashCode(array1);
        result = 31 * result + Arrays.hashCode(array2);
        return result;
    }
}
