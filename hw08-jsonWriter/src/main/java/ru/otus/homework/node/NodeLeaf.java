package ru.otus.homework.node;

import static ru.otus.homework.util.JsonUtil.COLON;
import static ru.otus.homework.util.JsonUtil.QUOTES;
import static ru.otus.homework.util.JsonUtil.encloseWithCheck;

public class NodeLeaf extends Node {
    private Object key;
    private Object value;

    public NodeLeaf(Object key, Object value) {
        super(null);
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return encloseWithCheck(key, QUOTES) + COLON + encloseWithCheck(value, QUOTES);
    }
}
