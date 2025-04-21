package org.mmc.util.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * KeyBindings 链式动态添加快捷键
 *
 * 以下是使用示例：
 * public class TableShortcutExample {
 *     public static void main(String[] args) {
 *         JFrame frame = new JFrame("JTable 链式快捷键示例");
 *         JTable table = new JTable(new Object[][]{
 *             {"数据1", "数据2"},
 *             {"数据3", "数据4"}
 *         }, new Object[]{"列1", "列2"});
 *
 *         // 使用链式调用添加快捷键
 *         KeyBindingUtil.forComponent(table)
 *             .bindCtrl(KeyEvent.VK_C, "copy", () -> {
 *                 System.out.println("执行复制操作");
 *                 // 实际复制逻辑
 *             })
 *             .bindCtrl(KeyEvent.VK_V, "paste", () -> {
 *                 System.out.println("执行粘贴操作");
 *                 // 实际粘贴逻辑
 *             })
 *             .bind(KeyEvent.VK_DELETE, "delete", () -> {
 *                 System.out.println("执行删除操作");
 *                 // 实际删除逻辑
 *             })
 *             .bindCtrl(KeyEvent.VK_Z, "undo", () -> {
 *                 System.out.println("执行撤销操作");
 *                 // 实际撤销逻辑
 *             })
 *             .bindCtrl(KeyEvent.VK_Y, "redo", () -> {
 *                 System.out.println("执行重做操作");
 *                 // 实际重做逻辑
 *             });
 *
 *         frame.add(new JScrollPane(table));
 *         frame.setSize(400, 300);
 *         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 *         frame.setVisible(true);
 *     }
 * }
 */
public class KeyBindingUtil {
    // 要绑定快捷键的组件
    private final JComponent component;
    // 输入映射，用于将按键映射到动作名称
    private final InputMap inputMap;
    // 动作映射，用于将动作名称映射到具体的动作
    private final ActionMap actionMap;

    /**
     * 构造函数，初始化组件、输入映射和动作映射
     * @param component 要绑定快捷键的组件
     */
    public KeyBindingUtil(JComponent component) {
        this.component = component;
        // 获取组件在祖先组件获得焦点时的输入映射
        this.inputMap = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        // 获取组件的动作映射
        this.actionMap = component.getActionMap();
    }

    /**
     * 静态工厂方法，用于创建 KeyBindingUtil 实例
     * @param component 要绑定快捷键的组件
     * @return KeyBindingUtil 实例
     */
    public static KeyBindingUtil forComponent(JComponent component) {
        return new KeyBindingUtil(component);
    }

    /**
     * 添加单个快捷键绑定
     * @param keyCode 按键码
     * @param modifiers 修饰键，如 Ctrl、Alt 等
     * @param actionName 动作名称
     * @param action 要执行的动作
     * @return 当前 KeyBindingUtil 实例，用于链式调用
     */
    public KeyBindingUtil bind(int keyCode, int modifiers, String actionName, Runnable action) {
        // 创建按键组合
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
        // 将按键组合映射到动作名称
        inputMap.put(keyStroke, actionName);
        // 将动作名称映射到具体的动作
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 执行动作
                action.run();
            }
        });
        return this;
    }

    /**
     * 添加不带修饰键的快捷键
     * @param keyCode 按键码
     * @param actionName 动作名称
     * @param action 要执行的动作
     * @return 当前 KeyBindingUtil 实例，用于链式调用
     */
    public KeyBindingUtil bind(int keyCode, String actionName, Runnable action) {
        // 调用 bind 方法，修饰键设为 0
        return bind(keyCode, 0, actionName, action);
    }

    /**
     * 添加 Ctrl 组合键
     * @param keyCode 按键码
     * @param actionName 动作名称
     * @param action 要执行的动作
     * @return 当前 KeyBindingUtil 实例，用于链式调用
     */
    public KeyBindingUtil bindCtrl(int keyCode, String actionName, Runnable action) {
        // 调用 bind 方法，修饰键设为 Ctrl
        return bind(keyCode, KeyEvent.CTRL_DOWN_MASK, actionName, action);
    }

    /**
     * 添加 Alt 组合键
     * @param keyCode 按键码
     * @param actionName 动作名称
     * @param action 要执行的动作
     * @return 当前 KeyBindingUtil 实例，用于链式调用
     */
    public KeyBindingUtil bindAlt(int keyCode, String actionName, Runnable action) {
        // 调用 bind 方法，修饰键设为 Alt
        return bind(keyCode, KeyEvent.ALT_DOWN_MASK, actionName, action);
    }

    /**
     * 添加 Shift 组合键
     * @param keyCode 按键码
     * @param actionName 动作名称
     * @param action 要执行的动作
     * @return 当前 KeyBindingUtil 实例，用于链式调用
     */
    public KeyBindingUtil bindShift(int keyCode, String actionName, Runnable action) {
        // 调用 bind 方法，修饰键设为 Shift
        return bind(keyCode, KeyEvent.SHIFT_DOWN_MASK, actionName, action);
    }
}