package org.mmc.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * json工具类
 */
public class GsonUtil {

    /**
     * 将 JSON 字符串转换为指定类型的对象
     *
     * @param json  要解析的 JSON 字符串
     * @param clazz 目标对象的 Class 类型
     * @param <T>   目标对象的泛型类型
     * @return 解析后的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        // 使用 Gson 库将 JSON 字符串解析为指定类型的对象
        return new Gson().fromJson(json, clazz);
    }

    /**
     * 将 JSON 字符串转换为指定元素类型的列表
     *
     * @param json         要解析的 JSON 字符串
     * @param elementClass 列表元素的 Class 类型
     * @param <T>          列表元素的泛型类型
     * @return 解析后的列表
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> elementClass) {
        // 获取列表类型的 Type 对象
        Type listType = TypeToken.getParameterized(List.class, elementClass).getType();
        // 使用 Gson 库将 JSON 字符串解析为指定元素类型的列表
        return new Gson().fromJson(json, listType);
    }

    /**
     * 将 JSON 字符串转换为指定元素类型的数组
     *
     * @param json         要解析的 JSON 字符串
     * @param elementClass 数组元素的 Class 类型
     * @param <T>          数组元素的泛型类型
     * @return 解析后的数组
     */
    public static <T> T[] fromJsonToArr(String json, Class<T> elementClass) {
        // 使用 Gson 库将 JSON 字符串解析为指定元素类型的数组
        return new Gson().fromJson(json, TypeToken.getArray(elementClass).getType());
    }

    /**
     * 将 JSON 字符串转换为字符串数组
     *
     * @param json 要解析的 JSON 字符串
     * @return 解析后的字符串数组
     */
    public static String[] fromJsonToStrArr(String json) {
        // 使用 Gson 库将 JSON 字符串解析为字符串数组
        return new Gson().fromJson(json, TypeToken.getArray(String.class).getType());
    }

    /**
     * 将对象转换为 JSON 字符串
     *
     * @param obj 要转换的对象
     * @param <T> 对象的泛型类型
     * @return 转换后的 JSON 字符串
     */
    public static <T> String toJson(T obj) {
        // 使用 Gson 库将对象转换为 JSON 字符串
        return new Gson().toJson(obj);
    }

    /**
     * 将指定类的实例转换为 Map
     *
     * @param clazz 要转换的类的 Class 类型
     * @param <T>   类的泛型类型
     * @return 转换后的 Map
     */
    public static <T> Map<String, Object> toMap(Class<T> clazz) {
        // 获取 Map 类型的 Type 对象
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        // 创建 Gson 实例
        Gson gson = new Gson();
        // 将类的实例转换为 JSON 字符串，再将 JSON 字符串解析为 Map
        return gson.fromJson(gson.toJson(clazz), type);
    }

    /**
     * 将 JSON 字符串解析为 JsonObject
     *
     * @param json 要解析的 JSON 字符串
     * @return 解析后的 JsonObject，如果解析失败则返回 null
     */
    public static JsonObject parseJson(String json) {
        try {
            // 使用 Gson 解析 JSON 字符串为 JsonElement
            JsonElement jsonElement = new Gson().fromJson(json, JsonElement.class);
            // 检查解析结果是否为 JsonObject
            if (jsonElement != null && jsonElement.isJsonObject()) {
                return jsonElement.getAsJsonObject();
            }
        } catch (JsonSyntaxException e) {
            // 处理 JSON 语法错误
            System.err.println("JSON 解析错误: " + e.getMessage());
        }
        return null;
    }

    /**
     * 从 JsonObject 中获取指定键对应的 JsonArray
     *
     * @param asJsonObject 要获取 JsonArray 的 JsonObject
     * @param key          要获取的 JsonArray 的键
     * @return 获取到的 JsonArray，如果键对应的值为 null，则返回一个空的 JsonArray
     */
    public static JsonArray getJsonArray(JsonObject asJsonObject, String key) {
        // 检查键对应的值是否不为 null
        if (!asJsonObject.get(key).isJsonNull()) {
            return asJsonObject.get(key).getAsJsonArray();
        }
        return new JsonArray();
    }

    /**
     * 将 JSON 字符串解析为 JsonElement
     *
     * @param json 要解析的 JSON 字符串
     * @return 解析后的 JsonElement，如果解析失败则返回 null
     */
    public static JsonElement parse(String json) {
        try {
            // 使用 Gson 解析 JSON 字符串为 JsonElement
            return new Gson().fromJson(json, JsonElement.class);
        } catch (JsonSyntaxException e) {
            // 处理 JSON 语法错误
            System.err.println("JSON 解析错误: " + e.getMessage());
        }
        return null;
    }

