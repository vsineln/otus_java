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
        StringBuilder sb = new StringBuilder();
        if (key != null) {
            sb.append(encloseWithCheck(key, QUOTES));
            sb.append(COLON);
        }
        sb.append(encloseWithCheck(value, QUOTES));
        return sb.toString();
    }
}
