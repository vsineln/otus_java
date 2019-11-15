package ru.otus.homework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Annotation for methods which should test some functionality. Expected field can hold
 * an exception type which can be thrown by test method
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    Class<? extends Throwable> expected() default None.class;

    class None extends Throwable {
    }
}
