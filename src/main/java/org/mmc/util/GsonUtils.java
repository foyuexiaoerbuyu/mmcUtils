package org.mmc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.mmc.util.annotation.NonNull;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/src/main/java/com/blankj/utilcode/util/GsonUtils.java
 * <p>
 * Gson工具类，用于提供线程安全的Gson实例
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2018/04/05
 *     desc  : utils about gson
 * </pre>
 */
public final class GsonUtils {
    // 默认Gson实例的键名
    private static final String KEY_DEFAULT = "defaultGson";
    // 代理Gson实例的键名
    private static final String KEY_DELEGATE = "delegateGson";
    // 日志工具Gson实例的键名
    private static final String KEY_LOG_UTILS = "logUtilsGson";

    // 存储不同类型的Gson实例的映射表，使用ConcurrentHashMap以支持线程安全
    private static final Map<String, Gson> GSONS = new ConcurrentHashMap<>();

    /**
     * 私有构造方法，防止实例化
     *
     * @throws UnsupportedOperationException 如果尝试实例化该工具类，将抛出此异常
     */
    private GsonUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Set the delegate of {@link Gson}.
     *
     * @param delegate The delegate of {@link Gson}.
     */
    public static void setGsonDelegate(Gson delegate) {
        if (delegate == null) return;
        GSONS.put(KEY_DELEGATE, delegate);
    }

    /**
     * Set the {@link Gson} with key.
     *
     * @param key  The key.
     * @param gson The {@link Gson}.
     */
    public static void setGson(final String key, final Gson gson) {
        if (StringUtil.isEmpty(key) || gson == null) return;
        GSONS.put(key, gson);
    }

    /**
     * Return the {@link Gson} with key.
     *
     * @param key The key.
     * @return the {@link Gson} with key
     */
    public static Gson getGson(final String key) {
        return GSONS.get(key);
    }

    /**
     * Return the {@link Gson}.
     *
     * @return the {@link Gson}
     */
    public static Gson getGson() {
        Gson gsonDelegate = GSONS.get(KEY_DELEGATE);
        if (gsonDelegate != null) {
            return gsonDelegate;
        }
        Gson gsonDefault = GSONS.get(KEY_DEFAULT);
        if (gsonDefault == null) {
            gsonDefault = createGson();
            GSONS.put(KEY_DEFAULT, gsonDefault);
        }
        return gsonDefault;
    }

    /**
     * Serializes an object into json.
     *
     * @param object The object to serialize.
     * @return object serialized into json.
     */
    public static String toJson(final Object object) {
        return toJson(getGson(), object);
    }

    /**
     * Serializes an object into json.
     *
     * @param src       The object to serialize.
     * @param typeOfSrc The specific genericized type of src.
     * @return object serialized into json.
     */
    public static String toJson(final Object src, @NonNull final Type typeOfSrc) {
        return toJson(getGson(), src, typeOfSrc);
    }

    /**
     * Serializes an object into json.
     *
     * @param gson   The gson.
     * @param object The object to serialize.
     * @return object serialized into json.
     */
    public static String toJson(@NonNull final Gson gson, final Object object) {
        return gson.toJson(object);
    }

    /**
     * Serializes an object into json.
     *
     * @param gson      The gson.
     * @param src       The object to serialize.
     * @param typeOfSrc The specific genericized type of src.
     * @return object serialized into json.
     */
    public static String toJson(@NonNull final Gson gson, final Object src, @NonNull final Type typeOfSrc) {
        return gson.toJson(src, typeOfSrc);
    }

    /**
     * Converts {@link String} to given type.
     *
     * @param json The json to convert.
     * @param type Type json will be converted to.
     * @param <T>  泛型
     * @return instance of type
     */
    public static <T> T fromJson(final String json, @NonNull final Class<T> type) {
        return fromJson(getGson(), json, type);
    }

    /**
     * Converts {@link String} to given type.
     *
     * @param json the json to convert.
     * @param type type type json will be converted to.
     * @param <T>  泛型
     * @return instance of type
     */
    public static <T> T fromJson(final String json, @NonNull final Type type) {
        return fromJson(getGson(), json, type);
    }

