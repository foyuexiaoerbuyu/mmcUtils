package org.mmc.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 该类提供了一系列用于操作列表的工具方法，包括元素的删除、过滤、列表与数组的转换、列表合并、分组等操作。
 */
public class ListUtils {
    /**
     * 从列表中移除指定元素。
     * 该方法使用 Java 8 的 removeIf 方法安全地移除列表中与指定元素相等的所有元素。
     * 如果列表为 null 或为空，则不进行任何操作直接返回。
     *
     * @param list    要操作的列表，可能为 null
     * @param element 要移除的元素
     * @param <T>     列表元素的类型
     */
    public static <T> void removeItems(List<T> list, T element) {
        // 检查列表是否为 null 或为空，如果是则直接返回
        if (list == null || list.isEmpty()) return;
        // 使用迭代器的 removeIf 方法安全地删除元素
        list.removeIf(current -> current.equals(element));
    }

    /**
     * 根据指定条件从列表中移除元素。
     * 该方法使用迭代器遍历列表，当元素满足指定条件时将其移除。
     * 如果列表为 null 或为空，则不进行任何操作直接返回。
     *
     * @param list      要操作的列表，可能为 null
     * @param condition 用于判断元素是否需要移除的条件
     * @param <T>       列表元素的类型
     */
    public static <T> void removeItems(List<T> list, Condition<T> condition) {
        // 检查列表是否为 null 或为空，如果是则直接返回
        if (list == null || list.isEmpty()) return;
        // 获取列表的迭代器
        Iterator<T> iterator = list.iterator();
        // 遍历列表
        while (iterator.hasNext()) {
            // 获取当前元素
            T item = iterator.next();
            // 判断当前元素是否满足条件
            if (condition.matches(item)) {
                // 如果满足条件，则使用迭代器的 remove 方法移除该元素
                iterator.remove();
            }
        }
    }

    /**
     * 根据指定条件从列表中移除元素。
     * 该方法是 JDK 1.8 之后推荐的方式，使用迭代器遍历列表，当元素满足指定条件时将其移除。
     * 如果列表为 null 或为空，则不进行任何操作直接返回。
     *
     * @param list     要操作的列表，可能为 null
     * @param iRemove  用于判断元素是否需要移除的条件
     * @param <T>      列表元素的类型
     */
    public static <T> void removeElements(List<T> list, Condition<T> iRemove) {
        // 检查列表是否为 null 或为空，如果是则直接返回
        if (list == null || list.size() == 0) return;
        // 获取列表的迭代器
        Iterator<T> iterator = list.iterator();
        // 遍历列表
        while (iterator.hasNext()) {
            // 获取当前元素
            T current = iterator.next();
            // 判断当前元素是否满足条件
            if (iRemove.matches(current)) {
                // 如果满足条件，则使用迭代器的 remove 方法移除该元素
                iterator.remove();
            }
        }
    }

    /**
     * 根据指定条件过滤列表中的元素。
     * 该方法遍历列表，将满足指定条件的元素添加到一个新列表中并返回。
     * 如果列表为 null 或为空，则返回 null。
     *
     * @param list     要过滤的列表，可能为 null
     * @param iRemove  用于判断元素是否需要保留的条件
     * @param <T>      列表元素的类型
     * @return 过滤后的新列表，如果原列表为 null 或为空则返回 null
     */
    public static <T> List<T> filterElements(List<T> list, Condition<T> iRemove) {
        // 检查列表是否为 null 或为空，如果是则返回 null
        if (list == null || list.size() == 0) return null;
        // 获取列表的迭代器
        Iterator<T> iterator = list.iterator();
        // 创建一个新的 ArrayList 用于存储过滤后的元素
        ArrayList<T> newlist = new ArrayList<>();
        // 遍历列表
        while (iterator.hasNext()) {
            // 获取当前元素
            T current = iterator.next();
            // 判断当前元素是否满足条件
            if (iRemove.matches(current)) {
                // 如果满足条件，则将该元素添加到新列表中
                newlist.add(current);
            }
        }
        // 返回过滤后的新列表
        return newlist;
    }

    /**
     * 定义一个条件接口，用于判断元素是否满足特定条件。
     * 实现该接口的类需要实现 matches 方法，该方法接受一个元素并返回一个布尔值，表示该元素是否满足条件。
     *
     * @param <T> 元素的类型
     */
    public interface Condition<T> {
        /**
         * 判断指定元素是否满足条件。
         *
         * @param current 要判断的元素
         * @return 如果元素满足条件则返回 true，否则返回 false
         */
        boolean matches(T current);
    }

