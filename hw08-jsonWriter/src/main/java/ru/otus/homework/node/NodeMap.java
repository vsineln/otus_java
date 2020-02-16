package ru.otus.homework.node;

import java.util.Map;
import java.util.stream.Collectors;

import static ru.otus.homework.util.JsonUtil.BRACES;
import static ru.otus.homework.util.JsonUtil.COLON;
import static ru.otus.homework.util.JsonUtil.COMMA;
import static ru.otus.homework.util.JsonUtil.QUOTES;
import static ru.otus.homework.util.JsonUtil.encloseWithCheck;
import static ru.otus.homework.util.JsonUtil.enclose;

public class NodeMap extends Node {
    private Object instance;

    public NodeMap(String name, Object instance) {
        super(name);
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
        Object result = ((Map) instance).entrySet().stream().map(element -> {
            Map.Entry<Object, Object> pair = (Map.Entry<Object, Object>) element;
            if (pair.getValue().getClass().isAssignableFrom(Node.class)) {
                String right = ((Node) pair.getValue()).getChildren().stream().map(node -> node.nodeToJson(new StringBuilder())).collect(Collectors.joining(COMMA));
                return enclose(pair.getKey(), QUOTES) + COLON + enclose(right, BRACES);
            }
            return enclose(pair.getKey(), QUOTES) + COLON + encloseWithCheck(pair.getValue(), QUOTES);
        }).collect(Collectors.joining(COMMA));
        return enclose(result, BRACES);
    }
}
