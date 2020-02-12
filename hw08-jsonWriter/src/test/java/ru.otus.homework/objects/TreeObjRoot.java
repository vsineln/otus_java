package ru.otus.homework.objects;

import java.util.Objects;

public class TreeObjRoot {
    private String s0 = "test0";
    private TreeObj1 obj1 = new TreeObj1();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeObjRoot obj0 = (TreeObjRoot) o;
        return Objects.equals(s0, obj0.s0) &&
                Objects.equals(obj1, obj0.obj1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(s0, obj1);
    }
}
