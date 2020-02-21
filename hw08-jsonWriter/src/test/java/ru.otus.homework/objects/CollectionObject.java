package ru.otus.homework.objects;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class CollectionObject {
    private List<String> list1 = List.of("test1", "test2", "test3");
    private List<TestObject> list2 = List.of(new TestObject(1, "a"), new TestObject(2, "b"));
    private List<TreeObj1> list3 = List.of(new TreeObj1());
    private Set<Integer> set1 = Set.of(1, 2, 3);
    private Set<Character> set2 = Set.of('a', 'b', 'c');
    private Queue<String> queue = new LinkedBlockingQueue<>(list1);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionObject that = (CollectionObject) o;
        return Objects.equals(list1, that.list1) &&
                Objects.equals(list2, that.list2) &&
                Objects.equals(list3, that.list3) &&
                Objects.equals(set1, that.set1) &&
                Objects.equals(set2, that.set2) &&
                Arrays.equals(queue.toArray(), that.queue.toArray());
    }

    @Override
    public int hashCode() {
        return Objects.hash(list1, list2, list3, set1, set2, queue);
    }
}
