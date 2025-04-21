package org.mmc.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 
 */
public class ListUtils {
    /**
     * 遍历删除元素 list.removeIf(current -> current.equals(element));
     */
    public static <T> void removeItems(List<T> list, T element) {
        if (list == null || list.isEmpty()) return;
        // 使用迭代器的remove方法安全地删除元素
        list.removeIf(current -> current.equals(element));
    }

    /**
     * 遍历删除元素 list.removeIf(current -> current.equals(element));
     */
    public static <T> void removeItems(List<T> list, Condition<T> condition) {
        if (list == null || list.isEmpty()) return;
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (condition.matches(item)) {
                iterator.remove();
            }
        }
    }

    /**
     * 遍历删除元素 jdk1.8后推荐方式 list.removeIf(current -> current.equals(element));
     */
    public static <T> void removeElements(List<T> list, Condition<T> iRemove) {
        if (list == null || list.size() == 0) return;
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T current = iterator.next();
            if (iRemove.matches(current)) {
                // 使用迭代器的remove方法安全地删除元素
                iterator.remove();
            }
        }
    }

    /**
     * 过滤元素
     */
    public static <T> List<T> filterElements(List<T> list, Condition<T> iRemove) {
        if (list == null || list.size() == 0) return null;
        Iterator<T> iterator = list.iterator();
        ArrayList<T> newlist = new ArrayList<>();
        while (iterator.hasNext()) {
            T current = iterator.next();
            if (iRemove.matches(current)) {
                // 使用迭代器的remove方法安全地删除元素
                newlist.add(current);
            }
        }
        return newlist;
    }

    public interface Condition<T> {
        boolean matches(T current);
    }


    /**
     * 将 List 转换为数组。
     *
     * @param list 要转换的 List
     * @param array 用于存储转换结果的数组，建议大小与 List 大小一致或更大
     * @return 转换后的数组，如果 list 为 null 则返回 null
     */
    public static <T> T[] toArray(List<T> list, T[] array) {
        if (list == null) return null;
        if (array == null) {
            throw new IllegalArgumentException("传入的数组不能为 null");
        }
        return list.toArray(array);
    }

    /**
     * 将 List 转换为数组。
     *
     * @param list 要转换的 List
     * @return 转换后的数组，如果 list 为 null 则返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list) {
        if (list == null) return null;
        if (list.isEmpty()) {
            return (T[]) new Object[0];
        }
        T[] array = (T[]) Array.newInstance(list.get(0).getClass(), list.size());
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

    /*分组*/
    public static <T> List<List<T>> partitionList(List<T> list, int groupSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += groupSize) {
            int end = Math.min(list.size(), i + groupSize);
            partitions.add(new ArrayList<>(list.subList(i, end)));
        }
        return partitions;
    }

    /**
     * 随机获取一个元素
     */
    public static <T> T pickRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    public static final <T> List<T> emptyList() {
        return new ArrayList<>();
    }

    /**
     * 模糊匹配数据
     */
    public static List<String> filtration(List<String> list, String keyword) {
        List<String> result = new ArrayList<>();
        for (String item : list) {
            if (item.contains(keyword)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 合并数组
     * @param arrays
     * @return
     * @param <T>
     */
    @SafeVarargs
    public static <T> T[] mergeArrays(T[]... arrays) {
        if (arrays == null || arrays.length == 0) {
            return (T[]) new Object[0];
        }
        int totalLength = 0;
        for (T[] array : arrays) {
            if (array != null) {
                totalLength += array.length;
            }
        }
        T[] result = (T[]) Array.newInstance(arrays[0].getClass().getComponentType(), totalLength);
        int destPos = 0;
        for (T[] array : arrays) {
            if (array != null) {
                System.arraycopy(array, 0, result, destPos, array.length);
                destPos += array.length;
            }
        }
        return result;
    }
}
