package org.mmc.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.mmc.util.swing.JswDialogUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class WinUtils {

    private static Point mousePosition;

    /**
     * @return 剪贴板中的文件
     */
    public static File getSysClipboardFile() {
        List<File> sysClipboardFiles = getSysClipboardFiles();
        return sysClipboardFiles.size() > 0 ? sysClipboardFiles.get(0) : null;
    }

    /**
     * @return 剪贴板中的文件
     */
    public static List<File> getSysClipboardFiles() {
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪切板中的内容
        Transferable clipTf = sysClip.getContents(null);

        if (clipTf != null) {
            // 检查内容是否是文本类型
            if (sysClip.isDataFlavorAvailable(DataFlavor.javaFileListFlavor)) {
                try {
                    List<File> fileList = (List<File>) sysClip.getData(DataFlavor.javaFileListFlavor);
                    // 处理文件列表
//                    for (File file : fileList) {
//                        String filePath = file.getAbsolutePath();
//                        System.out.println("复制的文件路径：" + filePath);
//                    }
                    return fileList;
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new ArrayList<>();
    }

    /**
     * 1. 从剪切板获得文字。
     */
    public static String getSysClipboardText() {
        String ret = "";
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪切板中的内容
        Transferable clipTf = sysClip.getContents(null);

        if (clipTf != null) {
            // 检查内容是否是文本类型
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    ret = (String) clipTf
                            .getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    /**
     * 2.将字符串复制到剪切板。
     */
    public static String setSysClipboardText(StringBuffer writeMe) {
        return setSysClipboardText(writeMe.toString());
    }

    /**
     * 2.将字符串复制到剪切板。
     */
    public static String setSysClipboardText(StringBuilder writeMe) {
        return setSysClipboardText(writeMe.toString());
    }

    /**
     * 2.将字符串复制到剪切板。
     */
    public static String setSysClipboardText(String writeMe) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
        return writeMe;
    }

    /**
     * 3. 从剪切板获得图片。
     */
    public static Image getImageFromClipboard() throws Exception {
        Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable cc = sysc.getContents(null);
        if (cc == null)
            return null;
        else if (cc.isDataFlavorSupported(DataFlavor.imageFlavor))
            return (Image) cc.getTransferData(DataFlavor.imageFlavor);
        return null;

    }

    /**
     * 4.复制图片到剪切板。
     */
    public static void setClipboardImage(final Image image) throws Exception {
        Transferable trans = new Transferable() {
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.imageFlavor};
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.imageFlavor.equals(flavor);
            }

            public Object getTransferData(DataFlavor flavor)
                    throws UnsupportedFlavorException, IOException {
                if (isDataFlavorSupported(flavor))
                    return image;
                throw new UnsupportedFlavorException(flavor);
            }

        };
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans,
                null);
    }

    /**
     * 依次执行批量命令行命令
     */
    public static String exeBatchCmd(String command) {
        ProcessBuilder pb;
        Process process = null;
        BufferedReader br = null;
        StringBuilder resMsg = null;
        OutputStream os = null;
        String cmd1 = "cmd";
        try {
            pb = new ProcessBuilder(cmd1);
            pb.redirectErrorStream(true);
            process = pb.start();
            os = process.getOutputStream();
            os.write(command.getBytes());
            os.flush();
            os.close();

            resMsg = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s;
            while ((s = br.readLine()) != null) {
                resMsg.append(s).append("\n");
            }
            resMsg.deleteCharAt(resMsg.length() - 1);
            int result = process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }
        return resMsg.toString();
    }

    /**
     * 依次执行批量命令行命令
     * Arrays.asList("","","");
     */
    public static void exeCmd(String... args) {
        try {
            for (String arg : args) {
                ProcessBuilder processBuilder1 = new ProcessBuilder("cmd.exe", "/c", arg);
                Process process1 = processBuilder1.start();
                process1.waitFor();//阻塞当前限制等待执行完毕
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 依次执行批量命令行命令
     * Arrays.asList("","","");
     */
    public static void exeCmd(List<String> args) {
        try {
            for (String arg : args) {
                ProcessBuilder processBuilder1 = new ProcessBuilder("cmd.exe", "/c", arg);
                Process process1 = processBuilder1.start();
                process1.waitFor();//阻塞当前限制等待执行完毕
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fileName 这个方法将文件内容复制到剪切板。
     */
    @Deprecated
    public static void copyFileToClipboard(String fileName) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "type " + fileName + " | clip");
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return getIpV4();
        }
    }

    public static InetAddress getLocalHostExactAddress() {
        try {
            InetAddress candidateAddress = null;

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface iface = networkInterfaces.nextElement();
                // 该网卡接口下的ip会有多个，也需要一个个的遍历，找到自己所需要的
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    // 排除loopback回环类型地址（不管是IPv4还是IPv6 只要是回环地址都会返回true）
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了 就是我们要找的
                            // ~~~~~~~~~~~~~绝大部分情况下都会在此处返回你的ip地址值~~~~~~~~~~~~~
                            return inetAddr;
                        }

                        // 若不是site-local地址 那就记录下该地址当作候选
                        if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }

                    }
                }
            }

            // 如果出去loopback回环地之外无其它地址了，那就回退到原始方案吧
            return candidateAddress == null ? InetAddress.getLocalHost() : candidateAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getIpV4() {
        InetAddress localHost = getLocalHostExactAddress();
        return localHost == null ? null : localHost.getHostAddress();
    }

    /**
     * 查看本机某端口是否被占用
     *
     * @param port 端口号
     * @return 如果被占用则返回true，否则返回false
     */
    public static boolean isLoclePortUsing(int port) {
        boolean flag = true;
        try {
            flag = isPortUsing("127.0.0.1", port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 根据IP和端口号，查询其是否被占用
     *
     * @param host IP
     * @param port 端口号
     * @return 如果被占用，返回true；否则返回false
     * @throws UnknownHostException IP地址不通或错误，则会抛出此异常
     */
    public static boolean isPortUsing(String host, int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        try {
            Socket socket = new Socket(theAddress, port);
            flag = true;
        } catch (IOException e) {
            //如果所测试端口号没有被占用，那么会抛出异常，这里利用这个机制来判断
            //所以，这里在捕获异常后，什么也不用做
        }
        return flag;
    }

    /**
     * 根据剪切板生成文件
     *
     * @param fileDir 生成文件所在文件夹
     */
    public static void sysClipboardTextToFile(String fileDir) {
        String sysClipboardText = WinUtils.getSysClipboardText();
        if (sysClipboardText.isEmpty()) {
            System.out.println("剪切板内容为空或不是文本内容");
            return;
        }
        String fileNmae = System.currentTimeMillis() + ".txt";
        if (sysClipboardText.contains("enum ") && sysClipboardText.contains("{")) {
            fileNmae = sysClipboardText.substring(sysClipboardText.indexOf("enum ") + 5, sysClipboardText.indexOf("{")).trim() + ".java";
        } else if (sysClipboardText.contains("class ") && sysClipboardText.contains("{")) {
            String className = sysClipboardText.substring(sysClipboardText.indexOf("class ") + 6, sysClipboardText.indexOf("{")).trim();
            if (className.contains(" extends")) {
                className = className.substring(0, className.indexOf(" extends"));
            }
            if (className.contains(" implements ")) {
                className = className.substring(0, className.indexOf(" implements"));
            }
            fileNmae = className + ".java";
        } else if (sysClipboardText.contains("public interface ") && sysClipboardText.contains("{")) {
            fileNmae = sysClipboardText.substring(sysClipboardText.indexOf("public interface ") + 16, sysClipboardText.lastIndexOf("{")).trim() + ".java";
        } else if (sysClipboardText.trim().contains("xmlns:android=\"http://schemas.android.com/apk/res/android\"")) {
            fileNmae = System.currentTimeMillis() + ".xml";
        } else if (sysClipboardText.startsWith("<!DOCTYPE html>")) {
            fileNmae = System.currentTimeMillis() + ".html";
        }
        String filePath = fileDir + fileNmae;
        FileUtils.writeFile(filePath, sysClipboardText);
        WinUtils.copyFileToClipboard(new File(filePath));
    }

    /**
     * @param path 调用系统打开文件或文件夹
     */
    public static void opeDir(String path) {
        try {
            File folder = new File(path);
            if (Desktop.isDesktopSupported() && folder.exists()) {
                // 执行打开文件夹的操作
                Desktop.getDesktop().open(folder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] getScreenSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        System.out.println("屏幕尺寸： " + screenWidth + " x " + screenHeight);
        return new int[]{screenWidth, screenHeight};
    }

    /**
     * mouseLocation.x   mouseLocation.y
     *
     * @return 获取鼠标当前位置(信息)
     */
    public static Point getMouseCurPos() {
        return MouseInfo.getPointerInfo().getLocation();
    }

    /**
     * @param commandStr 执行命令并返回内容
     * @return 执行命令并返回内容
     */
    public static String exeCmd(String commandStr) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec("cmd.exe " + commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        String content = sb.toString();
//        System.out.println("执行命令返回内容: " + content);
        return content;
    }

    /**
     * 打开浏览器
     */
    public static void open(String url) throws CheckException {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE))
                desktop.browse(new URI(url));
        } catch (Exception e) {
            try {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } catch (Exception ioException) {
                throw new CheckException("打开浏览器失败");
            }
        }
    }


    /**
     * java 实体转json
     */
    public static String javaBen2Json(String filePathOrStr) {
        JsonObject jo = new JsonObject();
        if (filePathOrStr.endsWith("}")) {
            try {
                StringUtil.readStrByLins(filePathOrStr, (linStr, i) -> {
                    ben2Json(jo, linStr);
                });
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("转换ben错误: " + e.getStackTrace());
            }
        } else {
            FileUtils.readTextFileLines(filePathOrStr, (index, linStr) -> {
                ben2Json(jo, linStr);
                return false;
            });
        }
        System.out.println("jo = " + jo.toString());
        return jo.toString();
    }

    private static void ben2Json(JsonObject jo, String linStr) {
        if ((
                linStr.contains(" int ")
                        || linStr.contains(" Integer ")
                        || linStr.contains(" String ")
                        || linStr.contains(" boolean ")
                        || linStr.contains(" Boolean ")
                        || linStr.contains(" Date ")
                        || linStr.contains(" long ")
                        || linStr.contains(" Long ")
                        || linStr.contains(" float ")
                        || linStr.contains(" Float ")
        ) && linStr.endsWith(";")) {
            linStr = linStr
                    .replace(" int ", "")
                    .replace(" Integer ", "")
                    .replace(" String ", "")
                    .replace(" boolean ", "")
                    .replace(" Boolean ", "")
                    .replace(" Date ", "")
                    .replace(" long ", "")
                    .replace(" Long ", "")
                    .replace(" float ", "")
                    .replace(" Float ", "")
                    .replace("private ", "")
                    .replace(";", "")
                    .replace(" ", "");
            jo.addProperty(linStr, "");
        }
    }

    /**
     * java 实体获取getset方法
     */
    public static String javaBenGetSet(String path) {
        StringBuffer all = new StringBuffer();
        StringBuffer className = new StringBuffer();
        StringBuffer example = new StringBuffer();
        StringBuffer getSb = new StringBuffer();
        StringBuffer setSb = new StringBuffer();
        if (path.endsWith("}")) {
            try {
                StringUtil.readStrByLins(path, (linStr, i) -> {
                    benGetSet(linStr, className, example, getSb, setSb);
                });
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("转换ben错误: " + e.getStackTrace());
            }
        } else {
            FileUtils.readTextFileLines(path, new FileUtils.ReadLines() {
                @Override
                public boolean onReadLine(int index, String linStr) {
                    benGetSet(linStr, className, example, getSb, setSb);
                    return false;
                }
            });
        }
        StringBuffer allSb = all.append(example).append(getSb).append(setSb);
        System.out.println(allSb);
        return allSb.toString();
    }

    private static void benGetSet(String linStr, StringBuffer className, StringBuffer example, StringBuffer getSb, StringBuffer setSb) {
        String tmp = "";
        if (linStr.contains("class")) {
            className.append(linStr.substring(linStr.indexOf("class")).replace(" ", "").replace("class", "").replace("{", ""));
            tmp = className + " " + StringUtil.firstOneToLowerCase(className.toString()) + " = new " + className + "();\n";
            example.append(tmp);
        }
        if (linStr.startsWith("    public ") && linStr.contains(") {") && (linStr.contains(" get") || linStr.contains(" set"))) {
            int startIndex = 0;
            if (linStr.contains("get")) {
                startIndex = linStr.indexOf("get");
            } else if (linStr.contains("set")) {
                startIndex = linStr.indexOf("set");
            }
            tmp = tmp + StringUtil.firstOneToLowerCase(className.toString()) + "." + linStr.substring(startIndex, linStr.indexOf("(")) + "();\n";
            if (linStr.contains("get")) {
                getSb.append(tmp);
            } else if (linStr.contains("set")) {
                setSb.append(tmp);
            }
        }
    }


    /**
     * 复制文件到剪切板
     */
    public static void copyFileToClipboard(File file) {
        List<File> files = new ArrayList<File>();
        files.add(file);

        FileTransferable fileTransferable = new FileTransferable(files);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(fileTransferable, null);
    }

    /**
     * 把文本设置到剪贴板（复制）
     */
    public static void getClipboardFilePath(IReadLin iReadLin) {
        try {
            // 获取系统剪贴板
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            // 获取剪贴板中的内容
            Transferable contents = clipboard.getContents(null);

            if (contents != null && contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                // 获取复制的文件列表
                List<File> files = (List<File>) contents.getTransferData(DataFlavor.javaFileListFlavor);

                // 遍历文件列表并打印每个文件的路径
                for (File file : files) {
                    iReadLin.readLin(file.getAbsolutePath());
//                    System.out.println("File path: " + file.getAbsolutePath());
                }
            } else {
                System.out.println("No files found in clipboard.");
            }
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
            iReadLin.onException(e);
            return;
        }
        iReadLin.end();
    }

    /**
     * @param num 模拟鼠标滚动3次向上
     */
    public static void mouseWheelUp(int num) {
        try {
            // 创建Robot对象
            Robot robot = new Robot();
            // 模拟鼠标滚动3次向上
            for (int i = 0; i < num; i++) {
                robot.mouseWheel(-1);
                // 等待一段时间，让滚轮滚动生效
                robot.delay(500);
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param num 模拟鼠标滚动2次向下
     */
    public static void mouseWheelDown(int num) {
        try {
            // 创建Robot对象
            Robot robot = new Robot();
            // 模拟鼠标滚动2次向下
            for (int i = 0; i < num; i++) {
                robot.mouseWheel(1);
                // 等待一段时间，让滚轮滚动生效
                robot.delay(500);
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return 获取屏幕中心位置
     */
    public static int[] getScreenCenterPos() {
        // 获取当前屏幕设备
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        // 获取屏幕设备的配置信息
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        // 获取屏幕的矩形边界
        Rectangle screenBounds = gc.getBounds();

        // 计算屏幕中心的坐标
        int centerX = screenBounds.x + screenBounds.width / 2;
        int centerY = screenBounds.y + screenBounds.height / 2;

        System.out.println("屏幕中心位置：X=" + centerX + ", Y=" + centerY);
        return new int[]{centerX, centerY};
    }

    /**
     * shift+ 按键
     *
     * @param r
     * @param key
     */
    public static void keyPressWithShift(Robot r, int key) {
        r.keyPress(KeyEvent.VK_SHIFT);
        r.keyPress(key);
        r.keyRelease(key);
        r.keyRelease(KeyEvent.VK_SHIFT);
        r.delay(100);
    }

    /**
     * ctrl+ 按键
     *
     * @param r
     * @param key
     */
    public static void keyPressWithCtrl(Robot r, int key) {
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(key);
        r.keyRelease(key);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.delay(100);
    }

    // alt+ 按键
    public static void keyPressWithAlt(Robot r, int key) {
        r.keyPress(KeyEvent.VK_ALT);
        r.keyPress(key);
        r.keyRelease(key);
        r.keyRelease(KeyEvent.VK_ALT);
        r.delay(100);
    }

    // 模拟鼠标左键点击
    public static void leftMouseClick(Robot r) {
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        r.delay(100);
    }

    //打印出字符串
    public static void keyPressString(Robot r, String str) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();//获取剪切板
        Transferable tText = new StringSelection(str);
        clip.setContents(tText, null); //设置剪切板内容
        keyPressWithCtrl(r, KeyEvent.VK_V);//粘贴
        r.delay(100);
    }

    //单个 按键
    public static void keyPress(Robot r, int key) {
        r.keyPress(key);
        r.keyRelease(key);
        r.delay(100);
    }

    /**
     * 按下 enter 换行
     *
     * @param r
     */
    public static void keyPressEnter(Robot r) {
        keyPress(r, KeyEvent.VK_ENTER); // 按下 enter 换行
    }

    /**
     * 按下 pageDown
     *
     * @param r
     */
    public static void keyPressPageDown(Robot r) {
        keyPress(r, KeyEvent.VK_PAGE_DOWN);
    }

    /**
     * 按下 pageUp
     *
     * @param r
     */
    public static void keyPressPageUp(Robot r) {
        keyPress(r, KeyEvent.VK_PAGE_UP);
    }

    /**
     * 按下 down
     *
     * @param r
     */
    public static void keyPressDown(Robot r) {
        keyPress(r, KeyEvent.VK_DOWN);
    }

    /**
     * 按下 up
     *
     * @param r
     */
    public static void keyPressUp(Robot r) {
        keyPress(r, KeyEvent.VK_UP);
    }

    /**
     * 按下 backSpace
     *
     * @param r
     */
    public static void keyPressBackSpace(Robot r) {
        keyPress(r, KeyEvent.VK_BACK_SPACE);
    }

    /**
     * 鼠标移动
     *
     * @param r
     * @param x
     * @param y
     */
    public static void mouseMove(Robot r, int x, int y) {
        // 获取当前鼠标位置
        Point currentMousePosition = MouseInfo.getPointerInfo().getLocation();
        // 移动鼠标到指定位置
        r.mouseMove(x, y);
    }

    /**
     * 鼠标移动到屏幕中心位置
     *
     * @param r
     */
    public static void mouseMoveToScreenCenterPos(Robot r) {
        int[] screenCenterPos = getScreenCenterPos();
        // 移动鼠标到指定位置
        r.mouseMove(screenCenterPos[0], screenCenterPos[1]);
    }

    /**
     * 鼠标位置显示 显示鼠标位置 模拟点击 自动化操作 模拟键盘输入
     */
    public static void showMousePos() {
        SwingUtilities.invokeLater(WinUtils::createAndShowGUI);
    }

    /**
     * 创建并显示 GUI
     */
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("显示鼠标位置");
        frame.setSize(200, 100);
        frame.setResizable(false); // 禁止调整大小
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("鼠标位置: ");
        frame.add(label, BorderLayout.CENTER);

        JButton button = new JButton("点击按钮");
        button.addActionListener(e -> {
            JswDialogUtils.showAutoCloseDialogShort("待实现 todo 修改代码");
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//            }
//            for (int i = 0; i < 20; i++) {
//                try {
//                    Robot r = new Robot();
//                    r.mouseMove(866, 142);
//                    r.delay(100);
//                    WinUtils.leftMouseClick(r);
//                    r.delay(100);
//                    r.mouseMove(750, 390);
//                    WinUtils.leftMouseClick(r);
//                    r.delay(100);
//                } catch (AWTException ex) {
//                }
//            }
        });
        frame.add(button, BorderLayout.SOUTH);

        // 设置窗口始终在最顶层显示
        frame.setAlwaysOnTop(true);


        // 定时器，每隔100毫秒更新一次鼠标位置
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取鼠标位置
                Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(mouseLocation, frame);
                // 更新标签文本
                label.setText("鼠标位置: " + mouseLocation.x + ", " + mouseLocation.y);
            }
        });
        timer.start();
        // 添加键盘监听器，按下空格键时退出程序
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    System.exit(0);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        // 确保焦点在窗口上，以便能够接收键盘事件
        frame.setFocusable(true);
        frame.requestFocus();

        frame.setVisible(true);
    }

    /**
     * 移动窗口
     *
     * @param frame
     */
    private static void moveFrameForCtrl(JFrame frame) {
        // 添加鼠标监听器
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 捕获鼠标位置
                mousePosition = e.getPoint();
            }
        });

        // 添加鼠标移动监听器
        frame.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // 如果同时按下了Ctrl键，则移动窗口
                if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {
                    Point newMousePosition = e.getLocationOnScreen();
                    frame.setLocation(newMousePosition.x - mousePosition.x, newMousePosition.y - mousePosition.y);
                }
            }
        });

    }

    /**
     * 是否为盘符开头
     */
    public static boolean startsWithWindowsDrive(String str) {
        if (str == null) return false;
        // 使用正则表达式判断字符串是否以 Windows 盘符开头
        return str.trim().matches("^[A-Za-z]:\\\\.*");
    }

    /**
     * 净化文件名
     */
    public static void clearFileName() {
        String clipboardString = WinUtils.getSysClipboardText();
        if (clipboardString != null)
            setSysClipboardText(FileUtils.clearFileName(clipboardString, true));
    }

    /**
     * 启动程序 阻塞waitFor()方法在程序启动完成后不会再阻塞线程了
     *
     * @param programPath 程序路径
     */
    public static void startExe(String programPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", programPath);
            Process p = pb.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开开机启动文件夹
     */
    public static void openSysStartDir() {

        // 获取用户的主目录
        String userHome = System.getProperty("user.home");

        // 构建开机启动文件夹的路径
        File startupFolder = new File(userHome + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup");

        // 检查文件夹是否存在
        if (startupFolder.exists()) {
            try {
                // 使用 Desktop 类打开文件夹
                Desktop.getDesktop().open(startupFolder);
            } catch (IOException e1) {
                e1.printStackTrace();
                setSysClipboardText("explorer shell:startup");
                JOptionPane.showMessageDialog(null, "打开异常，已复制命令行。", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            setSysClipboardText("explorer shell:startup");
            JOptionPane.showMessageDialog(null, "启动文件夹不存在，已复制命令行。", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static String getUserHomeDir() {
        return System.getProperty("user.home");
    }

    /**
     * 5.通过流获取，可读取图文混合
     */
    public void getImageAndTextFromClipboard() throws Exception {
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable clipTf = sysClip.getContents(null);
        DataFlavor[] dataList = clipTf.getTransferDataFlavors();
        int wholeLength = 0;
        for (int i = 0; i < dataList.length; i++) {
            DataFlavor data = dataList[i];
            if (data.getSubType().equals("rtf")) {
                Reader reader = data.getReaderForText(clipTf);
                OutputStreamWriter osw = new OutputStreamWriter(
                        new FileOutputStream("d:\\test.rtf"));
                char[] c = new char[1024];
                int leng = -1;
                while ((leng = reader.read(c)) != -1) {
                    osw.write(c, wholeLength, leng);
                }
                osw.flush();
                osw.close();
            }
        }
    }

    /**
     * 文件拖拽
     */
    public interface IReadLin {
        void readLin(String filePath);

        default void onException(Exception exception) {

        }

        default void end() {

        }
    }

    /**
     * 文件拖拽
     */
    static class FileTransferable implements Transferable {
        private List files;

        public FileTransferable(List files) {
            this.files = files;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.javaFileListFlavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return files;
        }
    }


    /**
     * 批量打开网页
     */
    public static void batchOpenUrl() throws Exception {

        String s = WinUtils.getSysClipboardText();
        System.out.println("s = " + s);
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.trim().isEmpty()) {
                if (line.startsWith("http")) {
                    String[] urls = line.split("http");
                    if (urls.length > 1) {
                        for (String url : urls) {
                            if (url.trim().isEmpty()) {
                                continue;
                            }
                            WinUtils.open("http" + url);
                        }
                    } else {
                        WinUtils.open(line);
                    }
                } else if (line.contains("http")) {
                    WinUtils.open(line.substring(line.indexOf("http")));
                }
            }
        }
    }

    /**
     * 生成批量网址
     */
    public static void batchCreateUrl() {
        JswDialogUtils.showEditDialogSimple("生成批量网址", "使用@符号分割", WinUtils.getSysClipboardText(), new JswDialogUtils.ISimpleCallBack() {
            @Override
            public void str(String content) {
                if (content != null) {
                    String[] split = content.split("@");
                    StringBuilder sb = new StringBuilder();
                    int startNum = Integer.parseInt(split[1]);
                    for (int i = 0; i < 100; i++) {
                        sb.append(split[0]).append(startNum + i).append(split[2]).append("\n");
                    }
                    WinUtils.setSysClipboardText(sb.toString());
                    JswDialogUtils.show("生成完毕");
                }
            }
        });
    }

    //--------------------------根据端口停止进程服务 start--------------------------------------

    private Set<?> ports;

    /**
     * KillServer
     * https://www.cnblogs.com/zhenzicheng/p/15125309.html
     * https://zhuanlan.zhihu.com/p/23675448
     * 根据端口停止服务(请输入需要kill的进程端口号，如果有多个以逗号分隔)
     *
     * @param port 服务端口
     * @throws InterruptedException 解析异常
     */
    public static void stopServiceByPort(String port) throws InterruptedException {
        // 校验逻辑
        String[] split = port.split(",");
        Set<Integer> ports = new HashSet<>();
        for (String pidStr : split) {
            try {
                int pid = Integer.parseInt(pidStr);
                ports.add(pid);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                //System.out.println(e.getMessage());
                System.out.println("输入的端口号格式不正确，请重新输入!");
                return;
            }
        }
        // 执行逻辑
        WinUtils server = new WinUtils();
        server.ports = ports;
        for (Integer pid : ports) {
            server.start(pid);
        }
        System.out.println("执行成功，程序即将推出...");
        Thread.sleep(2000);
        System.exit(0);
    }


    /**
     * 主要开始方法
     *
     * @param port 端口号
     */
    private void start(int port) {
        Runtime runtime = Runtime.getRuntime();
        try {
            // 查找进程号
            Process p = runtime.exec(String.format("cmd /c netstat -ano | findstr %d", port));
            InputStream is = p.getInputStream();
            List<String> read = read(is, "UTF-8");
            if (read.size() == 0) {
                System.out.printf("找不到 %d 端口号的进程，继续执行...%n", port);
            } else {
                System.out.printf("找到 %d 个进程，准备清理...%n", read.size());
                toSet(read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取输入流的每一行代码
     *
     * @param in      InputStream
     * @param charset 字符集
     * @return 每一行数据的List
     */
    private List<String> read(InputStream in, String charset) throws IOException {
        List<String> data = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
        String line;
        while ((line = reader.readLine()) != null) {
            boolean validPort = validPort(line);
            if (validPort) {
                data.add(line);
            }
        }
        reader.close();
        return data;
    }

    /**
     * 使用正则表达式执行是否为指定端口，findstr会将包含的所有端口都找出来 例如：80也会包含8083
     *
     * @param str 每一行
     * @return 端口是否有效
     */
    private boolean validPort(String str) {
        Pattern pattern = Pattern.compile("^ *[a-zA-Z]+ +\\S+");
        Matcher matcher = pattern.matcher(str);

        if (!matcher.find()) return false;
        String find = matcher.group();
        int lIndex = find.lastIndexOf(":");
        find = find.substring(lIndex + 1);
        int port;
        try {
            port = Integer.parseInt(find);
        } catch (NumberFormatException e) {
            System.out.println("查找到错误端口: " + find);
            return false;
        }

        return this.ports.contains(port);
    }

    /**
     * 将每一行进程信息的List转换为只有端口号的Set(去重)
     *
     * @param data 包含有全部端口信息的每一行数据list
     */
    private void toSet(List<String> data) {
        Set<Integer> pids = new HashSet<>();
        for (String line : data) {
            int lIndex = line.lastIndexOf(" ");
            String spid = line.substring(lIndex + 1).trim();
            int pid;
            try {
                pid = Integer.parseInt(spid);
                pids.add(pid);
            } catch (NumberFormatException e) {
                System.out.println("获取进程号错误: " + spid);
            }
            kill(pids);
        }
    }

    /**
     * 一次性kill所有端口
     *
     * @param pids 端口集合
     */
    private void kill(Set<Integer> pids) {
        for (Integer pid : pids) {
            try {
                Process process = Runtime.getRuntime().exec(String.format("taskkill /F /pid %d", pid));
                InputStream is = process.getInputStream();

                String txt = readTxt(is, "GBK");
                System.out.println(txt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 读取输入流
     *
     * @param in
     * @param charset
     * @return
     * @throws IOException
     */
    private String readTxt(InputStream in, String charset) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    //--------------------------根据端口停止进程服务 end--------------------------------------


    /**
     * 打开开机启动文件夹
     */
    public static void openStartupDir() {
        String startupFolderPath = System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup";
        File startupFolder = new File(startupFolderPath);

        if (startupFolder.exists()) {
            try {
                Desktop.getDesktop().open(startupFolder);
            } catch (IOException e) {
                e.printStackTrace();
                WinUtils.exeCmd("explorer C:\\ProgramData\\Microsoft\\Windows\\\"Start Menu\"\\Programs\\StartUp");
            }
        } else {
            System.out.println("Startup folder does not exist.");
        }
    }

    /**
     * 复制json到剪切板
     *
     * @param text
     */
    public static void copyJson(String text) {
        // 创建一个StringSelection对象，传入要复制的文本
        StringSelection stringSelection = new StringSelection(formattingJson(text));
        // 获取系统默认的剪贴板
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        System.out.println("已复制: " + text);
    }

    /**
     * 将未格式化的JSON字符串格式化为易读的格式
     *
     * @param unformattedJson 未格式化的JSON字符串
     * @return 格式化后的JSON字符串
     */
    public static String formattingJson(String unformattedJson) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonToMap(unformattedJson));
    }

    /**
     * 将JSON字符串转换为Map对象
     *
     * @param json
     * @return
     */
    private static Object jsonToMap(String json) {
        return new Gson().fromJson(json, Object.class);
    }


    /**
     * 保存剪贴板中的图片
     */
    public static void saveClipboardImg(String filePath) {
        try {
            // 获取默认工具包和剪贴板
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            // 检查剪贴板内容是否包含图像
            if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
                // 从剪贴板获取图像数据
                BufferedImage image = (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);

                // 保存图像到文件
                File output = new File(filePath + File.separator + System.currentTimeMillis() + ".png");
                ImageIO.write(image, "png", output);

                System.out.println("图像保存成功: " + output.getAbsolutePath());
            } else {
                System.out.println("剪贴板中没有图像数据。");
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取指定文件夹的路径。
     *
     * @param folderName 文件夹名称 Desktop Downloads Documents Videos Music
     * @return 文件夹路径
     */
    public static Path getFolderPath(String folderName) {
        return Paths.get(System.getProperty("user.home"), folderName);
    }

    /**
     *
     * @return 获取桌面
     */
    public static String getDesktop() {
        return Paths.get(System.getProperty("user.home"), "Desktop").toString() + "/";
    }

    /**
     *
     * @return 获取下载文件夹的路径
     */
    public static String getDownloads() {
        return Paths.get(System.getProperty("user.home"), "Downloads").toString() + "/";
    }

    /**
     * 获取文档文件夹的路径。
     *
     * @return 获取文档文件夹的路径
     */
    public static String getDocuments() {
        return Paths.get(System.getProperty("user.home"), "Documents").toString() + "/";
    }

    /**
     * 获取视频文件夹的路径。
     *
     * @return 获取视频文件夹的路径
     */
    public static String getVideos() {
        return Paths.get(System.getProperty("user.home"), "Videos").toString() + "/";
    }

    /**
     * 获取音乐文件夹的路径。
     *
     * @return 获取音乐文件夹的路径
     */
    public static String getMusic() {
        return Paths.get(System.getProperty("user.home"), "Music").toString() + "/";
    }

    /**
     * 发送一个系统通知
     *
     * @param title title
     * @param msg   msg
     */
    public static void sendSysNofityMsg(String title, String msg) {

        if (SystemTray.isSupported()) {
            try {

                //Obtain only one instance of the SystemTray object
                SystemTray tray = SystemTray.getSystemTray();

                //If the icon is a file
                Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
                //Alternative (if the icon is on the classpath):
                //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

                // 创建托盘图标
                TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");

                // 添加鼠标点击事件
                trayIcon.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) { // 左键点击
                            System.out.println("托盘图标被点击!");
                            JOptionPane.showMessageDialog(null, "托盘图标被点击!");
                        }
                    }
                });

                //Let the system resize the image if needed
                trayIcon.setImageAutoSize(true);
                //Set tooltip text for the tray icon
                trayIcon.setToolTip("这是一个系统通知");
                tray.add(trayIcon);

                trayIcon.displayMessage(title, msg, TrayIcon.MessageType.INFO);
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("System tray not supported!");
        }
    }

    public static String getPublicIp() {
        String ip = "";
//        OkHttpClient okHttpClient = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("https://ipinfo.io/ip")
//                .build();
//        try {
//            Response response = okHttpClient.newCall(request).execute();
//            if (response.isSuccessful()) {
//                if (response.body() != null) {
//                    String requestBody = response.body().string();
//                    Log.i("MainActivity", "requestBody length is " + requestBody.length());
//                    ip = requestBody;
//                    return ip;
//                }
//            }
//        } catch (Exception e) {
//            Log.e("MainActivity", "ipAddress is null exception is " + e);
//        }
        try {
            // 创建URL对象
            URL url = new URL("https://ipinfo.io/ip");
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为GET（这是默认的，但明确设置更好）
            connection.setRequestMethod("GET");
            // 设置一些请求头，可选，但对于一些服务端可能需要合理设置
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // 获取响应状态码
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // 输出返回的字符串（这里就是IP地址相关内容）
                System.out.println(ip = response.toString());
            } else {
                System.out.println("请求失败，状态码: " + responseCode);
            }
            // 关闭连接
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ip;
    }

    /**
     * 打开文件
     *
     * @param filePath
     */
    public static void opeFile(String filePath) {
        // 创建文件对象
        File file = new File(filePath);

        // 检查文件是否存在且可读
        if (file.exists() && file.canRead()) {
            try {
                // 获取桌面环境并尝试用默认应用打开文件
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                // 如果出现异常，可能是没有关联默认程序或无法打开文件
                System.err.println("Error opening file: " + e.getMessage());
            }
        } else {
            System.out.println("File does not exist or is not readable.");
        }
    }

}