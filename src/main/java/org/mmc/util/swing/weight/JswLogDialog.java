package org.mmc.util.swing.weight;

import org.mmc.util.DateUtil;
import org.mmc.util.WinUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * 日志弹框，用于显示日志信息，并提供刷新、测试写入等操作按钮
 */
public class JswLogDialog {

    // 单例模式，用于保证只有一个 JswLogDialog 实例
    private static JswLogDialog jswLogDialog;
    // 日志文件的路径
    private final String filePath;
    // 用户的主目录
    private final String userHomeDir;

    /**
     * 私有构造函数，用于初始化日志文件路径和创建日志文件
     */
    private JswLogDialog() {
        // 获取用户的主目录
        userHomeDir = WinUtils.getUserHomeDir();
        // 拼接日志文件的完整路径
        filePath = userHomeDir + "/tmp/250319_082543_log.txt";
        // 创建日志文件
        creatLogFile();
    }

    /**
     * 获取 JswLogDialog 的单例实例
     *
     * @return JswLogDialog 实例
     */
    public static JswLogDialog getInstance() {
        if (jswLogDialog == null) {
            jswLogDialog = new JswLogDialog();
        }
        return jswLogDialog;
    }

    /**
     * 显示自定义对话框，包含日志显示区域和操作按钮
     *
     * @param frame 父窗口
     */
    public void showCustomDialog(Frame frame) {
        // 创建一个模态对话框
        JDialog dialog = new JDialog(frame, "自定义对话框", true);
        // 设置对话框的大小
        dialog.setSize(400, 500);
        // 将对话框居中显示在父窗口上
        dialog.setLocationRelativeTo(frame);
        // 设置对话框的布局为 BorderLayout
        dialog.setLayout(new BorderLayout());

        // 创建可上下滑动自动换行的 JTextArea，用于显示日志内容
        JTextArea textArea = new JTextArea();
        // 从日志文件中读取内容并显示在文本区域
        textArea.setText(readLog(filePath));
        // 设置文本区域不可编辑
        textArea.setEditable(false);
        // 设置文本区域自动换行
        textArea.setLineWrap(true);
        // 设置按单词换行
        textArea.setWrapStyleWord(true);
        // 创建滚动面板，将文本区域添加到滚动面板中
        JScrollPane scrollPane = new JScrollPane(textArea);
        // 将滚动面板添加到对话框的中间位置
        dialog.add(scrollPane, BorderLayout.CENTER);

        // 底部三个按钮横向靠左排列的面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // 创建刷新按钮
        JButton printButton1 = new JButton("刷新");
        // 创建测试写入按钮
        JButton printButton2 = new JButton("测试写入");
        // 创建打印3按钮
        JButton printButton3 = new JButton("打印3");

        // 为刷新按钮添加点击事件监听器
        printButton1.addActionListener(e -> {
            System.out.println("点击了打印1按钮");
            // 刷新文本区域的内容
            textArea.setText(readLog(filePath));
        });
        // 为测试写入按钮添加点击事件监听器
        printButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("点击了打印2按钮");
                // 向日志文件中写入测试数据
                log("测试写入数据");
                // 刷新文本区域的内容
                textArea.setText(readLog(filePath));
            }
        });
        // 为打印3按钮添加点击事件监听器
        printButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("点击了打印3按钮");
            }
        });

        // 将三个按钮添加到按钮面板中
        buttonPanel.add(printButton1);
        buttonPanel.add(printButton2);
        buttonPanel.add(printButton3);
        // 将按钮面板添加到对话框的底部位置
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // 显示对话框
        dialog.setVisible(true);
    }

    /**
     * 创建日志文件，如果目录不存在则创建目录
     */
    private void creatLogFile() {
        // 创建临时目录
        new File(userHomeDir + "/tmp/").mkdir();
        // 创建日志文件对象
        File file = new File(filePath);
        // 如果文件不存在，则创建文件
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 向日志文件中写入日志信息
     *
     * @param logMsg 日志信息
     */
    public void log(String logMsg) {
        // 写入日志信息到文件，包含当前时间
        writeFile(filePath, DateUtil.formatDate("yyyy-MM-dd HH:mm:ss") + "  " + logMsg);
    }

    /**
     * 读取文件内容
     *
     * @param filePath 文件路径
     * @return 文件内容，如果读取失败返回 null
     */
    public String readLog(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // 逐行读取文件内容
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return content.toString();
    }

    /**
     * 写文件，将新内容换行写到文件开头
     *
     * @param filePath   文件路径
     * @param newContent 要写入的新内容
     * @return 写入是否成功
     */
    public boolean writeFile(String filePath, String newContent) {
        File file = new File(filePath);
        StringBuilder existingContent = new StringBuilder();
        // 先读取现有文件内容
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                existingContent.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // 写入新内容到文件开头
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(newContent + "\n");
            writer.write(existingContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}