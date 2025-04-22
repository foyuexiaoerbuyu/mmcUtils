package org.mmc.util.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
//new JListClickHandler<>(itemList, listModel, new JListClickHandler.JListClickCallback<String>() {
//     *     @Override
//    public void onSingleClick(int index, String item) {
//        System.out.println("单击: " + item);
//    }
//    @Override
//    public void onDoubleClick(int index, String item) {
//        System.out.println("双击: " + item);
//    }
//    @Override
//    public void onRightClick(int index, String item) {
//        System.out.println("右键点击: " + item);
//    }
//});

/**
 * 封装 JList 单击、双击和右键事件处理的类
 *
 * @param <E> 泛型类型，表示 JList 中元素的类型
 *            <p>
 *            示例使用方式：
 *            使用封装的 JListClickHandler 类处理事件，并传入回调接口的实现
 */
public class JswJListClickHandler<E> {

    /**
     * 定义回调接口，包含单击、双击和右键事件处理方法
     */
    public interface JListClickCallback<E> {
        /**
         * 处理 JList 中元素的单击事件
         *
         * @param index 被点击元素在 JList 中的索引
         * @param item  被点击的元素
         */
        void onSingleClick(int index, E item);

        /**
         * 处理 JList 中元素的双击事件
         *
         * @param index 被点击元素在 JList 中的索引
         * @param item  被点击的元素
         */
        void onDoubleClick(int index, E item);

        /**
         * 处理 JList 中元素的右键点击事件
         *
         * @param e     鼠标事件对象
         * @param index 被点击元素在 JList 中的索引
         * @param item  被点击的元素
         */
        void onRightClick(MouseEvent e, int index, E item);
    }

    /**定义仅处理单击事件的回调接口*/
    public interface singleClick<E> {
        /**
         * 处理 JList 中元素的单击事件
         *
         * @param index 被点击元素在 JList 中的索引
         * @param item  被点击的元素
         */
        void onSingleClick(int index, E item);
    }

    /** 用于区分单击和双击的定时器*/
    private final Timer clickTimer;
    /** 记录当前点击的元素索引*/
    private int clickedIndex = -1;

    /**
     * 构造函数，使用数据列表初始化 JList 的事件处理
     *
     * @param jlist    要处理事件的 JList 对象
     * @param datas    JList 中显示的数据列表
     * @param callback 事件处理回调接口的实现
     */
    public JswJListClickHandler(JList<E> jlist, List<E> datas, JListClickCallback<E> callback) {
        // 创建默认的列表模型
        DefaultListModel<E> listModel = new DefaultListModel<>();
        // 初始化定时器，用于区分单击和双击事件
        this.clickTimer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 如果有有效的点击索引
                if (clickedIndex >= 0) {
                    // 获取被点击的元素
                    E selectedItem = listModel.getElementAt(clickedIndex);
                    // 调用单击事件处理方法
                    callback.onSingleClick(clickedIndex, selectedItem);
                    // 重置点击索引
                    clickedIndex = -1;
                }
            }
        });
        // 设置定时器不重复执行
        clickTimer.setRepeats(false);

        // 为 JList 添加鼠标监听器
        jlist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 获取鼠标点击位置对应的元素索引
                int index = jlist.locationToIndex(e.getPoint());
                // 如果索引无效，直接返回
                if (index < 0) return;

                // 如果是双击事件
                if (e.getClickCount() == 2) {
                    // 停止定时器
                    clickTimer.stop();
                    // 获取被点击的元素
                    E selectedItem = listModel.getElementAt(index);
                    // 调用双击事件处理方法
                    callback.onDoubleClick(index, selectedItem);
                }
                // 如果是右键点击事件
                else if (SwingUtilities.isRightMouseButton(e)) {
                    // 获取被点击的元素
                    E selectedItem = listModel.getElementAt(index);
                    // 调用右键点击事件处理方法
                    callback.onRightClick(e, index, selectedItem);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // 获取鼠标释放位置对应的元素索引
                int index = jlist.locationToIndex(e.getPoint());
                // 如果索引无效，直接返回
                if (index < 0) return;

                // 如果是左键单击事件
                if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
                    // 记录点击的索引
                    clickedIndex = index;
                    // 重启定时器
                    clickTimer.restart();
                }
            }
        });
    }

    /**
     * 构造函数，使用列表模型初始化 JList 的事件处理
     *
     * @param jlist     要处理事件的 JList 对象
     * @param listModel JList 使用的列表模型
     * @param callback  事件处理回调接口的实现
     */
    public JswJListClickHandler(JList<E> jlist, DefaultListModel<E> listModel, JListClickCallback<E> callback) {
        // 初始化定时器，用于区分单击和双击事件
        this.clickTimer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 如果有有效的点击索引
                if (clickedIndex >= 0) {
                    // 获取被点击的元素
                    E selectedItem = listModel.getElementAt(clickedIndex);
                    // 调用单击事件处理方法
                    callback.onSingleClick(clickedIndex, selectedItem);
                    // 重置点击索引
                    clickedIndex = -1;
                }
            }
        });
        // 设置定时器不重复执行
        clickTimer.setRepeats(false);

        // 为 JList 添加鼠标监听器
        jlist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 获取鼠标点击位置对应的元素索引
                int index = jlist.locationToIndex(e.getPoint());
                // 如果索引无效，直接返回
                if (index < 0) return;

                // 如果是双击事件
                if (e.getClickCount() == 2) {
                    // 停止定时器
                    clickTimer.stop();
                    // 获取被点击的元素
                    E selectedItem = listModel.getElementAt(index);
                    // 调用双击事件处理方法
                    callback.onDoubleClick(index, selectedItem);
                }
                // 如果是右键点击事件
                else if (SwingUtilities.isRightMouseButton(e)) {
                    // 获取被点击的元素
                    E selectedItem = listModel.getElementAt(index);
                    // 调用右键点击事件处理方法
                    callback.onRightClick(e, index, selectedItem);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // 获取鼠标释放位置对应的元素索引
                int index = jlist.locationToIndex(e.getPoint());
                // 如果索引无效，直接返回
                if (index < 0) return;

                // 如果是左键单击事件
                if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
                    // 记录点击的索引
                    clickedIndex = index;
                    // 重启定时器
                    clickTimer.restart();
                }
            }
        });
    }

    /**
     * 仅处理 JList 中元素的单击事件
     *
     * @param jlist     要处理事件的 JList 对象
     * @param listModel JList 使用的列表模型
     * @param callback  单击事件处理回调接口的实现
     * @param <E>       泛型
     */
    public static <E> void singleClick(JList<E> jlist, DefaultListModel<E> listModel, singleClick<E> callback) {
        // 为 JList 添加鼠标监听器
        jlist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 获取鼠标点击位置对应的元素索引
                int index = jlist.locationToIndex(e.getPoint());
                // 如果索引无效，直接返回
                if (index < 0) return;
                // 调用单击事件处理方法
                callback.onSingleClick(index, listModel.getElementAt(jlist.getSelectedIndex()));
            }
        });
    }
}