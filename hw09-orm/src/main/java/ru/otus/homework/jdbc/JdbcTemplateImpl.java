package ru.otus.homework.jdbc;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import ru.otus.homework.annotation.Id;
import ru.otus.homework.api.service.DbServiceException;
import ru.otus.homework.jdbc.sessionmanager.SessionManagerJdbc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JdbcTemplateImpl implements JdbcTemplate {
    private SessionManagerJdbc sessionManager;

    public JdbcTemplateImpl(SessionManagerJdbc sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void create(Object objectData) {
        Class clazz = objectData.getClass();
        Field[] fields = clazz.getDeclaredFields();
        String idField = findId(fields).getName();
        Map<String, Object> valuesForFields = getValuesForFields(objectData, fields);
        valuesForFields.remove(idField);
        try {
            saveObject(getConnection(), clazz.getSimpleName(), valuesForFields);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    @Override
    public void update(Object objectData) {
        Class clazz = objectData.getClass();
        Field[] fields = clazz.getDeclaredFields();
        String idField = findId(fields).getName();
        Map<String, Object> valuesForFields = getValuesForFields(objectData, fields);
        Object idValue = valuesForFields.get(idField);
        valuesForFields.remove(idField);
        try {
            updateObject(getConnection(), clazz, valuesForFields, Pair.of(idField, idValue));
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    @Override
    public void createOrUpdate(Object objectData) {
        Class cl = objectData.getClass();
        Field[] fields = cl.getDeclaredFields();
        String idField = findId(fields).getName();
        Map<String, Object> valuesForFields = getValuesForFields(objectData, fields);
        Object idValue = valuesForFields.get(idField);

        Object object = load((Long) idValue, cl);
        try {
            if (object == null) {
                valuesForFields.remove(idField);
                saveObject(getConnection(), cl.getSimpleName(), valuesForFields);
            } else if (!objectData.equals(object)) {
                updateObject(getConnection(), cl, valuesForFields, Pair.of(idField, idValue));
            }
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    @Override
    public Object load(long id, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Field idField = findId(fields);
        Map<String, Class> typesForFields = getTypesForFields(fields);
        try {
            return getObjectById(getConnection(), clazz, Pair.of(idField.getName(), id), typesForFields);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    private void saveObject(Connection connection, String tableName, Map<String, Object> params) throws SQLException {
        Savepoint savePoint = connection.setSavepoint("savePoint");
        String insertSql = insertSql(tableName, new LinkedList<>(params.keySet()));

        try (PreparedStatement pst = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            List<String> values = new ArrayList(params.values());
            for (int idx = 0; idx < values.size(); idx++) {
                pst.setObject(idx + 1, values.get(idx));
            }
            pst.executeUpdate();
        } catch (SQLException ex) {
            connection.rollback(savePoint);
            throw new DbServiceException(ex);
        }
    }

    private Object getObjectById(Connection connection, Class clazz, Pair<String, Long> idPair,
                                 Map<String, Class> typesForFields) throws SQLException {
        String selectSql = selectByIdSql(clazz.getSimpleName(), idPair.getLeft());

        try (PreparedStatement pst = connection.prepareStatement(selectSql)) {
            pst.setLong(1, idPair.getRight());
            try (ResultSet rs = pst.executeQuery()) {
                Object objectInstance = getInstance(clazz);
                if (rs.next()) {
                    for (Map.Entry<String, Class> pair : typesForFields.entrySet()) {
                        Object paramValue = rs.getObject(pair.getKey(), pair.getValue());
                        Method method = clazz.getDeclaredMethod(
                                "set" + StringUtils.capitalize(pair.getKey()), pair.getValue());
                        method.invoke(objectInstance, paramValue);
                    }
                    return objectInstance;
                }
                return null;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new JdbcException(e);
            }
        }
    }

    private void updateObject(Connection connection, Class clazz, Map<String, Object> params,
                              Pair<String, Object> idPair) throws SQLException {
        String updateSql = updateSql(clazz.getSimpleName(), new LinkedList<>(params.keySet()), idPair.getLeft());

        try (PreparedStatement pst = connection.prepareStatement(updateSql)) {
            List<String> values = new ArrayList(params.values());
            for (int idx = 0; idx < values.size(); idx++) {
                pst.setObject(idx + 1, values.get(idx));
            }
            pst.setObject(values.size() + 1, idPair.getRight());
            pst.executeUpdate();
        }
    }

    private Object getInstance(Class clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new JdbcException(e);
        }
    }

    private Connection getConnection() {
        return sessionManager.getCurrentSession().getConnection();
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

    private Map<String, Class> getTypesForFields(Field[] fields) {
        Map<String, Class> typesForValues = new LinkedHashMap<>();
        for (Field field : fields) {
            typesForValues.put(field.getName(), field.getType());
        }
        return typesForValues;
    }
    
    private String insertSql(String tableName, List<String> params) {
        StringBuilder sql = new StringBuilder("insert into ");
        sql.append(tableName);
        sql.append("(");
        sql.append(StringUtils.join(params, ","));
        sql.append(") values (");
        sql.append(IntStream.range(0, params.size()).mapToObj(i -> "?").collect(Collectors.joining(",")));
        sql.append(")");
        return sql.toString();
    }

    private String selectByIdSql(String tableName, String key) {
        StringBuilder sql = new StringBuilder("select * from ");
        sql.append(tableName);
        sql.append(" where ");
        sql.append(key);
        sql.append("  = ?");
        return sql.toString();
    }

    private String updateSql(String tableName, List<String> params, String key) {
        StringBuilder sql = new StringBuilder("update ");
        sql.append(tableName);
        sql.append(" set ");
        sql.append(StringUtils.join(params, "=?,"));
        sql.append("=? where ");
        sql.append(key);
        sql.append("=?");
        return sql.toString();
    }
}
