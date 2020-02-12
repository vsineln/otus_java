package ru.otus.homework.objects;

import java.util.Arrays;
import java.util.Objects;

public class TreeObj1 {
    private String[] s1 = {"test1", "test11"};
    private TreeObj2 obj2 = new TreeObj2();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeObj1 treeObj1 = (TreeObj1) o;
        return Arrays.equals(s1, treeObj1.s1) &&
                Objects.equals(obj2, treeObj1.obj2);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(obj2);
        result = 31 * result + Arrays.hashCode(s1);
        return result;
    }
}
