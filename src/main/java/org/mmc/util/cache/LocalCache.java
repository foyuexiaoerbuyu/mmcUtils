package org.mmc.util.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mmc.util.TestUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 本地缓存类，用于将数据缓存到本地文件中，并支持数据的读写、清空和过期检查等操作。
 */
public class LocalCache {
    // 用于存储缓存数据的Map，键为字符串，值为对象
    private Map<String, Object> cacheMap;
    // 缓存文件的路径
    private String cacheFilePath;
    // 缓存的过期时间，单位为毫秒
    private long cacheExpirationTime;

    /**
     * 构造函数，初始化缓存对象，不设置缓存过期时间。
     *
     * @param cacheFilePath 缓存文件的路径
     */
    public LocalCache(String cacheFilePath) {
        this.cacheMap = new HashMap<>();
        this.cacheFilePath = cacheFilePath;
    }

    /**
     * 构造函数，初始化缓存对象，并设置缓存过期时间。
     *
     * @param cacheFilePath       缓存文件的路径
     * @param cacheExpirationTime 缓存的过期时间，单位为毫秒
     */
    public LocalCache(String cacheFilePath, long cacheExpirationTime) {
        this.cacheMap = new HashMap<>();
        this.cacheFilePath = cacheFilePath;
        this.cacheExpirationTime = cacheExpirationTime;
    }

    /**
     * 主方法，用于测试本地缓存类的功能。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 创建一个本地缓存对象，缓存文件名为cache.json，缓存有效期为1小时（3600000毫秒）
        LocalCache cache = new LocalCache("cache.json", 3600000);
        // 向缓存中添加数据
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", new TestUtils.Person("zhangsan", 12));

        // 从缓存中获取数据并打印
        System.out.println(cache.get("key3"));
        // 将获取到的数据转换为Person对象并打印姓名
        TestUtils.Person o = (TestUtils.Person) cache.get("key3");
        System.out.println(o.getName());

        // 清空缓存
        cache.clearCache();
    }

    /**
     * 向缓存中添加数据，并将缓存数据保存到文件中。
     *
     * @param key   缓存的键
     * @param value 缓存的值
     */
    public void put(String key, Object value) {
        // 将键值对添加到缓存Map中
        cacheMap.put(key, value);
        // 保存缓存数据到文件
        saveCacheToFile();
    }

    /**
     * 从缓存中获取数据。
     *
     * @param key 缓存的键
     * @return 缓存的值，如果键不存在则返回null
     */
    public Object get(String key) {
        // 检查缓存Map中是否包含该键
        if (cacheMap.containsKey(key)) {
            // 如果包含，则返回对应的值
            return cacheMap.get(key);
        }
        // 否则返回null
        return null;
    }

    /**
     * 将缓存数据保存到文件中。
     */
    private void saveCacheToFile() {
        try (Writer writer = new FileWriter(cacheFilePath)) {
            // 创建一个Gson对象
            Gson gson = new GsonBuilder().create();
            // 将缓存Map中的数据以JSON格式写入文件
            gson.toJson(cacheMap, writer);
        } catch (IOException e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }

    /**
     * 从文件中加载缓存数据。
     */
    public void loadCacheFromFile() {
        try (Reader reader = new FileReader(cacheFilePath)) {
            // 创建一个Gson对象
            Gson gson = new Gson();
            // 从文件中读取JSON数据并转换为Map对象
            cacheMap = gson.fromJson(reader, Map.class);
        } catch (FileNotFoundException e) {
            // 文件不存在或读取错误，不做处理
        } catch (IOException e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }

    /**
     * 清空缓存，并将清空后的缓存数据保存到文件中。
     */
    public void clearCache() {
        // 清空缓存Map
        cacheMap.clear();
        // 保存缓存数据到文件
        saveCacheToFile();
    }

    /**
     * 检查缓存是否过期。
     *
     * @return 如果缓存过期则返回true，否则返回false
     */
    public boolean isCacheExpired() {
        // 创建一个File对象，指向缓存文件
        File file = new File(cacheFilePath);
        // 检查文件是否存在
        if (file.exists()) {
            // 获取当前时间
            long currentTime = System.currentTimeMillis();
            // 获取文件的最后修改时间
            long lastModified = file.lastModified();
            // 计算时间差，并判断是否超过缓存过期时间
            return (currentTime - lastModified) > cacheExpirationTime;
        }
        // 如果文件不存在，则认为缓存过期
        return true;
    }
}