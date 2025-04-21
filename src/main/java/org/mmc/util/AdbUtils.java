package org.mmc.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AdbUtils 类提供了一系列通过 ADB（Android Debug Bridge）命令与 Android 设备进行交互的工具方法
 * 参考链接：
 * https://blog.csdn.net/xiaoerbuyu1233/article/details/122124201
 * https://blog.csdn.net/aoaoxiexie/article/details/53464716
 * https://www.huaweicloud.com/articles/39c8580fd6d8eacb6b0b89082f9d15b4.html
 * https://juejin.cn/post/6844903645289398280#heading-43
 */
public class AdbUtils {

    // 静态变量，用于获取系统运行时环境
    private static Runtime runtime;

    static {
        // 初始化运行时环境
        runtime = Runtime.getRuntime();
    }

    /**
     * 向 Android 设备的剪贴板发送文本内容
     *
     * @param sysClipboardText 要发送到剪贴板的文本内容
     * @param isBack           是否在发送成功后点击返回键
     */
    public static void sendSysClipboardText(String sysClipboardText, boolean isBack) {
        // 构建 ADB 命令字符串
        String commandStr = "adb shell am broadcast -a clipper.set -f 0x01000000 --es \"text\" \"" + sysClipboardText + "\"";
        System.out.println("发送剪切板内容: " + commandStr);
        // 执行 ADB 命令并获取结果
        String result = exeCmd(commandStr);
        System.out.println("result = " + result);
        // 如果需要点击返回键且发送成功，则执行点击返回键操作
        if (isBack && result.contains("文本被复制到剪贴板")) {
            AdbUtils.clickBack();
        }
    }

    /**
     * 获取 Android 设备剪贴板的文本内容
     *
     * @return 剪贴板的文本内容
     */
    public static String getSysClipboardText() {
        // 执行获取剪贴板内容的 ADB 命令并返回结果
        return exeCmd("adb shell am broadcast -a clipper.get");
    }

    /**
     * 模拟点击 Android 设备的返回键
     */
    public static void clickBack() {
        // 执行点击返回键的 ADB 命令
        exeCmd("adb shell input keyevent 4");
    }