    /**
     * 从 JsonObject 中获取指定键对应的字符串值
     *
     * @param asJsonObject 要获取字符串值的 JsonObject
     * @param key          要获取的字符串值的键
     * @return 获取到的字符串值，如果键对应的值为 null，则返回空字符串
     */
    public static String getString(JsonObject asJsonObject, String key) {
        // 检查键对应的值是否不为 null
        if (!asJsonObject.get(key).isJsonNull()) {
            return asJsonObject.get(key).getAsString();
        }
        return "";
    }

    /**
     * 从 JSON 字符串中获取指定键对应的字符串值
     *
     * @param jsonStr 要解析的 JSON 字符串
     * @param key     要获取的字符串值的键
     * @return 获取到的字符串值
     */
    public static String getString(String jsonStr, String key) {
        // 使用 Gson 将 JSON 字符串解析为 JsonObject 对象
        JsonObject jsonObject = new Gson().fromJson(jsonStr, JsonObject.class);
        // 从 JsonObject 中获取特定字段的值
        return jsonObject.get(key).getAsString();
    }

    /**
     * 将 JSON 字符串写入指定文件
     *
     * @param filePath 文件路径
     * @param joStr    要写入的 JSON 字符串
     */
    public static void putJoStrToFile(String filePath, String joStr) {
        // 检查文件是否存在，如果不存在则创建目录和文件
        if (FileUtils.exists(filePath)) {
            FileUtils.createDir(filePath);
            FileUtils.createFile(filePath);
        }
        // 将 JSON 字符串写入文件
        FileUtils.writeFile(filePath, joStr);
    }

    /**
     * 从指定文件中读取 JSON 字符串并解析为指定类型的对象
     *
     * @param filePath 文件路径
     * @param clazz    目标对象的 Class 类型
     * @param <T>      目标对象的泛型类型
     * @return 解析后的对象，如果文件内容为空则返回 null
     */
    public static <T> T getJoStrForFile(String filePath, Class<T> clazz) {
        // 读取文件内容
        String content = FileUtils.readFile(filePath);
        // 如果文件内容不为空，则将其解析为指定类型的对象
        if (content != null) {
            return fromJson(content, clazz);
        }
        return null;
    }

    /**
     * 将 JSON 字符串解析为 JsonElement
     *
     * @param json 要解析的 JSON 字符串
     * @return 解析后的 JsonElement
     */
    public static JsonElement fromJson(String json) {
        // 使用 Gson 解析 JSON 字符串为 JsonElement
        return new Gson().fromJson(json, JsonElement.class);
    }

    /**
     * 从 JSON 字符串中获取指定键对应的 JsonObject
     *
     * @param json 要解析的 JSON 字符串
     * @param key  要获取的 JsonObject 的键
     * @return 获取到的 JsonObject
     */
    public static JsonObject getJsonObject(String json, String key) {
        // 将 JSON 字符串解析为 JsonElement，再获取其 JsonObject 并获取指定键对应的 JsonObject
        return fromJson(json).getAsJsonObject().get(key).getAsJsonObject();
    }

    /**
     * 从 JSON 字符串中获取指定键对应的 JsonArray
     *
     * @param json 要解析的 JSON 字符串
     * @param key  要获取的 JsonArray 的键
     * @return 获取到的 JsonArray
     */
    public static JsonArray getJsonArray(String json, String key) {
        // 将 JSON 字符串解析为 JsonElement，再获取其 JsonObject 并获取指定键对应的 JsonArray
        return fromJson(json).getAsJsonObject().get(key).getAsJsonArray();
    }

    /**
     * 将 Map 转换为 JsonObject
     *
     * @param map 要转换的 Map
     * @return 转换后的 JsonObject
     */
    public static JsonObject map2Json(Map<String, Object> map) {
        // 使用 Gson 库将 Map 转换为 JsonElement，再获取其 JsonObject
        return new Gson().toJsonTree(map).getAsJsonObject();
    }

    /**
     * 将未格式化的JSON字符串格式化为易读的格式 美化json
     *
     * @param unformattedJson 未格式化的JSON字符串
     * @return 格式化后的JSON字符串
     */
    public static String formattingJson(String unformattedJson) {
        // 创建一个 Gson 实例，设置为美化输出格式
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // 将 JSON 字符串转换为 Map，再将 Map 转换为格式化后的 JSON 字符串
        return gson.toJson(jsonToMap(unformattedJson));
    }

