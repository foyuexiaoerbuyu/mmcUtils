package org.mmc.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时器工具类，提供多种定时任务执行方式
 */
public class TimerUtils {

    /**
     * 启动一个单次执行的定时器
     *
     * @param runnable 要执行的任务
     * @param delay    延迟时间(毫秒)
     * @return 创建的Timer对象
     */
    public static Timer startTimer(Runnable runnable, long delay) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, delay);
        return timer;
    }

    /**
     * 启动一个周期性执行的定时器
     *
     * @param runnable 要执行的任务
     * @param delay    首次执行的延迟时间(毫秒)
     * @param period   后续执行的间隔时间(毫秒)
     * @return 创建的Timer对象
     */
    public static Timer startTimer(Runnable runnable, long delay, long period) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, delay, period);
        return timer;
    }

    /**
     * 启动一个有限次数的周期性定时器
     *
     * @param runnable  要执行的任务
     * @param delay     首次执行的延迟时间(毫秒)
     * @param period    执行间隔时间(毫秒)
     * @param cyclesNum 执行次数
     * @return 创建的Timer对象
     */
    public static Timer startTimer(Runnable runnable, long delay, long period, int cyclesNum) {
        final int[] start = {0};  // 使用数组来绕过lambda表达式的final限制
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ++start[0];
                if (start[0] == cyclesNum) {
                    timer.cancel();  // 达到指定次数后取消定时器
                    return;
                }
                runnable.run();
            }
        }, delay, period);
        return timer;
    }

}