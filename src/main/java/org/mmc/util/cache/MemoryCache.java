package org.mmc.util.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 简单的内存缓存工具类，用于在内存中存储和管理缓存数据，并支持数据的读写、清除和过期处理等操作。
 */
public class MemoryCache {
    /**
     * 键值对集合，用于存储缓存数据，键为字符串，值为Entity对象
     */
    private final static Map<String, Entity> map = new HashMap<>();
    /**
     * 定时器线程池，用于清除过期缓存，使用单线程调度执行器
     */
    private final static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * 添加缓存，不设置过期时间。
     *
     * @param key  键
     * @param data 值
     */
    public synchronized static void put(String key, Object data) {
        // 调用带过期时间的put方法，过期时间设置为0表示无限长
        MemoryCache.put(key, data, 0);
    }

    /**
     * 添加缓存，并设置过期时间。
     *
     * @param key    键
     * @param data   值
     * @param expire 过期时间，单位：毫秒， 0表示无限长
     */
    public synchronized static void put(String key, Object data, long expire) {
        // 清除原键值对
        MemoryCache.remove(key);
        // 设置过期时间
        if (expire > 0) {
            // 安排一个定时任务，在指定的过期时间后执行
            Future future = executor.schedule(() -> {
                // 过期后清除该键值对
                synchronized (MemoryCache.class) {
                    map.remove(key);
                }
            }, expire, TimeUnit.MILLISECONDS);
            // 将键值对和定时器Future封装成Entity对象并添加到Map中
            map.put(key, new Entity(data, future));
        } else {
            // 不设置过期时间，将键值对和null封装成Entity对象并添加到Map中
            map.put(key, new Entity(data, null));
        }
    }

    /**
     * 读取缓存。
     *
     * @param key 键
     * @return 缓存的值，如果键不存在则返回null
     */
    public synchronized static <T> T get(String key) {
        // 从Map中获取Entity对象
        Entity entity = map.get(key);
        // 如果Entity对象不为空，则返回其值，否则返回null
        return entity == null ? null : (T) entity.value;
    }

    /**
     * 清除缓存。
     *
     * @param key 键
     * @return 被清除的缓存的值，如果键不存在则返回null
     */
    public synchronized static <T> T remove(String key) {
        // 清除原缓存数据
        Entity entity = map.remove(key);
        if (entity == null) {
            return null;
        }
        // 清除原键值对定时器
        if (entity.future != null) {
            entity.future.cancel(true);
        }
        // 返回被清除的缓存的值
        return (T) entity.value;
    }

    /**
     * 查询当前缓存的键值对数量。
     *
     * @return 缓存的键值对数量
     */
    public synchronized static int size() {
        // 返回Map的大小
        return map.size();
    }

    /**
     * 缓存实体类，用于封装缓存的值和定时器Future。
     */
    private static class Entity {
        /**
         * 键值对的value
         */
        public Object value;
        /**
         * 定时器Future
         */
        public Future future;

        /**
         * 构造函数，初始化Entity对象。
         *
         * @param value  键值对的value
         * @param future 定时器Future
         */
        public Entity(Object value, Future future) {
            this.value = value;
            this.future = future;
        }
    }
}