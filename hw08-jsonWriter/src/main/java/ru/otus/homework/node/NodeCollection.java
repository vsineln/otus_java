package ru.otus.homework.node;

import ru.otus.homework.JsonWriter;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static ru.otus.homework.util.JsonUtil.checkSimple;

public class NodeCollection extends NodeArray {

    public NodeCollection(String name, Object instance, Class cl) {
        super(name, instance, cl);
    }

    public NodeCollection(Object instance) {
        super(instance);
    }

    @Override
    protected List<Object> getNodeValues() {
        List<Object> list = new LinkedList<>();
        Iterator iterator = ((Collection) instance).iterator();
        if (cl == null || checkSimple(cl)) {
            while (iterator.hasNext()) {
                addElement(list, iterator.next());
            }
        } else {
            while (iterator.hasNext()) {
                list.add(new JsonWriter().parseObject(iterator.next(), new Node()));
            }
        }
        return list;
    }

    private void addElement(List<Object> list, Object o) {
        if (checkSimple(o.getClass())) {
            list.add(o);
        } else {
            list.add(new JsonWriter().parseObject(o, new Node()));
        }
    }
}
