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
     *
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
     *
     * @param conn 数据库连接
     * @param sql  sql 语句
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
     *
     * @param conn      数据库连接
     * @param sql       sql 语句
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
     *
     * @param conn 数据库连接
     * @param sql  sql 语句
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
     *
     * @param conn      数据库连接
     * @param sql       sql 语句
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
     *
     * @param conn 数据库连接
     * @param sql  sql 语句
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
     *
     * @param conn      数据库连接
     * @param sql       sql 语句
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
     *
     * @param conn         数据库连接
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
     *
     * @param procedureSql 存储过程
     * @param paramters    参数表
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
     *
     * @param conn         数据库连接
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
     *
     * @param conn         数据库连接
     * @param procedureSql 存储过程
     * @param paramters    参数列表
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
     *
     * @param conn         数据库连接
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
     *
     * @param conn         数据库连接
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
     *
     * @param conn         数据库连接
     * @param procedureSql 存储过程
     * @param parameters   参数列表
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
     *
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
     *
     * @param conn 数据库连接
     * @param sql  插入 sql 语句
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
     *
     * @param conn      数据库连接
     * @param sql       插入 sql 语句
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
     *
     * @param conn    数据库连接
     * @param sqlList 一组 sql 语句
     * @return 每个 sql 语句影响的行数数组
     * @throws SQLException 数据库操作异常
     */
    /**
     * 批量执行SQL语句（每1000条自动提交一次）
     *
     * @param conn    数据库连接（不能为null）
     * @param sqlList SQL语句列表（不能为null）
     * @return 每个SQL的执行结果（受影响行数）
     * @throws SQLException             如果数据库操作失败
     * @throws IllegalArgumentException 如果参数无效
     */
    public int[] batch(Connection conn, List<String> sqlList) throws SQLException {
        // 参数校验
        if (conn == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        if (sqlList == null) {
            throw new IllegalArgumentException("SQL list cannot be null");
        }
        if (sqlList.isEmpty()) {
            return new int[0];
        }

        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            try (Statement statement = conn.createStatement()) {
                List<Integer> allResults = new ArrayList<>(sqlList.size());
                final int batchSize = 1000;

                for (int i = 0; i < sqlList.size(); i++) {
                    String sql = sqlList.get(i);
                    if (sql == null || sql.trim().isEmpty()) {
                        allResults.add(0); // 空SQL视为不影响行
                        continue;
                    }

                    statement.addBatch(sql);

                    // 每1000条执行一次（避免内存问题）
                    if ((i + 1) % batchSize == 0) {
                        int[] batchResult = statement.executeBatch();
                        for (int r : batchResult) {
                            allResults.add(r);
                        }
                        statement.clearBatch();
                    }
                }

                // 执行剩余的SQL
                int[] remainingResults = statement.executeBatch();
                for (int r : remainingResults) {
                    allResults.add(r);
                }

                conn.commit();
                return allResults.stream().mapToInt(Integer::intValue).toArray();
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                e.addSuppressed(ex); // 回滚异常附加到主异常
            }
            throw e;
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                // 记录错误，但不应影响主逻辑
                System.err.println("Failed to restore auto-commit: " + e.getMessage());
            }
        }
    }


    /**
     * 批量执行SQL语句
     *
     * @param conn      数据库连接(不能为null)
     * @param sqlList   要执行的SQL语句列表(可为空)
     * @param batchSize 批处理大小(必须大于0)
     * @return 每个SQL语句的执行结果数组(受影响行数)
     * @throws SQLException             如果发生数据库错误
     * @throws IllegalArgumentException 如果参数无效
     */
    public int[] batchExecuteSql(Connection conn, List<String> sqlList, int batchSize) throws SQLException {
        if (conn == null) {
            throw new SQLException("Connection cannot be null");
        }
        if (conn.isClosed()) {
            throw new SQLException("Connection is closed");
        }
        if (sqlList == null || sqlList.isEmpty()) {
            return new int[0];
        }
        final int MIN_BATCH_SIZE = 1;
        if (batchSize < MIN_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch size must be at least " + MIN_BATCH_SIZE);
        }

        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            try (Statement statement = conn.createStatement()) {
                List<Integer> allResults = new ArrayList<>(sqlList.size());
                int count = 0;

                for (String sql : sqlList) {
                    if (sql == null || sql.trim().isEmpty()) {
                        allResults.add(0); // 对于空SQL，添加0表示没有执行
                        continue;
                    }

                    statement.addBatch(sql);
                    count++;

                    if (count % batchSize == 0) {
                        addBatchResults(allResults, statement.executeBatch());
                        statement.clearBatch();
                    }
                }

                // 执行剩余的批处理
                int[] remainingResults = statement.executeBatch();
                if (remainingResults.length > 0) {
                    addBatchResults(allResults, remainingResults);
                }

                conn.commit();
                return allResults.stream().mapToInt(i -> i).toArray();
            }
        } catch (SQLException e) {
            try {
                e.printStackTrace();
                System.out.println("Batch execution failed, attempting rollback");
                conn.rollback();
            } catch (SQLException ex) {
                e.addSuppressed(ex);
            }
            throw e;
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to restore auto-commit mode");
            }
        }
    }

    /**
     * 添加批处理结果
     *
     * @param results     结果列表
     * @param batchResult 批处理结果
     */
    private void addBatchResults(List<Integer> results, int[] batchResult) {
        Arrays.stream(batchResult).forEach(results::add);
    }


    /**
     * 建立数据库连接
     *
     * @return 数据库连接
     * @throws SQLException SQLException
     */
    public Connection getConnection() throws SQLException {
        if (connection != null) {
            return connection;
        }
        return connection(url, userName, password);
    }

    /**
     * 自动根据URL加载合适的数据库驱动
     *
     * @param url url
     * @throws SQLException SQLException
     */
    private void autoLoadDriver(String url) throws SQLException {
        if (url == null) {
            throw new SQLException("Database URL cannot be null");
        }

        String lowerUrl = url.toLowerCase();

        try {
            if (lowerUrl.contains(":mysql:")) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } else if (lowerUrl.contains(":oracle:")) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } else if (lowerUrl.contains(":postgresql:")) {
                Class.forName("org.postgresql.Driver");
            } else if (lowerUrl.contains(":sqlserver:") || lowerUrl.contains(":microsoft:")) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } else if (lowerUrl.contains(":dm:")) {
                Class.forName("dm.jdbc.driver.DmDriver");
            } else {
                throw new SQLException("Unsupported database type in URL: " + url + "\n找不到对应的驱动");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Failed to load JDBC driver for URL: " + url, e);
        }
    }


    /**
     * 建立数据库连接
     *
     * @param url      url
     * @param userName userName
     * @param password password
     * @return Connection 连接
     * @throws SQLException SQLException
     */
    public Connection connection(String url, String userName, String password) throws SQLException {
        if (url == null || userName == null || password == null || url.isEmpty() || userName.isEmpty() || password.isEmpty()) {
            throw new NullPointerException("url, userName, password 不能为空!!!");
        }

        // 自动加载驱动
        autoLoadDriver(url);

        this.url = url;
        this.userName = userName;
        this.password = password;
        connection = DriverManager.getConnection(url, userName, password);
        return connection;
    }

    /**
     * 释放连接
     *
     * @param conn conn
     */
    private static void freeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放statement
     *
     * @param statement statement
     */
    private static void freeStatement(Statement statement) {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放resultset
     *
     * @param rs rs
     */
    private static void freeResultSet(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     *
     * @param conn conn
     */
    public void free(Connection conn) {
        if (conn != null) {
            freeConnection(conn);
        }
    }

    /**
     * 释放资源
     *
     * @param statement statement
     */
    public void free(Statement statement) {
        if (statement != null) {
            freeStatement(statement);
        }
    }

    /**
     * 释放资源
     *
     * @param rs rs
     */
    public void free(ResultSet rs) {
        if (rs != null) {
            freeResultSet(rs);
        }
    }

    /**
     * 释放资源
     *
     * @param conn      conn
     * @param statement statement
     * @param rs        rs
     */
    public void free(Connection conn, Statement statement, ResultSet rs) {
        if (rs != null) {
            freeResultSet(rs);
        }
        if (statement != null) {
            freeStatement(statement);
        }
        if (conn != null) {
            freeConnection(conn);
        }
    }


    /**
     * 配置了主键自增 不需要设置主键 否则存在的情况会报错
     *
     * @param conn      conn
     * @param obj       obj
     * @param tableName tableName
     * @param <T>       泛型类
     * @return 影响行数
     * @throws Exception Exception
     */
    public <T> Long insert(Connection conn, T obj, String tableName) throws Exception {
        Long insertId = 0L;
        Class<?> clazz = obj.getClass();
        StringBuilder sql = new StringBuilder("insert into " + tableName + " (");
        StringBuilder sqlParams = new StringBuilder();

        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField != null && !tableField.exist()) {
                continue;
            }
            field.setAccessible(true);
            if (i > 0) {
                sql.append(", ");
                sqlParams.append(", ");
            }
            String fieldName = field.getName();
            sql.append(camelToUnderscore(fieldName));
            sqlParams.append((field.get(obj) == null ? "null" : "'" + handlerField(field, obj) + "'"));
        }
//        for (Field field : fields) {
//            field.setAccessible(true);
//            // 判断是否是数据库字段
//            TableField tableField = field.getAnnotation(TableField.class);
//            if (tableField != null && !tableField.exist()) {
//                continue;
//            }
//            String fieldName = field.getName();
//            sql.append(camelToUnderscore(fieldName)).append(", ");
//            sqlParams.append((field.get(obj)==null?"null, ":"'"+field.get(obj)+"', "));
//        }
//        if (sqlParams.toString().endsWith(", ")) {
//            sqlParams.delete(sql.length() - 2, sql.length()).append(")");
//        }
//        sql.delete(sqlParams.length() - 2, sqlParams.length());
//        sqlParams.delete(sqlParams.length() - 2, sqlParams.length());
        sql.append(") values (");
        sql.append(sqlParams).append(")");
        System.out.println("fields = " + sql);

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
//            for (int i = 1; i <= fields.length; i++) {
//                fields[i - 1].setAccessible(true);
//                Object value = fields[i - 1].get(obj);
//                pstmt.setObject(i, value);
//            }
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    insertId = rs.getLong(1);
                    System.out.println("New ID: " + insertId);
                } else {
                    System.out.println("No ID obtained.");
                }
            }
        }
        return insertId;
    }

    /**
     * 处理字段
     *
     * @param field 字段
     * @param obj   对象类
     * @param <T>   泛型
     * @return 对象
     * @throws IllegalAccessException e
     */
    private static <T> Object handlerField(Field field, T obj) throws IllegalAccessException {

        // 获取字段的类型
        Class<?> fieldType = field.getType();

        // 判断字段类型是否是日期类型
        if (Date.class.isAssignableFrom(fieldType)) {
            // 是日期类型，可以进行相应处理
//            System.out.println("Field is a Date type");

            // 假设这里获取到了一个 Date 类型的字段值
            Date dateValue = (Date) field.get(obj);

            // 使用 SimpleDateFormat 格式化日期
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(dateValue);
        } else {
            // 不是日期类型，可以进行其他处理
//            System.out.println("Field is not a Date type");
        }
        return field.get(obj);
    }


    /**
     * 驼峰转下划线字符串
     *
     * @param str 驼峰字符串
     * @return 下划线字符串
     */
    public String camelToUnderscore(String str) {
        StringBuilder result = new StringBuilder();

        if (str != null && !str.isEmpty()) {
            for (int i = 0; i < str.length(); i++) {
                char currentChar = str.charAt(i);

                if (Character.isUpperCase(currentChar)) {
                    result.append("_").append(Character.toLowerCase(currentChar));
                } else {
                    result.append(currentChar);
                }
            }
        }

        return result.toString();
    }

    /**
     * 下划线转驼峰字符串
     *
     * @param str 下划线字符串
     * @return 驼峰字符串
     */
    public String underscoreToCamel(String str) {
        StringBuilder result = new StringBuilder();

        if (str != null && !str.isEmpty()) {
            boolean nextUpperCase = false;
            for (int i = 0; i < str.length(); i++) {
                char currentChar = str.charAt(i);

                if (currentChar == '_') {
                    nextUpperCase = true;
                } else {
                    if (nextUpperCase) {
                        result.append(Character.toUpperCase(currentChar));
                        nextUpperCase = false;
                    } else {
                        result.append(Character.toLowerCase(currentChar));
                    }
                }
            }
        }

        return result.toString();
    }


    /**
     * 将ResultSet转换为List对象
     *
     * @param rs        ResultSet对象
     * @param classType 类型对象的class
     * @param <T>       泛型
     * @return 包含ResultSet数据的List对象
     * @throws SQLException 如果处理ResultSet时发生SQL异常
     */
    public <T> List<T> convertResultSetToList(ResultSet rs, Class<T> classType) throws SQLException {
        List<T> list = new ArrayList<>();

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            try {
                // 使用反射创建对象
                T obj = classType.getDeclaredConstructor().newInstance();

                // 设置对象的属性值
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object columnValue = rs.getObject(i);

                    // 使用反射设置对象的属性值
                    Field field = classType.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(obj, columnValue);
                }

                list.add(obj);
            } catch (Exception e) {
                throw new SQLException("Failed to map ResultSet to object", e);
            }
        }

        return list;
    }

    /**
     * 执行查询 转换实体
     *
     * @param conn      连接
     * @param sql       sql
     * @param rowMapper 传实体 然后设置对应的字段
     * @param <T>       泛型
     * @return 查询结果
     */
    public static <T> List<T> executeQuery(Connection conn, String sql, RowMapper<T> rowMapper) {
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

    /**
     * 映射结果集到实体的接口
     *
     * @param <T> 实体类型
     */
    public interface RowMapper<T> {
        T mapRow(ResultSet resultSet) throws SQLException;
    }

    /**
     * 检测数据库类型
     *
     * @param conn conn
     * @return 数据库类型
     * @throws SQLException e
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