package org.mmc.util;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Scanner;

/**
 * WebSocket客户端工具类，用于创建和管理WebSocket连接。
 * 示例连接地址：ws://192.144.219.245:8885
 *
 * 需要添加以下依赖：
 *     org.java-websocket
 *     Java-WebSocket
 *     1.5.3
 */
public class WebSocketClientUtils {

    // 单例模式，用于确保该类只有一个实例
    private static WebSocketClientUtils instance;
    // 用于处理WebSocket事件的回调接口
    private IWebSocketCallBack iWebSocketCallBack;
    // WebSocket客户端实例
    private WebSocketClient client;

    // 私有构造函数，防止外部直接实例化
    private WebSocketClientUtils() {

    }

    /**
     * 获取WebSocketClientUtils的单例实例。
     * 使用双重检查锁定机制确保线程安全。
     * @return WebSocketClientUtils的单例实例
     */
    public static WebSocketClientUtils getInstance() {
        if (instance == null) {
            synchronized (WebSocketClientUtils.class) {
                if (instance == null) {
                    instance = new WebSocketClientUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 程序入口，用于测试WebSocket连接。
     * 初始化连接，注册回调，允许用户输入消息并发送，输入“退出”可结束程序。
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 初始化WebSocket连接，指定服务器地址
        WebSocketClientUtils.getInstance().init("ws://192.144.219.245:8088/websocket/phone");
        // 发起连接
        WebSocketClientUtils.getInstance().connect();
        // 注册回调函数，处理收到的消息、连接关闭和错误事件
        WebSocketClientUtils.getInstance().registCallBack(new IWebSocketCallBack() {

            /**
             * 当收到服务器消息时调用。
             * @param message 收到的消息内容
             */
            @Override
            public void onMessage(String message) {
                System.out.println(message);
            }

            /**
             * 当连接关闭时调用。
             * @param code 关闭代码
             * @param reason 关闭原因
             * @param remote 是否是远程关闭
             */
            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("code = " + code + ", reason = " + reason + ", remote = " + remote);
            }

            /**
             * 当发生错误时调用。
             * @param ex 异常对象
             */
            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        });
        // 创建Scanner对象，用于读取用户输入
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入内容，按回车键发送。输入“退出”以结束程序。");
        // 循环读取用户输入
        while (true) {
            String input = scanner.nextLine();
            if ("退出".equals(input)) {
                System.out.println("程序已结束。");
                break;
            }
            System.out.println("您输入的内容是: " + input);
            // 发送用户输入的消息
            WebSocketClientUtils.getInstance().sendMessage(input);
        }
        // 关闭Scanner对象
        scanner.close();
    }

    /**
     * 初始化WebSocket客户端，指定服务器地址。
     * @param serverUri WebSocket服务器的URI，例如 "ws://192.144.219.245:8885"
     */
    public void init(String serverUri) {
        client = new WebSocketClient(URI.create(serverUri)) {
            /**
             * 当WebSocket连接成功打开时调用。
             * 若注册了回调函数，则调用回调函数的onOpen方法。
             * @param handshakedata 服务器握手数据
             */
            @Override
            public void onOpen(ServerHandshake handshakedata) {
//                System.out.println("连接成功");
                if (iWebSocketCallBack != null) iWebSocketCallBack.onOpen(handshakedata);
            }

            /**
             * 当收到服务器发送的消息时调用。
             * 若注册了回调函数，则调用回调函数的onMessage方法。
             * @param message 收到的消息内容
             */
            @Override
            public void onMessage(String message) {
//                System.out.println("收到消息：" + message);
                if (iWebSocketCallBack != null) iWebSocketCallBack.onMessage(message);
            }

            /**
             * 当WebSocket连接关闭时调用。
             * 若注册了回调函数，则调用回调函数的onClose方法。
             * @param code 关闭代码
             * @param reason 关闭原因
             * @param remote 是否是远程关闭
             */
            @Override
            public void onClose(int code, String reason, boolean remote) {
//                System.out.println("连接关闭");
                if (iWebSocketCallBack != null) iWebSocketCallBack.onClose(code, reason, remote);

            }

            /**
             * 当WebSocket连接发生错误时调用。
             * 若注册了回调函数，则调用回调函数的onError方法。
             * @param ex 异常对象
             */
            @Override
            public void onError(Exception ex) {
                if (iWebSocketCallBack != null) iWebSocketCallBack.onError(ex);
//                System.out.println("连接错误：" + ex.getMessage());
            }
        };
    }

    /**
     * 注册WebSocket事件的回调函数。
     * @param iWebSocketCallBack 实现了IWebSocketCallBack接口的回调对象
     */
    public void registCallBack(IWebSocketCallBack iWebSocketCallBack) {
        this.iWebSocketCallBack = iWebSocketCallBack;
    }

    /**
     * 取消注册WebSocket事件的回调函数。
     */
    public void unRegistCallBack() {
        iWebSocketCallBack = null;
    }

    /**
     * 向WebSocket服务器发送消息。
     * 若连接已打开，则发送消息；否则抛出运行时异常。
     * @param message 要发送的消息内容
     */
    public void sendMessage(String message) {
        if (client != null && client.isOpen()) {
            client.send(message);
        } else {
            throw new RuntimeException("WebSocket connection is not open");
        }
    }

    /**
     * 发起WebSocket连接。
     */
    public void connect() {
        client.connect();
    }

    /**
     * 关闭WebSocket连接。
     */
    public void close() {
        client.close();
    }

    /**
     * WebSocket事件回调接口，用于处理连接打开、消息接收、连接关闭和错误事件。
     */
    public interface IWebSocketCallBack {
        /**
         * 当WebSocket连接成功打开时调用。
         * 提供默认实现，可根据需要重写。
         * @param handshakedata 服务器握手数据
         */
        default void onOpen(ServerHandshake handshakedata) {

        }

        /**
         * 当收到服务器发送的消息时调用。
         * 必须实现该方法以处理接收到的消息。
         * @param message 收到的消息内容
         */
        void onMessage(String message);

        /**
         * 当WebSocket连接关闭时调用。
         * 必须实现该方法以处理连接关闭事件。
         * @param code 关闭代码
         * @param reason 关闭原因
         * @param remote 是否是远程关闭
         */
        void onClose(int code, String reason, boolean remote);

        /**
         * 当WebSocket连接发生错误时调用。
         * 必须实现该方法以处理错误事件。
         * @param ex 异常对象
         */
        void onError(Exception ex);
    }
}