    /**
     * 打开 Android 设备的文件闪传应用，并在 PC 端打开对应的网页链接
     */
    public static void openFilFastTransmissionApp() {
        // 执行打开文件闪传应用的 ADB 命令
        AdbUtils.exeCmd("adb shell am start app.eleven.com.fastfiletransfer/app.eleven.com.fastfiletransfer.ContainerActivity");
        // 构建网页链接
        String commandStr = "http://" + AdbUtils.getIP() + ":2333";
        System.out.println("打开文件闪传: " + commandStr);
        try {
            // 在 PC 端打开网页链接
            WinUtils.open(commandStr);
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }

    /**
     * 获取 Android 设备的基本信息，包括屏幕分辨率、密度、型号、系统版本等
     *
     * @return 设备的基本信息字符串
     */
    public static String getPhoneBaseInfo() {
        // 用于拼接设备基本信息的 StringBuilder 对象
        StringBuilder allSb = new StringBuilder();
        // 获取屏幕分辨率
        String wm_size = AdbUtils.exeCmd("adb shell wm size");
        // 获取屏幕密度
        String density = AdbUtils.exeCmd("adb shell wm density");
        // 获取设备型号
        String model = AdbUtils.exeCmd("adb shell getprop ro.product.model");
        // 获取设备的 Android ID
        String android_id = AdbUtils.exeCmd("adb shell settings get secure android_id");
        // 获取设备的系统版本
        String version = AdbUtils.exeCmd("adb shell getprop ro.build.version.release");
        // 获取设备的指纹信息
        String fingerprint = AdbUtils.exeCmd("adb shell getprop ro.build.fingerprint");
        // 获取设备的 CPU 架构信息
        String cpus = AdbUtils.exeCmd("adb shell getprop ro.product.cpu.abilist");
        // 获取设备的 SDK 版本
        String sdk = AdbUtils.exeCmd("adb shell getprop ro.build.version.sdk");
        // 获取设备的屏幕休眠时间
        String screen_off_timeout = AdbUtils.exeCmd("adb shell settings get system screen_off_timeout");
        // 获取设备的 IP 地址
        String ip = AdbUtils.getIP();

        // 拼接设备基本信息
        allSb.append("屏幕分辨率: ").append(wm_size.replace("Physical size: ", ""));
        allSb.append("密度:      ").append(density.replace("Physical density: ", ""));
        allSb.append("型号:      ").append(model);
        allSb.append("品牌:      ").append(fingerprint);
        allSb.append("系统版本:   ").append(version);
        allSb.append("sdk版本:   ").append(sdk);
        allSb.append("cpu架构:   ").append(cpus);
        allSb.append("设备id:    ").append(android_id);
        allSb.append("联网ip:    ").append(ip);
        allSb.append("休眠时间:   ").append(screen_off_timeout);

        // 获取拼接好的设备基本信息字符串
        String baseInfo = allSb.toString();
        // 将设备基本信息复制到 PC 端的剪贴板
        WinUtils.setSysClipboardText(baseInfo);
        System.out.println("手机基本信息:\n" + baseInfo);
        return baseInfo;
    }

    /**
     * 打开 Android 设备的设置页面
     */
    public static void startSettingPage() {
        // 执行打开设置页面的 ADB 命令
        exeCmd("adb shell am start com.android.settings/com.android.settings.Settings");
    }

    /**
     * 打开或关闭 Android 设备的 Wi-Fi
     *
     * @param enable 是否打开 Wi-Fi，true 为打开，false 为关闭
     */
    public static void openWifi(boolean enable) {
        // 构建打开或关闭 Wi-Fi 的 ADB 命令并执行
        exeCmd("adb shell svc wifi " + (enable ? "enable" : "disable"));
    }

    /**
     * 打开 Android 设备的屏幕常亮功能，并设置屏幕亮度为最大
     */
    public static void openScreenSteadyLight() {
        // 设置屏幕永不休眠
        AdbUtils.exeCmd("adb shell settings put system screen_off_timeout 2147483647");
        // 设置屏幕常亮
        AdbUtils.exeCmd("adb shell svc power stayon true");
        // 设置屏幕亮度为最大
        AdbUtils.exeCmd("adb shell settings put system screen_brightness 255");
    }

    /**
     * 获取 Android 设备当前 Activity 的详细信息
     *
     * @return Activity 的详细信息字符串
     */
    public static String getActivityDetails() {
        // 执行获取 Activity 详细信息的 ADB 命令并返回结果
        return exeCmd("adb shell dumpsys activity top");
    }

    /**
     * 模拟点击 Android 设备的 Tab 键
     */
    public static void inputKeyTab() {
        // 执行模拟点击 Tab 键的 ADB 命令
        exeCmd("adb shell input keyevent KEYCODE_TAB");
    }

    /**
     * 模拟点击 Android 设备的返回键
     */
    public static void inputKeyBack() {
        // 执行模拟点击返回键的 ADB 命令
        exeCmd("adb shell input keyevent KEYCODE_BACK");
    }

    /**
     * 从剪贴板内容中提取 Android 布局文件中的 View ID 信息，并生成 ButterKnife 和 findViewById 的代码，复制到剪贴板
     *
     * @throws IOException 当读取剪贴板内容时发生 I/O 异常
     */
    public static void fvbIdToClipboard() throws IOException {
        // 获取 PC 端剪贴板的文本内容
        String clipboardText = WinUtils.getSysClipboardText();
        // 用于存储 View ID 和对应的元素类型的 LinkedHashMap
        LinkedHashMap<String, String> keys = new LinkedHashMap<>();
        String element = null;
        // 创建 BufferedReader 用于读取剪贴板内容
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(clipboardText.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        String line;
        // 逐行读取剪贴板内容
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.equals("")) {
                if (line.startsWith("<")) {
                    if (line.contains(" xmlns:android=\"")) {
                        line = line.split(" xmlns:android=\"")[0];
                    }
                    if (line.contains(".")) {
                        String[] split = line.split("\\.");
                        line = split[split.length - 1];
                    }
                    element = line.replace("<", "");
                } else if (line.contains("android:id=\"@+id/")) {
                    keys.put(line.split("/")[1].replace("\"", ""), element);
                }
            }
        }
        // 用于拼接 ButterKnife 字段的 StringBuilder
        StringBuilder globalFieldSb = new StringBuilder();
        // 用于拼接 ButterKnife 点击事件的 StringBuilder
        StringBuilder clickMethod = new StringBuilder("@OnClick({");
        // 用于拼接 ButterKnife 点击事件主体的 StringBuilder
        StringBuilder clickBody = new StringBuilder("public void click(View view) {\n        switch (view.getId()) {\n");
        // 用于拼接 findViewById 字段的 StringBuilder
        StringBuilder fvbFieldSb = new StringBuilder("\n\n");
        // 用于拼接 findViewById 主体的 StringBuilder
        StringBuilder fvbBodySb = new StringBuilder("\n");

        // 遍历 keys 集合，生成 ButterKnife 和 findViewById 的代码
        keys.keySet().forEach(key -> {
            globalFieldSb.append("@BindView(R.id.").append(key).append(")\n")
                    .append(keys.get(key)).append(" ").append(key).append(";\n\n");
            clickMethod.append("R.id.").append(key).append(",");
            clickBody.append("case R.id.").append(key).append(" :\nbreak;\n");
            fvbFieldSb.append(keys.get(key)).append(" ").append(key).append(";\n");
            fvbBodySb.append(key).append(" = findViewById(R.id.").append(key).append(");\n");
        });
        // 拼接生成的代码
        String content = globalFieldSb.append(clickMethod.append("})\n")).append(clickBody.append("default:\n}}"))
                .append(fvbFieldSb).append(fvbBodySb).toString().replace(",})", "})");

        // 用于拼接额外信息的 StringBuilder
        StringBuilder sb = new StringBuilder();
        // 逐行读取剪贴板内容，提取额外信息
        StringUtil.readStrByLins(clipboardText, (lin, lineIndex) -> {
            if (lin.contains("android:id=\"@+id/")) {
                String id = lin.replace("android:id=\"@+id/", "").replace("\"", "").trim();
                sb.append("\r\nmBinding.").append(StringUtil.underscoreToCamel(id)).append("  //id: " + id);
            } else if (lin.contains("android:text=\"")) {
                String text = lin.replace("android:text=\"", "").replace("\"", "").trim();
                sb.append("  text: ").append(StringUtil.underscoreToCamel(text)).append("\r\n");
            }
        });
        // 拼接额外信息到生成的代码中
        content += "\n" + sb.toString().replaceAll("(?m)^[ \t]*\r?\n", "");

        // 将生成的代码复制到 PC 端的剪贴板
        WinUtils.setSysClipboardText(content);
    }