    /**
     * 将 List 转换为数组。
     * 该方法将列表中的元素复制到指定的数组中。
     * 如果列表为 null，则返回 null；如果传入的数组为 null，则抛出 IllegalArgumentException 异常。
     *
     * @param list  要转换的 List，可能为 null
     * @param array 用于存储转换结果的数组，建议大小与 List 大小一致或更大
     * @return 转换后的数组，如果 list 为 null 则返回 null
     */
    public static <T> T[] toArray(List<T> list, T[] array) {
        // 如果列表为 null，则返回 null
        if (list == null) return null;
        // 检查传入的数组是否为 null，如果是则抛出异常
        if (array == null) {
            throw new IllegalArgumentException("传入的数组不能为 null");
        }
        // 将列表元素复制到数组中并返回
        return list.toArray(array);
    }

    /**
     * 将 List 转换为数组。
     * 该方法创建一个与列表大小相同的数组，并将列表中的元素复制到该数组中。
     * 如果列表为 null，则返回 null；如果列表为空，则返回一个长度为 0 的 Object 数组。
     *
     * @param list 要转换的 List，可能为 null
     * @return 转换后的数组，如果 list 为 null 则返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list) {
        // 如果列表为 null，则返回 null
        if (list == null) return null;
        // 如果列表为空，则返回一个长度为 0 的 Object 数组
        if (list.isEmpty()) {
            return (T[]) new Object[0];
        }
        // 创建一个与列表大小相同的数组
        T[] array = (T[]) Array.newInstance(list.get(0).getClass(), list.size());
        // 将列表元素复制到数组中并返回
        return list.toArray(array);
    }

    /**
     * 将 defaultValue 列表中的所有元素添加到 list 列表中。
     * 如果 list 为 null，则返回 defaultValue；若 defaultValue 也为 null，则返回一个新的空 ArrayList。
     *
     * @param list         目标列表，可能为 null
     * @param defaultValue 要添加到目标列表中的元素列表，可能为 null
     * @param <T>          列表元素的类型
     * @return 包含所有元素的列表
     */
    public static <T> List<T> addAll(List<T> list, List<T> defaultValue) {
        // 若 list 不为 null
        if (list != null) {
            // 将 defaultValue 中的所有元素添加到 list 中
            list.addAll(defaultValue);
            // 返回添加元素后的 list
            return list;
        }
        // 若 list 为 null，检查 defaultValue 是否为 null
        return defaultValue == null ? new ArrayList<T>() : defaultValue;
    }

    /**
     * 获取一个非空的列表。
     * 如果传入的 list 为 null 或者为空，则创建一个新的列表并将 defaultValue 添加进去。
     *
     * @param list         待检查的列表，可能为 null
     * @param defaultValue 当 list 为空时要添加到新列表中的默认元素
     * @param <T>          列表元素的类型
     * @return 非空的列表
     */
    public static <T> List<T> getList(List<T> list, T defaultValue) {
        // 若 list 为 null 或者为空
        if (list == null || list.isEmpty()) {
            // 创建一个新的 ArrayList
            List<T> newList = new ArrayList<>();
            // 将 defaultValue 添加到新列表中
            newList.add(defaultValue);
            // 返回新列表
            return newList;
        } else {
            // 若 list 不为空，直接返回 list
            return list;
        }
    }

    /**
     * 获取一个非 null 的列表。
     * 如果传入的 list 为 null，则返回一个新的空 ArrayList。
     *
     * @param list 待检查的列表，可能为 null
     * @param <T>  列表元素的类型
     * @return 非 null 的列表
     */
    public static <T> List<T> getList(List<T> list) {
        // 若 list 为 null
        if (list == null) {
            // 返回一个新的空 ArrayList
            return new ArrayList<>();
        }
        // 若 list 不为 null，直接返回 list
        return list;
    }

    /**
     * 检查列表是否为空。
     * 当列表为 null 或者列表中没有元素时，认为列表为空。
     *
     * @param list 待检查的列表，可能为 null
     * @param <T>  列表元素的类型
     * @return 如果列表为空返回 true，否则返回 false
     */
    public static <T> boolean isEmpty(List<T> list) {
        // 判断 list 是否为 null 或者是否为空
        return list == null || list.isEmpty();
    }

