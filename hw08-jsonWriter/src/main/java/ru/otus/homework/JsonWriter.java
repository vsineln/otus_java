package ru.otus.homework;

import ru.otus.homework.exception.JsonException;
import ru.otus.homework.node.Node;
import ru.otus.homework.node.NodeArray;
import ru.otus.homework.node.NodeCollection;
import ru.otus.homework.node.NodeLeaf;
import ru.otus.homework.node.NodeMap;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import static ru.otus.homework.util.JsonUtil.checkSimple;

public class JsonWriter {

    public String toJson(Object object) {
        if (object == null) {
            return "null";
        }
        Node node = parseObject(object, new Node());
        return node.nodeToJson(new StringBuilder("{"));
    }

    public Node parseObject(Object object, Node node) {
        if (checkSimple(object.getClass())) {
            return new NodeLeaf(null, object);
        }
        Class objClass = object.getClass();
        if (objClass.isArray()) {
            return new NodeArray(object);
        } else if (Collection.class.isAssignableFrom(objClass)) {
            return new NodeCollection(object);
        } else if (Map.class.isAssignableFrom(objClass)) {
            return new NodeMap(object);
        }
        return parseFields(object, node);
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
                    node.addChild(new NodeArray(fieldName, fieldInst, field.getType().getComponentType()));
                } else if (Collection.class.isAssignableFrom(cl)) {
                    Class<?> colTypeArg = (Class<?>) getCollectionType(field)[0];
                    node.addChild(new NodeCollection(fieldName, fieldInst, colTypeArg));
                } else if (Map.class.isAssignableFrom(cl)) {
                    Class<?> valueTypeArg = (Class<?>) getCollectionType(field)[1];
                    node.addChild(new NodeMap(fieldName, fieldInst, valueTypeArg));
                } else {
                    Node newNode = new Node(fieldName);
                    node.addChild(newNode);
                    parseObject(fieldInst, newNode);
                }
            }
        } catch (IllegalAccessException e) {
            throw new JsonException(e.getMessage());
        }
        return node;
    }

    private Type[] getCollectionType(Field field) {
        ParameterizedType colType = (ParameterizedType) field.getGenericType();
        return colType.getActualTypeArguments();
    }
}
