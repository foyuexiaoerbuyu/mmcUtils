package org.mmc.util;//package com.ramostear.application.util;//package com.utils;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.core.env.Environment;
//import org.springframework.util.StringUtils;
//
//import java.lang.management.ManagementFactory;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;

//public class SpringbootUtils {
//
//    /**
//     * 控制台打印浏览器访问地址
//     */
//    public static void printServiceUrl(ConfigurableApplicationContext application) {
//        System.out.println(
//                "  ____  __  __    ___    ___     __    ____    ____  \n" +
//                        " /',__\\/\\ \\/\\ \\  /'___\\ /'___\\ /'__`\\ /',__\\  /',__\\ \n" +
//                        "/\\__, `\\ \\ \\_\\ \\/\\ \\__//\\ \\__//\\  __//\\__, `\\/\\__, `\\\n" +
//                        "\\/\\____/\\ \\____/\\ \\____\\ \\____\\ \\____\\/\\____/\\/\\____/\n" +
//                        " \\/___/  \\/___/  \\/____/\\/____/\\/____/\\/___/  \\/___/ \n");
//        Environment env = application.getEnvironment();
//        String ip = null;
//        try {
//            ip = InetAddress.getLocalHost().getHostAddress();
//            String port = env.getProperty("server.port");
//            String path = env.getProperty("server.servlet.context-path");
//            if (StringUtils.isEmpty(path)) {
//                path = "";
//            }
//            System.out.println("\n----------------------------------------------------------\n\t" +
//                    "Application  is running! Access URLs:\n\t" +
//                    "Local访问网址: \t\thttp://localhost:" + port + path + "\n\t" +
//                    "External访问网址: \thttp://" + ip + ":" + port + path + "\n\t" +
//                    "----------------------------------------------------------");
//            String jvmName = ManagementFactory.getRuntimeMXBean().getName();
//            System.out.println("当前项目进程号：" + jvmName.split("@")[0]);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 停止服务
//     */
//    public static void shutdown(ConfigurableApplicationContext application) {
//        SpringApplication.exit(application, () -> 0);
//    }
//
//    /**
//     * 打开浏览器
//     */
//    public static void open(String url) throws RuntimeException {
////        try {
////            Desktop desktop = Desktop.getDesktop();
////            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE))
////                desktop.browse(new URI(url));
////        } catch (Exception e) {
////            e.printStackTrace();
//        try {
//            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
//        } catch (Exception ioException) {
//            ioException.printStackTrace();
//            throw new RuntimeException("打开浏览器失败");
//        }
////        }
//    }
//
//    public static String getIP() {
//
//        try {
//            return InetAddress.getLocalHost().getHostAddress();
////            Runtime.getRuntime().exec("cmd   /c   start   http://" + hostAddress + ":8088/files");//可以指定自己的路径
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return "127.0.0.1";
//        }
//    }
//
//    public static void openUrl(String url) {
//        try {
//            Runtime.getRuntime().exec("cmd /c start " + url);
//        } catch (Exception ex) {
//        }
//    }
//
//    public static String getClientIpAddress(HttpServletRequest request, String logMsg) {
//        System.out.println("ComUtils#getClientIpAddress = " + logMsg);
//        String ipAddress = request.getHeader("X-Forwarded-For");
//
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("Proxy-Client-IP");
//        }
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
//        }
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("HTTP_X_FORWARDED");
//        }
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
//        }
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("HTTP_CLIENT_IP");
//        }
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("HTTP_FORWARDED_FOR");
//        }
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("HTTP_FORWARDED");
//        }
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("HTTP_VIA");
//        }
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("REMOTE_ADDR");
//        }
//        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getRemoteAddr();
//        }
//
//        // 如果客户端经过代理，X-Forwarded-For可以包含多个IP，第一个为真实IP
//        if (ipAddress != null && ipAddress.contains(",")) {
//            ipAddress = ipAddress.split(",")[0].trim();
//        }
//        System.out.println("ipAddress = " + ipAddress);
//        return ipAddress;
//    }

///**
// * 获取公网IP
// *
// * @return 公网ip地址
// */
//public static String getPublicIp() {
//    String ip = "";
////        OkHttpClient okHttpClient = new OkHttpClient();
////        Request request = new Request.Builder()
////                .url("https://ipinfo.io/ip")
////                .build();
////        try {
////            Response response = okHttpClient.newCall(request).execute();
////            if (response.isSuccessful()) {
////                if (response.body() != null) {
////                    String requestBody = response.body().string();
////                    Log.i("MainActivity", "requestBody length is " + requestBody.length());
////                    ip = requestBody;
////                    return ip;
////                }
////            }
////        } catch (Exception e) {
////            Log.e("MainActivity", "ipAddress is null exception is " + e);
////        }
////    try {
////        // 创建URL对象
////        URL url = new URL("https://ipinfo.io/ip");
////        // 打开连接
////        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
////        // 设置请求方法为GET（这是默认的，但明确设置更好）
////        connection.setRequestMethod("GET");
////        // 设置一些请求头，可选，但对于一些服务端可能需要合理设置
////        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
////
////        // 获取响应状态码
////        int responseCode = connection.getResponseCode();
////        if (responseCode == HttpURLConnection.HTTP_OK) {
////            // 读取响应内容
////            BufferedReader in = new BufferedReader(new InputStreamReader(
////                    connection.getInputStream()));
////            String inputLine;
////            StringBuilder response = new StringBuilder();
////            while ((inputLine = in.readLine()) != null) {
////                response.append(inputLine);
////            }
////            in.close();
////            // 输出返回的字符串（这里就是IP地址相关内容）
////            System.out.println(ip = response.toString());
////        } else {
////            System.out.println("请求失败，状态码: " + responseCode);
////        }
////        // 关闭连接
////        connection.disconnect();
////    } catch (IOException e) {
////        e.printStackTrace();
////    }
////    return ip;
////}
//}
