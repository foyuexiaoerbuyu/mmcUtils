package org.mmc.util.db_utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TableField 注解用于标记实体类的字段，指示该字段是否存在于数据库表中。
 * 可用于字段，运行时保留，方便在运行时通过反射获取注解信息。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableField {
    /**
     * 字段是否存在于数据库表中，默认为 true。
     * @return 字段是否存在于数据库表中
     */
    boolean exist() default true;
}