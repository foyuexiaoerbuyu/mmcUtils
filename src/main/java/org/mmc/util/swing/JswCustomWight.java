package org.mmc.util.swing;

import org.mmc.util.StringUtil;
import org.mmc.util.WinUtils;
import org.mmc.util.swing.weight.ClickButton;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 自定义控件类，提供了多种自定义的Swing控件创建方法
 * 包含编辑框、多行编辑框、滚动面板等控件的创建，同时支持拖拽文件功能
 * 还提供了禁用文本区域编辑功能的相关方法说明
 */
public class JswCustomWight {

    /**
     * 创建一个指定位置和大小的编辑框
     *
     * @param x 编辑框的x坐标
     * @param y 编辑框的y坐标
     * @param w 编辑框的宽度
     * @param h 编辑框的高度
     * @return 一个JTextField对象
     */
    public static JTextField getJTextField(int x, int y, int w, int h) {
        return getJTextField(x, y, w, h, null);
    }

    /**
     * 创建一个指定位置、大小和初始文本的编辑框
     *
     * @param x    编辑框的x坐标
     * @param y    编辑框的y坐标
     * @param w    编辑框的宽度
     * @param h    编辑框的高度
     * @param text 编辑框的初始文本
     * @return 一个JTextField对象
     */
    public static JTextField getJTextField(int x, int y, int w, int h, String text) {
        JTextField jTextField = new JTextField(text);
        jTextField.setBounds(x, y, w, h);
        return jTextField;
    }

