package org.mmc.util.swing;

import javax.swing.*;

/**
 * 多级菜单 右键菜单  右键多级菜单选项
 * 用于为组件添加右键菜单功能。
 * 示例用法：
 * // <code>JPanel panel = new JPanel();
 * // panel.setComponentPopupMenu(new JPopupMenus().addMenu("复制", new JPopupMenus.JPopupMenusClick() {
 * //     @Override
 * //     public void click(String menuName) {
 * //         WinUtils.setSysClipboardText(jTextAreaDrag.getText());
 * //     }
 * // }).addMenu("粘贴", new JPopupMenus.JPopupMenusClick() {
 * //     @Override
 * //     public void click(String menuName) {
 * //         jTextAreaDrag.setText(WinUtils.getSysClipboardText());
 * //     }
 * // }));
 * </code>
 */
public class JswPopupMenus extends JPopupMenu {

    /**
     * 默认构造函数，创建一个空的右键菜单。
     */
    public JswPopupMenus() {
    }

    /**
     * 构造函数，用于创建包含多个菜单项的右键菜单。
     *
     * @param jPopupMenusClick 菜单项点击事件的监听器
     * @param menuItems        菜单项的名称数组
     */
    public JswPopupMenus(JPopupMenusClicks jPopupMenusClick, String... menuItems) {
        // 遍历菜单项名称数组
        for (int i = 0; i < menuItems.length; i++) {
            int index = i;
            // 创建一个菜单项
            JMenuItem menuItem = new JMenuItem(menuItems[index]);
            // 为菜单项添加点击事件监听器
            menuItem.addActionListener(e -> {
                jPopupMenusClick.click(index, menuItems[index]);
            });
            // 将菜单项添加到菜单中
            add(menuItem);
        }
    }

    /**
     * 添加菜单组，即包含多个子菜单项的菜单。
     *
     * @param menuItemName     菜单组的名称
     * @param jPopupMenusClick 子菜单项点击事件的监听器
     * @param menuItems        子菜单项的名称数组
     * @return 返回当前的JswPopupMenus对象，以便进行链式调用
     */
    public JswPopupMenus addMenuGroup(String menuItemName, JPopupMenusClicks jPopupMenusClick, String... menuItems) {
        // 创建一个菜单组
        JMenu menu = new JMenu(menuItemName);
        // 将菜单组添加到菜单中
        add(menu);
        // 遍历子菜单项名称数组
        for (int i = 0; i < menuItems.length; i++) {
            int index = i;
            // 创建一个子菜单项
            JMenuItem menuItem = new JMenuItem(menuItems[index]);
            // 为子菜单项添加点击事件监听器
            menuItem.addActionListener(e -> {
                jPopupMenusClick.click(index, menuItems[index]);
            });
            // 将子菜单项添加到菜单组中
            menu.add(menuItem);
        }
        return this;
    }

    /**
     * 添加二级菜单。
     *
     * @param menuItemName     二级菜单的名称
     * @param jPopupMenusClick 二级菜单项点击事件的监听器
     * @param menuItems        二级菜单项的名称数组
     * @return 返回创建的二级菜单对象
     */
    public JMenu addJMenu(String menuItemName, JPopupMenusClicks jPopupMenusClick, String... menuItems) {
        // 创建一个二级菜单
        JMenu menu = new JMenu(menuItemName);
        // 将二级菜单添加到菜单中
        add(menu);
        // 遍历二级菜单项名称数组
        for (int i = 0; i < menuItems.length; i++) {
            int index = i;
            // 创建一个二级菜单项
            JMenuItem menuItem = new JMenuItem(menuItems[index]);
            // 为二级菜单项添加点击事件监听器
            menuItem.addActionListener(e -> {
                jPopupMenusClick.click(index, menuItems[index]);
            });
            // 将二级菜单项添加到二级菜单中
            menu.add(menuItem);
        }
        return menu;
    }

    /**
     * 添加单个菜单项。
     *
     * @param itemMenuName     菜单项的名称
     * @param jPopupMenusClick 菜单项点击事件的监听器
     * @return 返回当前的JswPopupMenus对象，以便进行链式调用
     */
    public JswPopupMenus addMenuItem(String itemMenuName, JPopupMenusClick jPopupMenusClick) {
        // 创建一个菜单项
        JMenuItem menuItem = new JMenuItem(itemMenuName);
        // 为菜单项添加点击事件监听器
        menuItem.addActionListener(e -> {
            jPopupMenusClick.click(itemMenuName, menuItem);
        });
        // 将菜单项添加到菜单中
        add(menuItem);
        return this;
    }

    /**
     * 菜单项点击事件的监听器接口，用于处理多个菜单项的点击事件。
     */
    public interface JPopupMenusClicks {
        /**
         * 当菜单项被点击时调用的方法。
         *
         * @param menuIndex 菜单项的索引
         * @param menuName  菜单项的名称
         */
        void click(int menuIndex, String menuName);
    }

    /**
     * 菜单操作的监听器接口，这里可能是预留接口，未在当前代码中使用。
     */
    public interface JPopupMenus {
        /**
         * 对菜单进行操作的方法。
         *
         * @param jMenu 要操作的菜单对象
         */
        void menu(JMenu jMenu);
    }

    /**
     * 单个菜单项点击事件的监听器接口，用于处理单个菜单项的点击事件。
     */
    public interface JPopupMenusClick {
        /**
         * 当菜单项被点击时调用的方法。
         *
         * @param menuName 菜单项的名称
         * @param menuItem 被点击的菜单项对象
         */
        void click(String menuName, JMenuItem menuItem);
    }
}