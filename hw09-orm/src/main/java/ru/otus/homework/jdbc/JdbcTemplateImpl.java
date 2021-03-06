package ru.otus.homework.jdbc;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import ru.otus.homework.api.service.DbServiceException;
import ru.otus.homework.jdbc.sessionmanager.SessionManagerJdbc;
import ru.otus.homework.jdbc.reflection.ReflectionHelper;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JdbcTemplateImpl<T> implements JdbcTemplate<T> {
    private SessionManagerJdbc sessionManager;
    private ReflectionHelper reflectionHelper = new ReflectionHelper();

    public JdbcTemplateImpl(SessionManagerJdbc sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public long create(T objectData) {
        Map<String, Object> valuesForFields = reflectionHelper.getFieldsWithValues(objectData);
        String idField = reflectionHelper.getIdFieldName(objectData);
        valuesForFields.remove(idField);
        try {
            return saveObject(getConnection(), objectData.getClass().getSimpleName(), valuesForFields);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    @Override
    public void update(T objectData) {
        Map<String, Object> valuesForFields = reflectionHelper.getFieldsWithValues(objectData);
        String idField = reflectionHelper.getIdFieldName(objectData);
        Object idValue = valuesForFields.get(idField);
        valuesForFields.remove(idField);
        try {
            updateObject(getConnection(), objectData.getClass(), valuesForFields, Pair.of(idField, idValue));
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    @Override
    public long createOrUpdate(T objectData) {
        Map<String, Object> valuesForFields = reflectionHelper.getFieldsWithValues(objectData);
        String idField = reflectionHelper.getIdFieldName(objectData);
        try {
            return mergeObject(getConnection(), objectData.getClass().getSimpleName(),
                    idField, new ArrayList(valuesForFields.values()));
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    @Override
    public <T> T load(long id, Class<T> clazz) {
        String idField = reflectionHelper.getIdFieldByClass(clazz);
        Field[] fields = reflectionHelper.getFields(clazz);
        try {
            return (T) getObjectById(getConnection(), clazz, Pair.of(idField, id), fields);
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    private long saveObject(Connection connection, String tableName, Map<String, Object> params) throws SQLException {
        Savepoint savePoint = connection.setSavepoint("savePoint");
        String insertSql = insertSql(tableName, new LinkedList<>(params.keySet()));

        try (PreparedStatement pst = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            List<String> values = new ArrayList(params.values());
            for (int idx = 0; idx < values.size(); idx++) {
                pst.setObject(idx + 1, values.get(idx));
            }
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            connection.rollback(savePoint);
            throw new DbServiceException(ex);
        }
    }

    private T getObjectById(Connection connection, Class clazz, Pair<String, Long> idPair,
                            Field[] fields) throws SQLException {
        String selectSql = selectByIdSql(clazz.getSimpleName(), idPair.getLeft());

        try (PreparedStatement pst = connection.prepareStatement(selectSql)) {
            pst.setLong(1, idPair.getRight());
            try (ResultSet rs = pst.executeQuery()) {
                T objectInstance = (T) reflectionHelper.getClassInstance(clazz);
                if (rs.next()) {
                    for (Field field : fields) {
                        Object paramValue = rs.getObject(field.getName(), field.getType());
                        reflectionHelper.setField(objectInstance, field, paramValue);
                    }
                    return objectInstance;
                }
                return null;
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

    private long mergeObject(Connection connection, String tableName, String idKey, List<Object> params) throws SQLException {
        Savepoint savePoint = connection.setSavepoint("savePoint");
        String mergeSql = mergeSql(tableName, idKey, params.size());

        try (PreparedStatement pst = connection.prepareStatement(mergeSql)) {
            for (int idx = 0; idx < params.size(); idx++) {
                pst.setObject(idx + 1, params.get(idx));
            }
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            connection.rollback(savePoint);
            throw new DbServiceException(ex);
        }
        return (long) params.get(0);
    }

    private Connection getConnection() {
        return sessionManager.getCurrentSession().getConnection();
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

    private String mergeSql(String tableName, String key, int paramsNum) {
        StringBuilder sql = new StringBuilder("merge into ");
        sql.append(tableName);
        sql.append(" key (");
        sql.append(key);
        sql.append(") values (");
        sql.append(IntStream.range(0, paramsNum).mapToObj(i -> "?").collect(Collectors.joining(",")));
        sql.append(")");
        return sql.toString();
    }
}
