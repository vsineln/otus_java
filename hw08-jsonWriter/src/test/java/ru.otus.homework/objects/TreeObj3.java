package ru.otus.homework.objects;

import java.util.Map;
import java.util.Objects;

public class TreeObj3 {
    private Map<Integer, String> s3 = Map.of(1, "test3");

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeObj3 treeObj3 = (TreeObj3) o;
        return Objects.equals(s3, treeObj3.s3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(s3);
    }
}
