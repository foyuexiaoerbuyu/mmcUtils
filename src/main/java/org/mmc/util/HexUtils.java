package org.mmc.util;

import java.math.BigInteger;

/**
 * 16进制工具类
 */
public class HexUtils {

    /**
     * https://blog.csdn.net/womenyiqilalala/article/details/103732195
     * 将 4字节的16进制字符串，转换为32位带符号的十进制浮点型
     * @param str 4字节 16进制字符
     * @return 转换后的32位带符号十进制浮点型
     */
    public static float hexToFloat(String str) {
        // 将十六进制字符串转换为 BigInteger，再转换为 int 类型，最后使用 Float.intBitsToFloat 方法转换为浮点数
        return Float.intBitsToFloat(new BigInteger(str, 16).intValue());
    }

    /**
     * https://blog.csdn.net/womenyiqilalala/article/details/103732195
     * 将带符号的32位浮点数装换为16进制
     * @param value 带符号的32位浮点数
     * @return 转换后的十六进制字符串
     */
    public static String folatToHexString(Float value) {
        // 将浮点数转换为 int 类型的位表示，再转换为十六进制字符串
        return Integer.toHexString(Float.floatToIntBits(value));
    }

    /**
     * 字节转十六进制
     * @param b 需要进行转换的byte字节
     * @return 转换后的Hex字符串
     */
    public static String byteToHex(byte b) {
        // 将字节与 0xFF 进行按位与操作，再转换为十六进制字符串
        String hex = Integer.toHexString(b & 0xFF);
        // 如果转换后的十六进制字符串长度小于 2，在前面补 0
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 字节数组转16进制
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        // 创建一个 StringBuffer 用于拼接十六进制字符串
        StringBuffer sb = new StringBuffer();
        // 遍历字节数组
        for (int i = 0; i < bytes.length; i++) {
            // 将字节转换为十六进制字符串
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            // 如果转换后的十六进制字符串长度小于 2，在前面补 0
            if (hex.length() < 2) {
                sb.append(0);
            }
            // 将十六进制字符串添加到 StringBuffer 中
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Hex字符串转byte
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        // 将十六进制字符串按十六进制解析为整数，再强制转换为 byte 类型
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * hex字符串转byte数组
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex) {
        // 获取十六进制字符串的长度
        int hexlen = inHex.length();
        byte[] result;
        // 判断十六进制字符串的长度是否为奇数
        if (hexlen % 2 == 1) {
            // 奇数长度，在前面补 0，长度加 1
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            // 偶数长度
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        // 遍历十六进制字符串，每两个字符转换为一个字节
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }
}