package org.mmc.util.swing.weight;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * JTextField 默认文字提示功能，当文本框没有输入内容时，显示提示文字
 * 参考链接：https://blog.csdn.net/yanjingtp/article/details/79282365
 */
public class JTextFieldHint extends JTextField {

    /**
     * 构造函数，初始化提示文字和焦点事件监听器
     *
     * @param hintText 提示文字
     */
    public JTextFieldHint(String hintText) {

        // 添加焦点事件监听器
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // 获取文本框当前的内容
                String temp = getText();
                // 如果当前内容等于提示文字，则清空文本框并设置文字颜色为黑色
                if (temp.equals(hintText)) {
                    setText("");
                    setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // 获取文本框当前的内容
                String temp = getText();
                // 如果当前内容为空，则显示提示文字并设置文字颜色为灰色
                if (temp.equals("")) {
                    setForeground(Color.GRAY);
                    setText(hintText);
                }
            }
        });
        // 默认直接显示提示文字
        setText(hintText);
        // 设置提示文字颜色为灰色
        setForeground(Color.GRAY);
    }
}