    /**
     * 创建一个指定位置、大小、初始文本和提示文本的编辑框
     * 当编辑框获得焦点时，如果显示的是提示文本，则清空；失去焦点时，如果文本为空，则显示提示文本
     *
     * @param x        编辑框的x坐标
     * @param y        编辑框的y坐标
     * @param w        编辑框的宽度
     * @param h        编辑框的高度
     * @param text     编辑框的初始文本
     * @param hintText 编辑框的提示文本
     * @return 一个JTextField对象
     */
    public static JTextField getJTextField(int x, int y, int w, int h, String text, String hintText) {
        JTextField jTextField = new JTextField(text);
        jTextField.setBounds(x, y, w, h);
        // 如果初始文本为空，则设置为提示文本
        if (StringUtil.isEmpty(text)) {
            jTextField.setText(hintText);
        }
        // 如果提示文本为空，则直接返回编辑框
        if (hintText == null || hintText.length() == 0) return jTextField;
        // 添加焦点监听器
        jTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("编辑框获得焦点");
                // 如果当前文本为提示文本，则清空
                if (jTextField.getText().equals(hintText)) jTextField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("编辑框失去焦点");
                // 如果文本为空，则设置为提示文本
                if (jTextField.getText().length() == 0) jTextField.setText(hintText);
            }
        });
        return jTextField;
    }

    /**
     * 创建一个指定位置、大小、初始文本和监听器的编辑框
     * 支持监听编辑框的动作事件和焦点事件
     *
     * @param x                 编辑框的x坐标
     * @param y                 编辑框的y坐标
     * @param w                 编辑框的宽度
     * @param h                 编辑框的高度
     * @param text              编辑框的初始文本
     * @param ijTextFieldLinser 编辑框的监听器
     * @return 一个JTextField对象
     */
    public static JTextField getJTextField(int x, int y, int w, int h, String text, IJTextFieldListener ijTextFieldLinser) {
        JTextField jTextField = new JTextField(text);
        jTextField.setBounds(x, y, w, h);
        // 如果监听器为空，则直接返回编辑框
        if (ijTextFieldLinser == null) {
            return jTextField;
        }
        // 添加动作监听器
        jTextField.addActionListener(e -> ijTextFieldLinser.actionPerformed(e, jTextField));
        // 添加焦点监听器
        jTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                ijTextFieldLinser.focusGained(jTextField);
            }

            @Override
            public void focusLost(FocusEvent e) {
                ijTextFieldLinser.focusLost(jTextField);
            }
        });
        return jTextField;
    }

    /**
     * 创建一个包含指定JTextArea的自动滚动的多行编辑框
     * 水平和垂直滚动条会根据需要自动出现
     *
     * @param jTextArea 要包含的JTextArea对象
     * @return 一个JScrollPane对象
     */
    public static JScrollPane getJScrollPane(JTextArea jTextArea) {
        JScrollPane scroll = new JScrollPane(jTextArea);
        scroll.setBounds(jTextArea.getX(), jTextArea.getY(), jTextArea.getWidth(), jTextArea.getHeight());
        // 分别设置水平和垂直滚动条自动出现
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scroll;
    }

    /**
     * 创建一个包含指定JList的自动滚动的多行编辑框
     * 水平和垂直滚动条会根据需要自动出现
     *
     * @param x      滚动面板的x坐标
     * @param y      滚动面板的y坐标
     * @param width  滚动面板的宽度
     * @param height 滚动面板的高度
     * @param jList  要包含的JList对象
     * @return 一个JScrollPane对象
     */
    public static <T> JScrollPane getJScrollPaneJList(int x, int y, int width, int height, JList<T> jList) {
        JScrollPane scroll = new JScrollPane(jList);
        scroll.setBounds(x, y, width, height);
        // 分别设置水平和垂直滚动条自动出现
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scroll;
    }

    /**
     * 创建一个指定位置和大小的自动滚动的多行编辑框
     * 水平和垂直滚动条会根据需要自动出现
     *
     * @param x 滚动面板的x坐标
     * @param y 滚动面板的y坐标
     * @param w 滚动面板的宽度
     * @param h 滚动面板的高度
     * @return 一个JScrollPane对象
     */
    public static JScrollPane getJScrollPaneJTextArea(int x, int y, int w, int h) {
        JScrollPane scroll = new JScrollPane(getJTextArea(x, y, w, h, null));
        scroll.setBounds(x, y, w, h);
        // 分别设置水平和垂直滚动条自动出现
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scroll;
    }

    /**
     * 创建一个指定位置和大小的多行编辑框
     *
     * @param x 多行编辑框的x坐标
     * @param y 多行编辑框的y坐标
     * @param w 多行编辑框的宽度
     * @param h 多行编辑框的高度
     * @return 一个JTextArea对象
     */
    public static JTextArea getJTextArea(int x, int y, int w, int h) {
        return getJTextArea(x, y, w, h, null);
    }

    /**
     * 创建一个指定位置、大小和初始文本的多行编辑框
     * 支持自动换行
     *
     * @param x    多行编辑框的x坐标
     * @param y    多行编辑框的y坐标
     * @param w    多行编辑框的宽度
     * @param h    多行编辑框的高度
     * @param text 多行编辑框的初始文本
     * @return 一个JTextArea对象
     */
    public static JTextArea getJTextArea(int x, int y, int w, int h, String text) {
        JTextArea jTextArea = new JTextArea(text);
        jTextArea.setBounds(x, y, w, h);
        jTextArea.setLineWrap(true);
        return jTextArea;
    }

    /**
     * 创建一个指定位置、大小、初始文本和鼠标监听器的多行编辑框
     * 支持自动换行，并可以监听鼠标事件
     *
     * @param x            多行编辑框的x坐标
     * @param y            多行编辑框的y坐标
     * @param w            多行编辑框的宽度
     * @param h            多行编辑框的高度
     * @param text         多行编辑框的初始文本
     * @param mouseAdapter 鼠标监听器
     * @return 一个JTextArea对象
     */
    public static JTextArea getJTextAreaMouse(int x, int y, int w, int h, String text, MouseAdapter mouseAdapter) {
        JTextArea jTextArea = new JTextArea(text);
        jTextArea.setBounds(x, y, w, h);
        jTextArea.setLineWrap(true);
        jTextArea.addMouseListener(mouseAdapter);
        return jTextArea;
    }

    /**
     * 创建一个指定位置、大小和拖拽文件回调的多行编辑框
     * 支持拖拽文件到编辑框，将文件路径显示在编辑框中，并触发回调
     *
     * @param x             多行编辑框的x坐标
     * @param y             多行编辑框的y坐标
     * @param w             多行编辑框的宽度
     * @param h             多行编辑框的高度
     * @param iDragCallBack 拖拽文件回调接口
     * @return 一个JTextArea对象
     */
    public static JTextArea getJTextAreaDrag(int x, int y, int w, int h, IDragCallBack iDragCallBack) {
        JTextArea jTextArea = new JTextArea();
        jTextArea.setBounds(x, y, w, h);
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        jTextArea.setEditable(false);
        // 如果回调接口为空，则直接返回编辑框
        if (iDragCallBack == null) return jTextArea;
        // 添加拖拽监听器
        jTextArea.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                // 判断是否支持拖拽文件
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    // 如果编辑框中已经包含文件路径，则直接返回
                    if (jTextArea.getText().contains(File.separator)) {
                        return true;
                    }
                    // 获取拖拽的文件列表
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    // 遍历文件列表并将文件路径添加到编辑框中
                    for (File file : files) {
                        jTextArea.append(file.getAbsolutePath() + "\n");
                    }
                    // 触发回调
                    iDragCallBack.dragCallBack(jTextArea.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        return jTextArea;
    }

    /**
     * 创建一个指定位置、大小和初始文本的编辑框，并添加拖拽文件回调功能
     *
     * @param x    编辑框的x坐标
     * @param y    编辑框的y坐标
     * @param w    编辑框的宽度
     * @param h    编辑框的高度
     * @param text 编辑框的初始文本
     * @return 一个JTextField对象
     */
    public static JTextField getJTextField2DragCallBack(int x, int y, int w, int h, String text) {
        return getJTextField2DragCallBack(x, y, w, h, text, null);
    }

    /**
     * 创建一个指定位置、大小的编辑框，并添加拖拽文件回调功能
     *
     * @param x 编辑框的x坐标
     * @param y 编辑框的y坐标
     * @param w 编辑框的宽度
     * @param h 编辑框的高度
     * @return 一个JTextField对象
     */
    public static JTextField getJTextField2DragCallBack(int x, int y, int w, int h) {
        return getJTextField2DragCallBack(x, y, w, h, null, null);
    }

    /**
     * 创建一个指定位置、大小和拖拽文件回调的编辑框
     *
     * @param x             编辑框的x坐标
     * @param y             编辑框的y坐标
     * @param w             编辑框的宽度
     * @param h             编辑框的高度
     * @param iDragCallBack 拖拽文件回调接口
     * @return 一个JTextField对象
     */
    public static JTextField getJTextField2DragCallBack(int x, int y, int w, int h, IDragCallBack iDragCallBack) {
        return getJTextField2DragCallBack(x, y, w, h, null, iDragCallBack);
    }

    /**
     * 创建一个指定位置、大小、初始文本和拖拽文件回调的编辑框
     * 支持拖拽文件到编辑框，将文件路径显示在编辑框中，并触发回调
     *
     * @param x             编辑框的x坐标
     * @param y             编辑框的y坐标
     * @param w             编辑框的宽度
     * @param h             编辑框的高度
     * @param text          编辑框的初始文本
     * @param iDragCallBack 拖拽文件回调接口
     * @return 一个JTextField对象
     */
    public static JTextField getJTextField2DragCallBack(int x, int y, int w, int h, String text, IDragCallBack iDragCallBack) {
        JTextField jTextField = new JTextField(text);
        jTextField.setBounds(x, y, w, h);
        // 添加拖拽处理程序
        jTextField.setTransferHandler(new TransferHandler() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean importData(JComponent comp, Transferable t) {
                try {
                    // 获取拖拽的数据
                    Object o = t.getTransferData(DataFlavor.javaFileListFlavor);
                    String filepath = o.toString();
                    // 处理文件路径字符串
                    if (filepath.startsWith("[")) {
                        filepath = filepath.substring(1);
                    }
                    if (filepath.endsWith("]")) {
                        filepath = filepath.substring(0, filepath.length() - 1);
                    }
                    System.out.println("拖拽文件路径: " + filepath);
                    // 如果回调接口不为空，则触发回调
                    if (iDragCallBack != null) {
                        iDragCallBack.dragCallBack(filepath);
                    }
                    // 设置编辑框的文本为文件路径
                    jTextField.setText(filepath);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean canImport(JComponent comp, DataFlavor[] flavors) {
                // 判断是否支持拖拽文件
                for (int i = 0; i < flavors.length; i++) {
                    if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                        return true;
                    }
                }
                return false;
            }
        });
        return jTextField;
    }

    /**
     * 文本
     *
     * @param x                      编辑框的x坐标
     * @param y                      编辑框的y坐标
     * @param w                      编辑框的宽度
     * @param h                      编辑框的高度
     * @param text                   文本
     * @param iFileDropLabelCallBack 回调
     * @return JLabel
     */
    public static JLabel getFileDropLabel(int x, int y, int w, int h,
                                          String text, JswFileDropLabel.IFileDropLabelCallBack iFileDropLabelCallBack) {
        JswFileDropLabel fileDropLabel = new JswFileDropLabel(iFileDropLabelCallBack);
        fileDropLabel.setBounds(x, y, w, h);
        fileDropLabel.setText(text);
        fileDropLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fileDropLabel.setOpaque(true);
        fileDropLabel.setBackground(Color.white);
        return fileDropLabel;
    }

    /**
     * 文本
     *
     * @param x    编辑框的x坐标
     * @param y    编辑框的y坐标
     * @param w    编辑框的宽度
     * @param h    编辑框的高度
     * @param text 文本
     * @return JLabel
     */
    public static JLabel getJLabel(int x, int y, int w, int h, String text) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, w, h);
        return label;
    }

    /**
     * 文本
     *
     * @param x    编辑框的x坐标
     * @param y    编辑框的y坐标
     * @param w    编辑框的宽度
     * @param h    编辑框的高度
     * @param text 文本
     * @return JLabel
     */
    public static JLabel getJLabel(int x, int y, int w, int h, String text, JswOnLongClickListener onLongClickListener) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, w, h);
        label.addMouseListener(onLongClickListener);
        return label;
    }

    /**
     * 文本
     *
     * @param w    编辑框的宽度
     * @param h    编辑框的高度
     * @param text 文本
     * @return JLabel
     */
    public static JLabel getJLabel(int w, int h, String text) {
        JLabel label = new JLabel(text);
        label.setBounds(new Rectangle(w, h));
        return label;
    }

    /**
     * 文本
     *
     * @param w    编辑框的宽度
     * @param h    编辑框的高度
     * @param text 文本
     * @return JLabel
     */
    public static JLabel getJLabel(int w, int h, String text, JswOnLongClickListener onLongClickListener) {
        JLabel label = new JLabel(text);
        label.setBounds(new Rectangle(w, h));
        label.addMouseListener(onLongClickListener);
        return label;
    }

    /**
     * 按钮
     *
     * @param text           按钮文字
     * @param actionListener 点击回调
     * @return 按钮
     */
    public static JButton getJButton(String text, ActionListener actionListener) {
        JButton label = new JButton(text);
        // 设置按钮的内边距为0
        label.setMargin(new Insets(0, 0, 0, 0));
        label.addActionListener(actionListener);
        return label;
    }

    /**
     * 设置固定宽高按钮
     *
     * @param text           按钮文字
     * @param width          width
     * @param height         height
     * @param actionListener 点击回调
     * @return 按钮
     */
    public static JButton getJButton(String text, int width, int height, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height)); // 设置按钮的宽度为80，高度为30
        button.addActionListener(actionListener);
        return button;
    }

    /**
     * 按钮
     *
     * @param text           文字
     * @param tip            提示
     * @param actionListener 点击回调
     * @return 按钮
     */
    public static JButton getJButton(String text, String tip, ActionListener actionListener) {
        JButton label = new JButton(text);
        label.setToolTipText(tip);
        // 设置按钮的内边距为0
        label.setMargin(new Insets(0, 0, 0, 0));
        label.addActionListener(actionListener);
        return label;
    }

    /**
     * 按钮
     *
     * @param text           文字
     * @param actionListener 点击回调
     * @return 按钮
     */
    public static JButton getJButtonMargin(String text, ActionListener actionListener) {
        return getJButtonMargin(text, null, actionListener);
    }

    /**
     * @param text           文字
     * @param tip            提示
     * @param actionListener 点击回调
     * @return 按钮
     */
    public static JButton getJButtonMargin(String text, String tip, ActionListener actionListener) {
        JButton label = new JButton(text);
        label.setToolTipText(tip);
        label.addActionListener(actionListener);
        return label;
    }

    /**
     * @param text           文字
     * @param actionListener 点击回调
     * @return 按钮
     */
    @Deprecated
    public static JButton getJButtonMargin(String text, JswOnLongClickListener actionListener) {
        JButton jButton = new JButton(text);
//        label.setMargin(new Insets(0, 0, 0, 0)); // 设置按钮的内边距为0
        jButton.addMouseListener(actionListener);
        actionListener.setBtnText(jButton.getText());
        return jButton;
    }

    /**
     * @param text           文字
     * @param actionListener 点击回调
     * @param text           text
     * @param actionListener actionListener
     * @return 按钮
     */
    @Deprecated
    public static JButton getJButtonMargin(String text, String tipText, JswOnLongClickListener actionListener) {
        JButton jButton = new JButton(text);
//        label.setMargin(new Insets(0, 0, 0, 0)); // 设置按钮的内边距为0
        jButton.addMouseListener(actionListener);
        actionListener.setBtnText(jButton.getText());
        jButton.setToolTipText(tipText);
        return jButton;
    }

    /**
     * 长按按钮
     *
     * @param text           文字
     * @param actionListener 点击回调
     * @return 按钮
     */
    @Deprecated
    public static JButton getJButton(String text, JswOnLongClickListener actionListener) {
        JButton label = new JButton(text);
        label.setMargin(new Insets(0, 0, 0, 0)); // 设置按钮的内边距为0
        actionListener.setBtnText(text);
        label.addMouseListener(actionListener);
        return label;
    }

    /**
     * 按钮
     *
     * @param x              x
     * @param y              y
     * @param w              w
     * @param h              h
     * @param text           text
     * @param actionListener 点击回调
     * @return 按钮
     */
    @Deprecated
    public static JButton getJButton(int x, int y, int w, int h, String text, ActionListener actionListener) {
        JButton label = new JButton(text);
        label.setBounds(x, y, w, h);
        label.setMargin(new Insets(0, 0, 0, 0)); // 设置按钮的内边距为0
        label.addActionListener(actionListener);
        return label;
    }

    /**
     * 按钮
     *
     * @param x              x
     * @param y              y
     * @param w              w
     * @param h              h
     * @param text           text
     * @param tip            tip
     * @param actionListener 点击回调
     * @return 按钮
     */
    public static JButton getJButton(int x, int y, int w, int h, String text, String tip, ActionListener actionListener) {
        JButton label = getJButton(x, y, w, h, text, actionListener);
        label.setToolTipText(tip);
        return label;
    }

    /**
     * 按钮 长按监听
     *
     * @param x              x
     * @param y              y
     * @param w              w
     * @param h              h
     * @param text           text
     * @param actionListener 点击回调
     * @return 按钮
     */
    public static JButton getJButton(int x, int y, int w, int h, String text, JswOnLongClickListener actionListener) {
        JButton label = new JButton(text);
        label.setBounds(x, y, w, h);
        label.setMargin(new Insets(0, 0, 0, 0)); // 设置按钮的内边距为0
        label.addMouseListener(actionListener);
        return label;
    }

    /**
     * 下拉选项
     *
     * @param x x
     * @param y y
     * @param w w
     * @param h h
     * @return 下拉框
     */
    public static JComboBox<String> getJComboBox(int x, int y, int w, int h) {
        return getJComboBox(x, y, w, h, null, null);
    }

    /**
     * 下拉选项
     *
     * @param items key:命令, val:名称
     * @param x     x
     * @param y     y
     * @param w     w
     * @param h     h
     * @return 下拉框
     */
    public static JComboBox<String> getJComboBox(int x, int y, int w, int h, List<String> items) {
        return getJComboBox(x, y, w, h, items, null);
    }

    /**
     * 下拉选项
     *
     * @param items key:命令, val:名称
     * @param items items
     * @param x     x
     * @param y     y
     * @param w     w
     * @param h     h
     * @return 下拉框
     */
    public static JComboBox<String> getJComboBox(int x, int y, int w, int h, List<String> items, ActionListener itemListener) {
        JComboBox<String> jComboBox = new JComboBox<>();
        jComboBox.setBounds(x, y, w, h);
        if (items != null) {
            items.forEach(jComboBox::addItem);
        }
//        jComboBox.setSelectedIndex(1);
//        jComboBox.setPopupVisible(false);
//        jComboBox.setVisible(true);
        jComboBox.addActionListener(itemListener);
        return jComboBox;
    }

    /**
     * 多行编辑框
     *
     * @param text text
     * @return 多行编辑框
     */
    public static JTextArea getJTextArea(String text) {
        JTextArea liftTextArea = new JTextArea(text);
        liftTextArea.setLineWrap(true);
        return liftTextArea;
    }


    /**
     * 多行编辑框
     *
     * @return 多行编辑框
     */
    public static JTextArea getJTextArea() {
        JTextArea liftTextArea = new JTextArea();
        liftTextArea.setLineWrap(true);
        return liftTextArea;
    }

    /**
     * 下拉列表回调
     */
    public interface JComboBoxClickCallBack {
        /**
         * 点击回调
         * @param jComboBox jComboBox
         * @param e e
         * @param selectedIndex selectedIndex
         * @param item item
         */
        void click(JComboBox<String> jComboBox, ActionEvent e, int selectedIndex, String item);
    }

    /**
     * 下拉选项
     *
     * @param items                  key:命令, val:名称
     * @param jComboBoxClickCallBack jComboBoxClickCallBack
     * @return JComboBox JComboBox
     */
    public static JComboBox<String> getJComboBox(List<String> items, JComboBoxClickCallBack jComboBoxClickCallBack) {
        JComboBox<String> jComboBox = new JComboBox<>();
        // 设置组合框为不可编辑（默认行为）
        jComboBox.setEditable(false);
        if (items != null) {
            items.forEach(jComboBox::addItem);
        }
//        jComboBox.setSelectedIndex(1);
//        jComboBox.setPopupVisible(false);
//        jComboBox.setVisible(true);
        jComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jComboBoxClickCallBack.click(jComboBox, e, jComboBox.getSelectedIndex(), (String) jComboBox.getSelectedItem());
            }
        });
        return jComboBox;
    }

    /**
     * 下拉选项
     *
     * @param items              key:命令, val:名称
     * @param x                  x
     * @param y                  y
     * @param w                  w
     * @param h                  h
     * @param ijComboBoxListener ijComboBoxListener
     * @return JComboBox
     */
    public static JComboBox<String> getJComboBoxEdit(int x, int y, int w, int h, List<String> items, IJComboBoxListener ijComboBoxListener) {
        JComboBox<String> jComboBox = new JComboBox<>();
        jComboBox.setBounds(x, y, w, h);
        if (items != null) {
            items.forEach(jComboBox::addItem);
        }
        jComboBox.setEditable(true);
        jComboBox.addActionListener(e -> {
            if (ijComboBoxListener != null)
                ijComboBoxListener.itemClick(e, jComboBox, jComboBox.getSelectedIndex(), String.valueOf(jComboBox.getSelectedItem()));
        });
        // 获取编辑框组件
        Component editorComponent = jComboBox.getEditor().getEditorComponent();
        // 添加鼠标点击监听器
        editorComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                jComboBox.setPopupVisible(true);// 设置下拉列表框可见
                System.out.println("编辑框被点击");
                if (ijComboBoxListener != null) ijComboBoxListener.mouseClicked(mouseEvent, jComboBox);
            }
        });
        // 添加焦点监听器
        editorComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("编辑框获得焦点");
                if (ijComboBoxListener != null) ijComboBoxListener.focusGained(e, jComboBox);
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("编辑框失去焦点");
                if (ijComboBoxListener != null) ijComboBoxListener.focusLost(e, jComboBox);
            }
        });
        // 添加键盘事件监听器
        jComboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (ijComboBoxListener != null) ijComboBoxListener.enter(e, jComboBox, jComboBox.getSelectedItem());
                }
            }
        });
        return jComboBox;
    }

    /**
     * JLabel自动换行
     *
     * @param jLabel  jLabel
     * @param content 内容
     */
    public static void JlabelSetText(JLabel jLabel, String content) {
        StringBuilder builder = new StringBuilder("<html>");
        char[] chars = content.toCharArray();
        FontMetrics fontMetrics = jLabel.getFontMetrics(jLabel.getFont());
        int start = 0;
        int len = 0;
        while (start + len < content.length()) {
            while (true) {
                len++;
                if (start + len > content.length()) break;
                if (fontMetrics.charsWidth(chars, start, len)
                        > jLabel.getWidth()) {
                    break;
                }
            }
            builder.append(chars, start, len - 1).append("<br/>");
            start = start + len - 1;
            len = 0;
        }
        builder.append(chars, start, content.length() - start);
        builder.append("</html>");
        jLabel.setText(builder.toString());
    }

    /**
     * @param x              x
     * @param y              y
     * @param w              w
     * @param h              h
     * @param btnName        btnName
     * @param actionListener actionListener
     * @return 复选框
     */
    public static JCheckBox getJCheckBox(int x, int y, int w, int h, String btnName, ActionListener actionListener) {
        JCheckBox checkBox = new JCheckBox(btnName);
        checkBox.setBounds(x, y, w, h);
        checkBox.addActionListener(actionListener);
//        checkBox.addActionListener(e -> {
//            JCheckBox source = (JCheckBox) e.getSource();
//            System.out.println("勾选状态: " + source.isSelected());
//        });
        return checkBox;
    }

    /**
     * @param actionListener actionListener
     * @param btnName        文字
     * @return 复选框
     * @return 复选框
     */
    public static JCheckBox getJCheckBox(String btnName, ActionListener actionListener) {
        JCheckBox checkBox = new JCheckBox(btnName);
        checkBox.addActionListener(actionListener);
//        checkBox.addActionListener(e -> {
//            JCheckBox source = (JCheckBox) e.getSource();
//            System.out.println("勾选状态: " + source.isSelected());
//        });
        return checkBox;
    }

    /**
     * @param x x
     * @param y y
     * @param w w
     * @param h h
     * @return JPanel
     */
    public static JPanel getFlowLayout(int x, int y, int w, int h) {
        JPanel otherJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        otherJPanel.setBounds(x, y, w, h);
        otherJPanel.setBackground(Color.white);  // 可选：设置背景颜色以更容易看到 JPanel
        otherJPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // 添加边框
        return otherJPanel;
    }

    public static JPanel getFlowLayout(Frame frame) {
        JPanel otherJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        otherJPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        return otherJPanel;
    }

    /**
     * 创建一个JToggleButton
     *
     * @param open   open
     * @param close  close
     * @param width  width
     * @param height height
     * @return JToggleButton
     */
    public static JToggleButton getJToggleButton(String open, String close, int width, int height) {
        JToggleButton toggleButton = new JToggleButton(close, false);
        if (width > 0 && height > 0) {
            // 设置按钮的宽高
            toggleButton.setPreferredSize(new Dimension(width, height)); // 设置宽150，高60
        }

        toggleButton.addChangeListener(e -> {
            if (toggleButton.isSelected()) {
                toggleButton.setText(open);
            } else {
                toggleButton.setText(close);
            }
        });
        return toggleButton;
    }

    /**
     * 开关按钮
     */
    public static JToggleButton getJToggleButton() {   // 创建一个JToggleButton
        return getJToggleButton("ON", "OFF", 0, 0);
    }

    /**
     * 拖拽回调
     */
    public interface IDragCallBack {
        /**
         * @param path path
         */
        void dragCallBack(String path);
    }

    /**
     * 文本框回调
     */
    public interface IJTextFieldListener {

        /**
         * 获取焦点
         *
         * @param jTextField jTextField
         */
        void focusGained(JTextField jTextField);

        /**
         * 失去焦点
         *
         * @param jTextField jTextField
         */
        void focusLost(JTextField jTextField);

        /**
         * 按下回车
         *
         * @param e          e
         * @param jTextField jTextField
         */
        void actionPerformed(ActionEvent e, JTextField jTextField);
    }

    /**
     * 组合框回调
     */
    public interface IJComboBoxListener {

        /**
         * 按下回车
         *
         * @param keyEvent     keyEvent
         * @param jComboBox    jComboBox
         * @param selectedItem selectedItem
         */
        default void enter(KeyEvent keyEvent, JComboBox<String> jComboBox, Object selectedItem) {

        }

        /**
         * 编辑框失去焦点
         *
         * @param focusEvent focusEvent
         * @param jComboBox  jComboBox
         */
        default void focusLost(FocusEvent focusEvent, JComboBox<String> jComboBox) {
        }

        /**
         * 编辑框获得焦点
         *
         * @param focusEvent focusEvent
         * @param jComboBox  jComboBox
         */
        default void focusGained(FocusEvent focusEvent, JComboBox<String> jComboBox) {
        }

        /**
         * 编辑框被点击
         *
         * @param mouseEvent mouseEvent
         * @param jComboBox  jComboBox
         */
        default void mouseClicked(MouseEvent mouseEvent, JComboBox<String> jComboBox) {
        }

        /**
         * 点击item
         *
         * @param actionEvent  actionEvent
         * @param jComboBox    jComboBox
         * @param index        获取到选中项的索引
         * @param selectedItem selectedItem
         */
        default void itemClick(ActionEvent actionEvent, JComboBox<String> jComboBox, int index, String selectedItem) {
        }
    }


    /**
     * 可撤销的 JTextArea
     *
     * @param x x
     * @param y y
     * @param w w
     * @param h h
     * @return JTextArea
     */
    public static JTextArea getJTextAreaUndo(int x, int y, int w, int h) {
        return getJTextAreaUndo(x, y, w, h, "");
    }

    /**
     * 可撤销的 JTextArea
     *
     * @param x    x
     * @param y    y
     * @param w    w
     * @param h    h
     * @param text text
     * @return JTextArea
     */
    public static JTextArea getJTextAreaUndo(int x, int y, int w, int h, String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_D) {
                    // 在此处处理快捷键操作
                    String selectedText = textArea.getSelectedText();
                    int caretPosition = textArea.getCaretPosition();
                    if (selectedText != null && !selectedText.isEmpty()) {
                        textArea.insert(selectedText, caretPosition);
                    } else {
                        int lineNumber;
                        try {
                            lineNumber = textArea.getLineOfOffset(caretPosition);
                            int lineStartOffset = textArea.getLineStartOffset(lineNumber);
                            int lineEndOffset = textArea.getLineEndOffset(lineNumber);
                            String lineText = textArea.getText(lineStartOffset, lineEndOffset - lineStartOffset).replace("\n", "");
                            System.out.println("光标所在行的文本：" + lineText);
                            textArea.insert("\n" + lineText, caretPosition);
                        } catch (BadLocationException e1) {
                            e1.printStackTrace();
                        }

                    }
                    System.out.println("保存");
                }
            }
        });
        textArea.setBounds(x, y, w, h);
        textArea.setComponentPopupMenu(new JswPopupMenus()
                .addMenuItem("复制全部", (menuName, menuItem) -> {
                    WinUtils.setSysClipboardText(textArea.getText());
                }).addMenuItem("粘贴文本", (menuName, menuItem) -> {
                    textArea.insert(WinUtils.getSysClipboardText(), textArea.getCaretPosition());
                }).addMenuItem("复制选中", (menuName, menuItem) -> {
                    WinUtils.setSysClipboardText(textArea.getSelectedText());
                }).addMenuItem("自动换行", (menuName, menuItem) -> {
                    textArea.setLineWrap(!textArea.getLineWrap());
                }).addMenuItem("去除空行", (menuName, menuItem) -> {
                    textArea.setText(StringUtil.removalNullLine(textArea.getText()));
                }).addMenuItem("去除空格", (menuName, menuItem) -> {
                    textArea.setText(StringUtil.removalSpace(textArea.getText()));
                }).addMenuItem("合并一行", (menuName, menuItem) -> {
                    if (textArea.getText().isEmpty()) {
                        textArea.setText(WinUtils.getSysClipboardText());
                    }
                    textArea.setText(StringUtil.mergeOneLine(textArea.getText(), false));
                }).addMenuItem("格式化json", (menuName, menuItem) -> {
                    if (textArea.getText().trim().isEmpty() || !textArea.getText().trim().startsWith("{") && !textArea.getText().trim().endsWith("}")) {
                        textArea.setText(StringUtil.formatJson(WinUtils.getSysClipboardText()));
                    } else {
                        textArea.setText(StringUtil.formatJson(textArea.getText()));
                    }
                    WinUtils.setSysClipboardText(textArea.getText());
                })/*.addMenu("json转实体", menuName -> {
                }).addMenu("实体json", menuName -> {
                })*/
                .addMenuGroup("行筛选", (menuIndex, menuName) -> {
                    if (menuIndex == 0) {
                        textArea.setText(WinUtils.getSysClipboardText());
                        JswDialogUtils.showEditDialogSimple(null, content -> {
                            String[] split = textArea.getText().split("\n");
                            StringBuilder sb = new StringBuilder();
                            for (String linStr : split) {
                                if (linStr.startsWith(content)) {
                                    sb.append(linStr).append("\n");
                                }
                            }
                            textArea.setText(sb.toString());
                            WinUtils.setSysClipboardText(sb.toString());
                        });
                    } else if (menuIndex == 1) {
                        textArea.setText(WinUtils.getSysClipboardText());
                        JswDialogUtils.showEditDialogSimple(null, content -> {
                            String[] split = textArea.getText().split("\n");
                            StringBuilder sb = new StringBuilder();
                            for (String linStr : split) {
                                if (linStr.endsWith(content)) {
                                    sb.append(linStr).append("\n");
                                }
                            }
                            textArea.setText(sb.toString());
                            WinUtils.setSysClipboardText(sb.toString());
                        });
                    } else if (menuIndex == 2) {
                        textArea.setText(WinUtils.getSysClipboardText());
                        JswDialogUtils.showEditDialogSimple(null, content -> {
                            String[] split = textArea.getText().split("\n");
                            StringBuilder sb = new StringBuilder();
                            for (String linStr : split) {
                                if (linStr.contains(content)) {
                                    sb.append(linStr).append("\n");
                                }
                            }
                            textArea.setText(sb.toString());
                            WinUtils.setSysClipboardText(sb.toString());
                        });
                    } else if (menuIndex == 3) {
                        textArea.setText(WinUtils.getSysClipboardText());
                        JswDialogUtils.showEditDialogSimple(null, content -> {
                            String[] split = textArea.getText().split("\n");
                            StringBuilder sb = new StringBuilder();
                            for (String linStr : split) {
                                if (!linStr.contains(content)) {
                                    sb.append(linStr).append("\n");
                                }
                            }
                            textArea.setText(sb.toString());
                            WinUtils.setSysClipboardText(sb.toString());
                        });
                    }
                }, "筛选开头", "筛选结尾", "包含", "不包含")
                .addMenuItem("json合并一行", (menuName, menuItem) -> {
                    String text1 = textArea.getText();
                    if (text1.length() == 0) {
                        text1 = WinUtils.getSysClipboardText();
                    }
                    textArea.setText(StringUtil.mergeOneLine(text1, true));
                }).addMenuItem("生成换行数字", (menuName, menuItem) -> {
                    JswDialogUtils.showEditDialogSimple("10", new JswDialogUtils.ISimpleCallBack() {
                        @Override
                        public void str(String content) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < Integer.parseInt(content); i++) {
                                sb.append(i).append("\n");
                            }
                            textArea.setText(sb.toString());
                            WinUtils.setSysClipboardText(sb.toString());
                            JswDialogUtils.showAutoCloseDialogShort("已复制");
                        }
                    });
                }).addMenuItem("开头添加数字", (menuName, menuItem) -> {
                    String textStr = textArea.getText();
                    if (textStr.isEmpty()) {
                        textStr = WinUtils.getSysClipboardText();
                    }
                    StringBuilder sb = new StringBuilder();
                    StringUtil.readStrByLine(textStr, (line, lineIndex) -> {
                        sb.append((lineIndex - 1)).append(line).append("\n");
                        return true;
                    });
                    textArea.setText(sb.toString());
                    WinUtils.setSysClipboardText(sb.toString());
                }).addMenuItem("结尾添加数字", (menuName, menuItem) -> {
                    String textStr = textArea.getText();
                    if (textStr.isEmpty()) {
                        textStr = WinUtils.getSysClipboardText();
                    }
                    StringBuilder sb = new StringBuilder();
                    StringUtil.readStrByLine(textStr, (line, lineIndex) -> {
                        sb.append(line).append((lineIndex - 1)).append("\n");
                        return true;
                    });
                    textArea.setText(sb.toString());
                    WinUtils.setSysClipboardText(sb.toString());
                    JswDialogUtils.showAutoCloseDialogShort("已复制");
                }).addMenuItem("清除重复行", (menuName, menuItem) -> {
                    ArrayList<String> strings = new ArrayList<>();
                    StringBuilder sb = new StringBuilder();
                    String[] split = textArea.getText().split("\n");
                    for (String lin : split) {
                        if (!strings.contains(lin)) {
                            strings.add(lin);
                            sb.append(lin).append("\n");
                        }
                    }
                    textArea.setText(sb.toString());
                }).addMenuItem("清空", (menuName, menuItem) -> {
                    textArea.setText("");
                })
        );
