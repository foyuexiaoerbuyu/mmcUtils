package org.mmc.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * aes对称加密使用:
 * //String key = "0123456789abcdef"; // 16字节的密钥
 * String key = AESUtils.generateKey( );
 * String data = "你好!";
 * String encryptedData = AESUtils.encrypt(key, data);
 * System.out.println("加密后的数据：" + encryptedData);
 * String decryptedData = AESUtils.decrypt(key, encryptedData);
 * System.out.println("解密后的数据：" + decryptedData);
 * 密钥必须16位吗?
 * 在AES算法中，密钥长度可以是128位（16字节）、192位（24字节）或256位（32字节）。这三种密钥长度分别对应AES-128、AES-192和AES-256。
 * 当使用AES算法进行加密时，密钥的长度必须与所选择的AES算法相匹配。例如，如果选择AES-128算法，则需要使用一个16字节（128位）的密钥。如果选择AES-256算法，则需要使用一个32字节（256位）的密钥。
 * 如果提供的密钥长度不符合要求，将会抛出异常。对于大多数情况而言，128位的密钥长度已经足够安全，并且更容易处理和传输。因此，通常建议使用128位的密钥来进行AES加密。
 */
public class AESUtils {
    // 定义AES加密算法的名称
    private static final String ALGORITHM = "AES";
    // 定义AES加密算法的变换方式
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * 生成AES加密密钥
     *
     * @return 生成的密钥字符串
     * @throws NoSuchAlgorithmException 如果生成密钥时出现异常
     */
    public static String generateKey() throws NoSuchAlgorithmException {
        // 创建KeyGenerator实例，并指定算法为AES
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        // 初始化KeyGenerator，默认使用128位密钥
        keyGenerator.init(128);
        // 生成密钥
        SecretKey secretKey = keyGenerator.generateKey();

        byte[] encoded = secretKey.getEncoded();
        StringBuilder sb = new StringBuilder();
        for (byte b : encoded) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * 加密数据
     *
     * @param key 加密密钥
     * @param data 待加密的数据
     * @return 加密后的数据
     * @throws Exception 如果加密过程中出现异常
     */
    public static String encrypt(String key, String data) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 解密数据
     *
     * @param key 解密密钥
     * @param encryptedData 待解密的数据
     * @return 解密后的数据
     * @throws Exception 如果解密过程中出现异常
     */
    public static String decrypt(String key, String encryptedData) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }


    /**
     * 另一种加密数据的方式
     *
     * @param data 待加密的数据
     * @param key 加密密钥
     * @return 加密后的数据
     * @throws Exception 如果加密过程中出现异常
     */
    public static String encrypt2(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 另一种解密数据的方式
     *
     * @param encryptedData 待解密的数据
     * @param key 解密密钥
     * @return 解密后的数据
     * @throws Exception 如果解密过程中出现异常
     */
    public static String decrypt2(String encryptedData, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }
}
//补充说明

//对称加密算法和非对称加密算法哪个更强  有什么区别
//2023/8/7 13:43:11
//
//对称加密算法和非对称加密算法在安全性和使用场景上存在一些区别，没有明确的说哪个更强。下面是它们的一些区别和特点：
//
//对称加密算法：
//
//使用相同的密钥对数据进行加密和解密。
//加密和解密速度较快，适用于大量数据的加密。
//密钥管理相对简单，但需要安全地共享密钥。
//安全性取决于密钥的保密性，如果密钥泄漏，则数据容易受到攻击。
//非对称加密算法：
//
//使用公钥进行加密，使用私钥进行解密。
//加密和解密速度较慢，适用于少量数据或密钥交换。
//提供了更高的安全性，因为私钥是保密的，只有持有私钥的人才能解密数据。
//密钥管理复杂，需要处理公钥和私钥的生成、分发和存储。
//可以用于数字签名、密钥交换等其他安全操作。
//对称加密算法通常比非对称加密算法更快，但在密钥的分发和管理方面存在挑战。非对称加密算法提供了更高的安全性，但其计算成本较高，适合用于密钥交换和数字签名等场景。
//
//实际应用中，通常会将对称加密和非对称加密算法结合使用，以充分发挥它们的优势。比如，在安全传输密钥过程中，可以使用非对称加密算法进行密钥交换，然后使用对称加密算法加密通信内容。这样既保证了安全性又提高了效率。