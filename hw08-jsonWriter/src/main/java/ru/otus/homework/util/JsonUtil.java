package ru.otus.homework.util;

import org.apache.commons.lang3.tuple.Pair;

public class JsonUtil {
    public static final Pair<String, String> SQUARE = Pair.of("[", "]");
    public static final Pair<String, String> BRACES = Pair.of("{", "}");
    public static final Pair<String, String> QUOTES = Pair.of("\"", "\"");
    public static final String COLON = ":";
    public static final String COMMA = ",";

    public static String encloseWithCheck(Object param, Pair<String, String> pair) {
        if (param.getClass().equals(String.class) || param.getClass().equals(Character.class)) {
            return enclose(param, pair);
        }
        return param.toString();
    }

    public static String enclose(Object param, Pair<String, String> pair) {
        return pair.getLeft() + param + pair.getRight();
    }

    private JsonUtil() {
    }
}
