package ru.otus.homework.node;

import java.util.List;
import java.util.stream.Collectors;

import static ru.otus.homework.util.JsonUtil.BRACES;
import static ru.otus.homework.util.JsonUtil.COLON;
import static ru.otus.homework.util.JsonUtil.COMMA;
import static ru.otus.homework.util.JsonUtil.QUOTES;
import static ru.otus.homework.util.JsonUtil.SQUARE;
import static ru.otus.homework.util.JsonUtil.encloseWithCheck;

public class NodeArray extends Node {
    private List<Object> list;

    public NodeArray(String name, List<Object> list) {
        super(name);
        this.list = list;
    }

    @Override
    public String toString() {
        return encloseWithCheck(name, QUOTES) + COLON + listToString();
    }

    private String listToString() {
        String result = list.stream().map(element -> {
            if (element.getClass().isAssignableFrom(Node.class)) {
                String s = ((Node) element).getChildren().stream().map(Node::toString).collect(Collectors.joining(COMMA));
                return encloseWithCheck(s, BRACES);
            }
            return encloseWithCheck(element, QUOTES);
        }).collect(Collectors.joining(COMMA));
        return encloseWithCheck(result, SQUARE);
    }
}