    /**
     * 检查列表是否不为空。
     * 当列表不为 null 且列表中有元素时，认为列表不为空。
     *
     * @param list 待检查的列表，可能为 null
     * @param <T>  列表元素的类型
     * @return 如果列表不为空返回 true，否则返回 false
     */
    public static <T> boolean isNotEmpty(List<T> list) {
        // 调用 isEmpty 方法取反
        return !isEmpty(list);
    }

    /**
     * 将列表按指定大小进行分组。
     * 该方法将列表中的元素按指定的组大小进行分组，每个分组是一个新的列表，最终返回一个包含所有分组的列表。
     *
     * @param list      要分组的列表，可能为 null
     * @param groupSize 每组的元素数量
     * @param <T>       列表元素的类型
     * @return 包含所有分组的列表
     */
    /*分组*/
    public static <T> List<List<T>> partitionList(List<T> list, int groupSize) {
        // 创建一个新的 ArrayList 用于存储分组后的列表
        List<List<T>> partitions = new ArrayList<>();
        // 遍历列表，按组大小进行分组
        for (int i = 0; i < list.size(); i += groupSize) {
            // 计算当前分组的结束位置
            int end = Math.min(list.size(), i + groupSize);
            // 将当前分组添加到结果列表中
            partitions.add(new ArrayList<>(list.subList(i, end)));
        }
        // 返回包含所有分组的列表
        return partitions;
    }

    /**
     * 从列表中随机选取一个元素。
     * 如果列表为 null 或为空，则返回 null。
     *
     * @param list 要选取元素的列表，可能为 null
     * @param <T>  列表元素的类型
     * @return 随机选取的元素，如果列表为 null 或为空则返回 null
     */
    public static <T> T pickRandomElement(List<T> list) {
        // 检查列表是否为 null 或为空，如果是则返回 null
        if (list == null || list.isEmpty()) {
            return null;
        }
        // 创建一个 Random 对象
        Random random = new Random();
        // 生成一个随机索引
        int randomIndex = random.nextInt(list.size());
        // 返回随机选取的元素
        return list.get(randomIndex);
    }

    /**
     * 返回一个空的列表。
     *
     * @param <T> 列表元素的类型
     * @return 一个新的空 ArrayList
     */
    public static final <T> List<T> emptyList() {
        return new ArrayList<>();
    }

    /**
     * 从字符串列表中进行模糊匹配，返回包含指定关键字的元素列表。
     *
     * @param list    要进行匹配的字符串列表，可能为 null
     * @param keyword 要匹配的关键字
     * @return 包含指定关键字的元素列表
     */
    public static List<String> filtration(List<String> list, String keyword) {
        // 创建一个新的 ArrayList 用于存储匹配结果
        List<String> result = new ArrayList<>();
        // 遍历列表
        for (String item : list) {
            // 判断当前元素是否包含关键字
            if (item.contains(keyword)) {
                // 如果包含关键字，则将该元素添加到结果列表中
                result.add(item);
            }
        }
        // 返回匹配结果列表
        return result;
    }

    /**
     * 合并多个数组为一个数组。
     * 如果传入的数组数组为 null 或长度为 0，则返回一个长度为 0 的 Object 数组。
     *
     * @param arrays 要合并的数组数组
     * @param <T>    数组元素的类型
     * @return 合并后的数组
     */
    @SafeVarargs
    public static <T> T[] mergeArrays(T[]... arrays) {
        // 检查传入的数组数组是否为 null 或长度为 0，如果是则返回一个长度为 0 的 Object 数组
        if (arrays == null || arrays.length == 0) {
            return (T[]) new Object[0];
        }
        // 计算所有数组的总长度
        int totalLength = 0;
        for (T[] array : arrays) {
            if (array != null) {
                totalLength += array.length;
            }
        }
        // 创建一个新的数组，用于存储合并后的结果
        T[] result = (T[]) Array.newInstance(arrays[0].getClass().getComponentType(), totalLength);
        // 用于记录目标数组的起始位置
        int destPos = 0;
        // 遍历所有数组，将它们的元素复制到结果数组中
        for (T[] array : arrays) {
            if (array != null) {
                System.arraycopy(array, 0, result, destPos, array.length);
                destPos += array.length;
            }
        }
        // 返回合并后的数组
        return result;
    }
}