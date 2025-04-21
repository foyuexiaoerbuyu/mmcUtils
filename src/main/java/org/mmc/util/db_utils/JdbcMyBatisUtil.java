package org.mmc.util.db_utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.mmc.util.GsonUtil;
import org.mmc.util.StringUtil;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * JdbcMyBatisUtil 类是一个通用的数据库操作工具类，提供了一系列用于数据库增删改查操作的方法。
 * 它使用 JDBC 进行数据库连接和操作，支持将查询结果映射到 Java 对象，同时也提供了生成 SQL 语句、关闭资源等辅助功能。
 *
 * @param <T> 泛型类型，用于指定实体类的类型，方便在方法中进行类型转换和操作。
 */
public class JdbcMyBatisUtil<T> {

    // 数据库连接的 URL，这里默认连接到本地的 MySQL 数据库的 mydatabase 库
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydatabase";
    // 数据库用户名
    private static final String DB_USER = "username";
    // 数据库密码
    private static final String DB_PASSWORD = "password";
    // 数据库连接对象
    private static Connection conn = null;

    /**
     * 获取数据库连接。
     * 如果连接已经存在，则直接返回；否则，使用 JDBC 驱动管理器创建新的连接。
     *
     * @return 数据库连接对象
     * @throws SQLException 如果在获取连接过程中出现 SQL 异常
     */
    private static Connection getConnection() throws SQLException {
        if (conn != null) {
            return conn;
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * 插入操作，将实体对象插入到数据库中，并返回插入的主键值。
     *
     * @param entity 要插入的实体对象
     * @return 插入记录的主键值，如果插入失败则返回 null
     * @throws SQLException 如果在执行插入操作过程中出现 SQL 异常
     */
    public static Long insert(Object entity) throws SQLException {
        // 预编译语句对象
        PreparedStatement stmt = null;
        // 结果集对象，用于获取生成的主键
        ResultSet rs = null;
        // 插入记录的主键值
        Long generatedId = null;

        try {
            // 获取数据库连接
            conn = getConnection();
            // 生成插入 SQL 语句
            String sql = generateInsertSQL(entity);
            // 创建预编译语句对象，并设置返回生成的主键
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            // 设置预编译语句的参数
            setParameters(stmt, entity);
            // 执行插入操作
            stmt.executeUpdate();
            // 获取生成的主键结果集
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                // 获取主键值
                generatedId = rs.getLong(1);
            }
        } catch (SQLException | IllegalAccessException e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭连接、预编译语句和结果集
            close(conn, stmt, rs);
        }
        return generatedId;
    }

    /**
     * 更新操作，根据实体对象的 id 属性更新数据库中的记录。
     *
     * @param entity 要更新的实体对象
     * @return 更新操作影响的行数
     * @throws SQLException 如果在执行更新操作过程中出现 SQL 异常
     */
    public static int updateById(Object entity) throws SQLException {
        // 预编译语句对象
        PreparedStatement stmt = null;
        // 更新操作影响的行数
        int rows = 0;

        try {
            // 获取数据库连接
            conn = getConnection();
            // 生成更新 SQL 语句
            String sql = generateUpdateSQL(entity);
            // 创建预编译语句对象
            stmt = conn.prepareStatement(sql);
            // 设置预编译语句的参数
            setParameters(stmt, entity);
            // 执行更新操作
            rows = stmt.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭连接和预编译语句
            close(conn, stmt, null);
        }
        return rows;
    }

    /**
     * 删除操作，根据实体类和 id 删除数据库中的记录。
     *
     * @param clazz 实体类的 Class 对象
     * @param id    要删除记录的 id
     * @return 删除操作影响的行数
     * @throws SQLException 如果在执行删除操作过程中出现 SQL 异常
     */
    public static int deleteById(Class<?> clazz, Long id) throws SQLException {
        // 预编译语句对象
        PreparedStatement stmt = null;
        // 删除操作影响的行数
        int rows = 0;

        try {
            // 获取数据库连接
            conn = getConnection();
            // 默认表名为类名的小写形式并加上 "s"
            String tableName = clazz.getSimpleName().toLowerCase() + "s";
            // 生成删除 SQL 语句
            String sql = "DELETE FROM " + tableName + " WHERE id = ?";
            // 创建预编译语句对象
            stmt = conn.prepareStatement(sql);
            // 设置预编译语句的 id 参数
            stmt.setLong(1, id);
            // 执行删除操作
            rows = stmt.executeUpdate();
        } catch (SQLException e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭连接和预编译语句
            close(conn, stmt, null);
        }
        return rows;
    }

    /**
     * 根据 ID 查询单条记录，并将结果映射到指定类型的实体对象。
     *
     * @param clazz 实体类的 Class 对象
     * @param id    要查询记录的 id
     * @param <T>   实体类的类型
     * @return 查询到的实体对象，如果未找到则返回 null
     * @throws SQLException           如果在执行查询操作过程中出现 SQL 异常
     * @throws InstantiationException 如果在实例化实体对象时出现异常
     * @throws IllegalAccessException 如果在访问实体对象的属性时出现异常
     * @throws NoSuchFieldException   如果在实体类中找不到对应的字段
     */
    public static <T> T selectById(Class<T> clazz, Long id) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        // 预编译语句对象
        PreparedStatement stmt = null;
        // 结果集对象
        ResultSet rs = null;
        // 查询到的实体对象
        T result = null;

        try {
            // 获取数据库连接
            conn = getConnection();
            // 默认表名为类名的小写形式并加上 "s"
            String tableName = clazz.getSimpleName().toLowerCase() + "s";
            // 生成查询 SQL 语句
            String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
            // 创建预编译语句对象
            stmt = conn.prepareStatement(sql);
            // 设置预编译语句的 id 参数
            stmt.setLong(1, id);
            // 执行查询操作
            rs = stmt.executeQuery();
            if (rs.next()) {
                // 将结果集映射到实体对象
                result = mapResultSetToObject(rs, clazz);
            }
        } catch (SQLException e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭连接、预编译语句和结果集
            close(conn, stmt, rs);
        }
        return result;
    }

