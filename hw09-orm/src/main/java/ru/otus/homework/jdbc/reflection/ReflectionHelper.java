package ru.otus.homework.jdbc.reflection;

import ru.otus.homework.annotation.Id;
import ru.otus.homework.jdbc.JdbcException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReflectionHelper {

    public String getIdFieldName(Object objectData) {
        Class clazz = objectData.getClass();
        return getIdFieldByClass(clazz);
    }

    public String getIdFieldByClass(Class clazz) {
        return getIdField(clazz).getName();
    }

    public Field getIdField(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        return findId(fields);
    }

    public Map<String, Object> getFieldsWithValues(Object objectData) {
        Field[] fields = objectData.getClass().getDeclaredFields();
        return getValuesForFields(objectData, fields);
    }

    public Field[] getFields(Class clazz) {
        return clazz.getDeclaredFields();
    }

    public Object getClassInstance(Class clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new JdbcException(e);
        }
    }

    public void setField(Object objectInstance, Field field, Object fieldValue) {
        try {
            field.setAccessible(true);
            field.set(objectInstance, fieldValue);
        } catch (IllegalAccessException e) {
            throw new JdbcException(e);
        }
    }

    public void setIdField(Object objectInstance, Object fieldValue) {
        try {
            Field idField = getIdField(objectInstance.getClass());
            idField.setAccessible(true);
            idField.set(objectInstance, fieldValue);
        } catch (IllegalAccessException e) {
            throw new JdbcException(e);
        }
    }

    public String getIdFieldValue(Object object) {
        try {
            Field idField = getIdField(object.getClass());
            idField.setAccessible(true);
            return idField.get(object).toString();
        } catch (IllegalAccessException e) {
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
