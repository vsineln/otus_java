package ru.otus.homework.node;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Collectors;

import static ru.otus.homework.util.JsonUtil.BRACES;
import static ru.otus.homework.util.JsonUtil.COLON;
import static ru.otus.homework.util.JsonUtil.COMMA;
import static ru.otus.homework.util.JsonUtil.QUOTES;
import static ru.otus.homework.util.JsonUtil.encloseWithCheck;
import static ru.otus.homework.util.JsonUtil.enclose;

public class NodeMap extends Node {
    private List<Pair<Object, Object>> list;

    public NodeMap(String name, List<Pair<Object, Object>> list) {
        super(name);
        this.list = list;
    }

    @Override
    public String toString() {
        return enclose(name, QUOTES) + COLON + listToString();
    }

    private String listToString() {
        String result = list.stream().map(element -> {
            if (element.getRight().getClass().isAssignableFrom(Node.class)) {
                String right = ((Node) element.getRight()).getChildren().stream().map(Node::toString).collect(Collectors.joining(COMMA));
                return enclose(element.getLeft(), QUOTES) + COLON + enclose(right, BRACES);
            }
            return enclose(element.getLeft(), QUOTES) + COLON + encloseWithCheck(element.getRight(), QUOTES);
        }).collect(Collectors.joining(COMMA));
        return encloseWithCheck(result, BRACES);
    }
}
