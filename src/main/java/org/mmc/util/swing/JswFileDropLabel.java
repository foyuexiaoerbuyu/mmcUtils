package org.mmc.util.swing;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * 自定义的 JLabel 类，支持文件拖拽功能
 */
public class JswFileDropLabel extends JLabel {

    // 文件拖拽回调接口的实现
    private IFileDropLabelCallBack iFileDropLabelCallBack;

    /**
     * 无参构造函数，初始化拖拽目标
     */
    public JswFileDropLabel() {
        // 初始化拖拽目标
        initDropTarget();
    }

    /**
     * 带回调接口的构造函数，初始化拖拽目标并设置回调接口
     * @param iFileDropLabelCallBack 文件拖拽回调接口的实现
     */
    public JswFileDropLabel(IFileDropLabelCallBack iFileDropLabelCallBack) {
        // 设置回调接口
        this.iFileDropLabelCallBack = iFileDropLabelCallBack;
        // 初始化拖拽目标
        initDropTarget();
    }

    /**
     * 主方法，用于测试文件拖拽标签
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 在事件调度线程中创建并显示 GUI
        SwingUtilities.invokeLater(() -> {
            // 创建一个 JFrame 窗口
            JFrame frame = new JFrame("File Drop Label");
            // 设置窗口关闭操作
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // 设置窗口大小
            frame.setSize(300, 200);
            // 将窗口居中显示
            frame.setLocationRelativeTo(null);

            // 创建一个文件拖拽标签
            JswFileDropLabel label = new JswFileDropLabel();
            // 设置标签文本
            label.setText("Drop files here");
            // 设置标签文本水平居中
            label.setHorizontalAlignment(SwingConstants.CENTER);
            // 将标签添加到窗口中
            frame.add(label);

            // 显示窗口
            frame.setVisible(true);
        });
    }

    /**
     * 初始化拖拽目标，添加拖拽事件监听器
     */
    private void initDropTarget() {
        // 创建一个 DropTarget 对象，支持复制或移动操作
        DropTarget dt = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, null);
        // 激活 DropTarget
        dt.setActive(true);
        try {
            // 为 DropTarget 添加拖拽事件监听器
            dt.addDropTargetListener(new DropTargetAdapter() {
                @Override
                public void drop(DropTargetDropEvent dtde) {
                    try {
                        // 接收拖拽过来的数据
                        Transferable tr = dtde.getTransferable();
                        // 获取数据的所有数据格式
                        DataFlavor[] flavors = tr.getTransferDataFlavors();
                        // 遍历所有数据格式
                        for (DataFlavor flavor : flavors) {
                            // 如果数据格式是 Java 文件列表类型
                            if (flavor.isFlavorJavaFileListType()) {
                                // 接受拖拽操作
                                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                                // 获取拖拽过来的文件列表
                                List<File> files = (List<File>) tr.getTransferData(flavor);
                                // 处理拖拽过来的文件
                                handleFiles(files);
                                // 标记拖拽操作完成
                                dtde.dropComplete(true);
                                // 如果有回调接口，调用回调方法
                                if (iFileDropLabelCallBack != null) {
                                    iFileDropLabelCallBack.dropLabelCallBack(files);
                                }
                                return;
                            }
                        }
                    } catch (Exception e) {
                        // 打印异常信息
                        e.printStackTrace();
                    }
                    // 拒绝拖拽操作
                    dtde.rejectDrop();
                }
            });
        } catch (TooManyListenersException e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }

    /**
     * 处理拖拽过来的文件列表
     * @param files 拖拽过来的文件列表
     */
    private void handleFiles(List<File> files) {
        // 如果文件列表为空，直接返回
        if (files == null || files.isEmpty()) {
            return;
        }
        // 遍历文件列表
        for (File file : files) {
            // 处理拖拽过来的文件，这里只是简单打印文件的绝对路径
            System.out.println("File: " + file.getAbsolutePath());
        }
    }

    /**
     * 文件拖拽回调接口，用于处理文件拖拽完成后的操作
     */
    public interface IFileDropLabelCallBack {
        /**
         * 文件拖拽完成后的回调方法
         * @param files 拖拽过来的文件列表
         */
        void dropLabelCallBack(List<File> files);
    }
}