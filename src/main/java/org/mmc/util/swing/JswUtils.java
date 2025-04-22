package org.mmc.util.swing;

import org.mmc.util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Swing 工具类
 */
public class JswUtils {

    /**
     * 居中显示 JFrame 窗口
     * @param frame 要居中显示的 JFrame 窗口
     */
    public static void setScreenMiddle(JFrame frame) {
//        frame.setLocationRelativeTo(null);//这个更方便
        // 获得窗口宽度
        int windowWidth = frame.getWidth();
        // 获得窗口高度
        int windowHeight = frame.getHeight();
        // 定义工具包
        Toolkit kit = Toolkit.getDefaultToolkit();
        // 获取屏幕的尺寸
        Dimension screenSize = kit.getScreenSize();
        // 获取屏幕的宽度
        int screenWidth = screenSize.width;
        // 获取屏幕的高度
        int screenHeight = screenSize.height;
        // 设置窗口居中显示
        frame.setLocation((screenWidth - windowWidth) / 2, (screenHeight - windowHeight) / 2);
    }

    /**
     * 获取 JTextField 组件右侧位置的 X 坐标
     * @param textField JTextField 组件
     * @return 右侧位置的 X 坐标
     */
    public static int getPosX(JTextField textField) {
        return textField.getX() + textField.getWidth() + 10;
    }

    /**
     * 获取 JTextField 组件下方位置的 Y 坐标
     * @param textField JTextField 组件
     * @return 下方位置的 Y 坐标
     */
    public static int getPosY(JTextField textField) {
        return textField.getY() + textField.getHeight() + 10;
    }

    /**
     * 获取 JButton 组件右侧位置的 X 坐标
     * @param jButton JButton 组件
     * @return 右侧位置的 X 坐标
     */
    public static int getPosX(JButton jButton) {
        return jButton.getX() + jButton.getWidth() + 10;
    }

    /**
     * 获取 JButton 组件下方位置的 Y 坐标
     * @param jButton JButton 组件
     * @return 下方位置的 Y 坐标
     */
    public static int getPosY(JButton jButton) {
        return jButton.getY() + jButton.getHeight() + 10;
    }

    /**
     * 获取 JScrollPane 组件右侧位置的 X 坐标
     * @param jScrollPane JScrollPane 组件
     * @return 右侧位置的 X 坐标
     */
    public static int getPosX(JScrollPane jScrollPane) {
        return jScrollPane.getX() + jScrollPane.getWidth() + 10;
    }

    /**
     * 获取 JScrollPane 组件下方位置的 Y 坐标
     * @param jScrollPane JScrollPane 组件
     * @return 下方位置的 Y 坐标
     */
    public static int getPosY(JScrollPane jScrollPane) {
        return jScrollPane.getY() + jScrollPane.getHeight() + 10;
    }

    /**
     * 设置 JList 的数据
     * @param jList JList 组件
     * @param datas 要设置的数据列表
     * @param <T> 数据类型
     */
    public static <T> void setJListDatas(JList<T> jList, List<T> datas) {
        // 调用带回调的 setJListDatas 方法，回调设为 null
        setJListDatas(jList, datas, null);
    }

