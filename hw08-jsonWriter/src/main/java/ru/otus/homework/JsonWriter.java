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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.otus.homework.util.JsonUtil.checkSimple;

public class JsonWriter {

    public String toJson(Object object) {
        if (object == null) {
            return "null";
        }
        Node node = parseObject(object, new Node());
        return node.nodeToJson(new StringBuilder("{"));
    }

    private Node parseObject(Object object, Node node) {
        if (checkSimple(object.getClass())) {
            return new NodeLeaf(null, object);
        }
        Class objClass = object.getClass();
        if (objClass.isArray()) {
            return getArrayNode(objClass.getComponentType(), null, object);
        } else if (Collection.class.isAssignableFrom(objClass)) {
            Iterator iterator = ((Collection) object).iterator();
            if (iterator.hasNext()) {
                return getCollectionNode(iterator.next().getClass(), null, object);
            }
            return new NodeCollection(null, object);
        } else if (Map.class.isAssignableFrom(objClass)) {
            Iterator iterator = ((Map<Object, Object>) object).values().iterator();
            if (iterator.hasNext()) {
                return getMapNode(iterator.next().getClass(), null, object);
            }
            return new Node();
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
                    node.addChild(getArrayNode(field.getType().getComponentType(), fieldName, fieldInst));
                } else if (Collection.class.isAssignableFrom(cl)) {
                    Class<?> colTypeArg = (Class<?>) getCollectionType(field)[0];
                    node.addChild(getCollectionNode(colTypeArg, fieldName, fieldInst));
                } else if (Map.class.isAssignableFrom(cl)) {
                    Class<?> valueTypeArg = (Class<?>) getCollectionType(field)[1];
                    node.addChild(getMapNode(valueTypeArg, fieldName, fieldInst));
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

    private Node getArrayNode(Class cl, String fieldName, Object instance) {
        if (checkSimple(cl)) {
            return new NodeArray(fieldName, instance);
        }
        return new NodeArray(fieldName, Arrays.stream((Object[]) instance).map(
                element -> parseObject(element, new Node())).toArray());
    }

    private Node getCollectionNode(Class cl, String fieldName, Object instance) {
        if (checkSimple(cl)) {
            return new NodeCollection(fieldName, instance);
        }
        return new NodeCollection(fieldName, ((Collection) instance).stream().map(
                element -> parseObject(element, new Node())).collect(Collectors.toList()));
    }

    private Node getMapNode(Class cl, String fieldName, Object instance) {
        if (checkSimple(cl)) {
            return new NodeMap(fieldName, instance);
        }
        Map<Object, Object> map = ((Map<Object, Object>) instance).entrySet().stream().collect(Collectors.
                toMap(Map.Entry::getKey, element -> parseObject(element.getValue(), new Node())));
        return new NodeMap(fieldName, map);
    }

    private Type[] getCollectionType(Field field) {
        ParameterizedType colType = (ParameterizedType) field.getGenericType();
        return colType.getActualTypeArguments();
    }
}
