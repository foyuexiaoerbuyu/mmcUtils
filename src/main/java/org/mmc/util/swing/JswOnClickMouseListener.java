package org.mmc.util.swing;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 单机 双击 长按
 * 该类用于处理鼠标的单击、双击和长按事件。
 */
public abstract class JswOnClickMouseListener extends MouseAdapter {
    // 用来判断是否已经执行双击事件
    private static boolean flag = false;
    // 用来判断是否该执行双击事件
    private static int clickNum = 0;
    // 定时器，用于处理长按事件
    private Timer newTimer;
    // 标记是否已经执行了长按事件
    private boolean isExeLong;

    /**
     * 鼠标按下事件发生时调用的方法。当用户按下鼠标按钮时触发，可以通过MouseEvent对象获取有关鼠标按下的信息。
     *
     * @param e 鼠标事件对象
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            // 鼠标左键按下
            if (newTimer != null) {
                return;
            }
            isExeLong = false;
            newTimer = new Timer();
            // 启动定时器，延迟1000毫秒后执行长按事件
            newTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("long");
                    // 停止定时器
                    stopTimer();
                    // 调用长按事件处理方法
                    onLongClick(e);
                    isExeLong = true;
                    stopTimer();
//                    isExeLong = false;
                }
            }, 1000);
        }
    }

    /**
     * 鼠标释放事件发生时调用的方法。当用户释放鼠标按钮时触发，可以通过MouseEvent对象获取有关鼠标释放的信息。
     *
     * @param e 鼠标事件对象
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        // 停止定时器
        stopTimer();
    }

    /**
     * 鼠标点击事件发生时调用的方法。当用户在组件上按下并释放鼠标按钮时触发。可以通过MouseEvent对象获取有关鼠标点击的信息，例如鼠标指针的坐标。
     *
     * @param e 鼠标事件对象
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        final MouseEvent me = e; // 事件源
        this.flag = false; // 每次点击鼠标初始化双击事件执行标志为false

        if (this.clickNum == 1) { // 当clickNum==1时执行双击事件
            if (isExeLong) return;
            // 停止定时器
            stopTimer();
            // 执行双击事件处理方法
            this.mouseDoubleClicked(me);
            this.clickNum = 0; // 初始化双击事件执行标志为0
            this.flag = true; // 双击事件已执行,事件标志为true
            return;
        }

        // 定义定时器
        Timer timer = new Timer();

        // 定时器开始执行,延时0.2秒后确定是否执行单击事件
        timer.schedule(new TimerTask() {
            private int n = 0; // 记录定时器执行次数

            public void run() {
                if (JswOnClickMouseListener.flag) { // 如果双击事件已经执行,那么直接取消单击执行
                    n = 0;
                    JswOnClickMouseListener.clickNum = 0;
                    this.cancel();
                    return;
                }
                if (n == 1) { // 定时器等待0.2秒后,双击事件仍未发生,执行单击事件
                    if (!isExeLong) {
                        // 停止定时器
                        stopTimer();
                        // 执行单击事件处理方法
                        mouseSingleClicked(me);
                    }
                    JswOnClickMouseListener.flag = true;
                    JswOnClickMouseListener.clickNum = 0;
                    n = 0;
                    this.cancel();
                    return;
                }
                System.out.println("n = " + clickNum);
                clickNum++;
                n++;
            }
        }, new Date(), 200);
    }

    /**
     * 鼠标单击事件处理方法，需要子类实现。
     *
     * @param e 事件源参数
     */
    public abstract void mouseSingleClicked(MouseEvent e);

    /**
     * 鼠标双击事件处理方法，可根据需要在子类中重写。
     *
     * @param e 事件源参数
     */
    public void mouseDoubleClicked(MouseEvent e) {
//        System.out.println("Doublc Clicked!");
    }

    /**
     * 鼠标长按事件处理方法，可根据需要在子类中重写。
     *
     * @param e 事件源参数
     */
    public void onLongClick(MouseEvent e) {
//        System.out.println("onLongClick Clicked!");
    }

    /**
     * 停止定时器的方法。
     */
    private void stopTimer() {
        if (newTimer == null) {
            return;
        }
        // 取消定时器任务
        newTimer.cancel();
        newTimer = null;
    }
}