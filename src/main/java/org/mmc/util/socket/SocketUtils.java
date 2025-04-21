package org.mmc.util.socket;

import org.mmc.util.FileUtils;
import org.mmc.util.GsonUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * 该类提供了基于Socket的网络通信工具方法，包括启动服务端、客户端连接服务端、发送消息和文件等功能。
 * 参考文档：https://www.runoob.com/java/net-serversocket-socket.html
 */
public class SocketUtils {

    // 缓冲区大小，设置为2MB
    private static final int BUFFER_SIZE = 1024 * 1024 * 2;
    // 服务端通知消息的回调接口实例
    private final IServiceNotifyMsg iServiceNotifyMsg;
    // 服务端Socket对象
    private ServerSocket serverSocket;
    // 客户端Socket对象
    private Socket mClientSocket;
    // 服务端与客户端通信的Socket对象
    private Socket mServiceSocket;

    /**
     * 构造函数，初始化服务端通知消息的回调接口。
     * @param iServiceNotifyMsg 服务端通知消息的回调接口实例
     */
    public SocketUtils(IServiceNotifyMsg iServiceNotifyMsg) {
        this.iServiceNotifyMsg = iServiceNotifyMsg;
    }

    /**
     * 启动服务端，在指定端口监听客户端连接，并接收客户端消息。
     * @param port 服务器监听的端口号
     * @param receiverPath 接收文件的保存路径
     * @param iReceiverMsg 接收客户端消息的回调接口实例
     */
    public void startService(int port, String receiverPath, IReceiverMsg iReceiverMsg) {
        new Thread(() -> {
            try {
                // 创建接收文件的目录
                FileUtils.makeDirs(receiverPath);
                // 创建服务端Socket并绑定指定端口
                serverSocket = new ServerSocket(port);
                // 通知服务启动成功
                iReceiverMsg.startSuccess("启动服务成功");
                System.out.printf("启动服务 %d\n", serverSocket.getLocalPort());

                // 持续监听客户端连接
                while (true) {
                    // 接受客户端连接
                    mServiceSocket = serverSocket.accept();
                    // 创建输入流读取客户端消息
                    BufferedReader serviceReader = new BufferedReader(new InputStreamReader(mServiceSocket.getInputStream()));
                    // 接收客户端消息
                    receiverMsg(receiverPath, iReceiverMsg, serviceReader);
                }

            } catch (IOException e) {
//                iServiceNotifyMsg.errMsg(e, "启动服务器失败<启动后直接停止也会抛异常,这是暂时不处理>");
                e.printStackTrace();
            } finally {
                try {
                    if (mServiceSocket != null) {
                        // 关闭服务端与客户端通信的Socket
                        mServiceSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 客户端尝试连接多个服务端地址，直到连接成功。
     * @param ips 服务端地址列表，格式为 "ip:port"
     * @param receiverPath 接收文件的保存路径
     * @param iReceiverMsg 接收服务端消息的回调接口实例
     */
    public void connService(List<String> ips, String receiverPath, IReceiverMsg iReceiverMsg) {
        new Thread(() -> {
            try {
                for (String ip : ips) {
                    if (ip.contains(":")) {
                        String[] split = ip.split(":");
                        String host = split[0];
                        int port = Integer.parseInt(split[1]);
                        try {
                            // 尝试连接服务端
                            mClientSocket = new Socket(host, port);
                            break;
                        } catch (IOException e) {
                        }
                    }
                }
                if (mClientSocket == null) {
                    // 连接失败，记录日志
                    iReceiverMsg.log(IReceiverMsg.MSG_TYPE_COMM_LOG, "连接服务器异常.");
                    return;
                }
                // 创建输入流读取服务端消息
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
                // 通知连接成功
                iReceiverMsg.log(IReceiverMsg.MSG_TYPE_COMM_LOG, "连接服务器成功");
                // 发送连接成功消息给服务端
                sendMsgToService("客户端连接服务器成功");
                // 接收服务端消息
                receiverMsg(receiverPath, iReceiverMsg, clientReader);
            } catch (IOException e) {
                // 连接异常，记录日志并通知服务端
                iReceiverMsg.log(IReceiverMsg.MSG_TYPE_COMM_LOG, "连接服务器异常");
                iServiceNotifyMsg.errMsg(e, "连接服务器异常");
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 客户端连接指定服务端地址和端口。
     * @param host 服务端地址
     * @param port 服务端端口号
     * @param receiverPath 接收文件的保存路径
     * @param iReceiverMsg 接收服务端消息的回调接口实例
     */
    public void connService(String host, int port, String receiverPath, IReceiverMsg iReceiverMsg) {
        new Thread(() -> {
            try {
                // 连接服务端
                mClientSocket = new Socket(host, port);
                // 创建输入流读取服务端消息
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
                // 通知连接成功
                iReceiverMsg.log(IReceiverMsg.MSG_TYPE_COMM_LOG, "连接服务器成功");
                iReceiverMsg.connSuccess();
                // 发送连接成功消息给服务端
                sendMsgToService("客户端连接服务器成功");
                // 接收服务端消息
                receiverMsg(receiverPath, iReceiverMsg, clientReader);
            } catch (IOException e) {
                // 连接异常，记录日志并通知服务端
                iReceiverMsg.log(IReceiverMsg.MSG_TYPE_COMM_LOG, "连接服务器异常");
                iServiceNotifyMsg.errMsg(e, "连接服务器异常");
                iReceiverMsg.connFailure(e);
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 发送原始字符串数据给服务端（标记过期只是标识一下）。
     * @param str 客户端发送给服务端的原始数据
     */
    @Deprecated
    private void sendMsgToServicePrimitive(String str) {
        try {
            // 创建输出流发送消息给服务端
            BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream()));
            clientWriter.write(str);
            clientWriter.newLine();
            clientWriter.flush();
        } catch (IOException e) {
            // 发送异常，通知服务端
            iServiceNotifyMsg.errMsg(e, "客户端发送消息异常");
            e.printStackTrace();
        }
    }

    /**
     * 发送原始字符串数据给客户端（标记过期只是标识一下）。
     * @param str 服务端发送给客户端的原始数据
     */
    @Deprecated
    private void sendMsgToClientPrimitive(String str) {
        try {
            // 创建输出流发送消息给客户端
            BufferedWriter serviceWriter = new BufferedWriter(new OutputStreamWriter(mServiceSocket.getOutputStream()));
            serviceWriter.write(str);
            serviceWriter.newLine();
            serviceWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            // 发送异常，通知服务端
            iServiceNotifyMsg.errMsg(e, "服务端发送消息异常");
        }
    }

    /**
     * 接收消息并根据消息类型处理，包括文件和文本消息。
     * @param receiverPath 接收文件的保存路径
     * @param clientCall 接收消息的回调接口实例
     * @param reader 输入流，用于读取消息
     */
    private void receiverMsg(String receiverPath, IReceiverMsg clientCall, BufferedReader reader) {
        String msg;
        try {
            while ((msg = reader.readLine()) != null) {
                // 将接收到的JSON消息转换为ChatMsg对象
                ChatMsg chatMsg = GsonUtil.fromJson(msg, ChatMsg.class);
                if (chatMsg.getMsgType() == ChatMsgType.MSG_TYPE_FILE) {
//                    XLog.showLogArgs(receiverPath, chatMsg.getFileName(), chatMsg.getMd5());
                    // 拼接文件保存路径
                    String filePath = receiverPath + chatMsg.getFileName();
                    if (FileUtils.isFileExist(filePath) && FileUtils.getFileMD5(filePath).equals(chatMsg.getMd5())) {
                        // 文件已存在且MD5值匹配，通知接收完毕
                        clientCall.receiverMsg("接收文件完毕");
                        continue;
                    }
                    // 将文件数据写入文件
                    FileUtils.writeFile(filePath, chatMsg.getFileData(), true);
                } else if (chatMsg.getMsgType() == ChatMsgType.MSG_TYPE_MSG) {
                    // 处理文本消息
                    clientCall.receiverMsg(chatMsg.getMsgContent().trim());
                }
                if ("Bye".equals(chatMsg.getMsgContent())) {
                    // 处理下线消息
                    clientCall.receiverMsg("已下线");
                    break;
                }
            }
        } catch (IOException e) {
            // 接收异常，通知服务端
            iServiceNotifyMsg.errMsg(e, "接收消息异常");
            e.printStackTrace();
        }
    }

    /**
     * 停止服务端。
     */
    public void stopService() {
        if (serverSocket == null) return;
        try {
            // 关闭服务端Socket
            serverSocket.close();
        } catch (IOException e) {
            // 停止失败，通知服务端
            iServiceNotifyMsg.errMsg(e, "停止失败");
            e.printStackTrace();
        }
    }

    /**
     * 停止客户端。
     */
    public void stopClient() {
        try {
            // 关闭客户端Socket
            mClientSocket.close();
        } catch (IOException e) {
            // 停止失败，通知服务端
            iServiceNotifyMsg.errMsg(e, "停止客户端失败");
            e.printStackTrace();
        }
    }

    /**
     * 接收消息的回调接口，定义了接收消息、进度更新、日志记录等方法。
     */
    public interface IReceiverMsg {
        // 普通日志类型
        static final int MSG_TYPE_COMM_LOG = 0;

        /**
         * 接收消息的方法。
         * @param receiveMsg 接收到的消息内容
         */
        void receiverMsg(String receiveMsg);

        /**
         * 进度更新的默认方法。
         * @param progress 当前进度
         */
        default void progress(int progress) {

        }

        /**
         * 记录日志的默认方法。
         * @param type 日志类型
         * @param msg 日志内容
         */
        default void log(int type, String msg) {

        }

        /**
         * 服务启动成功的通知方法。
         * @param msg 通知消息
         */
        void startSuccess(String msg);

        /**
         * 连接成功的默认方法。
         */
        default void connSuccess() {

        }

        /**
         * 连接失败的默认方法。
         * @param e 异常对象
         */
        default void connFailure(Exception e) {

        }
    }

    /**
     * 服务端通知消息的回调接口，用于处理异常消息。
     */
    @FunctionalInterface
    public interface IServiceNotifyMsg {
        /**
         * 处理异常消息的方法。
         * @param e 异常对象
         * @param errMsg 错误消息
         */
        void errMsg(Exception e, String errMsg);
    }

    //-----------------------------------------------------------------------------------------
    /**
     * 发送消息给客户端。
     * @param msg 要发送的消息内容
     */
    public void sendMsgToClient(String msg) {
        sendMsgToClientPrimitive(new ChatMsg(msg).toJson());
    }

    /**
     * 发送消息给服务端。
     * @param msg 要发送的消息内容
     */
    public void sendMsgToService(String msg) {
        sendMsgToServicePrimitive(new ChatMsg(msg).toJson());
    }

    /**
     * 发送文件给服务端。
     * @param filePath 要发送的文件路径
     */
    public void sendFileToService(String filePath) {
        File file = new File(filePath);
        // 获取文件的MD5值
        String md5 = FileUtils.getFileMD5(file);
        System.out.println("start send file----" + file.getName());
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long curProgress = 0;
            while ((bytesRead = fis.read(buffer)) != -1) {
                // 复制读取的字节数据
                byte[] dataToSend = Arrays.copyOf(buffer, bytesRead);
                // 创建文件消息对象
                ChatMsg fileMsg = new ChatMsg(file.getName(), md5, file.length(), dataToSend);
                curProgress = curProgress + bytesRead;
                // 设置当前进度
                fileMsg.setProgress(curProgress);
                System.out.println("发送文件到服务端进度 = " + fileMsg.getProgress() + "  " + fileMsg.getFileSize());
                // 发送文件消息给服务端
                sendMsgToServicePrimitive(fileMsg.toJson());
            }

            System.out.println("File send: " + filePath);
        } catch (IOException e) {
            // 发送文件异常，通知客户端和服务端
            sendMsgToClient("接收文件异常: " + e.getMessage());
            iServiceNotifyMsg.errMsg(e, "接收文件异常: " + e.getMessage());
            System.err.println("Error sending file: " + e.getMessage());
        }
    }

    /**
     * 发送文件给客户端。
     * @param filePath 要发送的文件路径
     * @param savePath 文件保存路径
     */
    public void sendFileToClient(String filePath, String savePath) {
        File file = new File(filePath);
        // 获取文件的MD5值
        String md5 = FileUtils.getFileMD5(file);
        System.out.println("start send file" + file.getName());
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long curProgress = 0;
            while ((bytesRead = fis.read(buffer)) != -1) {
                // 复制读取的字节数据
                byte[] dataToSend = Arrays.copyOf(buffer, bytesRead);
                // 创建文件消息对象
                ChatMsg fileMsg = new ChatMsg(file.getName(), md5, file.length(), dataToSend);
                curProgress = curProgress + bytesRead;
                // 设置当前进度
                fileMsg.setProgress(curProgress);
                // 设置文件保存路径
                fileMsg.setSavePath(savePath);
//                BigDecimal result = BigDecimal.valueOf( fileMsg.getProgress()).divide(BigDecimal.valueOf(fileMsg.getFileSize()), 2, BigDecimal.ROUND_HALF_UP);
                if (fileMsg.getProgress() > 0 && fileMsg.getFileSize() > 0) {
                    // 计算进度百分比
                    String resStr = String.format("%.2f", (fileMsg.getProgress() / fileMsg.getFileSize()) * 100f) + "%";
                    System.out.println("发送文件到客户端进度 = " + fileMsg.getProgress() + "  " + fileMsg.getFileSize() + "  进度: " + resStr);
                }
                // 发送文件消息给客户端
                sendMsgToClientPrimitive(fileMsg.toJson());
            }

            System.out.println("File send: " + filePath);
        } catch (IOException e) {
            // 发送文件异常，通知服务端和客户端
            sendMsgToService("接收文件异常: " + e.getMessage());
            iServiceNotifyMsg.errMsg(e, "接收文件异常: " + e.getMessage());
            System.err.println("Error sending file: " + e.getMessage());
        }
    }
}