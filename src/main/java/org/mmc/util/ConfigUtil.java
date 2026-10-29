package org.mmc.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>Properties配置文件读写工具类，属于 mmcUtils 工具包</p>
 *
 * <p>功能清单：</p>
 * <ul>
 *     <li>支持自定义配置文件绝对/相对路径，自由指定保存位置</li>
 *     <li>内置AES-128对称加密，可开关加密，仅对value加密，key保持明文</li>
 *     <li>后台定时监听文件变更，自动重载配置，重载失败保留旧配置，不丢配置</li>
 *     <li>提供多类型读取方法：String、int、boolean、long、double，自带默认值兜底</li>
 *     <li>支持对应类型写入方法：setInt / setBoolean / setLong / setDouble</li>
 *     <li>支持注册多个配置变更监听器，文件重载完成后触发回调通知</li>
 * </ul>
 *
 * <p>注意事项：</p>
 * <ol>
 *     <li>AES密钥默认硬编码仅用于测试，生产环境建议从环境变量、启动参数读取密钥，禁止硬编码上线</li>
 *     <li>加密只对配置值生效，配置key始终明文存储</li>
 *     <li>监听线程为守护线程，JVM退出自动结束；也可手动调用 {@link #stopWatch()} 停止监听</li>
 *     <li>配置文件格式为标准 .properties，不支持yaml、json</li>
 *     <li>回调监听器异常会内部捕获，单个监听器报错不会阻断其他监听器执行</li>
 * </ol>
 *
 * @author 手可摘星辰
 * @version 1.4
 * @since 1.4
 */
public class ConfigUtil {

    /**
     * AES-128 密钥，长度固定16位
     * <p>【测试用】生产环境务必替换为外部读取方式，不要硬编码</p>
     */
    private static final String SECRET_KEY = "1234567890abcdef";
    /**
     * AES加密算法名称
     */
    private static final String AES_ALGORITHM = "AES";

    /**
     * 配置文件对象
     */
    private final File configFile;
    /**
     * 内存配置缓存
     */
    private final Properties props = new Properties();
    /**
     * 是否开启value加密：true开启AES加密，false明文存储
     */
    private boolean enableEncrypt = false;
    /**
     * 配置文件上一次修改时间戳，用于判断文件是否变更
     */
    private long lastModifyTime = 0L;
    /**
     * 文件定时监听任务线程池
     */
    private ScheduledExecutorService watcher;
    /**
     * 文件变更检测间隔，单位毫秒
     */
    private final long watchIntervalMs;

    /**
     * 配置变更监听器集合，支持多个监听器同时注册
     */
    private final List<ConfigChangeListener> listenerList = new ArrayList<>();

    /**
     * <p>配置文件变更回调接口，函数式接口，支持lambda写法</p>
     * <p>当外部修改配置文件、工具类重载完成配置后触发该回调</p>
     *
     * @author 手可摘星辰
     * @since 1.4
     */
    @FunctionalInterface
    public interface ConfigChangeListener {
        /**
         * 配置文件重载完成后的回调方法
         *
         * @param configUtil 当前ConfigUtil实例，可以直接读取最新配置
         */
        void onChanged(ConfigUtil configUtil);
    }

    /**
     * 默认构造器
     * <p>配置文件默认路径：config.properties，检测间隔默认2000ms</p>
     */
    public ConfigUtil() {
        this("config.properties", 2000);
    }

    /**
     * 指定配置文件路径构造器，默认检测间隔2000ms
     *
     * @param filePath 配置文件路径，支持相对路径或绝对路径
     */
    public ConfigUtil(String filePath) {
        this(filePath, 2000);
    }

    /**
     * 全参构造器
     *
     * @param filePath       配置文件路径，支持相对路径或绝对路径
     * @param watchIntervalMs 文件变更定时检测间隔，单位毫秒
     */
    public ConfigUtil(String filePath, long watchIntervalMs) {
        this.configFile = new File(filePath);
        this.watchIntervalMs = watchIntervalMs;
        load();
    }

    /**
     * 添加配置变更监听器，支持多次添加多个监听器
     *
     * @param listener 变更回调监听器，lambda {@link ConfigChangeListener}
     */
    public void addChangeListener(ConfigChangeListener listener) {
        if (listener != null) {
            listenerList.add(listener);
        }
    }

    /**
     * 移除指定的配置变更监听器
     *
     * @param listener 需要移除的监听器实例
     */
    public void removeChangeListener(ConfigChangeListener listener) {
        listenerList.remove(listener);
    }

    /**
     * 清空全部注册的配置变更监听器
     */
    public void clearChangeListener() {
        listenerList.clear();
    }

    /**
     * 通知所有监听器，触发变更回调
     * <p>内部捕获单个监听器异常，避免一个报错导致全部回调中断</p>
     */
    private void notifyListener() {
        for (ConfigChangeListener listener : listenerList) {
            try {
                listener.onChanged(this);
            } catch (Exception e) {
                System.err.println("[ConfigUtil] 配置变更回调执行异常: " + e.getMessage());
            }
        }
    }

    /**
     * 手动加载配置文件，读取文件到内存缓存
     * <p>加载失败时，保留原有内存配置，不会清空</p>
     */
    public void load() {
        if (!configFile.exists()) {
            return;
        }
        try (FileInputStream fis = new FileInputStream(configFile)) {
            Properties temp = new Properties();
            temp.load(fis);
            // 加载成功才替换内存配置
            props.clear();
            props.putAll(temp);
            lastModifyTime = configFile.lastModified();
        } catch (Exception e) {
            System.err.println("[ConfigUtil] 加载配置文件失败，保留旧配置：" + configFile.getAbsolutePath());
            e.printStackTrace();
        }
    }

    /**
     * 将内存中的配置写入本地配置文件
     * <p>写入完成自动更新文件修改时间，防止立刻触发重载</p>
     *
     * @throws RuntimeException 文件写入IO异常时抛出
     */
    public void save() {
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            props.store(fos, "auto config file | mmcUtils");
            lastModifyTime = configFile.lastModified();
        } catch (Exception e) {
            throw new RuntimeException("保存配置文件失败：" + configFile.getAbsolutePath(), e);
        }
    }

    /**
     * 开启文件变更监听，后台定时检测文件修改
     * <p>监听线程为守护线程，JVM退出自动关闭；重复调用不会重复创建任务</p>
     */
    public void startWatch() {
        if (watcher != null && !watcher.isShutdown()) {
            return;
        }
        watcher = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "config-file-watch-thread");
            t.setDaemon(true);
            return t;
        });
        watcher.scheduleAtFixedRate(this::checkFileChange, watchIntervalMs, watchIntervalMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止文件变更监听，关闭定时任务
     */
    public void stopWatch() {
        if (watcher != null) {
            watcher.shutdown();
            watcher = null;
        }
    }

    /**
     * 定时检测文件是否修改，发生变更则重载配置并触发回调
     */
    private void checkFileChange() {
        if (!configFile.exists()) {
            return;
        }
        long currentModify = configFile.lastModified();
        if (currentModify > lastModifyTime) {
            System.out.printf("[ConfigUtil] 检测到配置文件变更，开始重载：%s%n", configFile.getAbsolutePath());
            load();
            notifyListener();
        }
    }

    /**
     * 设置是否开启加密
     * <p>开启后，调用set方法存入的value会AES加密存储；get读取时自动解密</p>
     *
     * @param enable true开启加密，false明文存储
     */
    public void setEnableEncrypt(boolean enable) {
        this.enableEncrypt = enable;
    }

    /**
     * 获取当前加密开关状态
     *
     * @return true开启加密，false明文
     */
    public boolean isEnableEncrypt() {
        return enableEncrypt;
    }

    /**
     * 读取字符串配置值，自动解密（加密开启时）
     *
     * @param key 配置key
     * @return 配置value，不存在返回null
     */
    public String get(String key) {
        String val = props.getProperty(key);
        if (val == null) return null;
        return enableEncrypt ? decrypt(val) : val;
    }

    /**
     * 读取字符串配置值，带默认值兜底
     *
     * @param key          配置key
     * @param defaultValue 不存在或者为空时返回默认值
     * @return 配置value
     */
    public String get(String key, String defaultValue) {
        String val = get(key);
        return val == null ? defaultValue : val;
    }

    /**
     * 读取int类型配置，解析失败/不存在返回默认值
     *
     * @param key          配置key
     * @param defaultValue 默认值
     * @return 解析后的int值
     */
    public int getInt(String key, int defaultValue) {
        String val = get(key);
        if (val == null || val.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 读取boolean类型配置
     * <p>支持识别：true / 1 / yes / on （忽略大小写）为true；其余为false</p>
     *
     * @param key          配置key
     * @param defaultValue 默认值
     * @return boolean值
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String val = get(key);
        if (val == null || val.trim().isEmpty()) {
            return defaultValue;
        }
        String lower = val.trim().toLowerCase();
        return "true".equals(lower) || "1".equals(lower) || "yes".equals(lower) || "on".equals(lower);
    }

    /**
     * 读取long类型配置，解析失败/不存在返回默认值
     *
     * @param key          配置key
     * @param defaultValue 默认值
     * @return 解析后的long值
     */
    public long getLong(String key, long defaultValue) {
        String val = get(key);
        if (val == null || val.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 读取double类型配置，解析失败/不存在返回默认值
     *
     * @param key          配置key
     * @param defaultValue 默认值
     * @return 解析后的double值
     */
    public double getDouble(String key, double defaultValue) {
        String val = get(key);
        if (val == null || val.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 设置字符串配置，自动加密（加密开启时）
     *
     * @param key   配置key
     * @param value 配置value
     */
    public void set(String key, String value) {
        String storeVal;
        if (enableEncrypt && value != null) {
            storeVal = encrypt(value);
        } else {
            storeVal = value;
        }
        props.setProperty(key, storeVal);
    }

    /**
     * 设置int类型配置
     *
     * @param key   配置key
     * @param value int值
     */
    public void setInt(String key, int value) {
        set(key, String.valueOf(value));
    }

    /**
     * 设置boolean类型配置
     *
     * @param key   配置key
     * @param value boolean值
     */
    public void setBoolean(String key, boolean value) {
        set(key, String.valueOf(value));
    }

    /**
     * 设置long类型配置
     *
     * @param key   配置key
     * @param value long值
     */
    public void setLong(String key, long value) {
        set(key, String.valueOf(value));
    }

    /**
     * 设置double类型配置
     *
     * @param key   配置key
     * @param value double值
     */
    public void setDouble(String key, double value) {
        set(key, String.valueOf(value));
    }

    /**
     * 删除指定key的配置项
     *
     * @param key 配置key
     */
    public void remove(String key) {
        props.remove(key);
    }

    /**
     * AES加密明文，返回十六进制密文字符串
     *
     * @param content 原始明文
     * @return 十六进制密文
     * @throws RuntimeException 加密异常
     */
    private String encrypt(String content) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return byte2Hex(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    /**
     * AES解密十六进制密文，返回原始明文
     *
     * @param hexStr 十六进制密文字符串
     * @return 解密后的明文
     * @throws RuntimeException 解密失败（密钥不对、密文损坏、加密开关不匹配）
     */
    private String decrypt(String hexStr) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] bytes = hex2Byte(hexStr);
            byte[] origin = cipher.doFinal(bytes);
            return new String(origin, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("解密失败，检查密钥或者加密开关", e);
        }
    }

    /**
     * byte数组转十六进制字符串
     *
     * @param bytes 字节数组
     * @return 小写十六进制字符串
     */
    private String byte2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String temp;
        for (byte b : bytes) {
            temp = Integer.toHexString(b & 0xFF);
            if (temp.length() == 1) {
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    /**
     * 十六进制字符串转回byte数组
     *
     * @param hex 十六进制字符串
     * @return 字节数组
     */
    private byte[] hex2Byte(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}
