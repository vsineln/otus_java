package ru.otus.homework.objects;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class NullFieldsObject {
    private String str = null;
    private Object o = null;
    private int[] array1 = null;
    private String[] array2 = null;
    private List<TestObject> list = null;
    private Set<Integer> set = null;
    private Map<String, Integer> map = null;

    @Override
    public boolean equals(Object o1) {
        if (this == o1) return true;
        if (o1 == null || getClass() != o1.getClass()) return false;
        NullFieldsObject that = (NullFieldsObject) o1;
        return Objects.equals(str, that.str) &&
                Objects.equals(o, that.o) &&
                Arrays.equals(array1, that.array1) &&
                Arrays.equals(array2, that.array2) &&
                Objects.equals(list, that.list) &&
                Objects.equals(set, that.set) &&
                Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(str, o, list, set, map);
        result = 31 * result + Arrays.hashCode(array1);
        result = 31 * result + Arrays.hashCode(array2);
        return result;
    }
}
