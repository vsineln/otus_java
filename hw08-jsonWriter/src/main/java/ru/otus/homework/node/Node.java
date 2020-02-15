package ru.otus.homework.node;

import org.apache.commons.collections4.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.otus.homework.util.JsonUtil.BRACES;
import static ru.otus.homework.util.JsonUtil.COLON;
import static ru.otus.homework.util.JsonUtil.COMMA;
import static ru.otus.homework.util.JsonUtil.QUOTES;
import static ru.otus.homework.util.JsonUtil.enclose;
import static ru.otus.homework.util.JsonUtil.encloseWithCheck;

public class Node {
    private List<Node> children = new LinkedList<>();
    protected Object name;

    public Node() {
    }

    public Node(String name) {
        this.name = name;
    }

    public Object getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public String nodeToJson(StringBuilder sb) {
        if (getName() != null) {
            sb.append(encloseWithCheck(getName(), QUOTES));
            sb.append(COLON);
            sb.append("{");
        }
        if (CollectionUtils.isNotEmpty(getChildren())) {
            List<Node> nodes = getChildren();
            sb.append(nodes.stream().map(node1 -> {
                if (CollectionUtils.isNotEmpty(node1.getChildren())) {
                    return node1.nodeToJson(new StringBuilder());
                }
                return node1.toString();
            }).collect(Collectors.joining(COMMA)));
        } else {
            return toString();
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getName() != null) {
            sb.append(encloseWithCheck(name, QUOTES));
            sb.append(COLON);
            sb.append("{");
            sb.append(children.stream().map(Node::toString).collect(Collectors.joining(COMMA)));
            sb.append("}");
            return sb.toString();
        } else {
            return enclose(sb.toString(), BRACES);
        }
    }
}
