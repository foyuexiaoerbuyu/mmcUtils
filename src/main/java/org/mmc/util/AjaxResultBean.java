/**
 * 用于封装Ajax请求结果的泛型类
 * 
 * @param <T> 泛型参数，用于表示返回的数据类型
 */
package org.mmc.util;

/**
 * Ajax请求结果的JavaBean类
 * 该类用于封装Ajax请求的响应结果，包括状态码、消息和具体数据
 * 
 * @param <T> 泛型参数，表示返回的数据类型
 */
public class AjaxResultBean<T> {
    /**
     * 状态码，用于表示请求的结果状态
     */
    private int code;
    /**
     * 消息，用于描述请求结果的详细信息
     */
    private String msg;
    /**
     * 数据，存储请求返回的具体数据
     */
    private T data;

    /**
     * 获取状态码
     * 
     * @return int 返回状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置状态码
     * 
     * @param code 状态码，用于表示请求的结果状态
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取消息
     * 
     * @return String 返回描述请求结果的详细信息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置消息
     * 
     * @param msg 消息，描述请求结果的详细信息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取数据
     * 
     * @return T 返回请求返回的具体数据，数据类型由泛型参数指定
     */
    public T getData() {
        return data;
    }

    /**
     * 设置数据
     * 
     * @param data 请求返回的具体数据，数据类型由泛型参数指定
     */
    public void setData(T data) {
        this.data = data;
    }

}
