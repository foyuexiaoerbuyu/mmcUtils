package org.mmc.util.swing.weight;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义的 JTextArea 类，支持撤销、重做、自定义快捷键和右键菜单功能
 * // 示例代码：
 * // // 创建主窗口
 * // JFrame frame = new JFrame("UndoJTextArea 示例");
 * // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 * // frame.setSize(600, 400);
 * // <p>
 * // // 创建 UndoJTextArea 实例
 * // UndoJTextArea textArea = new UndoJTextArea();
 * // textArea.setLineWrap(true);
 * // textArea.setWrapStyleWord(true);
 * // <p>
 * // // 添加一些初始文本
 * // textArea.setText("尝试在这里编辑文本。\n使用 Ctrl+Z 撤销，Ctrl+Y 重做。\n右键点击查看上下文菜单。");
 * // <p>
 * // // 添加自定义快捷键示例
 * // textArea.addShortcut("clear", KeyStroke.getKeyStroke("control shift C"),
 * // new AbstractAction() {
 * // @Override public void actionPerformed(ActionEvent e) {
 * // textArea.setText("");
 * // }
 * // });
 * // <p>
 * // // 添加自定义右键菜单项
 * // textArea.addPopupMenuItem(new JMenuItem(new AbstractAction("插入日期") {
 * // @Override public void actionPerformed(ActionEvent e) {
 * // textArea.insert(java.time.LocalDate.now().toString(), textArea.getCaretPosition());
 * // }
 * // }), "insertDate");
 * // <p>
 * // // 添加滚动条
 * // JScrollPane scrollPane = new JScrollPane(textArea);
 * // frame.add(scrollPane, BorderLayout.CENTER);
 * // <p>
 * // // 添加底部面板显示信息
 * // JPanel bottomPanel = new JPanel();
 * // bottomPanel.add(new JLabel("快捷键: Ctrl+Z 撤销, Ctrl+Y 重做, Ctrl+Shift+C 清空"));
 * // frame.add(bottomPanel, BorderLayout.SOUTH);
 * // <p>
 * // // 显示窗口
 * // frame.setVisible(true);
 */
public class MJTextArea extends JTextArea {
    // 撤销管理器，用于管理文本的撤销和重做操作
    private final UndoManager undoManager;
    // 快捷键映射，用于存储自定义的快捷键
    private final Map<String, KeyStroke> shortcuts;
    // 右键菜单，用于显示自定义的菜单项
    private final JPopupMenu popupMenu;

    /**
     * 构造函数，初始化撤销管理器、快捷键映射和右键菜单
     */
    public MJTextArea() {
        super();

        // 初始化撤销管理器
        undoManager = new UndoManager();
        // 为文档添加撤销可编辑事件监听器，将每次编辑操作添加到撤销管理器中
        getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        // 初始化快捷键映射
        shortcuts = new HashMap<>();

        // 初始化右键菜单
        popupMenu = new JPopupMenu();

        // 添加默认的撤销/重做功能
        setupUndoRedo();

        // 添加默认的右键菜单项
//        setupDefaultPopupMenu();
        // 添加鼠标监听器以显示右键菜单
        this.setComponentPopupMenu(popupMenu);
    }

    /**
     * 设置默认的撤销和重做功能，添加对应的快捷键
     */
    private void setupUndoRedo() {
        // 添加撤销动作
        addShortcut("undo", KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK),
                new AbstractAction("Undo") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // 如果可以撤销，则执行撤销操作
                        if (undoManager.canUndo()) {
                            undoManager.undo();
                        }
                    }
                });

        // 添加重做动作
        addShortcut("redo", KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK),
                new AbstractAction("Redo") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // 如果可以重做，则执行重做操作
                        if (undoManager.canRedo()) {
                            undoManager.redo();
                        }
                    }
                });
    }

    /**
     * 设置默认的右键菜单项，如剪切、复制、粘贴、全选等
     */
    private void setupDefaultPopupMenu() {
        // 添加剪切菜单项
        addPopupMenuItem(new JMenuItem(new DefaultEditorKit.CutAction()), "Cut");
        // 添加复制菜单项
        addPopupMenuItem(new JMenuItem(new DefaultEditorKit.CopyAction()), "Copy");
        // 添加粘贴菜单项
        addPopupMenuItem(new JMenuItem(new DefaultEditorKit.PasteAction()), "Paste");
        // 添加分隔符
        addPopupSeparator();
        // 添加全选菜单项
        addPopupMenuItem(new JMenuItem(new AbstractAction("Select All") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 全选文本
                selectAll();
            }
        }), "Select All");
    }

    /**
     * 添加新的快捷键
     *
     * @param name      快捷键名称
     * @param keyStroke 按键组合
     * @param action    对应的动作
     */
    public void addShortcut(String name, KeyStroke keyStroke, Action action) {
        // 如果已存在同名快捷键，先移除
        if (shortcuts.containsKey(name)) {
            getInputMap().remove(shortcuts.get(name));
        }

        // 添加新的快捷键映射
        getInputMap().put(keyStroke, name);
        getActionMap().put(name, action);
        shortcuts.put(name, keyStroke);
    }

    /**
     * 移除指定的快捷键
     *
     * @param name 要移除的快捷键名称
     */
    public void removeShortcut(String name) {
        if (shortcuts.containsKey(name)) {
            getInputMap().remove(shortcuts.get(name));
            getActionMap().remove(name);
            shortcuts.remove(name);
        }
    }

    /**
     * 添加右键菜单项
     *
     * @param menuItem 菜单项
     * @param name     菜单项名称（用于后续删除）
     */
    public void addPopupMenuItem(JMenuItem menuItem, String name) {
        menuItem.setName(name);
        popupMenu.add(menuItem);
    }

    /**
     * 添加右键菜单分隔符
     */
    public void addPopupSeparator() {
        popupMenu.addSeparator();
    }

    /**
     * 移除指定的右键菜单项
     *
     * @param name 要移除的菜单项名称
     */
    public void removePopupMenuItem(String name) {
        for (int i = 0; i < popupMenu.getComponentCount(); i++) {
            if (popupMenu.getComponent(i) instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) popupMenu.getComponent(i);
                if (name.equals(item.getName())) {
                    popupMenu.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * 清空所有右键菜单项
     */
    public void clearPopupMenu() {
        popupMenu.removeAll();
    }

    /**
     * 获取撤销管理器，以便进行更精细的控制
     *
     * @return UndoManager实例
     */
    public UndoManager getUndoManager() {
        return undoManager;
    }
}