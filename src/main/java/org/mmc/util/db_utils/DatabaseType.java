package org.mmc.util.db_utils;


/**
 * 数据库类型枚举
 */

/**
 * 定义数据库类型枚举
 * 用于在程序中以类型安全的方式表示不同的数据库类型
 */
public enum DatabaseType {
    /**
     * MySQL数据库类型
     */
    MYSQL,
    /**
     * Dameng数据库类型
     */
    DM,
    /**
     * 未知数据库类型，用于处理无法识别的数据库情况
     */
    UNKNOWN
}
