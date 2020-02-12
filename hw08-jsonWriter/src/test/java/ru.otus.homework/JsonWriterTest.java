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
import ru.otus.homework.objects.TreeObjRoot;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class JsonWriterTest {
    private Gson gson = new Gson();
    private JsonWriter jsonWriter = new JsonWriter();

    @Test
    void testNull() {
        TestObject obj = null;
        String json0 = jsonWriter.toJson(obj);
        String json1 = gson.toJson(obj);

        assertEquals(gson.fromJson(json0, TestObject.class), gson.fromJson(json1, TestObject.class));
    }

    @Test
    void testObjectWithoutFields() {
        ObjectWithoutFields obj = new ObjectWithoutFields();
        String json0 = jsonWriter.toJson(obj);
        String json1 = gson.toJson(obj);

        assertEquals(json0, json1);
    }

    @ParameterizedTest(name = "Test JsonWriter for {1}")
    @MethodSource("getObjects")
    void testJsonWriter(Object obj, String s) {
        Class cl = obj.getClass();
        String json0 = jsonWriter.toJson(obj);
        String json1 = gson.toJson(obj);

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
