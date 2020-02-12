package ru.otus.homework.objects;

import java.util.Objects;
import java.util.Set;

public class TreeObj2 {
    private Set<String> s2 = Set.of("test2", "test22");
    private TreeObj3 obj3 = new TreeObj3();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeObj2 treeObj2 = (TreeObj2) o;
        return Objects.equals(s2, treeObj2.s2) &&
                Objects.equals(obj3, treeObj2.obj3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(s2, obj3);
    }
}