    /**
     * 设置 JList 的数据，并添加点击回调
     * @param jList JList 组件
     * @param datas 要设置的数据列表
     * @param jListClickCallback 点击回调接口
     * @param <T> 数据类型
     */
    public static <T> void setJListDatas(JList<T> jList, List<T> datas, jListClickCallback jListClickCallback) {
        // 移除 JList 中的所有元素
        jList.removeAll();
        // 创建默认列表模型
        DefaultListModel<T> model = new DefaultListModel<>();
        // 将数据添加到列表模型中
        for (T item : datas) {
            model.addElement(item);
        }
        // 设置 JList 的模型
        jList.setModel(model);
        // 如果回调为空，则直接返回
        if (jListClickCallback == null) {
            return;
        }
        // 为 JList 添加鼠标点击监听器
        jList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 调用回调方法
                jListClickCallback.click();
            }
        });
    }

    /**
     * 定义一个接口，用于在 Swing 线程中执行操作
     */
    public interface SwingRun {
        /**
         * 执行操作的方法
         * @param frame JFrame 窗口
         * @param tabPane JTabbedPane 选项卡面板
         */
        void run(JFrame frame, JTabbedPane tabPane);
    }

    /**
     * 在 Swing 线程中运行指定的操作
     * @param swingRun 要执行的操作
     */
    public static void run(SwingRun swingRun) {
        // 在事件调度线程中执行操作
        SwingUtilities.invokeLater(() -> {
            // 创建一个新的 JFrame 窗口
            JFrame frame = new JFrame("JTextArea Example");
            // 设定窗口宽度
            int frameW = 900;
            // 设定窗口高度
            int frameH = 570;
            // 设定窗口大小
            frame.setSize(frameW, frameH);
            // 创建一个新的 JTabbedPane 选项卡面板
            JTabbedPane tabPane = new JTabbedPane();
            // 设置窗口的内容面板为选项卡面板
            frame.setContentPane(tabPane);
            // 将窗口居中显示
            frame.setLocationRelativeTo(null);
            // 设置窗口关闭时的操作
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // 执行传入的操作
            swingRun.run(frame, tabPane);
            // 显示窗口
            frame.setVisible(true);
        });
    }

    /**
     * 定义一个接口，用于处理 JList 的点击事件
     */
    interface jListClickCallback {
        /**
         * 点击事件处理方法
         */
        void click();
    }

    /**
     * 设置 JFrame 窗口始终置顶
     * @param frame 要设置的 JFrame 窗口
     */
    public static void setAlwaysOnTop(JFrame frame) {
        frame.setAlwaysOnTop(true);
    }

    /**
     * 初始化崩溃处理机制
     */
    public static void initCrash() {
        // 1. 设置全局未捕获异常处理器
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());

        // 2. 设置 Swing 事件队列异常处理器
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new SafeEventQueue());
    }

    /**
     * 全局未捕获异常处理器
     */
    private static class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            // 打印全局捕获到的未处理异常信息
            System.err.println("全局捕获到未处理异常 [" + t.getName() + "]: " + e.getMessage());
            // 打印异常堆栈信息
            e.printStackTrace();

            // 在事件调度线程中显示错误对话框
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null,
                            "程序发生错误: " + e.getMessage(),
                            "严重错误",
                            JOptionPane.ERROR_MESSAGE));
        }
    }

    /**
     * 安全的事件队列，用于处理 Swing 事件线程中的异常
     */
    private static class SafeEventQueue extends EventQueue {
        @Override
        protected void dispatchEvent(AWTEvent event) {
            try {
                // 调用父类的事件分发方法
                super.dispatchEvent(event);
            } catch (Throwable t) {
                // 打印 Swing 事件线程捕获到的异常信息
                System.err.println("Swing事件线程捕获到异常: " + t.getMessage());
                // 打印异常堆栈信息
                t.printStackTrace();

                // 在事件调度线程中显示错误对话框
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null,
                                "界面操作错误: " + t.getMessage(),
                                "界面错误",
                                JOptionPane.ERROR_MESSAGE));
            }
        }
    }

    /**
     * 处理应用初始化错误
     * @param t 异常对象
     */
    private static void handleInitializationError(Throwable t) {
        // 打印应用初始化失败的信息
        System.err.println("应用初始化失败: " + t.getMessage());
        // 打印异常堆栈信息
        t.printStackTrace();

        // 显示错误对话框
        JOptionPane.showMessageDialog(null,
                "应用启动失败: " + t.getMessage(),
                "启动错误",
                JOptionPane.ERROR_MESSAGE);
        // 退出应用程序
        System.exit(1);
    }

    /**
     * 复制当前光标所在行，并追加到文本末尾
     * @param textArea JTextArea 组件
     */
    public static void copyLine(JTextArea textArea) {
        // 如果有选中的文本，则将选中的文本插入到选中区域的末尾
        if (StringUtil.isNotEmpty(textArea.getSelectedText())) {
            textArea.insert(textArea.getSelectedText(), textArea.getSelectionEnd());
            return;
        }
        try {
            // 获取光标位置
            int caretPos = textArea.getCaretPosition();
            // 获取行号
            int lineNum = textArea.getLineOfOffset(caretPos);
            // 获取行起始位置
            int lineStart = textArea.getLineStartOffset(lineNum);
            // 获取行结束位置
            int lineEnd = textArea.getLineEndOffset(lineNum);
            // 获取当前行内容
            String lineContent = textArea.getText(lineStart, lineEnd - lineStart).trim();
            // 如果当前行内容为空，则设为空字符串
            if (lineContent == null) {
                lineContent = "";
            } else {
                // 复制当前行内容并添加换行符
                lineContent = lineContent + lineContent + "\n";
            }
            // 将复制的内容插入到当前行的末尾
            textArea.replaceRange("\n" + lineContent, lineStart, lineEnd);
            // 移动光标到末尾（可选）
            textArea.setCaretPosition(caretPos);
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
        }
    }
}