//        JScrollPane scrollPane = new JScrollPane(textArea);
        // 添加鼠标事件，检测双击
        /*textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) { // 双击事件 双击粘贴
                    // 调用粘贴方法
                    try {
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        String data = (String) clipboard.getData(DataFlavor.stringFlavor);
                        textArea.setText(textArea.getText() + data); // 将剪贴板内容粘贴到文本区域
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });*/
        // 创建一个UndoManager
        UndoManager undoManager = new UndoManager();

        // 将UndoManager注册到Document中
        Document doc = textArea.getDocument();
        doc.addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });

        // 撤销操作
        KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
        textArea.getInputMap().put(undoKeyStroke, "undo");
        textArea.getActionMap().put("undo", new AbstractAction("undo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        });

        // 重做操作
        KeyStroke redoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);
        textArea.getInputMap().put(redoKeyStroke, "redo");
        textArea.getActionMap().put("redo", new AbstractAction("redo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        });
        return textArea;
    }

    /**
     * 不能编辑的 JTextArea
     *
     * @param x             x
     * @param y             y
     * @param w             w
     * @param h             h
     * @param text          text
     * @param mouseListener mouseListener
     * @return JTextArea
     */
    public static JTextArea getJTextAreaDescription(int x, int y, int w, int h, String text, MouseListener mouseListener) {
        JTextArea textArea = new JTextArea();
        textArea.setBounds(x, y, w, h); // x, y, width, height
        textArea.setEditable(false);
        textArea.setText(text);
//        // 将JTextArea放入JScrollPane中
//        JScrollPane scrollPane = new JScrollPane(textArea);
//        // 设置滚动面板的大小和位置
//        scrollPane.setBounds(x, y, w, h); // x, y, width, height
//        if (mouseListener != null) {
//            textArea.addMouseListener(mouseListener);
//        }
        return textArea;
    }

    /**
     * 不能编辑的 JTextArea
     *
     * @param x             x
     * @param y             y
     * @param w             w
     * @param h             h
     * @param text          text
     * @param mouseListener mouseListener
     * @return JScrollPane JTextArea
     */
    public static JScrollPane getJTextAreaScrol(int x, int y, int w, int h, String text, MouseListener mouseListener) {
        JTextArea textArea = new JTextArea();
        textArea.setBounds(x, y, w, h); // x, y, width, height
        textArea.setEditable(false);
        textArea.setText(text);
        // 将JTextArea放入JScrollPane中
        JScrollPane scrollPane = new JScrollPane(textArea);
        // 设置滚动面板的大小和位置
        scrollPane.setBounds(x, y, w, h); // x, y, width, height
        if (mouseListener != null) {
            textArea.addMouseListener(mouseListener);
        }
        return scrollPane;
    }

    /**
     * 右键menu和单机按钮
     *
     * @param btnName          btnName
     * @param menuJButtonClick menuJButtonClick
     * @param items            items
     * @return JButton
     */
    public static JButton getRightMenuJButton(String btnName, IRightMenuJButtonClick menuJButtonClick, String... items) {
        return getRightMenuJButton(btnName, null, menuJButtonClick, items);
    }

    /**
     * 可设置位置
     * 带右键菜单的按钮
     * 右键menu和单机按钮
     *
     * @param x                x
     * @param y                y
     * @param w                w
     * @param h                h
     * @param btnName          btnName
     * @param tip              tip
     * @param menuJButtonClick menuJButtonClick
     * @param items            items
     * @return JButton
     */
    public static JButton getRightMenuJButton(int x, int y, int w, int h, String btnName, String tip, IRightMenuJButtonClick menuJButtonClick, String... items) {
        JButton rightMenuJButton = getRightMenuJButton(btnName, tip, menuJButtonClick, items);
        rightMenuJButton.setBounds(x, y, w, h);
        return rightMenuJButton;
    }

    /**
     * 带右键菜单的按钮
     * 右键menu和单机按钮
     *
     * @param btnName          btnName
     * @param tip              tip
     * @param menuJButtonClick menuJButtonClick
     * @param items            items
     * @return JButton
     */
    public static JButton getRightMenuMarginJButton(String btnName, String tip, IRightMenuJButtonClick menuJButtonClick, String... items) {
        JButton rightMenuJButton = getRightMenuJButton(btnName, tip, menuJButtonClick, items);
        rightMenuJButton.setMargin(new Insets(0, 0, 0, 0)); // 设置按钮的内边距为0
        return rightMenuJButton;
    }

    /**
     * 带右键菜单的按钮
     * 右键menu和单机按钮
     *
     * @param btnName          btnName
     * @param tip              tip
     * @param menuJButtonClick menuJButtonClick
     * @param items            items
     * @return JButton
     */
    public static JButton getRightMenuJButton(String btnName, String tip, IRightMenuJButtonClick menuJButtonClick, String... items) {
        // 添加按钮，设置点击事件
        JButton button = new JButton(btnName);
        if (tip != null && !tip.isEmpty()) {
            button.setToolTipText(tip);
        }
        button.addActionListener(e -> menuJButtonClick.clickBtn(button));
        if (items == null || items.length == 0) {
            return button;
        }
        // 添加鼠标事件，设置右键菜单
        JPopupMenu popupMenu = new JPopupMenu();
        for (int i = 0; i < items.length; i++) {
            JMenuItem menuItem = new JMenuItem(items[i]);
            int finalI = i;
            menuItem.addActionListener(e -> menuJButtonClick.clickMenu(finalI, button));
            popupMenu.add(menuItem);
        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    // 左键双击的操作
                    menuJButtonClick.doubleClick();
                } else if (e.getButton() == MouseEvent.BUTTON3) { // 判断是否是右键单击事件
                    popupMenu.show(button, e.getX(), e.getY()); // 显示右键菜单
                }
            }
        });
        return button;
    }

    /**
     * 带右键菜单的按钮
     * 右键menu和单机按钮
     *
     * @param btnName          btnName
     * @param tip              tip
     * @param menuJButtonClick menuJButtonClick
     * @param items            items
     * @return JButton
     */
    public static JButton getRightMenuJButton2(String btnName, String tip, IRightMenuJButtonClick2 menuJButtonClick, ClickButton... items) {
        // 添加按钮，设置点击事件
        JButton button = new JButton(btnName);
        if (tip != null && !tip.isEmpty()) {
            button.setToolTipText(tip);
        }
        button.addActionListener(e -> menuJButtonClick.clickBtn());
        if (items == null || items.length == 0) {
            return button;
        }
        // 添加鼠标事件，设置右键菜单
        JPopupMenu popupMenu = new JPopupMenu();
        for (int i = 0; i < items.length; i++) {
            JMenuItem menuItem = new JMenuItem(items[i].getLabel());
            int finalI = i;
            menuItem.addActionListener(e -> items[finalI].triggerClick());
            popupMenu.add(menuItem);
        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    // 左键双击的操作
                    menuJButtonClick.doubleClick();
                } else if (e.getButton() == MouseEvent.BUTTON3) { // 判断是否是右键单击事件
                    popupMenu.show(button, e.getX(), e.getY()); // 显示右键菜单
                }
            }
        });
        return button;
    }

    /**
     * 带右键菜单的按钮
     * 右键menu和单机按钮
     *
     * @param tip   tip
     * @param items items
     * @return JButton
     */
    public static JButton getRightMenuJButton3(String tip, ClickButton... items) {
        if (items == null || items.length == 0) {
            return new JButton("需要添加按钮");
        }
        // 添加按钮，设置点击事件
        JButton button = new JButton(items[0].getLabel());
        if (tip != null && !tip.isEmpty()) {
            button.setToolTipText(tip);
        }
        button.addActionListener(e -> items[0].triggerClick());
        if (items.length == 1) {
            return button;
        }
        // 添加鼠标事件，设置右键菜单
        JPopupMenu popupMenu = new JPopupMenu();
        for (int i = 1; i < items.length; i++) {
            JMenuItem menuItem = new JMenuItem(items[i].getLabel());
            int finalI = i;
            menuItem.addActionListener(e -> items[finalI].triggerClick());
            popupMenu.add(menuItem);
        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    // 左键双击的操作
//                    menuJButtonClick.doubleClick();
                } else if (e.getButton() == MouseEvent.BUTTON3) { // 判断是否是右键单击事件
                    popupMenu.show(button, e.getX(), e.getY()); // 显示右键菜单
                }
            }
        });
        return button;
    }

    /**
     * JButton
     *
     * @param text           text
     * @param tip            tip
     * @param actionListener actionListener
     * @return JButton
     */
    @Deprecated
    public static JButton getJButton(String text, String tip, JswOnLongClickListener actionListener) {
        JButton jButton = new JButton(text);
        jButton.setMargin(new Insets(0, 0, 0, 0)); // 设置按钮的内边距为0
        jButton.addMouseListener(actionListener);
        actionListener.setBtnText(jButton.getText());
        jButton.setToolTipText(tip);
        return jButton;
    }

    /**
     * 下拉框
     *
     * @param items             items
     * @param IPosClickListener IPosClickListener
     * @param <T>               泛型
     * @return 下拉框
     */
    public static <T> JComboBox<T> createComboBox(ArrayList<T> items, IPosClickListener IPosClickListener) {
        JComboBox<T> comboBox = new JComboBox<>();
        DefaultComboBoxModel<T> model = new DefaultComboBoxModel<>();
        for (T item : items) {
            model.addElement(item);
        }
        comboBox.setModel(model);
        comboBox.addActionListener(e -> {
            JComboBox comboBox1 = (JComboBox) e.getSource();
            String selectedValue = (String) comboBox1.getSelectedItem();
            System.out.println("Selected: " + selectedValue + "  " + comboBox1.getSelectedIndex());
            IPosClickListener.click(comboBox1.getSelectedIndex(), selectedValue);
        });

        return comboBox;
    }

    /**
     * 单选按钮
     *
     * @param frame              frame
     * @param ijRadioButtonClick ijRadioButtonClick
     * @param btns               btns
     */
    private static void getJRadioButton(final JFrame frame, IJRadioButtonClick ijRadioButtonClick, String... btns) {
//更多请阅读：https://www.yiibai.com/swingexamples/using_radiobutton_group.html
        JPanel panel = new JPanel();
        LayoutManager layout = new FlowLayout();
        panel.setLayout(layout);
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < btns.length; i++) {
            JRadioButton radioButton = new JRadioButton(btns[i]);
            group.add(radioButton);
            panel.add(radioButton);
            int clickIndex = i;
            radioButton.setAction(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ijRadioButtonClick.click(clickIndex, btns[clickIndex]);
                }
            });
        }
        frame.getContentPane().add(panel, BorderLayout.CENTER);
    }

    /**
     * JList
     *
     * @param data     data
     * @param callback callback
     * @param <T>      泛型
     * @return JList
     */
    public static <T> JList<T> createJList(List<T> data, CellRendererCallback<T> callback) {
        DefaultListModel<T> model = new DefaultListModel<>();
        return createJList(model, data, callback);
    }

    /**
     * @param data     data
     * @param callback callback
     * @param <T>      泛型
     * @param model    model
     * @return JList
     */
    public static <T> JList<T> createJList(DefaultListModel<T> model, List<T> data, CellRendererCallback<T> callback) {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        for (T item : data) {
            model.addElement(item);
        }
        JList<T> tjList = new JList<>(model);
        tjList.setCellRenderer(new CustomListRenderer<>(callback));
        // 添加鼠标事件监听器
        tjList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 获取双击的项目索引
                int index = tjList.locationToIndex(e.getPoint());
                if (index < 0) {
                    return;
                }
                // 判断是否为双击事件（通过点击次数来判断）
                if (e.getClickCount() == 2) {
                    atomicBoolean.set(true);
                    // 获取双击的项目值
                    T selectedItem = tjList.getModel().getElementAt(index);
                    System.out.println("双击 clicked: " + selectedItem);
                    callback.doubleClick(selectedItem);
                } else if (e.getClickCount() == 1) {

                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (atomicBoolean.get()) {
                                timer.cancel();
                                atomicBoolean.set(false);
                                return;
                            }
                            T selectedItem = tjList.getModel().getElementAt(index);
                            System.out.println("单击 clicked: " + selectedItem);
                            callback.click(selectedItem);
                        }
                    }, 300);
                }
            }
        });
        return tjList;
    }

    /**
     * 单选按钮回调
     */
    public interface IJRadioButtonClick {
        /**
         * 点击回调
         *
         * @param pos     pos
         * @param btnName btnName
         */
        void click(int pos, String btnName);
    }

    /**
     * 列表回调
     *
     * @param <T> 泛型
     */
    public interface CellRendererCallback<T> {
        //        void click(JLabel cell, T value, boolean isSelected);

        /**
         * 获取文本
         *
         * @param t t
         * @return 字符串
         */
        String getText(T t);

        /**
         * 单击回调
         *
         * @param t t
         */
        void click(T t);

        /**
         * 双击回调
         *
         * @param t t
         */
        void doubleClick(T t);
    }

    /**
     * 自定义列表渲染器
     *
     * @param <T> 泛型
     */
    static class CustomListRenderer<T> implements ListCellRenderer<T> {
        private final CellRendererCallback<T> callback;

        /**
         * @param callback 回调
         */
        public CustomListRenderer(CellRendererCallback<T> callback) {
            this.callback = callback;
        }

        /**
         * 渲染列表项
         *
         * @param list         The JList we're painting.
         * @param value        The value returned by list.getModel().getElementAt(index).
         * @param index        The cells index.
         * @param isSelected   True if the specified cell was selected.
         * @param cellHasFocus True if the specified cell has the focus.
         * @return Component
         */
        @Override
        public Component getListCellRendererComponent(JList<? extends T> list, T value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel cell = new JLabel();
            cell.setText(callback.getText(value)); // 这里假设 Person 有一个 getName 方法
            cell.setOpaque(true);
            cell.setBackground(isSelected ? new Color(184, 207, 229) : Color.WHITE); // 根据选中状态设置背景色
            cell.setForeground(isSelected ? Color.WHITE : Color.BLACK); // 根据选中状态设置前景色
            return cell;
        }
    }

    /**
     * 下拉框
     *
     * @param x                 x
     * @param y                 y
     * @param w                 w
     * @param h                 h
     * @param items             items
     * @param iPosClickListener iPosClickListener
     * @param <T>               泛型
     * @return JComboBox
     */
    public static <T> JComboBox<T> createComboBox(int x, int y, int w, int h, ArrayList<T> items, IPosClickListener iPosClickListener) {
        JComboBox<T> comboBox2 = createComboBox(items, iPosClickListener);
        comboBox2.setBounds(x, y, w, h);
        return comboBox2;
    }

    /**
     * 添加顶部菜单按钮
     *
     * @param menuBar            menuBar
     * @param menuTitle          menuTitle
     * @param iItemClickListener iItemClickListener
     * @param menuItems          menuItems
     */
    public static void addJMenus(JMenuBar menuBar, String menuTitle, IItemClickListener iItemClickListener, String... menuItems) {
        // 创建菜单并添加到菜单栏
        JMenu optionsMenu = new JMenu(menuTitle);
        // 添加置顶菜单项
        for (int i = 0; i < menuItems.length; i++) {
            JMenuItem topItem = new JMenuItem(menuItems[i]);
            int pos = i;
            topItem.addActionListener(e -> {
                iItemClickListener.click(pos);
            });
            optionsMenu.add(topItem);
        }
        menuBar.add(optionsMenu);
    }

    /**
     * 获取多个按钮
     *
     * @param x    x
     * @param y    y
     * @param w    w
     * @param h    h
     * @param btns btns
     * @return Component
     */
    public static Component getMrBtns(int x, int y, int w, int h, String... btns) {
        JPanel otherJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        otherJPanel.setBounds(x, y, w, h);
        ArrayList<Mrb> buttons = new ArrayList<>();
        int index = 0;
        for (String btn : btns) {
            Mrb button = new Mrb(btn);
            button.setBackground(Color.LIGHT_GRAY);
            ++index;
            button.setIndex(index);
            buttons.add(button);
            otherJPanel.add(button);
//            button.setBounds(x, y, w, h);
            button.addActionListener(e -> {
                // 设置当前点击按钮为选中状态，其他按钮为非选中状态
                for (Mrb mbtn : buttons) {
                    if (mbtn == button) {
                        mbtn.setBackground(Color.RED); // 点击的按钮背景色设为红色
                        System.out.println(" xuanzhong " + mbtn.getIndex());
                    } else {
                        mbtn.setBackground(Color.LIGHT_GRAY); // 其他按钮背景色设为蓝色
                    }
                }
            });

        }

        return otherJPanel;
    }

    /**
     * 顶部菜单按钮点击回调
     */
    public interface IPosClickListener extends EventListener {
        /**
         * 点击回调
         * @param index index
         * @param val val
         */
        void click(int index, String val);
    }

    /**
     * 顶部菜单按钮
     */
    static class Mrb extends JButton {
        private String extraInfo;
        private int index;

        public Mrb(String text) {
            super(text);
        }

        public String getExtraInfo() {
            return extraInfo;
        }

        /**
         * 设置额外信息
         * @param extraInfo extraInfo
         */
        public void setExtraInfo(String extraInfo) {
            this.extraInfo = extraInfo;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    /**
     * 添加顶部菜单按钮
     *
     * @param frame              frame
     * @param menuTitle          menuTitle
     * @param iItemClickListener iItemClickListener
     * @param menuItems          menuItems
     */
    public static void addJMenus(JFrame frame, String menuTitle, IItemClickListener iItemClickListener, String... menuItems) {
        JMenuBar menuBar = frame.getJMenuBar();
        if (menuBar == null) {
            menuBar = new JMenuBar();
        }
//        JMenuBar menuBar = new JMenuBar();
        // 创建菜单并添加到菜单栏
        JMenu optionsMenu = new JMenu(menuTitle);
        // 添加置顶菜单项
        for (int i = 0; i < menuItems.length; i++) {
            JMenuItem topItem = new JMenuItem(menuItems[i]);
            int pos = i;
            topItem.addActionListener(e -> {
                iItemClickListener.click(pos);
            });
            optionsMenu.add(topItem);
        }
        menuBar.add(optionsMenu);
        frame.setJMenuBar(menuBar);

    }

    /**
     * 右键菜单
     */
    public interface IRightMenuJButtonClick {
        /**
         * 双击响应
         */
        default void doubleClick() {

        }

        /**
         *
         * 单机响应
         * @param button button
         */
        void clickBtn(JButton button);

        /**
         * 点击菜单
         *
         * @param pos    pos
         * @param button button
         */
        void clickMenu(int pos, JButton button);
    }

    /**
     * 右键菜单
     */
    public interface IRightMenuJButtonClick2 {
        /**
         * 双击响应
         */
        default void doubleClick() {

        }

        /**单机响应*/
        void clickBtn();
    }

    /**
     * 顶部菜单按钮点击回调
     */
    public interface IItemClickListener {
        /**
         * 点击回调
         * @param pos pos
         */
        void click(int pos);
    }

    /**
     * 添加右键菜单
     * @param list list
     * @param clickListener clickListener
     * @param items items
     */
    public static void addRightClickMenu(JList<?> list, IItemClickListener clickListener, String... items) {
        JPopupMenu popupMenu = new JPopupMenu();

        for (int i = 0; i < items.length; i++) {
            JMenuItem jMenuItem = new JMenuItem(items[i]);

            // 添加菜单项的动作监听器
            int finalI = i;
            jMenuItem.addActionListener(e -> clickListener.click(finalI));
            popupMenu.add(jMenuItem);
        }

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            private void showPopupMenu(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                if (index != -1) {
                    list.setSelectedIndex(index);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

}
