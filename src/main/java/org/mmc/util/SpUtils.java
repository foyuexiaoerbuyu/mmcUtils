package org.mmc.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * SpUtils 类用于管理应用程序的偏好设置，以 JSON 文件的形式存储键值对。
 * 支持保存和读取字符串、对象，删除指定键值对，清除所有数据，检查键是否存在等操作。
 * 提供了默认实例和自定义实例的获取方法。
 */
public class SpUtils {

    // 默认的偏好设置文件名
    private static final String DEFAULT_FILE_NAME = "app_prefs.json";
    // 默认的偏好设置文件保存的基础目录，使用用户主目录下的 MyAppPrefs 文件夹
    private static final String DEFAULT_BASE_DIR = System.getProperty("user.home") + "/MyAppPrefs/";
    // 偏好设置文件对象
    private static File prefsFile;
    // Gson 对象，用于 JSON 数据的序列化和反序列化
    private static Gson gson;
    // 默认的单例实例
    private static SpUtils defaultInstance;

    // 偏好设置文件的文件名
    private String fileName;
    // 偏好设置文件的保存基础目录
    private String baseDir;

    /**
     * 私有构造函数，用于创建 SpUtils 实例。
     *
     * @param fileName 偏好设置文件的文件名
     * @param baseDir  偏好设置文件的保存基础目录
     */
    private SpUtils(String fileName, String baseDir) {
        this.fileName = fileName;
        this.baseDir = baseDir;
        // 初始化相关资源
        init();
    }

    /**
     * 获取默认实例（使用默认文件名和保存路径）。
     * 如果默认实例还未创建，则创建一个新的实例。
     *
     * @return SpUtils 的默认实例
     */
    public static SpUtils getInstance() {
        if (defaultInstance == null) {
            defaultInstance = new SpUtils(DEFAULT_FILE_NAME, DEFAULT_BASE_DIR);
        }
        return defaultInstance;
    }

    /**
     * 获取自定义实例（允许指定文件名，使用默认保存路径）。
     * 如果默认实例还未创建，则创建一个新的实例。
     *
     * @param fileName 偏好设置文件的文件名
     * @return SpUtils 的自定义实例
     */
    public static SpUtils getInstance(String fileName) {
        if (defaultInstance == null) {
            defaultInstance = new SpUtils(fileName, DEFAULT_BASE_DIR);
        }
        return defaultInstance;
    }

    /**
     * 获取自定义实例（允许指定文件名和保存路径）。
     * 如果默认实例还未创建，则创建一个新的实例。
     *
     * @param fileName 偏好设置文件的文件名
     * @param baseDir  偏好设置文件的保存基础目录
     * @return SpUtils 的自定义实例
     */
    public static SpUtils getInstance(String fileName, String baseDir) {
        if (defaultInstance == null) {
            defaultInstance = new SpUtils(fileName, baseDir);
        }
        return defaultInstance;
    }