    /**
     * 查询指定实体类对应的表中的所有记录，并将结果映射到实体对象列表。
     *
     * @param clazz 实体类的 Class 对象
     * @param <T>   实体类的类型
     * @return 包含所有记录的实体对象列表
     * @throws SQLException           如果在执行查询操作过程中出现 SQL 异常
     * @throws IllegalAccessException 如果在访问实体对象的属性时出现异常
     * @throws InstantiationException 如果在实例化实体对象时出现异常
     * @throws NoSuchFieldException   如果在实体类中找不到对应的字段
     */
    public static <T> List<T> selectAll(Class<T> clazz) throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        // 语句对象
        Statement stmt = null;
        // 结果集对象
        ResultSet rs = null;
        // 包含所有记录的实体对象列表
        List<T> results = new ArrayList<>();

        try {
            // 获取数据库连接
            conn = getConnection();
            // 默认表名为类名的小写形式并加上 "s"
            String tableName = clazz.getSimpleName().toLowerCase() + "s";
            // 生成查询 SQL 语句
            String sql = "SELECT * FROM " + tableName;
            // 创建语句对象
            stmt = conn.createStatement();
            // 执行查询操作
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                // 将结果集映射到实体对象
                T entity = mapResultSetToObject(rs, clazz);
                // 将实体对象添加到列表中
                results.add(entity);
            }
        } catch (SQLException e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭连接、语句对象和结果集
            close(conn, stmt, rs);
        }
        return results;
    }

    /**
     * 将 ResultSet 中的一行数据映射到指定类型的实体对象。
     *
     * @param rs    结果集对象
     * @param clazz 实体类的 Class 对象
     * @param <T>   实体类的类型
     * @return 映射后的实体对象
     * @throws IllegalAccessException 如果在访问实体对象的属性时出现异常
     * @throws InstantiationException 如果在实例化实体对象时出现异常
     * @throws SQLException           如果在获取结果集数据时出现 SQL 异常
     * @throws NoSuchFieldException   如果在实体类中找不到对应的字段
     */
    private static <T> T mapResultSetToObject(ResultSet rs, Class<T> clazz) throws IllegalAccessException, InstantiationException, SQLException, NoSuchFieldException {
        // 实例化实体对象
        T entity = clazz.newInstance();
        // 获取结果集的元数据
        ResultSetMetaData metaData = rs.getMetaData();
        // 获取结果集的列数
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            // 获取列名
            String columnName = metaData.getColumnName(i);
            // 获取实体类中对应的字段
            Field field = clazz.getDeclaredField(columnName);
            // 设置字段可访问
            field.setAccessible(true);
            // 获取列值
            Object value = rs.getObject(i);
            // 设置实体对象的字段值
            field.set(entity, value);
        }
        return entity;
    }

    /**
     * 生成插入 SQL 语句。
     * 根据实体对象的类名和字段名生成插入 SQL 语句。
     *
     * @param entity 要插入的实体对象
     * @return 生成的插入 SQL 语句
     */
    private static String generateInsertSQL(Object entity) {
        // 获取实体对象的类
        Class<?> clazz = entity.getClass();
        // 默认表名为类名的小写形式并加上 "s"
        String tableName = clazz.getSimpleName().toLowerCase() + "s";
        // 用于拼接 SQL 语句的 StringBuilder 对象
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append(" (");

        // 获取实体类的所有字段
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sql.append(", ");
            }
            // 拼接字段名
            sql.append(fields[i].getName());
        }

        sql.append(") VALUES (");
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sql.append(", ");
            }
            // 拼接占位符
            sql.append("?");
        }
        sql.append(")");

        return sql.toString();
    }

    /**
     * 生成更新 SQL 语句。
     * 根据实体对象的类名和字段名生成更新 SQL 语句。
     *
     * @param entity 要更新的实体对象
     * @return 生成的更新 SQL 语句
     */
    private static String generateUpdateSQL(Object entity) {
        // 获取实体对象的类
        Class<?> clazz = entity.getClass();
        // 默认表名为类名的小写形式
        String tableName = clazz.getSimpleName().toLowerCase();
        // 用于拼接 SQL 语句的 StringBuilder 对象
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(tableName).append(" SET ");

        // 获取实体类的所有字段
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (!fields[i].getName().equals("id")) {
                if (i > 0) {
                    sql.append(", ");
                }
                // 拼接字段名和占位符
                sql.append(fields[i].getName()).append(" = ?");
            }
        }

        sql.append(" WHERE id = ?");
        return sql.toString();
    }

    /**
     * 设置预编译语句的参数。
     * 根据实体对象的字段值设置预编译语句的参数。
     *
     * @param stmt   预编译语句对象
     * @param entity 实体对象
     * @throws SQLException           如果在设置参数过程中出现 SQL 异常
     * @throws IllegalAccessException 如果在访问实体对象的属性时出现异常
     */
    private static void setParameters(PreparedStatement stmt, Object entity) throws SQLException, IllegalAccessException {
        // 获取实体类的所有字段
        Field[] fields = entity.getClass().getDeclaredFields();
        int index = 1;
        for (Field field : fields) {
            // 设置字段可访问
            field.setAccessible(true);
            // 获取字段值
            Object value = field.get(entity);
            // 设置预编译语句的参数
            stmt.setObject(index++, value);
        }
        // 设置更新操作的 id 参数
        stmt.setObject(index, getIdValue(entity));
    }

    /**
     * 获取实体类的 id 属性值。
     *
     * @param entity 实体对象
     * @return 实体对象的 id 属性值，如果未找到则返回 null
     * @throws IllegalAccessException 如果在访问实体对象的属性时出现异常
     */
    private static Object getIdValue(Object entity) throws IllegalAccessException {
        // 获取实体类的所有字段
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals("id")) {
                // 设置字段可访问
                field.setAccessible(true);
                // 获取 id 属性值
                return field.get(entity);
            }
        }
        return null;
    }

    /**
     * 关闭数据库连接、语句对象和结果集。
     *
     * @param conn 数据库连接对象
     * @param stmt 语句对象
     * @param rs   结果集对象
     */
    private static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                // 关闭结果集
                rs.close();
            }
            if (stmt != null) {
                // 关闭语句对象
                stmt.close();
            }
            if (conn != null) {
                // 关闭数据库连接
                conn.close();
            }
        } catch (SQLException e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }

    /**
     * 执行查询 SQL 语句，并将结果映射到指定类型的实体对象列表。
     *
     * @param conn  数据库连接对象
     * @param sql   查询 SQL 语句
     * @param clazz 实体类的 Class 对象
     * @param <T>   实体类的类型
     * @return 包含查询结果的实体对象列表
     * @throws Exception 如果在执行查询或映射结果时出现异常
     */
    public static <T> List<T> query(Connection conn, String sql, Class<T> clazz) throws Exception {
        // 包含查询结果的实体对象列表
        List<T> resultList = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            // 获取 ResultSetMetaData 对象，用于获取查询结果的列名和列类型
            ResultSetMetaData metaData = resultSet.getMetaData();
            // 获取查询结果的列数
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                // 创建对象实例
                T obj = clazz.newInstance();

                // 遍历每一列
                for (int i = 1; i <= columnCount; i++) {
                    // 获取列名和列值
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);

                    // 根据列名找到对应的属性
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);

                    // 设置属性值
                    field.set(obj, columnValue);
                }

                resultList.add(obj);
            }
        }

        return resultList;
    }


    public List<T> executeSql2Ben(Connection conn, String sqlStatements, Class<T> tClass, boolean isCamel) {

        if (conn != null) {
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlStatements)) {
                ResultSetMetaData metaData = rs.getMetaData();
                JsonArray array = new JsonArray();
                while (rs.next()) {
                    JsonObject jo = new JsonObject();
                    String[] strings = new String[metaData.getColumnCount()];
                    for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                        String columnName = metaData.getColumnName(i);//获取字段名
                        strings[i - 1] = rs.getString(i) == null ? "" : rs.getString(i);
//                    System.out.println("key = " + columnName + " val = " + strings[i - 1]);
                        jo.addProperty(isCamel ? StringUtil.toCamelCase(columnName) : columnName, strings[i - 1]);
                    }
                    array.add(jo);
                }
                return GsonUtil.fromJsonToList(array.toString(), tClass);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<T>();
    }

    /**
     * 生成java 实体
     */
    public void creatJavaBenFile(Connection conn, String tableName) {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("select * from " + tableName);) {
            generateEntityClass(rs, tableName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成实体类
     */
    private void generateEntityClass(ResultSet resultSet, String tableName) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            StringBuilder entityClass = new StringBuilder("package chat_test.tmp03;\n\n public class " + (tableName.substring(0, 1).toUpperCase() + tableName.substring(1)) + " {\n");
            HashMap<String, String> getSets = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String columnType = metaData.getColumnTypeName(i).startsWith("INT") ? "int" : "String";
                entityClass.append(" private ").append(columnType).append(" ").append(columnName).append(";\n");
                getSets.put(columnName, columnType);
            }
            for (String key : getSets.keySet()) {
                String upperCaseStr = key.substring(0, 1).toUpperCase() + key.substring(1);
                entityClass.append("\n public void set").append(upperCaseStr).append("( ").append(getSets.get(key)).append(" ").append(key).append("){this.").append(key).append(" = ").append(key).append(";}\n");
                entityClass.append("\n public ").append(getSets.get(key)).append(" get").append(upperCaseStr).append("(){").append(" return ").append(key).append(";}\n");
            }
            entityClass.append("}\n");
            System.out.println(entityClass.toString());
//            String filePath = "C:/tmp/" + tableName + ".java";
//            FileUtils.writeFile(filePath, entityClass.toString());
//            WindowsUtils.copyFileToClipboard(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param sql
     * @param rowMapper 传实体 然后设置对应的字段
     * @param <T>
     * @return
     */
    public static <T> List<T> executeQuery(Connection conn, String sql, RowMapper<T> rowMapper) {

        // public class UserRowMapper implements RowMapper<User> {
        // public User mapRow(ResultSet resultSet) throws SQLException {
        // User user = new User();
        // user.setId(resultSet.getInt("id"));
        // user.setName(resultSet.getString("name"));
        // user.setEmail(resultSet.getString("email"));
        // return user;
        // }
        // }
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
     * 生成建表语句(导出已有数据库表结构,导出数据库表结构,导出表结构)
     * String URL = "jdbc:mysql://localhost:3306/test"; // 数据库URL
     * String USER = "root"; // 数据库用户名
     * String PASSWORD = "root"; // 数据库密码
     * String tableName = "projects"; // 要导出的表名
     *
     * @param tableName 表名
     * @return 建表语句
     */
    public static String generateCreateTableSql(Connection connection, String tableName) {
        try {
            // 获取数据库元数据
            DatabaseMetaData metaData = connection.getMetaData();
            StringBuilder createTableSql = new StringBuilder();
            createTableSql.append("CREATE TABLE ").append(tableName).append(" (\n");

            // 获取表注释
            try (ResultSet tables = metaData.getTables(null, null, tableName, null)) {
                if (tables.next()) {
                    String tableComment = tables.getString("REMARKS");
                    if (tableComment != null && !tableComment.isEmpty()) {
                        createTableSql.append("/* ").append(tableComment).append(" */\n");
                    }
                }
            }

            // 获取列信息
            try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    int columnSize = columns.getInt("COLUMN_SIZE");
                    int nullable = columns.getInt("NULLABLE");
                    String isAutoIncrement = columns.getString("IS_AUTOINCREMENT");
                    String columnComment = columns.getString("REMARKS");

                    // 拼接列定义
                    createTableSql.append("    ").append(columnName).append(" ").append(columnType);

                    // 添加列长度（如果适用）
                    if (columnSize > 0 && !columnType.equalsIgnoreCase("DATE") && !columnType.equalsIgnoreCase("DATETIME")) {
                        createTableSql.append("(").append(columnSize).append(")");
                    }

                    // 添加是否允许为空
                    if (nullable == DatabaseMetaData.columnNoNulls) {
                        createTableSql.append(" NOT NULL");
                    }

                    // 添加自增属性
                    if ("YES".equalsIgnoreCase(isAutoIncrement)) {
                        createTableSql.append(" AUTO_INCREMENT");
                    }

                    // 添加字段注释
                    if (columnComment != null && !columnComment.isEmpty()) {
                        createTableSql.append(" COMMENT '").append(columnComment).append("'");
                    }

                    createTableSql.append(",\n");
                }
            }

            // 获取主键信息
            try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName)) {
                if (primaryKeys.next()) {
                    createTableSql.append("    PRIMARY KEY (");
                    do {
                        String primaryKeyColumn = primaryKeys.getString("COLUMN_NAME");
                        createTableSql.append(primaryKeyColumn).append(", ");
                    } while (primaryKeys.next());
                    createTableSql.delete(createTableSql.length() - 2, createTableSql.length()); // 去掉最后的逗号和空格
                    createTableSql.append(")\n");
                }
            }

            createTableSql.append(");");


            return createTableSql.toString();
        } catch (SQLException e) {
            System.err.println("数据库操作失败: " + e.getMessage());
        }
        return null;
    }

}
