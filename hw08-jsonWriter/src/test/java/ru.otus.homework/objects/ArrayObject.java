package ru.otus.homework.objects;

import java.util.Arrays;

public class ArrayObject {
    private int[] array1 = {1, 2, 3};
    private Integer[] array2 = {1, 2, 3};
    private String[] array3 = {"a", "b", "c"};
    private char[] array4 = {'d', 'e', 'f'};
    private TestObject[] array5 = {new TestObject(1, "a"), new TestObject(2, "b")};
    private TreeObj1[] array6 = {new TreeObj1()};

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayObject that = (ArrayObject) o;
        return Arrays.equals(array1, that.array1) &&
                Arrays.equals(array2, that.array2) &&
                Arrays.equals(array3, that.array3) &&
                Arrays.equals(array4, that.array4) &&
                Arrays.equals(array5, that.array5) &&
                Arrays.equals(array6, that.array6);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(array1);
        result = 31 * result + Arrays.hashCode(array2);
        result = 31 * result + Arrays.hashCode(array3);
        result = 31 * result + Arrays.hashCode(array4);
        result = 31 * result + Arrays.hashCode(array5);
        result = 31 * result + Arrays.hashCode(array6);
        return result;
    }
}
