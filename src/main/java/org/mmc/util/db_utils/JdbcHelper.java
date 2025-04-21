package org.mmc.util.db_utils;

import java.lang.reflect.Field;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * 数据库访问帮助类，提供了一系列数据库操作的方法，包括查询、增删改、调用存储过程等。
 * 采用单例模式确保在整个应用程序中只有一个实例。
 *
 * @author administrator
 */
public class JdbcHelper {

    /**
     * 数据库连接地址
     */
    private String url;

    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;

    private Connection connection;

    private JdbcHelper() {
    }

    // 双重检查锁定实现线程安全单例
    private static volatile JdbcHelper instance;

    /**
     * 获取 JdbcHelper 的单例实例。
     * @return JdbcHelper 的单例实例
     */
    public static JdbcHelper getInstance() {
        if (instance == null) {
            synchronized (JdbcHelper.class) {
                if (instance == null) {
                    instance = new JdbcHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 用于查询，返回结果集。
     * 使用 try-with-resources 自动关闭 PreparedStatement 和 ResultSet 资源。
     * @param conn 数据库连接
     * @param sql sql 语句
     * @return 结果集，以集合形式返回
     * @throws SQLException 数据库操作异常
     */
    public List<Map<String, Object>> query(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            return resultToListMap(rs);
        }
    }

    /**
     * 用于带参数的查询，返回结果集。
     * @param conn 数据库连接
     * @param sql sql 语句
     * @param paramters 参数集合
     * @return 结果集，以 集合 形式返回
     * @throws SQLException 数据库操作异常
     */
    @SuppressWarnings("rawtypes")
    public List<Map<String, Object>> query(Connection conn, String sql, Object... paramters)
            throws SQLException {
        ResultSet rs = null;
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);) {
            for (int i = 0; i < paramters.length; i++) {
                preparedStatement.setObject(i + 1, paramters[i]);
            }
            rs = preparedStatement.executeQuery();
            return resultToListMap(rs);
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * 返回单个结果的值，如 count\min\max 等等。
     * @param conn 数据库连接
     * @param sql sql 语句
     * @return 单个结果的值
     * @throws SQLException 数据库操作异常
     */
    public Object getSingle(Connection conn, String sql) throws SQLException {
        Object result = null;
        ResultSet rs = null;
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);) {
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                result = rs.getObject(1);
            }
            return result;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * 返回单个结果值，如 count\min\max 等。
     * @param conn 数据库连接
     * @param sql sql 语句
     * @param paramters 参数列表
     * @return 单个结果的值
     * @throws SQLException 数据库操作异常
     */
    public Object getSingle(Connection conn, String sql, Object... paramters)
            throws SQLException {
        Object result = null;
        ResultSet rs = null;
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (int i = 0; i < paramters.length; i++) {
                preparedStatement.setObject(i + 1, paramters[i]);
            }
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                result = rs.getObject(1);
            }
            return result;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * 用于增删改操作。
     * @param conn 数据库连接
     * @param sql sql 语句
     * @return 影响行数
     * @throws SQLException 数据库操作异常
     */
    public int update(Connection conn, String sql) throws SQLException {
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);) {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * 用于增删改（带参数）操作。
     * @param conn 数据库连接
     * @param sql sql 语句
     * @param paramters sql 语句的参数
     * @return 影响行数
     * @throws SQLException 数据库操作异常
     */
    public int update(Connection conn, String sql, Object... paramters)
            throws SQLException {
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);) {
            for (int i = 0; i < paramters.length; i++) {
                preparedStatement.setObject(i + 1, paramters[i]);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * 调用存储过程执行查询。
     * @param conn 数据库连接
     * @param procedureSql 存储过程
     * @return 结果集，以集合形式返回
     * @throws SQLException 数据库操作异常
     */
    @SuppressWarnings("rawtypes")
    public List<Map<String, Object>> callableQuery(Connection conn, String procedureSql) throws SQLException {
        ResultSet rs = null;
        try (CallableStatement callableStatement = conn.prepareCall(procedureSql);) {
            rs = callableStatement.executeQuery();
            return resultToListMap(rs);
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * 调用存储过程（带参数），执行查询。
     * @param procedureSql 存储过程
     * @param paramters 参数表
     * @return 结果集，以集合形式返回
     * @throws SQLException 数据库操作异常
     */
    @SuppressWarnings("rawtypes")
    public List<Map<String, Object>> callableQuery(String procedureSql, Object... paramters)
            throws SQLException {
        ResultSet rs = null;
        try (CallableStatement callableStatement = connection.prepareCall(procedureSql);) {
            for (int i = 0; i < paramters.length; i++) {
                callableStatement.setObject(i + 1, paramters[i]);
            }
            rs = callableStatement.executeQuery();
            return resultToListMap(rs);
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * 调用存储过程，查询单个值。
     * @param conn 数据库连接
     * @param procedureSql 存储过程
     * @return 单个结果的值
     * @throws SQLException 数据库操作异常
     */
    public Object callableGetSingle(Connection conn, String procedureSql)
            throws SQLException {
        Object result = null;
        ResultSet rs = null;
        try (CallableStatement callableStatement = conn.prepareCall(procedureSql);) {
            rs = callableStatement.executeQuery();
            while (rs.next()) {
                result = rs.getObject(1);
            }
            return result;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * 调用存储过程(带参数)，查询单个值。
     * @param conn 数据库连接
     * @param procedureSql 存储过程
     * @param paramters 参数列表
     * @return 单个结果的值
     * @throws SQLException 数据库操作异常
     */
    public Object callableGetSingle(Connection conn, String procedureSql,
                                    Object... paramters) throws SQLException {
        Object result = null;
        ResultSet rs = null;
        try (CallableStatement callableStatement = conn.prepareCall(procedureSql);) {
            for (int i = 0; i < paramters.length; i++) {
                callableStatement.setObject(i + 1, paramters[i]);
            }
            rs = callableStatement.executeQuery();
            while (rs.next()) {
                result = rs.getObject(1);
            }
            return result;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * 调用带参数的存储过程。
     * @param conn 数据库连接
     * @param procedureSql 存储过程
     * @return 存储过程的输出参数值
     * @throws SQLException 数据库操作异常
     */
    public Object callableWithParamters(Connection conn, String procedureSql)
            throws SQLException {
        try (CallableStatement callableStatement = conn.prepareCall(procedureSql);) {
            callableStatement.registerOutParameter(0, Types.OTHER);
            callableStatement.execute();
            return callableStatement.getObject(0);
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * 调用存储过程，执行增删改操作。
     * @param conn 数据库连接
     * @param procedureSql 存储过程
     * @return 影响行数
     * @throws SQLException 数据库操作异常
     */
    public int callableUpdate(Connection conn, String procedureSql) throws SQLException {
        try (CallableStatement callableStatement = conn.prepareCall(procedureSql);) {
            return callableStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * 调用存储过程（带参数），执行增删改操作。
     * @param conn 数据库连接
     * @param procedureSql 存储过程
     * @param parameters 参数列表
     * @return 影响行数
     * @throws SQLException 数据库操作异常
     */
    public int callableUpdate(Connection conn, String procedureSql, Object... parameters)
            throws SQLException {
        try (CallableStatement callableStatement = conn.prepareCall(procedureSql);) {
            for (int i = 0; i < parameters.length; i++) {
                callableStatement.setObject(i + 1, parameters[i]);
            }
            return callableStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    private static String driver;

    /**
     * 将 ResultSet 转换为 List<Map<String, Object>> 形式的结果集。
     * @param rs ResultSet 对象
     * @return 结果集，以集合形式返回
     * @throws SQLException 数据库操作异常
     */
    private List<Map<String, Object>> resultToListMap(ResultSet rs) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();

        while (rs.next()) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                map.put(md.getColumnLabel(i), rs.getObject(i));
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 插入值后返回主键值。
     * @param conn 数据库连接
     * @param sql 插入 sql 语句
     * @return 返回结果，主键值
     * @throws SQLException 数据库操作异常
     */
    public Object insertWithReturnPrimeKey(Connection conn, String sql)
            throws SQLException {
        ResultSet rs = null;
        Object result = null;
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.execute();
            rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                result = rs.getObject(1);
            }
            return result;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * 插入值后返回主键值，带参数。
     * @param conn 数据库连接
     * @param sql 插入 sql 语句
     * @param paramters 参数列表
     * @return 返回结果，主键值
     * @throws SQLException 数据库操作异常
     */
    public Object insertWithReturnPrimeKey(Connection conn, String sql,
                                           Object... paramters) throws SQLException {
        ResultSet rs = null;
        Object result = null;
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);) {
            for (int i = 0; i < paramters.length; i++) {
                preparedStatement.setObject(i + 1, paramters[i]);
            }
            preparedStatement.execute();
            rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                result = rs.getObject(1);
            }
            return result;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * 批量更新数据。
     * @param conn 数据库连接
     * @param sqlList 一组 sql 语句
     * @return 每个 sql 语句影响的行数数组
     * @throws SQLException 数据库操作异常
     */
    public int[] batchUpdate(Connection conn, List<String> sqlList) throws SQLException {
        try {
            conn.setAutoCommit(false);
            try (Statement statement = conn.createStatement()) {
                for (String sql : sqlList) {
                    statement.addBatch(sql);
                    // 分段提交，避免内存问题
                    if (sqlList.indexOf(sql) % 1000 == 0) {
                        statement.executeBatch();
                    }
                }
                int[] result = statement.executeBatch();
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * 获取 PreparedStatement
     */
    public static <T> List<T> executeQuery(Connection conn, String sql, JdbcMyBatisUtil.RowMapper<T> rowMapper) {
        List<T> result = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                T entity = rowMapper.mapRow(resultSet);
                result.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 可以根据实际情况处理异常
        }
        return result;
    }

    public interface RowMapper<T> {
        T mapRow(ResultSet resultSet) throws SQLException;
    }

    /**
     * 检测数据库类型
     */
    public DatabaseType detectDatabaseType(Connection conn) throws SQLException {
        String productName = conn.getMetaData().getDatabaseProductName().toLowerCase();
        // 方法1：通过元数据
        if (productName.contains("mysql")) {
            return DatabaseType.MYSQL;
        } else if (productName.contains("dm")) {
            return DatabaseType.DM;
        }
//        // 方法2：通过URL
//        String url = conn.getMetaData().getURL().toLowerCase();
//        if (url.contains(":dm:")) {
//            return DatabaseType.DM;
//        } else if (url.contains(":mysql:")) {
//            return DatabaseType.MYSQL;
//        }
//
//        // 方法3：通过特定SQL
//        try (java.sql.Statement stmt = conn.createStatement()) {
//            stmt.executeQuery("SELECT DM_SESSION_CONTEXT('CLIENT_VERSION')");
//            return DatabaseType.DM;
//        } catch (SQLException e) {
//            return DatabaseType.MYSQL;
//        }
        return DatabaseType.UNKNOWN;
    }
}