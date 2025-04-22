package org.mmc.util.swing;

import org.mmc.util.StringUtil;
import org.mmc.util.WinUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * 该类提供了一系列用于创建和显示各种对话框的工具方法，
 * 包括简单输入对话框、带标题和内容的编辑对话框、提示对话框、自动关闭对话框以及图片对话框等。
 */
public class JswDialogUtils {

    // 存储主窗口框架，用于定位对话框
    private static JFrame frame;

    /**
     * 初始化主窗口框架，用于后续对话框的定位。
     *
     * @param frame 主窗口框架
     */
    public static void init(JFrame frame) {
        JswDialogUtils.frame = frame;
    }

    /**
     * 定义对话框点击事件的接口，包含确定按钮点击、取消按钮点击和取消监听方法。
     */
    public interface IDialogClick {
        /**
         * 确定按钮点击事件处理方法。
         */
        void okBtnClick();

        /**
         * 取消按钮点击事件处理的默认方法。
         */
        default void cancelBtnClick() {
        }

        /**
         * 取消监听事件处理的默认方法。
         */
        default void canceListen() {
        }
    }

    /**
     * 定义图片对话框的接口，包含打印、关闭和点击图片的方法。
     */
    public interface IImageDialog {
        /**
         * 打印图片的默认方法。
         */
        default void print() {
        }

        /**
         * 关闭图片对话框的默认方法。
         */
        default void close() {
        }

        /**
         * 点击图片事件处理方法。
         */
        void clickImg();
    }

    /**
     * 定义确认对话框回调接口，包含回调方法，根据用户选择返回布尔值。
     */
    public interface IConfirmDialogCallBack {
        /**
         * 回调方法，根据用户选择返回布尔值。
         *
         * @param isOk 用户是否点击了确定按钮
         */
        void callBack(boolean isOk);
    }

    /**
     * 定义点击事件的接口，包含回调方法，返回点击索引和按钮名称。
     */
    public interface IClick {
        /**
         * 点击事件回调方法，返回点击索引和按钮名称。
         *
         * @param index   点击的按钮索引
         * @param btnName 按钮名称
         */
        void callBack(int index, String btnName);
    }

    /**
     * 定义带标题和内容的回调接口，包含回调方法，返回标题和内容。
     */
    public interface ICallBack {
        /**
         * 回调方法，返回标题和内容。
         *
         * @param title   标题
         * @param content 内容
         */
        void str(String title, String content);
    }

    /**
     * 定义简单回调接口，包含回调方法，返回内容。
     */
    public interface ISimpleCallBack {
        /**
         * 回调方法，返回内容。
         *
         * @param content 内容
         */
        void str(String content);
    }

    /**
     * 定义简单输入回调接口，包含回调方法，返回文本框和内容。
     */
    public interface ISimpleInputCallBack {
        /**
         * 回调方法，返回文本框和内容。
         *
         * @param jTextField 文本框
         * @param content    内容
         */
        void str(JTextField jTextField, String content);
    }