    /**
     * 同时控制两台 Android 设备在指定坐标点击
     */
    public static void testTapForTwoAndroid() {
        try {
            // 执行控制第一台设备点击的 ADB 命令
            runtime.exec("adb -s 13b6e4c4 shell input tap 400 400 ");
            // 执行控制第二台设备点击的 ADB 命令
            runtime.exec("adb -s 296ec3e2 shell input tap 400 400 ");
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }

    /**
     * 控制 Android 设备在指定坐标点击
     *
     * @param x 点击位置的 X 坐标
     * @param y 点击位置的 Y 坐标
     */
    public static void click(int x, int y) {
        try {
            // 执行点击操作的 ADB 命令
            runtime.exec("adb shell input tap " + x + " " + y);
            // 线程休眠 1 秒
            Thread.sleep(1000);
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }

    /**
     * 控制 Android 设备在指定坐标长按
     *
     * @param x 长按位置的 X 坐标
     * @param y 长按位置的 Y 坐标
     */
    public static void longClick(String x, String y) {
        try {
            // 执行长按操作的 ADB 命令
            runtime.exec("adb shell input swipe " + x + " " + y + " " + x + " " + y);
            // 线程休眠 1 秒
            Thread.sleep(1000);
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }

    /**
     * 控制 Android 设备从指定起点滑动到指定终点
     *
     * @param satrtX 滑动起点的 X 坐标
     * @param satrtY 滑动起点的 Y 坐标
     * @param endX   滑动终点的 X 坐标
     * @param endY   滑动终点的 Y 坐标
     */
    public static void swipe(String satrtX, String satrtY, String endX, String endY) {
        try {
            // 执行滑动操作的 ADB 命令
            runtime
                    .exec("adb shell input swipe " + satrtX + " " + satrtY + " " + endX + " " + endY);
            // 线程休眠 1 秒
            Thread.sleep(1000);
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }

    /**
     * 控制 Android 设备从指定起点滑动到指定终点，并指定滑动持续时间
     *
     * @param satrtX   滑动起点的 X 坐标
     * @param satrtY   滑动起点的 Y 坐标
     * @param endX     滑动终点的 X 坐标
     * @param endY     滑动终点的 Y 坐标
     * @param duration 滑动持续时间（毫秒）
     */
    public static void swipe(int satrtX, int satrtY, int endX, int endY, int duration) {
        try {
            // 执行滑动操作的 ADB 命令
            runtime
                    .exec("adb shell input swipe " + satrtX + " " + satrtY + " " + endX + " " + endY + " " + duration);
            // 线程休眠 1 秒
            Thread.sleep(1000);
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        }
    }

    /**
     * 在 Android 设备的指定坐标输入文本内容
     *
     * @param x           输入位置的 X 坐标
     * @param y           输入位置的 Y 坐标
     * @param inputStr    要输入的文本内容
     * @param isClickBack 是否在输入完成后点击返回键
     */
    public static void inutText(int x, int y, String inputStr, boolean isClickBack) {
        inputStr = StringUtil.replaceAllChinese(inputStr);
        try {
            runtime.exec("adb shell input tap " + x + " " + y);
            runtime.exec("adb shell input text \"" + inputStr + "\"");
            if (isClickBack) {
                runtime.exec("adb shell input keyevent 4");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输入文字 不能输入汉字 特殊符号需要隐藏键盘或设置键盘为英文才能输入
     *
     * @param inputStr    输入信息
     * @param isClickBack 是否点击返回
     */
    public static void inutText(String inputStr, boolean isClickBack) {
        inputStr = StringUtil.replaceAllChinese(inputStr);
        try {
            runtime.exec("adb shell input text \"" + inputStr + "\"");
            if (isClickBack) {
                runtime.exec("adb shell input keyevent 4");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使当前线程暂停执行一段时间，以允许其他线程运行或等待某个条件
     * 这个方法主要用于测试或协调多线程程序中的执行顺序
     */
    public static void sleep() {
        try {
            // 暂停当前线程500毫秒
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // 当线程被中断时，捕获InterruptedException
            // 打印异常堆栈跟踪信息，指示发生了中断
            e.printStackTrace();
        }
    }

    /**
     * @param millis 间隔时间
     */
    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param commandStr 执行命令并返回内容
     * @return 命令执行结果
     */
    public static String exeCmd(String commandStr) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec("cmd.exe /c " + commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));//编码: Charset.forName("GBK")
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
     * 执行命令并获取输出
     * 该方法使用Runtime类来执行系统命令，并捕获命令的输出和错误信息
     * 注意：该方法仅适用于能够快速完成的命令，避免长时间运行的命令导致资源泄露
     */
    public static void execCommandAndGetOutput() {
        try {
            // 获取当前Java虚拟机的Runtime对象
            Runtime runtime = Runtime.getRuntime();
            // 执行系统命令，这里以Windows系统的ipconfig命令为例
            // 注意：该命令及其参数应根据实际操作系统进行调整
            Process process = runtime.exec("cmd.exe /c ipconfig");

            // 输出结果，必须写在 waitFor 之前
            String outStr = getStreamStr(process.getInputStream());
            // 错误结果，必须写在 waitFor 之前
            String errStr = getStreamStr(process.getErrorStream());

            // 等待命令执行完成，并获取退出值
            // 退出值 0 为正常，其他为异常
            int exitValue = process.waitFor();
            System.out.println("exitValue: " + exitValue);
            System.out.println("outStr: " + outStr);
            System.out.println("errStr: " + errStr);

            // 销毁进程，释放资源
            process.destroy();
        } catch (IOException | InterruptedException e) {
            // 捕获可能的IO异常或中断异常
            e.printStackTrace();
        }
    }

    /**
     * 读取流内容
     *
     * @param is 输入流
     * @return 内容
     * @throws IOException e
     */
    public static String getStreamStr(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "GBK"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        br.close();
        return sb.toString();
    }

    /**
     * 开启触摸位置显示
     *
     * @param postionTag true 开启 false 关闭
     */
    public static void offTachPostion(boolean postionTag) {
        exeCmd("adb shell settings put system pointer_location " + (postionTag ? "1" : "0"));
    }

    /**
     * adb shell dumpsys window | findstr mCurrentFocus
     * 前台包名和顶部Activity
     *
     * @return 命令执行结果
     */
    public static String getTopAppPack2ActyName() {
        String content = AdbUtils.exeCmd("adb shell dumpsys window w |findstr \\/ |findstr name= ");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("line = " + line);
                if (line.length() > 0 && line.contains(".") && line.contains("mSurface=Surface(name=") && line.contains("Activity") || line.contains("Acty)")) {
                    int count = StringUtil.countMatches(line, "/");
                    if (count == 1) {
                        return line.replace("mSurface=Surface(name=", "").replace(")", "").trim();
                    }
                    return line.substring(0, line.lastIndexOf(")/")).replace("mSurface=Surface(name=", "").trim();
                }
//                line = line.substring(0, line.lastIndexOf("/@"));
//                if (!line.contains(".") && line.startsWith("name=com.android.")) {
//                    String replace = line.replace("mSurface=Surface(name=", "").replace(")", "");
//                    System.out.println(replace);
//                    return replace.substring(0, replace.lastIndexOf("/"));
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 顶部Activity名称
     *
     * @return 命令执行结果
     */
    public static String getTopAppActyName() {
        return getTopAppPack2ActyName().split("/")[1];
    }

    /**
     * 顶部包名
     *
     * @return 命令执行结果
     */
    public static String getTopAppPackageName() {
        return getTopAppPack2ActyName().split("/")[0];
    }

    /**
     * 前台包名
     *
     * @return 命令执行结果
     */
    public static String getFrontAppPackageName() {
        //com.hebccc.crm/com.hebccc.crm.bussiness.main.MainActivity
//        String packName = AdbUtils.exeCmd("adb shell dumpsys window w |findstr \\/ |findstr name= ");
        String packName = getTopAppPack2ActyName();
        if (packName.contains("/")) {
            packName = packName.split("/")[0];
        }
        return packName;
    }

    /**
     * 前台Activity 名称
     *
     * @return 命令执行结果
     */
    public static String getFrontActyName() {
        //com.hebccc.crm/com.hebccc.crm.bussiness.main.MainActivity
//        String packName = AdbUtils.exeCmd("adb shell dumpsys window w |findstr \\/ |findstr name= ");
        String packName = getTopAppPack2ActyName();
        if (packName.contains("/")) {
            packName = packName.split("/")[0];
        }
        return packName;
    }

    /**
     * 卸载前台app
     */
    public static void unInstallFrontApp() {
        exeCmd("adb uninstall " + getFrontAppPackageName());
    }

    /**
     * 打开布局边界(需要重启app)
     *
     * @param off true:开启布局边界 false:关闭布局边界
     */
    public static void offLayoutView(boolean off) {
        exeCmd("adb shell setprop debug.layout " + off);
    }

    /**
     * 保持唤醒状态
     * <p>
     * 暂时不清楚和 keepScreenSteadyLight() 方法区别
     */
    public static void keepSober() {
        exeCmd("adb shell am start -n com.android.settings/.DisplaySettings");
    }

    /**
     * 保存屏幕常亮
     * 暂时不清楚和 keepSober() 方法区别
     * adb shell svc power stayon [true/false/usb/ac] 设置屏幕的常亮，true保持常亮，false不保持，usb当插入usb时常亮，ac当插入电源时常亮
     *
     * @param mode true/false/usb/ac 设置屏幕的常亮，true保持常亮，false不保持，usb当插入usb时常亮，ac当插入电源时常亮
     */
    public static void keepScreenSteadyLight(String mode) {
        exeCmd("adb shell svc power stayon " + mode);
    }

    /**
     * 开发者界面
     */
    public static void openDeveloperSetting() {
        //360n7pro
        exeCmd("adb shell am start com.android.settings/.DevelopmentSettings");
        //一加9rt
        exeCmd("adb shell am start -n com.android.settings/com.android.settings.Settings\\$DevelopmentSettingsDashboardActivity");
    }

    /**
     * 推pc文件到手机
     */
    public static void pushFile(String filePath) {
        filePath = filePath.replace(" ", "");
        String fileName = FileUtils.getFileName(filePath);
        String commandStr = "adb push \"" + filePath + "\" /sdcard/Download/\"" + fileName + "\"";
        String s = exeCmd(commandStr);
        System.out.println("adb命令: " + commandStr + " \nadb命令执行结果: " + s + " 文件名:" + fileName);
    }

    /**
     * /sdcard/Download/
     * 提取手机文件到pc桌面
     * 需要先创建pc本地文件夹才行,不存在的情况下可能会执行失败
     * adb pull "sdcard/_电脑传输/" D:\tmp\_电脑传输\
     * adb pull /sdcard/Android/data/cn.mvp/files [本地目标路径]
     * <p>
     * 授权给ADB访问文件的权限
     * adb shell
     * run-as cn.mvp
     * chmod -R 777 files
     * exit
     * adb pull /sdcard/Android/data/cn.mvp/files [本地目标路径]
     *
     * @param fromPath 源
     * @param toPath   目标
     */

    public static void pullFile(String fromPath, String toPath) {
        if (toPath == null) {
            toPath = "D:/tmp";
        }
        if (!fromPath.contains("/sdcard/")) {
            fromPath = ("/sdcard/" + fromPath);
        }
        fromPath = fromPath.replace("//", "");
        //byte ptext[] = androidFilePath.getBytes("UTF-8");
        //androidFilePath = new String(ptext, "CP1252");

        String fileName = FileUtils.getFileName(fromPath);
        String commandStr = "adb pull \"" + fromPath + "\" \"" + toPath + "\"";
        String s = exeCmd(commandStr);
        System.out.println("adb命令: " + commandStr + " \nadb命令执行结果: " + s + " 文件名:" + fileName);
    }

    /**
     * @return 设备宽高分辨率(单位 : PX)
     */
    public static String getScreenWH() {
        return exeCmd("adb shell wm size");
    }

    /**
     * @return 获取android设备屏幕密度：
     */
    public static String getScreenDensity() {
        return exeCmd("adb shell wm density");
    }

    /**
     * @param x x
     * @param y y
     * @return 修改android设备屏幕密度：
     */
    public static String modifyScreenDensity(int x, int y) {
        return exeCmd(" adb shell wm size " + x + "X" + y);
    }

    /**
     * @return 重置屏幕尺寸
     */
    public static String resetScreenSize() {
        return exeCmd("adb shell wm size reset");
    }

    /**
     * @param size dpi
     * @return 设置屏幕 dpi
     */
    public static String setDpi(int size) {
        return exeCmd("adb shell wm density " + size);
    }

    /**
     * @return 重置 dpi
     */
    public static String retDpi() {
        return exeCmd("adb shell wm density reset");
    }

    /**
     * 在输出中，查找 "init=" 字符串后面的数字，这就是屏幕分辨率 ，例如： init=1080x1920 420dpi
     *
     * @return 设备屏幕详细信息
     */
    public static String getDisplaysDetails() {
        return exeCmd("adb shell dumpsys window displays");
    }

    /**
     * https://blog.csdn.net/yaoyaozaiye/article/details/122826340
     *
     * @param keyCode 键码
     */
    public static void keyCode(int keyCode) {
        exeCmd("adb shell input keyevent " + keyCode);
    }

    /**
     * 截图(暂时不可用)
     * cd /
     * D:
     * md pic
     * SET str="%date:~0,4%-%date:~5,2%-%date:~8,2%"
     * adb shell screencap -p /sdcard/%str%.png
     * adb pull /sdcard/%str%.png D:\pic
     * adb shell rm /sdcard/*.png
     * exit
     */
    public static void screenshot() {

        exeCmd("cd /\n" +
                "D:\n" +
                "md pic\n" +
                "SET str=\"%date:~0,4%-%date:~5,2%-%date:~8,2%\"\n" +
                "adb shell screencap -p /sdcard/%str%.png\n" +
                "adb pull /sdcard/%str%.png D:\\pic\n" +
                "adb shell rm /sdcard/*.png\n" +
                "exit");
    }

    /**
     * 截图
     */
    public static void screenshot1() {
        String absolutePath = new File("").getAbsolutePath() + "/src/main/resources/adb截图.bat";
        String commandStr = "cmd /c start " + absolutePath;
        System.out.println("截图: " + commandStr);
        AdbUtils.exeCmd(commandStr);
        return;
    }

    /**
     * 停止当前app
     */
    public static void stopCurrApp() {
        String topAppPackageName = getTopAppPackageName();
        System.out.println("topAppPackageName = " + topAppPackageName);
        String commandStr = "adb shell am force-stop " + topAppPackageName;
        System.out.println("停止当前app:" + commandStr);
        exeCmd(commandStr);
    }

    /**
     * 开发者模式
     */
    public static void startDevelopmentSettings() {
        exeCmd("adb shell am start -n com.android.settings/com.android.settings.DevelopmentSettings");
    }

    /**
     * 筛选ip
     * https://blog.csdn.net/c1024197824/article/details/120595003
     *
     * @return 命令执行结果
     */
    public static String getIP() {
        String content = exeCmd("adb shell ip addr show wlan0");
        String reg = "inet\\s(\\d+?\\.\\d+?\\.\\d+?\\.\\d+?)/\\d+";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }

    /**
     * @param enable 数据开关
     */
    public static void openPhoneFlow(boolean enable) {
        exeCmd("adb shell svc data " + (enable ? "enable" : "disadle"));
    }


    /**
     * 根据包名查看版本名及版本号
     *
     * @param packageName 包名
     */
    public static void queryCurrAppVersionCode2Name(String packageName) {
        exeCmd("adb shell dumpsys package " + packageName + " | findstr /R /C:\"versionCode\" /C:\"versionName\"");
    }

    /**
     * main
     *
     * @param args arg
     */
    public static void main(String[] args) {
//adb pull  目标系统中的文件路径(a.txt)  本机系统要存放取出来的路径(a.txt)
//adb pull  /sdcard/_电脑传输/"MikelProject Demo-dev.zip"  "D:\tmp\MikelProject Demo-dev.zip"
        String activityDetails = getActivityDetails();
        System.out.println("activityDetails = " + activityDetails);
    }


    /**
     * adb执行回调
     */
    public interface IAdbExeCallBack {
        /**
         * @param content 命令执行结果
         */
        void exeCallBack(String content);
    }
}