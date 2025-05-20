package org.mmc.util;

import java.io.Serializable;

/**
 * 统一API响应结果封装
 * 包含状态码、提示信息和泛型数据字段
 *
 * @param <T> 响应数据的类型
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务状态码
     * 200表示成功，其他值表示不同类型的错误
     */
    private int code;

    /**
     * 操作结果描述信息
     * 用于向客户端展示的提示文本
     */
    private String message;

    /**
     * 响应携带的数据对象
     * 成功响应时通常包含业务数据
     * 失败响应时通常为null
     */
    private T data;

    public Result() {
        // 默认构造函数
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 快速构建成功响应
     * 默认状态码200，提示信息"操作成功"
     *
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return 成功响应实例
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 快速构建自定义提示的成功响应
     * 默认状态码200
     *
     * @param message 自定义提示信息
     * @param data    响应数据
     * @param <T>     响应数据类型
     * @return 成功响应实例
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 快速构建自定义错误响应
     *
     * @param code    自定义错误码
     * @param message 错误提示信息
     * @param <T>     响应数据类型
     * @return 错误响应实例
     */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 快速构建默认错误响应
     * 默认状态码500，表示服务器内部错误
     *
     * @param message 错误提示信息
     * @param <T>     响应数据类型
     * @return 错误响应实例
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置状态码
     *
     * @param code 状态码
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取提示信息
     *
     * @return 提示信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置提示信息
     *
     * @param message 提示信息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取响应数据
     *
     * @return 响应数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置响应数据
     *
     * @param data 响应数据
     */
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}