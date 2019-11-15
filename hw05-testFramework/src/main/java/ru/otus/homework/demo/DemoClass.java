package ru.otus.homework.demo;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/*
 * Class with methods which will be tested
 */
public class DemoClass {
    private List<String> catalog;

    public DemoClass() {
    }

    public DemoClass(List<String> catalog) {
        this.catalog = catalog;
    }

    public boolean addItem(String item) {
        if (catalog == null) {
            catalog = new ArrayList<>();
        }
        if (catalog.contains(item)) {
            throw new RuntimeException("Item already exists");
        }
        return catalog.add(item);
    }

    public boolean removeItem(String item) {
        if (CollectionUtils.isEmpty(catalog)) {
            throw new RuntimeException("Catalog is empty");
        }
        if (!catalog.contains(item)) {
            throw new RuntimeException("Item does not exists");
        }
        return catalog.remove(item);
    }

    @Override
    public String toString() {
        return "DemoClass{" +
                "catalog=" + catalog +
                '}';
    }
}
