package org.mmc.util.db_utils;

import java.lang.annotation.*;

/**
 * TableName 注解用于指定实体类对应的数据库表名及相关信息。
 * 可用于类和注解类型，运行时保留，方便在运行时通过反射获取注解信息。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface TableName {
    /**
     * 表名，默认为空字符串。
     * @return 表名
     */
    String value() default "";

    /**
     * 数据库模式，默认为空字符串。
     * @return 数据库模式
     */
    String schema() default "";

    /**
     * 是否保留全局表名前缀，默认为 false。
     * @return 是否保留全局表名前缀
     */
    boolean keepGlobalPrefix() default false;

    /**
     * 结果映射，默认为空字符串。
     * @return 结果映射
     */
    String resultMap() default "";

    /**
     * 是否自动生成结果映射，默认为 false。
     * @return 是否自动生成结果映射
     */
    boolean autoResultMap() default false;

    /**
     * 排除的属性数组，默认为空数组。
     * @return 排除的属性数组
     */
    String[] excludeProperty() default {};
}