    /**
     * 将 JSON 字符串转换为 Object
     *
     * @param json 要转换的 JSON 字符串
     * @return 转换后的 Object
     */
    private static Object jsonToMap(String json) {
        try {
            // 使用 Gson 库将 JSON 字符串解析为 Object
            return new Gson().fromJson(json, Object.class);
        } catch (Exception e) {
            // 处理异常，抛出非法参数异常
            throw new IllegalArgumentException("Invalid JSON input", e);
        }
    }

    /**
     * @param mapList 数据源
     * @param clazz   目标对象的 Class
     * @param <T>     目标对象的类型
     * @return 转换后的对象列表
     */
    public static <T> List<T> mapList(List<Map<String, Object>> mapList, Class<T> clazz) {
        //将 List<Map<String, Object>> 转换为 List<T> 对象列表
        // 创建一个空的列表用于存储转换后的对象
        List<T> resultList = new ArrayList<>();
        // 创建 Gson 实例
        Gson gson = new Gson();
        // 遍历 Map 列表
        for (Map<String, Object> map : mapList) {
            // 将 Map 转换为 JsonElement，再将 JsonElement 解析为指定类型的对象
            T obj = gson.fromJson(gson.toJsonTree(map), clazz);
            // 将对象添加到结果列表中
            resultList.add(obj);
        }
        return resultList;
    }

    /**
     * 复制对象
     *
     * @param source 要复制的对象
     * @param clazz  目标对象的类
     * @param <T>    目标对象的类型
     * @return 复制后的新对象
     */
    public static <T> T copy(T source, Class<T> clazz) {
        // 创建 Gson 实例
        Gson gson = new Gson();
        // 如果源对象为 null，则返回 null
        if (source == null) {
            return null;
        }
        // 将源对象转换为 JSON 字符串
        String json = gson.toJson(source);
        // 将 JSON 字符串解析为指定类型的对象
        return gson.fromJson(json, clazz);
    }


    /**
     * 将 JSON 字符串转换为 Map
     * 将 JSON 字符串转换为 Map
     * Map(String, Object) map = GsonUtils.jsonToMap(jsonString, String.class, Object.class);
     *
     * @param jsonString JSON 字符串
     * @param keyClass   keyClass
     * @param valueClass valueClass
     * @param <K>        K
     * @param <V>        V
     * @return 转换后的 Map
     */
    public static <K, V> Map<K, V> jsonToMap(String jsonString, Class<K> keyClass, Class<V> valueClass) {
        if (jsonString == null) {
            return new HashMap<>();
        }
        Gson gson = new Gson();
        Type type = TypeToken.getParameterized(Map.class, keyClass, valueClass).getType();
        return gson.fromJson(jsonString, type);
    }

    /**
     * 获取泛型类型的 Type 对象
     *
     * @param clazz 目标对象的类
     * @return 转换后的 Map
     */
    public static <T> Type getType(Class<T> clazz) {
        return TypeToken.get(clazz).getType();
    }

    /**
     * 通用的 JSON 字符串转 AjaxResult"T" 方法
     *
     * @param json      JSON 字符串
     * @param dataClass 泛型 T 的类型
     * @param <T>       泛型类型
     * @return 对象
     */
    public static <T> AjaxResultBean<T> parseJsonToAjaxResult(String json, Class<T> dataClass) {
        Type type = TypeToken.getParameterized(AjaxResultBean.class, dataClass).getType();
        return new Gson().fromJson(json, type);
    }

    /**
     * @param json      JSON 字符串
     * @param dataClass 泛型 T 的类型
     * @param <T>       泛型类型
     * @return 对象
     */
    public static <T> AjaxResultBean<List<T>> parseJsonToAjaxResultList(String json, Class<T> dataClass) {
        Type listType = TypeToken.getParameterized(List.class, dataClass).getType();
        Type type = TypeToken.getParameterized(AjaxResultBean.class, listType).getType();

        return new Gson().fromJson(json, type);
    }

    /**
     * 将 JsonObject 转换为实体类对象
     *
     * @param jsonObject JsonObject 对象
     * @param clazz      目标实体类的 Class 类型
     * @param <T>        泛型类型
     * @return 实体类对象
     */
    public static <T> T fromJsonObject(JsonObject jsonObject, Class<T> clazz) {
        return new Gson().fromJson(jsonObject, clazz);
    }

    /**
     * 将 JsonArray 转换为实体类列表
     *
     * @param jsonArray JsonArray 对象
     * @param clazz     目标实体类的 Class 类型
     * @param <T>       泛型类型
     * @return 实体类列表
     */
    public static <T> List<T> fromJsonArray(JsonArray jsonArray, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        Gson gson = new Gson();
        for (JsonElement jsonElement : jsonArray) {
            list.add(gson.fromJson(jsonElement, clazz));
        }
        return list;
    }

}
