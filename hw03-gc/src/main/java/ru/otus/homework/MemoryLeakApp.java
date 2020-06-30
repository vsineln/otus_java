package ru.otus.homework;

import java.util.ArrayList;
import java.util.List;

public class MemoryLeakApp {
    private static final List<String> TEMP = new ArrayList<>();
    private static final int ITERATION = 100_000;
    private final List<String> init = new ArrayList<>();

    public MemoryLeakApp(List<String> in) {
        init.addAll(in);
    }

    public void run() {
        for (int i = 0; i < ITERATION; i++) {
            TEMP.addAll(init);
            for (int j = 0; j < ITERATION / 2; j++) {
                TEMP.add("empty" + i);
            }
            TEMP.removeAll(init);
        }
    }
}
