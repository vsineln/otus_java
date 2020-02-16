package ru.otus.homework.node;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class NodeCollection extends NodeArray {

    public NodeCollection(String name, Object instance) {
        super(name, instance);
    }

    @Override
    protected List<Object> getNodeValues() {
        return new LinkedList<>(((Collection) instance));
    }
}