    /**
     * 初始化方法，用于创建 Gson 对象和偏好设置文件。
     * 如果文件不存在，则创建文件并写入一个空的 Map。
     */
    private void init() {
        // 创建 Gson 对象
        gson = new Gson();
        // 创建偏好设置文件对象
        prefsFile = new File(baseDir, fileName);
        // 检查文件是否存在
        if (!prefsFile.exists()) {
            // 创建文件的父目录
            prefsFile.getParentFile().mkdirs();
            try {
                // 创建文件
                prefsFile.createNewFile();
                // 写入一个空的 Map
                writePrefsMap(new HashMap<>());
            } catch (IOException e) {
                // 打印异常信息
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存字符串到偏好设置中。
     *
     * @param key   键
     * @param value 值
     */
    public void putStr(String key, String value) {
        // 读取偏好设置的 Map
        Map<String, String> prefsMap = readPrefsMap();
        // 将键值对添加到 Map 中
        prefsMap.put(key, value);
        // 将更新后的 Map 写入文件
        writePrefsMap(prefsMap);
    }

    /**
     * 获取字符串，若键不存在则返回空字符串。
     *
     * @param key 键
     * @return 保存的字符串，如果不存在返回空字符串
     */
    public String getStr(String key) {
        // 读取偏好设置的 Map
        Map<String, String> prefsMap = readPrefsMap();
        // 检查 Map 是否为空或键对应的值是否为空
        return prefsMap == null || prefsMap.get(key) == null ? "" : prefsMap.get(key);
    }

    /**
     * 获取字符串，若键不存在则返回指定的默认值。
     *
     * @param key    键
     * @param defVal 默认值
     * @return 保存的字符串，如果不存在返回默认值
     */
    public String getStr(String key, String defVal) {
        // 读取偏好设置的 Map
        Map<String, String> prefsMap = readPrefsMap();
        // 检查 Map 是否为空
        if (prefsMap == null) {
            return defVal;
        }
        // 检查键对应的值是否为空
        return prefsMap.get(key) == null ? defVal : prefsMap.get(key);
    }

    /**
     * 保存对象到偏好设置中，先将对象转换为 JSON 字符串再保存。
     *
     * @param key   键
     * @param value 要保存的对象
     * @param <T>   对象的类型
     */
    public <T> void putObj(String key, T value) {
        // 读取偏好设置的 Map
        Map<String, String> prefsMap = readPrefsMap();
        // 将对象转换为 JSON 字符串
        String json = gson.toJson(value);
        // 将键和 JSON 字符串添加到 Map 中
        prefsMap.put(key, json);
        // 将更新后的 Map 写入文件
        writePrefsMap(prefsMap);
    }

    /**
     * 获取对象，将保存的 JSON 字符串转换为指定类型的对象。
     *
     * @param key  键
     * @param type 对象类型，
     * @param <T>  对象的类型
     * @return 保存的对象，如果不存在返回 null
     */
    public <T> T getObj(String key, Type type) {
        //type 例如 new TypeToken<User>() {}.getType()
        // 读取偏好设置的 Map
        Map<String, String> prefsMap = readPrefsMap();
        // 获取键对应的 JSON 字符串
        String json = prefsMap.get(key);
        // 检查 JSON 字符串是否为空
        if (json == null) {
            return null;
        }
        // 将 JSON 字符串转换为指定类型的对象
        return gson.fromJson(json, type);
    }

    /**
     * 删除指定的键值对。
     *
     * @param key 键
     */
    public void remove(String key) {
        // 读取偏好设置的 Map
        Map<String, String> prefsMap = readPrefsMap();
        // 从 Map 中移除指定的键值对
        prefsMap.remove(key);
        // 将更新后的 Map 写入文件
        writePrefsMap(prefsMap);
    }

    /**
     * 清除所有数据，将偏好设置的 Map 清空并写入文件。
     */
    public void clear() {
        // 读取偏好设置的 Map
        Map<String, String> prefsMap = readPrefsMap();
        // 清空 Map
        prefsMap.clear();
        // 将更新后的 Map 写入文件
        writePrefsMap(prefsMap);
    }

    /**
     * 检查是否存在指定的键。
     *
     * @param key 键
     * @return 是否存在，存在返回 true，不存在返回 false
     */
    public boolean contains(String key) {
        // 读取偏好设置的 Map
        Map<String, String> prefsMap = readPrefsMap();
        // 检查 Map 中是否包含指定的键
        return prefsMap.containsKey(key);
    }

    /**
     * 获取所有键值对。
     *
     * @return 所有键值对组成的 Map
     */
    public Map<String, String> getAll() {
        // 读取偏好设置的 Map
        return readPrefsMap();
    }

    /**
     * 从文件中读取偏好设置的 Map。
     *
     * @return 偏好设置的 Map
     */
    private Map<String, String> readPrefsMap() {
        try (
                // 创建文件输入流
                FileInputStream fis = new FileInputStream(prefsFile);
                // 创建输入流读取器，使用 UTF-8 编码
                InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)
        ) {
            // 将 JSON 数据转换为 Map
            return gson.fromJson(reader, new TypeToken<Map<String, String>>() {
            }.getType());
        } catch (IOException e) {
            // 打印异常信息
            e.printStackTrace();
            // 返回一个空的 Map
            return new HashMap<>();
        }
    }

    /**
     * 将偏好设置的 Map 写入文件。
     *
     * @param prefsMap 偏好设置的 Map
     */
    private void writePrefsMap(Map<String, String> prefsMap) {
        try (
                // 创建文件输出流
                FileOutputStream fos = new FileOutputStream(prefsFile);
                // 创建输出流写入器，使用 UTF-8 编码
                OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)
        ) {
            // 将 Map 转换为 JSON 数据并写入文件
            gson.toJson(prefsMap, writer);
        } catch (IOException e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }
}