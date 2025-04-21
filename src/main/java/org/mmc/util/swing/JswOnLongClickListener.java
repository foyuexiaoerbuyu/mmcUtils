package org.mmc.util.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 该类用于处理鼠标的长按和单击事件。
 * 下面是MouseEvent类中的一些常用方法：
 * int getX()和int getY()：获取鼠标指针相对于事件源组件的x和y坐标。这些坐标是相对于组件左上角的位置，单位为像素。
 * int getButton()：获取与鼠标事件相关的鼠标按钮。返回一个整数值，代表不同的鼠标按钮。常见的取值包括MouseEvent.BUTTON1（鼠标左键）、MouseEvent.BUTTON2（鼠标中键）、MouseEvent.BUTTON3（鼠标右键）等。
 * boolean isShiftDown()、boolean isControlDown()、boolean isAltDown()等：检查鼠标事件发生时是否按下了Shift、Ctrl、Alt等修饰键。如果按下了相应的修饰键，则返回true；否则返回false。
 * long getWhen()：获取鼠标事件发生的时间戳。返回一个表示事件发生时间的长整型值，以毫秒为单位。
 */
public abstract class JswOnLongClickListener extends MouseAdapter {

    // 记录定时器执行次数
    private int count = 0;
    // 按钮文本
    private String btnText;
    // 定时器，用于处理长按事件
    private Timer timer;
    // 标记是否已经执行了长按事件
    private boolean longClick;

    /**
     * 长按事件处理方法，可根据需要在子类中重写。
     */
    public void onLongClick() {

    }

    /**
     * 单击事件处理方法，需要子类实现。
     */
    public abstract void onClick();

    /**
     * 鼠标按下事件发生时调用的方法。当用户按下鼠标按钮时触发，可以通过MouseEvent对象获取有关鼠标按下的信息。
     *
     * @param e 鼠标事件对象
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            // 鼠标左键按下
            System.out.println("鼠标左键按下");
            longClick = false;
            timer = new Timer(50, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    count++;
                    if (count >= 10) {
                        // 长按事件
                        System.out.println("长按事件触发了");
                        // 调用长按事件处理方法
                        onLongClick();
                        // 停止定时器
                        timer.stop();
                        count = 0;
                        longClick = true;
                    }
                }
            });
            // 启动定时器
            timer.start();
        } else if (SwingUtilities.isRightMouseButton(e)) {
            // 鼠标右键按下
            System.out.println("鼠标右键按下");
        }
    }

    /**
     * 鼠标释放事件发生时调用的方法。当用户释放鼠标按钮时触发，可以通过MouseEvent对象获取有关鼠标释放的信息。
     *
     * @param e 鼠标事件对象
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            // 鼠标左键释放
            System.out.println("鼠标左键释放");
            // 停止定时器
            timer.stop();
            if (!longClick && count < 10) {
                // 点击事件
                System.out.println("单机事件触发了");
                // 调用单击事件处理方法
                onClick();
            }
            count = 0;
        } else if (SwingUtilities.isRightMouseButton(e)) {
            // 鼠标右键释放
            System.out.println("鼠标右键释放");
        }
    }

    /**
     * 设置按钮文本的方法。
     *
     * @param text 按钮文本
     */
    public void setBtnText(String text) {
        btnText = text;
    }

    /**
     * 忽略鼠标拖拽事件。
     *
     * @param e 鼠标事件对象
     */
    @Override
    public void mouseDragged(MouseEvent e) {
    }

    /**
     * 鼠标进入事件发生时调用的方法。当鼠标指针进入组件的可见区域时触发，可以通过MouseEvent对象获取有关鼠标进入的信息。
     *
     * @param e 鼠标事件对象
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * 鼠标点击事件发生时调用的方法。当用户在组件上按下并释放鼠标按钮时触发。可以通过MouseEvent对象获取有关鼠标点击的信息，例如鼠标指针的坐标。
     *
     * @param e 鼠标事件对象
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }
}