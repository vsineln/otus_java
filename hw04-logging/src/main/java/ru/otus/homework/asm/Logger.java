package ru.otus.homework.asm;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class is called by LogMethodVisitor to log method`s name and arguments
 */
public class Logger {

    public static void logMethod(String methodName, Object[] args) {
        String log = String.format("executed method %s", methodName);
        if (args.length > 0) {
            String params = Arrays.stream(args).
                    filter(Objects::nonNull).
                    map(Object::toString).collect(Collectors.joining(", "));
            log = log.concat(", params: ").concat(params);
        }
        System.out.println(log);
    }
}
