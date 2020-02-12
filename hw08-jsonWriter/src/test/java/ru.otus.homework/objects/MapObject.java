package ru.otus.homework.objects;

import java.util.Map;
import java.util.Objects;

public class MapObject {
    private Map<Long, String> map1 = Map.of(1L, "key1", 2L, "key2");
    private Map<Integer, Character> map2 = Map.of(1, 'c', 2, 'd');
    private Map<String, Integer> map3 = Map.of("key1", 1, "key2", 2);
    private Map<Integer, TestObject> map4 = Map.of(1, new TestObject(1, "a"), 2, new TestObject(2, "b"));
    private Map<Integer, TreeObj1> map5 = Map.of(1, new TreeObj1());

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapObject mapObject = (MapObject) o;
        return Objects.equals(map1, mapObject.map1) &&
                Objects.equals(map2, mapObject.map2) &&
                Objects.equals(map3, mapObject.map3) &&
                Objects.equals(map4, mapObject.map4) &&
                Objects.equals(map5, mapObject.map5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map1, map2, map3, map4, map5);
    }
}
