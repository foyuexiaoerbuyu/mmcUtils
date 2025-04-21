package org.mmc.util;

/**
 * 自定义异常类 CheckException，继承自 Exception 类，用于在特定检查逻辑中抛出异常
 */
public class CheckException extends Exception{

    /**
     * 默认构造函数
     */
    public CheckException() {
    }

    /**
     * 带有消息参数的构造函数
     * @param message 异常消息
     */
    public CheckException(String message) {
        super(message);
    }

    /**
     * 带有消息和异常原因参数的构造函数
     * @param message 异常消息
     * @param cause 异常原因
     */
    public CheckException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带有异常原因参数的构造函数
     * @param cause 异常原因
     */
    public CheckException(Throwable cause) {
        super(cause);
    }

}