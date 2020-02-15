package ru.otus.homework.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

public class JsonUtil {
    public static final Pair<String, String> SQUARE = Pair.of("[", "]");
    public static final Pair<String, String> BRACES = Pair.of("{", "}");
    public static final Pair<String, String> QUOTES = Pair.of("\"", "\"");
    public static final String COLON = ":";
    public static final String COMMA = ",";

    private static final Set<Class> objectsSet = Set.of(Number.class, String.class, Integer.class, Long.class,
            Double.class, Float.class, Boolean.class, Byte.class, Character.class, Short.class);

    public static String encloseWithCheck(Object param, Pair<String, String> pair) {
        if (param.getClass().equals(String.class) || param.getClass().equals(Character.class)) {
            return enclose(param, pair);
        }
        return param.toString();
    }

    public static String enclose(Object param, Pair<String, String> pair) {
        return pair.getLeft() + param + pair.getRight();
    }

    public static boolean checkSimple(Class cl) {
        return cl.isPrimitive() || objectsSet.contains(cl);
    }

    private JsonUtil() {
    }
}
