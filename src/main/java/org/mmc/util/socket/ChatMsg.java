package org.mmc.util.socket;

import org.mmc.util.GsonUtil;

/**
 * 该类表示聊天消息，包含了消息的各种属性，如消息ID、消息类型、文件名、文件数据等。
 */
public class ChatMsg {
    // 消息的唯一标识符
    private long id;
    // 消息类型，0表示文本消息，1表示文件消息
    private int msgType;
    // 文件传输的进度
    private long progress;
    // 文件的大小
    private long fileSize;
    // 文件名
    private String fileName;
    // 文件的保存路径
    private String savePath;
    // 文件的MD5值
    private String md5;
    // 文本消息的内容
    private String msgContent;
    // 额外的文本信息
    private String extra;
    // 文件的二进制数据
    private byte[] fileData;
    // 额外的对象信息
    private Object extraObj;

    /**
     * 构造一个文本消息对象。
     *
     * @param msgContent 文本消息的内容
     */
    public ChatMsg(String msgContent) {
        // 使用当前时间戳作为消息的唯一ID
        this.id = System.currentTimeMillis();
        this.msgContent = msgContent;
    }

    /**
     * 构造一个文件消息对象。
     *
     * @param fileName  文件名
     * @param md5       文件的MD5值
     * @param fileSize  文件的大小
     * @param fileData  文件的二进制数据
     */
    public ChatMsg(String fileName, String md5, long fileSize, byte[] fileData) {
        // 使用当前时间戳作为消息的唯一ID
        this.id = System.currentTimeMillis();
        // 设置消息类型为文件消息
        this.msgType = ChatMsgType.MSG_TYPE_FILE;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.md5 = md5;
        this.fileData = fileData;
    }

    /**
     * 判断该消息是否为文件消息。
     *
     * @return 如果是文件消息返回true，否则返回false
     */
    public boolean isFile() {
        return msgType == ChatMsgType.MSG_TYPE_FILE;
    }

    /**
     * 获取消息的唯一标识符。
     *
     * @return 消息的ID
     */
    public long getId() {
        return id;
    }

    /**
     * 设置消息的唯一标识符。
     *
     * @param id 要设置的消息ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * 获取消息的类型。
     *
     * @return 消息类型的整数表示
     */
    public int getMsgType() {
        return msgType;
    }

    /**
     * 设置消息的类型。
     *
     * @param msgType 要设置的消息类型
     */
    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    /**
     * 获取文件名。
     *
     * @return 文件名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置文件名。
     *
     * @param fileName 要设置的文件名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取文件的MD5值。
     *
     * @return 文件的MD5值
     */
    public String getMd5() {
        return md5;
    }

    /**
     * 设置文件的MD5值。
     *
     * @param md5 要设置的文件MD5值
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * 获取文本消息的内容。
     *
     * @return 文本消息的内容
     */
    public String getMsgContent() {
        return msgContent;
    }

    /**
     * 设置文本消息的内容。
     *
     * @param msgContent 要设置的文本消息内容
     */
    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    /**
     * 获取文件的二进制数据。
     *
     * @return 文件的二进制数据
     */
    public byte[] getFileData() {
        return fileData;
    }

    /**
     * 设置文件的二进制数据。
     *
     * @param fileData 要设置的文件二进制数据
     */
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    /**
     * 设置文件的大小。
     *
     * @param fileSize 要设置的文件大小
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * 获取文件的大小。
     *
     * @return 文件的大小
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * 获取额外的文本信息。
     *
     * @return 额外的文本信息
     */
    public String getExtra() {
        return extra;
    }

    /**
     * 设置额外的文本信息。
     *
     * @param extra 要设置的额外文本信息
     */
    public void setExtra(String extra) {
        this.extra = extra;
    }

    /**
     * 获取文件传输的进度。
     *
     * @return 文件传输的进度
     */
    public long getProgress() {
        return progress;
    }

    /**
     * 设置文件传输的进度。
     *
     * @param progress 要设置的文件传输进度
     */
    public void setProgress(long progress) {
        this.progress = progress;
    }

    /**
     * 获取额外的对象信息。
     *
     * @return 额外的对象信息
     */
    public Object getExtraObj() {
        return extraObj;
    }

    /**
     * 设置额外的对象信息。
     *
     * @param extraObj 要设置的额外对象信息
     */
    public void setExtraObj(Object extraObj) {
        this.extraObj = extraObj;
    }

    /**
     * 获取文件的保存路径。
     *
     * @return 文件的保存路径
     */
    public String getSavePath() {
        return savePath;
    }

    /**
     * 设置文件的保存路径。
     *
     * @param savePath 要设置的文件保存路径
     */
    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    /**
     * 将该聊天消息对象转换为JSON字符串。
     *
     * @return 聊天消息对象的JSON字符串表示
     */
    public String toJson() {
        return GsonUtil.toJson(this);
    }
}