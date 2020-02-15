package ru.otus.homework.node;

import ru.otus.homework.JsonWriter;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.otus.homework.util.JsonUtil.BRACES;
import static ru.otus.homework.util.JsonUtil.COLON;
import static ru.otus.homework.util.JsonUtil.COMMA;
import static ru.otus.homework.util.JsonUtil.QUOTES;
import static ru.otus.homework.util.JsonUtil.SQUARE;
import static ru.otus.homework.util.JsonUtil.checkSimple;
import static ru.otus.homework.util.JsonUtil.enclose;
import static ru.otus.homework.util.JsonUtil.encloseWithCheck;

public class NodeArray extends Node {
    protected Object instance;
    protected Class cl;

    public NodeArray(String name, Object instance, Class cl) {
        super(name);
        this.instance = instance;
        this.cl = cl;
    }

    public NodeArray(Object instance) {
        this.instance = instance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (name != null) {
            sb.append(encloseWithCheck(name, QUOTES));
            sb.append(COLON);
        }
        sb.append(listToString());
        return sb.toString();
    }

    protected String listToString() {
        String result = getNodeValues().stream().map(element -> {
            if (element.getClass().isAssignableFrom(Node.class)) {
                String s = ((Node) element).getChildren().stream().map(node -> node.nodeToJson(new StringBuilder())).collect(Collectors.joining(COMMA));
                return enclose(s, BRACES);
            }
            return encloseWithCheck(element, QUOTES);
        }).collect(Collectors.joining(COMMA));
        return enclose(result, SQUARE);
    }

    protected List<Object> getNodeValues() {
        List<Object> list = new LinkedList<>();
        if ((cl == null || checkSimple(cl)) && checkSimple(instance.getClass().getComponentType())) {
            for (int i = 0; i < Array.getLength(instance); i++) {
                list.add(Array.get(instance, i));
            }
        } else {
            for (int i = 0; i < Array.getLength(instance); i++) {
                list.add(new JsonWriter().parseObject(Array.get(instance, i), new Node()));
            }
        }
        return list;
    }
}
