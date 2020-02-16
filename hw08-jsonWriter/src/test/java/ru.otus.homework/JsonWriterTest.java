package ru.otus.homework;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.otus.homework.objects.ArrayObject;
import ru.otus.homework.objects.CollectionObject;
import ru.otus.homework.objects.EmptyValuesObject;
import ru.otus.homework.objects.ObjectWithoutFields;
import ru.otus.homework.objects.MapObject;
import ru.otus.homework.objects.NullFieldsObject;
import ru.otus.homework.objects.SimpleObject;
import ru.otus.homework.objects.TestObject;
import ru.otus.homework.objects.TreeObj1;
import ru.otus.homework.objects.TreeObjRoot;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class JsonWriterTest {
    private Gson gson = new Gson();
    private JsonWriter jsonWriter = new JsonWriter();

    @Test
    void customTest() {
        assertEquals(gson.toJson(null), jsonWriter.toJson(null));
        assertEquals(gson.toJson(true), jsonWriter.toJson(true));
        assertEquals(gson.toJson(false), jsonWriter.toJson(false));
        assertEquals(gson.toJson((byte) 1), jsonWriter.toJson((byte) 1));
        assertEquals(gson.toJson((short) 2f), jsonWriter.toJson((short) 2f));
        assertEquals(gson.toJson(3), jsonWriter.toJson(3));
        assertEquals(gson.toJson(4L), jsonWriter.toJson(4L));
        assertEquals(gson.toJson(5f), jsonWriter.toJson(5f));
        assertEquals(gson.toJson(6d), jsonWriter.toJson(6d));
        assertEquals(gson.toJson("aaa"), jsonWriter.toJson("aaa"));
        assertEquals(gson.toJson('b'), jsonWriter.toJson('b'));
        assertEquals(gson.toJson(new int[]{1, 2, 3}), jsonWriter.toJson(new int[]{1, 2, 3}));
        assertEquals(gson.toJson(new TreeObj1[]{new TreeObj1()}), jsonWriter.toJson(new TreeObj1[]{new TreeObj1()}));
        assertEquals(gson.toJson(List.of(4, 5, 6)), jsonWriter.toJson(List.of(4, 5, 6)));
        assertEquals(gson.toJson(Collections.singletonList(7)), jsonWriter.toJson(Collections.singletonList(7)));
        assertEquals(gson.toJson(Map.of(1, "a", 2, "b")), jsonWriter.toJson(Map.of(1, "a", 2, "b")));
        assertEquals(gson.toJson(List.of(new TreeObj1())), jsonWriter.toJson(List.of(new TreeObj1())));
        assertEquals(gson.toJson(Map.of(1, new TreeObj1())), jsonWriter.toJson(Map.of(1, new TreeObj1())));
        assertEquals(gson.toJson(Collections.EMPTY_LIST), jsonWriter.toJson(Collections.EMPTY_LIST));
        assertEquals(gson.toJson(Collections.EMPTY_MAP), jsonWriter.toJson(Collections.EMPTY_MAP));
    }

    @Test
    void testNull() {
        TestObject obj = null;
        String json0 = gson.toJson(obj);
        String json1 = jsonWriter.toJson(obj);

        assertEquals(gson.fromJson(json0, TestObject.class), gson.fromJson(json1, TestObject.class));
    }

    @Test
    void testObjectWithoutFields() {
        ObjectWithoutFields obj = new ObjectWithoutFields();
        String json0 = gson.toJson(obj);
        String json1 = jsonWriter.toJson(obj);

        assertEquals(json0, json1);
    }

    @ParameterizedTest(name = "Test JsonWriter for {1}")
    @MethodSource("getObjects")
    void testJsonWriter(Object obj, String s) {
        Class cl = obj.getClass();
        String json0 = gson.toJson(obj);
        String json1 = jsonWriter.toJson(obj);

        assertEquals(gson.fromJson(json0, cl), gson.fromJson(json1, cl));
    }

    static Stream<Arguments> getObjects() {
        return Stream.of(
                arguments(new SimpleObject(), "primitives"),
                arguments(new ArrayObject(), "arrays"),
                arguments(new CollectionObject(), "collections"),
                arguments(new MapObject(), "maps"),
                arguments(new TreeObjRoot(), "nested custom objects"),
                arguments(new NullFieldsObject(), "object with null fields"),
                arguments(new EmptyValuesObject(), "object with empty collection and array fields"));
    }
}
