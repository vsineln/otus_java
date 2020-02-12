package ru.otus.homework;

import org.apache.commons.lang3.tuple.Pair;
import ru.otus.homework.exception.JsonException;
import ru.otus.homework.node.Node;
import ru.otus.homework.node.NodeArray;
import ru.otus.homework.node.NodeLeaf;
import ru.otus.homework.node.NodeMap;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonWriter {

    public String toJson(Object object) {
        if (object == null) {
            return null;
        }
        Node node = parseFields(object, new Node());
        return node.nodeToJson(new StringBuilder("{"));
    }

    private Node parseFields(Object object, Node node) {
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            if (fields.length == 0) {
                return new Node();
            }
            for (Field field : fields) {
                field.setAccessible(true);
                Class cl = field.getType();
                Object fieldInst = field.get(object);
                if (fieldInst == null) {
                    return new Node();
                }
                String fieldName = field.getName();
                if (checkSimple(cl)) {
                    node.addChild(new NodeLeaf(fieldName, fieldInst));
                } else if (field.getType().isArray()) {
                    node.addChild(new NodeArray(fieldName, getArrayValues(field, fieldInst)));
                } else if (Collection.class.isAssignableFrom(cl)) {
                    node.addChild(new NodeArray(fieldName, getCollectionValues(field, fieldInst)));
                } else if (Map.class.isAssignableFrom(cl)) {
                    node.addChild(new NodeMap(fieldName, getMapValues(field, fieldInst)));
                } else {
                    Node newNode = new Node(fieldName);
                    node.addChild(newNode);
                    parseFields(fieldInst, newNode);
                }
            }
        } catch (IllegalAccessException e) {
            throw new JsonException(e.getMessage());
        }
        return node;
    }

    private boolean checkSimple(Class cl) {
        return cl.isPrimitive() || cl == Number.class || cl == String.class || cl == Integer.class || cl == Long.class || cl == Double.class || cl == Float.class
                || cl == Boolean.class || cl == Byte.class || cl == Character.class || cl == Short.class;
    }

    private List<Object> getArrayValues(Field field, Object fieldInst) {
        List<Object> list = new LinkedList<>();
        if (checkSimple(field.getType().getComponentType())) {
            for (int i = 0; i < Array.getLength(fieldInst); i++) {
                list.add(Array.get(fieldInst, i));
            }
        } else {
            for (int i = 0; i < Array.getLength(fieldInst); i++) {
                list.add(parseFields(Array.get(fieldInst, i), new Node()));
            }
        }
        return list;
    }

    private List<Object> getCollectionValues(Field field, Object fieldInst) {
        Class<?> colTypeArg = (Class<?>) getCollectionType(field)[0];
        List<Object> list = new LinkedList<>();

        Iterator iterator = ((Collection) fieldInst).iterator();
        if (checkSimple(colTypeArg)) {
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
        } else {
            while (iterator.hasNext()) {
                list.add(parseFields(iterator.next(), new Node()));
            }
        }
        return list;
    }

    private List<Pair<Object, Object>> getMapValues(Field field, Object fieldInst) {
        Class<?> valueTypeArg = (Class<?>) getCollectionType(field)[1];
        List<Pair<Object, Object>> list = new LinkedList<>();

        Iterator iterator = ((Map) fieldInst).entrySet().iterator();
        if (checkSimple(valueTypeArg)) {
            while (iterator.hasNext()) {
                Map.Entry<Object, Object> pair = (Map.Entry<Object, Object>) iterator.next();
                list.add(Pair.of(pair.getKey(), pair.getValue()));
            }
        } else {
            while (iterator.hasNext()) {
                Map.Entry<Object, Object> pair = (Map.Entry<Object, Object>) iterator.next();
                list.add(Pair.of(pair.getKey(), parseFields(pair.getValue(), new Node())));
            }
        }
        return list;
    }

    private Type[] getCollectionType(Field field) {
        ParameterizedType colType = (ParameterizedType) field.getGenericType();
        return colType.getActualTypeArguments();
    }
}