    /**
     * 显示简单的编辑对话框，用户输入值后，通过回调函数处理输入结果。
     *
     * @param title        对话框标题
     * @param message      提示信息
     * @param defaultValue 默认值
     * @param iCallBack    回调函数，处理用户输入结果
     */
    public static void showEditDialogSimple(String title, String message, String defaultValue, ISimpleCallBack iCallBack) {
        // 显示输入对话框，获取用户输入的值
        String inputValue = JOptionPane.showInputDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE, null, null, defaultValue).toString();
        if (inputValue != null) {
            // 用户点击了确定按钮，调用回调函数处理输入值
            iCallBack.str(inputValue);
            // 打印用户输入的值
            System.out.println("用户输入的值是：" + inputValue);
        } else {
            // 用户点击了取消按钮，打印提示信息
            System.out.println("用户取消了输入");
        }
    }

    /**
     * 显示简单的编辑对话框，使用默认标题和提示信息，用户输入值后，通过回调函数处理输入结果。
     *
     * @param iCallBack 回调函数，处理用户输入结果
     */
    public static void showEditDialogSimple(ISimpleCallBack iCallBack) {
        showEditDialogSimple("", "请输入一个值", iCallBack);
    }

    /**
     * 显示简单的编辑对话框，使用默认提示信息，用户输入值后，通过回调函数处理输入结果。
     *
     * @param defaultValue 默认值
     * @param iCallBack    回调函数，处理用户输入结果
     */
    public static void showEditDialogSimple(String defaultValue, ISimpleCallBack iCallBack) {
        showEditDialogSimple(defaultValue, "请输入一个值", iCallBack);
    }

    /**
     * 显示简单的编辑对话框，用户输入值后，通过回调函数处理输入结果。
     *
     * @param defaultValue 默认值
     * @param msg          提示信息
     * @param iCallBack    回调函数，处理用户输入结果
     */
    public static void showEditDialogSimple(String defaultValue, String msg, ISimpleCallBack iCallBack) {
        // 显示输入对话框，获取用户输入的值
        String inputValue = JOptionPane.showInputDialog(frame, msg, defaultValue);
        if (inputValue != null) {
            // 用户点击了确定按钮，调用回调函数处理输入值
            iCallBack.str(inputValue);
            // 打印用户输入的值
            System.out.println("用户输入的值是：" + inputValue);
        } else {
            // 用户点击了取消按钮，打印提示信息
            System.out.println("用户取消了输入");
        }
    }

    /**
     * 显示带标题和内容的编辑弹框，用户可以编辑标题和内容，点击保存按钮后，通过回调函数处理结果。
     *
     * @param title     初始标题
     * @param content   初始内容
     * @param iCallBack 回调函数，处理用户编辑后的标题和内容
     */
    public static void showEditTitleContentDialog(String title, String content, ICallBack iCallBack) {
        // 创建一个新的对话框
        JDialog jDialog = new JDialog();
        // 设置对话框标题
        jDialog.setTitle("编辑对话框");
        // 设置对话框大小
        jDialog.setSize(350, 300);

        // 获取屏幕尺寸
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // 计算对话框居中显示的位置
        jDialog.setLocation(new Point((int) ((screenSize.getWidth() - jDialog.getWidth()) / 2),
                (int) ((screenSize.getHeight() - jDialog.getHeight()) / 2)));

        // 创建一个文本编辑框用于输入标题
        JTextArea textAreaTitle = new JTextArea();
        // 设置标题文本
        textAreaTitle.setText(title);
        // 创建滚动面板，将标题文本框添加到滚动面板中
        JScrollPane scrollPane1 = new JScrollPane(textAreaTitle);
        if (title != null) {
            // 将滚动面板添加到对话框的北部
            jDialog.add(scrollPane1, BorderLayout.NORTH);
        }

        // 创建一个文本编辑框用于输入内容
        JTextArea textAreaContent = new JTextArea();
        // 设置内容文本
        textAreaContent.setText(content);
        // 创建滚动面板，将内容文本框添加到滚动面板中
        JScrollPane scrollPane = new JScrollPane(textAreaContent);
        // 将滚动面板添加到对话框的中部
        jDialog.add(scrollPane, BorderLayout.CENTER);

        // 创建底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // 添加保存按钮，点击保存按钮时，调用回调函数处理用户输入的标题和内容，并关闭对话框
        buttonPanel.add(JswCustomWight.getJButtonMargin("保存", e -> {
            if (iCallBack != null) iCallBack.str(textAreaTitle.getText(), textAreaContent.getText());
            jDialog.dispose();
        }));

        // 添加关闭按钮，点击关闭按钮时，关闭对话框
        buttonPanel.add(JswCustomWight.getJButtonMargin("关闭", e -> jDialog.dispose()));

        // 将按钮面板添加到对话框的南部
        jDialog.add(buttonPanel, BorderLayout.SOUTH);

        // 设置对话框可见
        jDialog.setVisible(true);
    }

    /**
     * 显示带标题和内容的编辑弹框，支持设置标题和内容的提示信息，用户可以编辑标题和内容，
     * 点击保存按钮后，通过回调函数处理结果。
     *
     * @param title       初始标题
     * @param titleHint   标题提示信息
     * @param content     初始内容
     * @param contentHint 内容提示信息
     * @param iCallBack   回调函数，处理用户编辑后的标题和内容
     */
    public static void showEditTitleContentDialog(String title, String titleHint, String content, String contentHint, ICallBack iCallBack) {
        // 创建一个新的对话框
        JDialog jDialog = new JDialog();
        // 设置对话框标题
        jDialog.setTitle("编辑对话框");
        // 设置对话框大小
        jDialog.setSize(350, 300);

        // 获取屏幕尺寸
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // 计算对话框居中显示的位置
        jDialog.setLocation(new Point((int) ((screenSize.getWidth() - jDialog.getWidth()) / 2),
                (int) ((screenSize.getHeight() - jDialog.getHeight()) / 2)));

        // 创建一个文本编辑框用于输入标题
        JTextArea textAreaTitle = new JTextArea();
        // 设置标题文本
        textAreaTitle.setText(title);
        // 创建滚动面板，将标题文本框添加到滚动面板中
        JScrollPane scrollPane1 = new JScrollPane(textAreaTitle);
        // 将滚动面板添加到对话框的北部
        jDialog.add(scrollPane1, BorderLayout.NORTH);

        // 创建一个文本编辑框用于输入内容
        JTextArea textAreaContent = new JTextArea();
        // 设置内容文本
        textAreaContent.setText(content);
        // 创建滚动面板，将内容文本框添加到滚动面板中
        JScrollPane scrollPane = new JScrollPane(textAreaContent);
        // 将滚动面板添加到对话框的中部
        jDialog.add(scrollPane, BorderLayout.CENTER);

        // 创建底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // 添加关闭按钮，点击关闭按钮时，关闭对话框
        JButton button1 = new JButton("关闭");
        button1.addActionListener(e -> jDialog.dispose());
        buttonPanel.add(button1);

        // 添加保存按钮，点击保存按钮时，调用回调函数处理用户输入的标题和内容，并关闭对话框
        JButton button2 = new JButton("保存");
        button2.addActionListener(e -> {
            if (iCallBack != null) iCallBack.str(textAreaTitle.getText(), textAreaContent.getText());
            jDialog.dispose();
        });
        buttonPanel.add(button2);

        // 将按钮面板添加到对话框的南部
        jDialog.add(buttonPanel, BorderLayout.SOUTH);

        // 添加关闭对话框监听器，对话框关闭时打印提示信息并关闭对话框
        jDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("对话框关闭");
                jDialog.dispose();
            }
        });

        // 设置对话框可见
        jDialog.setVisible(true);
    }

    /**
     * 为对话框的消息添加复制功能，右键点击消息时可以复制。
     *
     * @param msg 要复制的消息
     * @param op  对话框选项面板
     */
    private static void copy(String msg, JOptionPane op) {
        // 创建一个弹出菜单
        JPopupMenu popupMenu = new JPopupMenu();
        // 创建一个复制菜单项
        JMenuItem copyItem = new JMenuItem("复制");
        // 为复制菜单项添加点击事件监听器，点击时将消息复制到剪贴板
        copyItem.addActionListener(e -> {
            StringSelection selection = new StringSelection(msg);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        });
        // 将复制菜单项添加到弹出菜单中
        popupMenu.add(copyItem);

        // 将弹出菜单关联到对话框选项面板
        op.setComponentPopupMenu(popupMenu);
    }

    /**
     * 显示一个简单的提示对话框。
     *
     * @param title 对话框标题
     * @param msg   提示消息
     */
    public static void show(String title, String msg) {
        // 显示信息对话框
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示一个简单的提示对话框，使用默认标题“提示”。
     *
     * @param msg 提示消息
     */
    public static void show(String msg) {
        show("提示", msg);
    }

    /**
     * 显示一个自动关闭的短时间提示对话框。
     *
     * @param title 对话框标题
     * @param msg   提示消息
     * @param delay 延迟关闭时间（毫秒）
     */
    public static void showAutoCloseDialogShort(String title, String msg, int delay) {
        // 创建一个信息类型的对话框选项面板
        JOptionPane op = new JOptionPane(msg, JOptionPane.INFORMATION_MESSAGE);
        // 创建一个对话框
        final JDialog dialog = op.createDialog(title);
        // 为对话框的消息添加复制功能
        copy(msg, op);

        // 创建一个新计时器
        Timer timer = new Timer();
        // 创建一个定时任务，在指定延迟时间后关闭对话框
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                dialog.setVisible(false);
                dialog.dispose();
            }
        };
        // 安排定时任务在指定延迟时间后执行
        timer.schedule(task, delay);

        // 设置对话框关闭时的操作
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // 设置对话框始终置顶
        dialog.setAlwaysOnTop(true);
        // 设置对话框非模态
        dialog.setModal(false);
        // 设置对话框可见
        dialog.setVisible(true);

        // 添加窗口关闭监听器，对话框关闭时取消计时器任务
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                // 在对话框关闭时取消计时器任务
                task.cancel();
                timer.cancel();
            }
        });
    }

    /**
     * 显示一个自动关闭的短时间提示对话框，使用默认标题“提示”。
     *
     * @param msg   提示消息
     * @param delay 延迟关闭时间（毫秒）
     */
    public static void showAutoCloseDialogShort(String msg, int delay) {
        showAutoCloseDialogShort("提示", msg, delay);
    }

    /**
     * 显示一个自动关闭的短时间提示对话框，使用默认标题“提示”和延迟时间 500 毫秒。
     *
     * @param msg 提示消息
     */
    public static void showAutoCloseDialogShort(String msg) {
        showAutoCloseDialogShort("提示", msg, 500);
    }

    /**
     * 显示一个自动关闭的长时间提示对话框，使用默认标题“提示”和延迟时间 1000 毫秒。
     *
     * @param msg 提示消息
     */
    public static void showAutoCloseDialogLong(String msg) {
        showAutoCloseDialogShort("提示", msg, 1000);
    }

    /**
     * 显示一个长时间的提示对话框，使用默认标题“提示”和延迟时间 333000 毫秒。
     *
     * @param msg 提示消息
     */
    public static void showLong(String msg) {
        showAutoCloseDialogShort("提示", msg, 333000);
    }

    /**
     * 显示一个自动关闭的短时间提示对话框，指定父窗口。
     *
     * @param parent 父窗口
     * @param msg    提示消息
     */
    public static void showAutoCloseDialogShort(JFrame parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "提示", JOptionPane.WARNING_MESSAGE);
    }
    // 显示图片弹框

    /**
     * 显示一个包含图片的对话框，支持打印、关闭图片以及点击图片的操作。
     *
     * @param frame        父窗口框架
     * @param path         图片的文件路径
     * @param iImageDialog 处理图片相关操作的接口实例
     */
    public static void showImageDialog(JFrame frame, String path, IImageDialog iImageDialog) {
        // 创建一个面板用于承载图片和按钮
        JPanel panel = new JPanel(new BorderLayout());

        // 创建JLabel并设置图片
        JLabel imageLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon(path); // 替换为你的图片路径
        imageLabel.setIcon(imageIcon);
        // 创建滚动窗格，并将图片添加到其中
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建两个按钮
        JButton printButton = new JButton("打印");
        JButton closeButton = new JButton("关闭");

        // 将按钮添加到面板的第二行
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 创建自定义对话框
        JDialog dialog = new JDialog(frame, "图片弹框", true);
        dialog.setContentPane(panel);
        int iconHeight = imageIcon.getIconHeight();
        // 获取默认工具包
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        // 获取屏幕尺寸
        Dimension screenSize = toolkit.getScreenSize();
        // 获取屏幕高度
        int screenHeight = screenSize.height;
        if (iconHeight > screenHeight) {
            iconHeight = screenHeight - 150;
        }
        dialog.setSize(imageIcon.getIconWidth(), iconHeight); // 设置对话框的大小
        dialog.setLocationRelativeTo(frame); // 居中显示对话框

        // 为按钮添加点击事件监听器
        printButton.addActionListener(e -> {
            System.out.println("点击了打印按钮");
            // 在这里添加你想要执行的打印逻辑
            dialog.dispose();
            if (iImageDialog != null) {
                iImageDialog.print();
            }
        });

        closeButton.addActionListener(e -> {
            System.out.println("点击了关闭按钮");
            dialog.dispose();
            // 调用关闭图片对话框的方法
            // closeImageDialog(frame);
            if (iImageDialog != null) {
                iImageDialog.close();
            }
        });

        // 添加鼠标点击事件监听器
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("点击了图片");
                dialog.dispose();
                // 在这里添加你想要执行的点击事件逻辑
                // closeImageDialog(frame);
                if (iImageDialog != null) {
                    iImageDialog.clickImg();
                }
            }
        });
        // 显示对话框
        dialog.setVisible(true);
    }

    // 关闭图片弹框

    /**
     * 关闭与指定框架相关的图片对话框。
     *
     * @param frame 父窗口框架
     */
    private static void closeImageDialog(JFrame frame) {
        Container parent = frame.getParent();
        while (parent != null) {
            if (parent instanceof JDialog) {
                ((JDialog) parent).dispose();
                break;
            }
            parent = parent.getParent();
        }
    }

    /**
     * 显示一个确认对话框，包含确定和取消按钮。
     *
     * @param title         对话框的标题
     * @param message       对话框显示的消息
     * @param confirmAction 处理确定和取消操作的接口实例
     */
    public static void showConfirmDialog(String title, String message, IDialogClick confirmAction) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setModalityType(Dialog.ModalityType.MODELESS);
        dialog.setLayout(new BorderLayout());

        JLabel label = new JLabel(message);
        dialog.add(label, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("确定");
        confirmButton.addActionListener(e -> {
            confirmAction.okBtnClick();
            dialog.dispose();
        });
        buttonPanel.add(confirmButton);

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> {
            confirmAction.cancelBtnClick();
            dialog.dispose();
        });
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * 显示一个包含复选框的对话框，用户可以选择多个选项。
     *
     * @param parent         父组件
     * @param title          对话框的标题
     * @param message        对话框显示的消息
     * @param checkboxLabels 复选框的标签数组
     */
    public static void showCheckboxDialog(Component parent, String title, String message, String[] checkboxLabels) {
        JCheckBox[] checkboxes = new JCheckBox[checkboxLabels.length];

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(checkboxLabels.length, 1));

        for (int i = 0; i < checkboxLabels.length; i++) {
            checkboxes[i] = new JCheckBox(checkboxLabels[i]);
            panel.add(checkboxes[i]);
        }

        int result = JOptionPane.showConfirmDialog(parent, panel, title, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            StringBuilder selectedItems = new StringBuilder();
            for (JCheckBox checkbox : checkboxes) {
                if (checkbox.isSelected()) {
                    selectedItems.append(checkbox.getText()).append(", ");
                }
            }
            if (selectedItems.length() > 0) {
                selectedItems.delete(selectedItems.length() - 2, selectedItems.length());
            }
            JOptionPane.showMessageDialog(parent, "Selected items: " + selectedItems.toString());
        }
    }

    /**
     * 显示一个包含单选按钮的对话框，用户只能选择一个选项。
     *
     * @param parent            父组件
     * @param title             对话框的标题
     * @param message           对话框显示的消息
     * @param radioButtonLabels 单选按钮的标签数组
     */
    public static void showRadioButtonDialog(Component parent, String title, String message, String[] radioButtonLabels) {
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton[] radioButtons = new JRadioButton[radioButtonLabels.length];
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(radioButtonLabels.length, 1));

        for (int i = 0; i < radioButtonLabels.length; i++) {
            radioButtons[i] = new JRadioButton(radioButtonLabels[i]);
            panel.add(radioButtons[i]);
            buttonGroup.add(radioButtons[i]);
        }

        int result = JOptionPane.showConfirmDialog(parent, panel, title, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            for (JRadioButton radioButton : radioButtons) {
                if (radioButton.isSelected()) {
                    JOptionPane.showMessageDialog(parent, "Selected item: " + radioButton.getText());
                    break;
                }
            }
        }
    }

    /**
     * 显示一个包含多个按钮的对话框，点击按钮会触发相应的回调。
     * <p>
     * 使用示例：
     * JswDialogUtils.showBtnsDialog(new JswDialogUtils.IClick() {
     *
     * @param iClick            处理按钮点击事件的接口实例
     * @param radioButtonLabels 按钮的标签数组
     */
    public static void showBtnsDialog(IClick iClick, String... radioButtonLabels) {
        JButton[] jButtons = new JButton[radioButtonLabels.length];
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(radioButtonLabels.length, 1));
        JDialog dialog = new JDialog();
        dialog.setTitle("操作");

        for (int i = 0; i < radioButtonLabels.length; i++) {
            int index = i;
            String btnName = radioButtonLabels[index];
            jButtons[index] = new JButton(btnName);
            jButtons[index].addActionListener(e -> {
                iClick.callBack(index, btnName);
                dialog.dispose();
            });
            panel.add(jButtons[i]);
        }

        dialog.setModalityType(Dialog.ModalityType.MODELESS);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.SOUTH);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * 显示一个确认对话框，询问用户是否执行操作。
     *
     * @param message                对话框显示的消息
     * @param iConfirmDialogCallBack 处理用户选择结果的接口实例
     */
    public static void showConfirmDialog(String message, IConfirmDialogCallBack iConfirmDialogCallBack) {
        if (message == null) {
            message = "确定要执行操作吗？";
        }
        int choice = JOptionPane.showOptionDialog(null, message, "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (choice == JOptionPane.YES_OPTION) {
            // 用户点击了确定按钮
            System.out.println("用户选择了确定");
            // 执行相关操作
        } else if (choice == JOptionPane.NO_OPTION) {
            // 用户点击了取消按钮
            System.out.println("用户选择了取消");
            // 执行其他操作或关闭应用程序
        }
        iConfirmDialogCallBack.callBack(choice == JOptionPane.YES_OPTION);
    }

    /**
     * 显示一个自定义的编辑对话框，包含输入框和确定、取消按钮。
     *
     * @param parent          父窗口框架
     * @param title           对话框的标题
     * @param message         输入框的提示消息
     * @param defText         输入框的默认文本
     * @param iSimpleCallBack 处理用户输入结果的接口实例
     */
    public static void showCustomEditDialog(JFrame parent, String title, String message, String defText, ISimpleInputCallBack iSimpleCallBack) {
        if (parent == null) parent = frame;
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setResizable(false);

        // 创建输入框和文本标签
        JLabel label = new JLabel(message);
        // 设置外边距
        JTextField textField = new JTextField(defText, 20);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // 创建面板
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel inputPane = new JPanel(new GridLayout(0, 1));
        inputPane.add(label);
        inputPane.add(textField);
        contentPane.add(inputPane, BorderLayout.CENTER);
        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // 创建底部的按钮
        JButton okButton = new JButton("确定");
        okButton.addActionListener(e -> {
            dialog.dispose();
            iSimpleCallBack.str(textField, textField.getText());
        });
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> {
            dialog.dispose();
        });
        buttonPane.add(okButton);
        buttonPane.add(cancelButton);
        contentPane.add(buttonPane, BorderLayout.SOUTH);

        dialog.setContentPane(contentPane);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * 显示一个确认对话框，根据用户的选择调用回调方法。
     *
     * @param msg        对话框显示的消息
     * @param okCallback 处理用户点击确定按钮的接口实例
     */
    public static void show(String msg, IOkClick okCallback) {
        int result = JOptionPane.showConfirmDialog(null, msg, "提示", JOptionPane.DEFAULT_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            // 用户点击了确定按钮
            okCallback.ok();
            System.out.println("用户点击了确定按钮");
        }
    }

    /**
     * 定义一个接口，用于处理用户点击确定按钮的操作。
     */
    public interface IOkClick {
        /**
         * 点击确定按钮后执行的抽象方法
         */
        public abstract void ok();
    }

    /**
     * 显示一个包含左右两个JTextArea面板的对话框，提供合并、生成序号和清空的操作按钮。
     *
     * @param frame 父窗口框架
     */
    public static void showLeft2RightJTextAreaDialog(JFrame frame) {
        // 创建对话框
        JDialog dialog = new JDialog(frame, "Custom Dialog", true);
        dialog.setSize(500, 500);

        // 设置对话框居中显示
        dialog.setLocationRelativeTo(null);

        // 使用BorderLayout布局管理器
        dialog.setLayout(new BorderLayout());

        // 创建一个面板用于放置两个JTextArea，使用GridLayout布局
        JPanel textAreaPanel = new JPanel(new GridLayout(1, 2));

        // 创建两个JTextArea
        JTextArea leftTextArea = new JTextArea();
        JTextArea rightTextArea = new JTextArea();

        // 将两个JTextArea添加到面板中
        textAreaPanel.add(new JScrollPane(leftTextArea));
        textAreaPanel.add(new JScrollPane(rightTextArea));

        // 将包含JTextArea的面板添加到dialog的中间位置
        dialog.add(textAreaPanel, BorderLayout.CENTER);

        // 创建流布局面板并添加三个按钮
        JPanel flowLayoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton button1 = new JButton("合并左右");
        button1.setToolTipText("左右两个行数必须相等");
        button1.addActionListener(e1 -> {
            String leftText = leftTextArea.getText();
            String rightText = rightTextArea.getText();

            List<String> leftList = StringUtil.linToList(leftText);
            List<String> rightList = StringUtil.linToList(rightText);
            if (leftList.size() != rightList.size()) {
                JswDialogUtils.showAutoCloseDialogShort("两边行数不一致");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < leftList.size(); i++) {
                sb.append("\n");
                sb.append(rightList.get(i));
                sb.append("\n");
                sb.append(leftList.get(i));
            }
            rightTextArea.setText("");
            leftTextArea.setText(sb.toString().trim());
            WinUtils.setSysClipboardText(leftTextArea.getText());
        });
        JButton button2 = new JButton("生成序号");
        button2.addActionListener(e12 -> {
            rightTextArea.setText("");
            leftTextArea.setText("");
            for (int i = 0; i < 10; i++) {
                leftTextArea.append((((i + 1) <= 9 ? "0" + i : (i + 1)) + "\n"));
                rightTextArea.append((((i + 1) <= 9 ? "0" + i : (i + 1)) + "\n"));
            }
        });
        JButton button3 = new JButton("清空两边");
        button3.addActionListener(e13 -> {
            leftTextArea.setText("");
            rightTextArea.setText("");
        });

        flowLayoutPanel.add(button1);
        flowLayoutPanel.add(button2);
        flowLayoutPanel.add(button3);

        // 将流布局面板添加到dialog底部
        dialog.add(flowLayoutPanel, BorderLayout.SOUTH);

        // 显示对话框
        dialog.setVisible(true);
    }

    /**
     * 显示一个流布局的按钮弹框，默认宽度为300，高度为200。
     *
     * @param frame   父窗口框架
     * @param buttons 要显示的按钮数组
     */
    public static void showFlowLayoutDialog(JFrame frame, JButton... buttons) {
        showFlowLayoutDialog(frame, 300, 200, buttons);
    }

    /**
     * 显示一个流布局的按钮弹框，可自定义宽度和高度。
     *
     * @param frame   父窗口框架
     * @param width   对话框的宽度
     * @param height  对话框的高度
     * @param buttons 要显示的按钮数组
     */
    public static void showFlowLayoutDialog(JFrame frame, int width, int height, JButton... buttons) {
        JDialog dialog = new JDialog(frame, "对话框", true);
        dialog.setLayout(new FlowLayout());

        // 设置对话框的固定宽高
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(frame); // 居中显示

        if (buttons.length > 0) {
            for (int i = 0; i < buttons.length; i++) {
                dialog.add(buttons[i]);
            }
        }
        // 显示对话框
        dialog.setVisible(true);
    }

    /**
     * 显示一个自定义对话框，包含输入框和多个按钮。
     * <p>
     * 使用示例：
     * SwingUtilities.invokeLater(() -> {
     * // 显示对话框
     * JswDialogUtils.showInputDialog(null, "自定义对话框",
     * new JswDialogUtils.ButtonConfig("确定", text -> {
     * System.out.println("点击了确定按钮:");
     * System.out.println(text);
     * }),
     * new JswDialogUtils.ButtonConfig("取消", text -> {
     * System.out.println("点击了取消按钮:");
     * System.out.println(text);
     * System.exit(0);
     * }));
     * });
     *
     * @param parent        父组件
     * @param title         对话框的标题
     * @param JTextAreaText 输入框的初始文本
     * @param buttons       按钮配置数组
     */
    public static void showInputDialog(Component parent, String title, String JTextAreaText, ClickButton... buttons) {
        // 创建对话框
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setSize(500, 500);
        dialog.setLayout(new BorderLayout());

        // 设置居中显示（如果 parent 为 null，则相对于屏幕居中）
        dialog.setLocationRelativeTo(parent);

        // 设置模态（阻止其他窗口操作）
        if (parent != null) {
            dialog.setModal(true);
        }

        // 创建内容面板（使用 BoxLayout 垂直排列）
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        // 添加输入框（示例：JTextArea）
        JTextArea textArea = new JTextArea(JTextAreaText);
        textArea.setPreferredSize(new Dimension(400, 200));
        textArea.setLineWrap(true); // 自动换行
        JScrollPane scrollPane = new JScrollPane(textArea); // 添加滚动条
        contentPanel.add(scrollPane);

        // 创建按钮面板（流布局，右对齐）
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // 添加按钮（从 ButtonConfig 动态生成）
        for (ClickButton buttonConfig : buttons) {
            JButton jButton = new JButton(buttonConfig.getText());
            jButton.addActionListener(e -> {
                // 获取输入框内容并传递给回调函数
                String inputText = textArea.getText();
                buttonConfig.getAction().accept(inputText);
            });
            buttonPanel.add(jButton);
        }

        // 将内容面板和按钮面板添加到对话框
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // 显示对话框
        dialog.setVisible(true);
    }

    /**
     * 按钮配置类，包含按钮文本和点击按钮时执行的操作。
     */
    public static class ClickButton {
        private final String text;
        private final Consumer<String> action;

        /**
         * 构造函数，初始化按钮文本和点击操作。
         *
         * @param text   按钮显示的文本
         * @param action 点击按钮时执行的操作
         */
        public ClickButton(String text, Consumer<String> action) {
            this.text = text;
            this.action = action;
        }

        /**
         * 获取按钮显示的文本。
         *
         * @return 按钮文本
         */
        public String getText() {
            return text;
        }

        /**
         * 获取点击按钮时执行的操作。
         *
         * @return 点击操作
         */
        public Consumer<String> getAction() {
            return action;
        }
    }

    /**
     * 显示一个多行文本不可编辑的对话框。
     *
     * @param text 要显示的文本内容
     */
    public static void showTextDialog(String text) {
        // 创建主窗口
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建对话框
        JDialog dialog = new JDialog(frame, "信息提示", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(null); // 屏幕居中

        // 创建文本区域
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false); // 不可编辑

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(textArea);
        dialog.add(scrollPane);

        // 显示对话框
        dialog.setVisible(true);
    }

    /**
     * 创建一个自定义弹框，包含多个按钮，点击按钮会触发相应的回调。
     *
     * @param title    窗口标题
     * @param click    处理按钮点击事件的接口实例
     * @param jButtons 按钮的标签数组
     * @return 创建的JDialog对象
     */
    public static JDialog createFlowLayoutDialog(String title, IClickCallBack click, String... jButtons) {
        // 创建对话框
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setSize(500, 500);
        dialog.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // 居中显示
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - dialog.getWidth()) / 2;
        int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);
        for (int i = 0; i < jButtons.length; i++) {
            int finalI = i;
            JButton jButton = new JButton(jButtons[i]);
            jButton.addActionListener(e -> {
                click.onClick(finalI, jButtons[finalI]);
                dialog.dispose();
            });
            dialog.add(jButton);
        }
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        return dialog;
    }

    /**
     * 自定义控件接口，用于处理按钮点击事件。
     */
    public interface IClickCallBack {
        /**
         * 按钮点击时执行的方法。
         *
         * @param index 按钮的索引
         * @param text  按钮的文本
         */
        void onClick(int index, String text);
    }
}