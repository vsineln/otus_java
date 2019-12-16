package ru.otus.homework.jdbc.reflection;

import ru.otus.homework.annotation.Id;
import ru.otus.homework.jdbc.JdbcException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReflectionHelper {

    public String getIdFieldName(Object objectData) {
        Class clazz = objectData.getClass();
        return getIdFieldByClass(clazz);
    }

    public String getIdFieldByClass(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        return findId(fields).getName();
    }

    public Map<String, Object> getFieldsWithValues(Object objectData) {
        Field[] fields = objectData.getClass().getDeclaredFields();
        return getValuesForFields(objectData, fields);
    }

    public Map<String, Class> getTypesForFields(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Class> typesForValues = new LinkedHashMap<>();
        for (Field field : fields) {
            typesForValues.put(field.getName(), field.getType());
        }
        return typesForValues;
    }

    public Object getClassInstance(Class clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new JdbcException(e);
        }
    }

    public void invokeMethod(Object objectInstance, Class clazz, String methodName, Object paramValue, Class paramType) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, paramType);
            method.invoke(objectInstance, paramValue);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new JdbcException(e);
        }
    }

    private Field findId(Field[] fields) {
        for (Field field : fields) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (Id.class.equals(annotation.annotationType())) {
                    return field;
                }
            }
        }
        throw new IllegalArgumentException("Field for @Id is not found");
    }

    private Map<String, Object> getValuesForFields(Object object, Field[] fields) {
        Map<String, Object> fieldsMap = new LinkedHashMap<>();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                fieldsMap.put(field.getName(), field.get(object));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
        return fieldsMap;
    }
}