    /**
     * Converts {@link Reader} to given type.
     *
     * @param reader the reader to convert.
     * @param type   type type json will be converted to.
     * @param <T>    泛型
     * @return instance of type
     */
    public static <T> T fromJson(@NonNull final Reader reader, @NonNull final Class<T> type) {
        return fromJson(getGson(), reader, type);
    }

    /**
     * Converts {@link Reader} to given type.
     *
     * @param reader the reader to convert.
     * @param type   type type json will be converted to.
     * @param <T>    泛型
     * @return instance of type
     */
    public static <T> T fromJson(@NonNull final Reader reader, @NonNull final Type type) {
        return fromJson(getGson(), reader, type);
    }

    /**
     * Converts {@link String} to given type.
     *
     * @param gson The gson.
     * @param json The json to convert.
     * @param type Type json will be converted to.
     * @param <T>  泛型
     * @return instance of type
     */
    public static <T> T fromJson(@NonNull final Gson gson, final String json, @NonNull final Class<T> type) {
        return gson.fromJson(json, type);
    }

    /**
     * Converts {@link String} to given type.
     *
     * @param gson The gson.
     * @param json the json to convert.
     * @param type type type json will be converted to.
     * @param <T>  泛型
     * @return instance of type
     */
    public static <T> T fromJson(@NonNull final Gson gson, final String json, @NonNull final Type type) {
        return gson.fromJson(json, type);
    }

    /**
     * Converts {@link Reader} to given type.
     *
     * @param gson   The gson.
     * @param reader the reader to convert.
     * @param type   type type json will be converted to.
     * @param <T>    泛型
     * @return instance of type
     */
    public static <T> T fromJson(@NonNull final Gson gson, final Reader reader, @NonNull final Class<T> type) {
        return gson.fromJson(reader, type);
    }

    /**
     * Converts {@link Reader} to given type.
     *
     * @param gson   The gson.
     * @param reader the reader to convert.
     * @param type   type type json will be converted to.
     * @param <T>    泛型
     * @return instance of type
     */
    public static <T> T fromJson(@NonNull final Gson gson, final Reader reader, @NonNull final Type type) {
        return gson.fromJson(reader, type);
    }

    /**
     * Return the type of {@link List} with the {@code type}.
     *
     * @param type The type.
     * @return the type of {@link List} with the {@code type}
     */
    public static Type getListType(@NonNull final Type type) {
        return TypeToken.getParameterized(List.class, type).getType();
    }

    /**
     * Return the type of {@link Set} with the {@code type}.
     *
     * @param type The type.
     * @return the type of {@link Set} with the {@code type}
     */
    public static Type getSetType(@NonNull final Type type) {
        return TypeToken.getParameterized(Set.class, type).getType();
    }

    /**
     * Return the type of map with the {@code keyType} and {@code valueType}.
     *
     * @param keyType   The type of key.
     * @param valueType The type of value.
     * @return the type of map with the {@code keyType} and {@code valueType}
     */
    public static Type getMapType(@NonNull final Type keyType, @NonNull final Type valueType) {
        return TypeToken.getParameterized(Map.class, keyType, valueType).getType();
    }

    /**
     * Return the type of array with the {@code type}.
     *
     * @param type The type.
     * @return the type of map with the {@code type}
     */
    public static Type getArrayType(@NonNull final Type type) {
        return TypeToken.getArray(type).getType();
    }

    /**
     * Return the type of {@code rawType} with the {@code typeArguments}.
     *
     * @param rawType       The raw type.
     * @param typeArguments The type of arguments.
     * @return the type of map with the {@code type}
     */
    public static Type getType(@NonNull final Type rawType, @NonNull final Type... typeArguments) {
        return TypeToken.getParameterized(rawType, typeArguments).getType();
    }

    /**
     * Get gson for log utils.
     *
     * @return gson for log utils
     */
    static Gson getGson4LogUtils() {
        Gson gson4LogUtils = GSONS.get(KEY_LOG_UTILS);
        if (gson4LogUtils == null) {
            gson4LogUtils = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            GSONS.put(KEY_LOG_UTILS, gson4LogUtils);
        }
        return gson4LogUtils;
    }

    private static Gson createGson() {
        return new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    }
}
