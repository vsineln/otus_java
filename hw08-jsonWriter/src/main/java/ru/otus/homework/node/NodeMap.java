package ru.otus.homework.node;

import org.apache.commons.lang3.tuple.Pair;
import ru.otus.homework.JsonWriter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.otus.homework.util.JsonUtil.BRACES;
import static ru.otus.homework.util.JsonUtil.COLON;
import static ru.otus.homework.util.JsonUtil.COMMA;
import static ru.otus.homework.util.JsonUtil.QUOTES;
import static ru.otus.homework.util.JsonUtil.checkSimple;
import static ru.otus.homework.util.JsonUtil.encloseWithCheck;
import static ru.otus.homework.util.JsonUtil.enclose;

public class NodeMap extends Node {
    private Object instance;
    private Class cl;

    public NodeMap(String name, Object instance, Class cl) {
        super(name);
        this.instance = instance;
        this.cl = cl;
    }

    public NodeMap(Object instance) {
        this.instance = instance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (name != null) {
            sb.append(enclose(name, QUOTES));
            sb.append(COLON);
        }
        sb.append(listToString());
        return sb.toString();
    }

    private String listToString() {
        String result = getMapValues().stream().map(element -> {
            if (element.getRight().getClass().isAssignableFrom(Node.class)) {
                String right = ((Node) element.getRight()).getChildren().stream().map(node -> node.nodeToJson(new StringBuilder())).collect(Collectors.joining(COMMA));
                return enclose(element.getLeft(), QUOTES) + COLON + enclose(right, BRACES);
            }
            return enclose(element.getLeft(), QUOTES) + COLON + encloseWithCheck(element.getRight(), QUOTES);
        }).collect(Collectors.joining(COMMA));
        return enclose(result, BRACES);
    }

    private List<Pair<Object, Object>> getMapValues() {
        List<Pair<Object, Object>> list = new LinkedList<>();
        Iterator iterator = ((Map) instance).entrySet().iterator();
        if (cl == null || checkSimple(cl)) {
            while (iterator.hasNext()) {
                Map.Entry<Object, Object> pair = (Map.Entry<Object, Object>) iterator.next();
                Object o = pair.getValue();
                if (checkSimple(o.getClass())) {
                    list.add(Pair.of(pair.getKey(), o));
                } else {
                    list.add(Pair.of(pair.getKey(), new JsonWriter().parseObject(o, new Node())));
                }
            }
        } else {
            while (iterator.hasNext()) {
                Map.Entry<Object, Object> pair = (Map.Entry<Object, Object>) iterator.next();
                list.add(Pair.of(pair.getKey(), new JsonWriter().parseObject(pair.getValue(), new Node())));
            }
        }
        return list;
    }
}
