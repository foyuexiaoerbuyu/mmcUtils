package org.mmc.util;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述: 文件工具类
 *
 * @author miweiqiang
 */
public class FileUtils {
    /**
     * 定义文件后缀名分隔符
     */
    public final static String FILE_SUFFIX_SEPARATOR = ".";

    /**
     * 定义GBK编码方式
     */
    public final static String ENCODING_GBK = "GBK";
    /**
     * 定义UTF-8编码方式
     */
    public final static String ENCODING_UTF_8 = "UTF-8";

    /**
     * 创建文件夹
     *
     * @param filePath 文件夹路径
     * @return 创建文件夹是否成功
     */
    public static File createDirByPath(String filePath) {
        if (createDir(filePath)) {
            return new File(filePath);
        }
        return null;
    }

    /**
     * 创建文件夹
     *
     * @param filePath 文件夹路径
     * @return 创建文件夹是否成功
     */
    public static boolean createDir(String filePath) {
        File dir = new File(filePath);
        if (dir.exists()) {
//            System.out.println("创建目录" + filePath + " 失败，目标目录已经存在");
            return false;
        }
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            System.out.println("创建目录" + filePath + " 成功！");
            return true;
        } else {
            System.out.println("创建目录" + filePath + " 失败！");
            return false;
        }
    }

    /**
     * 文件是否存在
     *
     * @param filePath 文件路径
     * @return 文件是否存在
     */
    public static boolean exists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

//    /**
//     * 创建文件
//     *
//     * @param filePath 文件路径
//     * @return 创建文件是否成功
//     */
//    public static boolean createFile(String filePath) {
//        File file = new File(filePath);
//        if (!file.exists()) {
//            try {
//                return file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }


    /*----------------------------------------------------------------------------------------------------------------------*/

    /**
     * 移动文件
     *
     * @param from 源
     * @param to   目标
     * @throws Exception 异常
     */
    public static void fileMove(String from, String to) throws Exception {
        try {
            File dir = new File(from);
            File[] files = dir.listFiles();
            if (files == null) {
                return;
            }
            File moveDir = new File(to);
            if (!moveDir.exists()) {
                moveDir.mkdirs();
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    fileMove(file.getPath(), to + "\\" + file.getName());
                    file.delete();
                }
                File moveFile = new File(moveDir.getPath() + "\\"
                        + file.getName());
                if (moveFile.exists()) {
                    moveFile.delete();
                }
                file.renameTo(moveFile);
                System.out.println(file + " 移动成功");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 复制文件到指定文件夹下
     *
     * @param fromFilePath 源文件路径(带文件名)
     * @param toPath       目标路径
     * @param fileName     重命名文件
     */
    public static void copyFileToDir(String fromFilePath, String toPath, String fileName) {
        File file = new File(fromFilePath);
        try {
            FileInputStream input = new FileInputStream(file);
            createDir(toPath);
            String dirFile = toPath + "/" + ("".equals(fileName) ? file.getName() : fileName);
            int index;
            byte[] bytes = new byte[1024];
            FileOutputStream downloadFile = new FileOutputStream(dirFile);
            while ((index = input.read(bytes)) != -1) {
                downloadFile.write(bytes, 0, index);
                downloadFile.flush();
            }
            downloadFile.close();
            input.close();
            System.out.print("复制文件到指定目录下成功");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("复制文件到指定目录下失败");
        }
    }

    /**
     * 文件重命名 FileUtils.reName("F:\\BEYOND - 海阔天空[weiyun].mp3", "海阔天空.mp3");
     *
     * @param filePath 文件路径(带文件后缀名)
     * @param reName   新名称(必须有后缀名)
     */
    public static void reNameSelf(String filePath, String reName) {
        String oldName = getFileName(filePath);
//        String newName = getFileName(reName);
        reName = replaceIllegalStr(getFileName(replaceSeparator(reName)));
        File file1 = new File(filePath);
        if (!file1.exists()) {
            System.out.println("文件不存在: " + file1);
            return;
        }
        System.out.println("file1 = " + file1);
        File file2 = new File(filePath.replace(oldName, reName));
        System.out.println("file2 = " + file2);
        boolean reNameSuccess = file1.renameTo(file2);
        if (reNameSuccess) {
            System.out.println("文件重命名成功");
        } else {
            System.out.println("文件重命名失败");
        }
    }

    /**
     * @param dir 文件夹
     * @return 文件夹下的所有文件路径
     */
    public static String[] listFile(File dir) {// 获取文件绝对路径
        String absolutPath = dir.getAbsolutePath();// 声获字符串赋值为路传入文件的路径
        String[] paths = dir.list();// 文件名数组
        String[] files = new String[paths.length];// 声明字符串数组，长度为传入文件的个数
        for (int i = 0; i < paths.length; i++) {// 遍历显示文件绝对路径
            files[i] = absolutPath + "/" + paths[i];
        }
        return files;
    }

    /**
     * @param filePath 创建文件
     */
    public static void createFile(String filePath) {
        // 创建文件对象
        File file = new File(filePath);
        if (file.exists()) return;
        try {
            // 获取文件所在目录
            File parentDir = file.getParentFile();

            if (!parentDir.exists()) {
                // 如果父目录不存在，则创建父目录
                boolean created = parentDir.mkdirs();
                if (created) {
                    System.out.println("父目录创建成功");
                } else {
                    System.out.println("父目录创建失败");
                }
            }

            // 创建文件
            boolean created = file.createNewFile();
            if (created) {
                System.out.println("文件创建成功");
            } else {
                System.out.println("文件创建失败");
            }
        } catch (IOException e) {
            System.out.println("文件创建失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件夹下所有文件大小或文件大小
     *
     * @param path 文件路径
     */
    public static void getFilesSize(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
//                    int fileSize = (int) (file1.length() / 1024);
                    double fileSize = file1.length() / 1024 / 1024;
                    double bytes = file.length();
                    double kilobytes = (bytes / 1024);
                    double megabytes = (kilobytes / 1024);
                    double gigabytes = (megabytes / 1024);
                    String outInfo = "文件名:[" + file1.getName() + "]   文件大小:[" + fileSize + "兆]";
                    System.out.println(outInfo);
                }
            }
        } else {
//            int fileSize = (int) (file.length() / 1024);
            long fileSize = file.length() / 1024 / 1024;
            String outInfo = "文件名:[" + file.getName() + "]   文件大小:[" + fileSize + "兆]";
            double bytes = file.length();
            double kilobytes = (bytes / 1024);
            double megabytes = (kilobytes / 1024);
            double gigabytes = (megabytes / 1024);
            System.out.println(outInfo);
        }
    }

    /**
     * 递归删除(推荐)
     *
     * @param dir 文件夹
     */
    public static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDir(file);
                }
            }
        }
        boolean delete = dir.delete();
    }

    /**
     * 读取大文件内容为字符串
     *
     * @param file 文件
     * @return 文件内容的字符串表示
     * @throws IOException 如果读取文件时发生 I/O 错误
     */
    public static String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }

        return content.toString();
    }

    /**
     * Read file
     *
     * @param filePath 路径
     * @return 文件内容
     */
    public static String readFile(String filePath) {
        return readFile(filePath, null);
    }

    /**
     * Read file
     *
     * @param charsetName 编码格式
     * @param filePath    路径
     * @return 文件内容
     */
    public static String readFile(String filePath, String charsetName) {
        if (charsetName == null) {
            charsetName = "UTF-8";
        }
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder();
        if (!isFileExist(filePath)) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!isEmpty(fileContent.toString())) {
                    fileContent.append("\n");
                }
                fileContent.append(line);
            }
            return fileContent.toString();
        } catch (IOException e) {
//            throw new RuntimeException("IOException", e);
            return null;
        } finally {
            close(reader);
        }
    }

    /**
     * Write file
     *
     * @param filePath  路径
     * @param byteArray 数据
     * @param append    是否追加
     */
    public static void writeFile(String filePath, byte[] byteArray, boolean append) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileOutputStream fos = new FileOutputStream(file, append); // Open the file in append mode
             FileChannel fileChannel = fos.getChannel()) {
            fileChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("写文件失败,请重试...  " + e.getMessage());
        }
    }

    /**
     * @param filePath 路径
     * @param content  内容
     * @param append   追加
     * @return 是否成功
     */
    public static boolean writeFile(String filePath, String content, boolean append) {
        if (isEmpty(content)) {
            return false;
        }
        BufferedWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(filePath, append),
                            StandardCharsets.UTF_8));
            fileWriter.write(content);
            fileWriter.flush();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            close(fileWriter);
        }
    }

    /**
     * write file, the string will be written to the begin of the file
     *
     * @param filePath 文件路径
     * @param content  内容
     * @return true if success, otherwise false
     */
    public static boolean writeFile(String filePath, String content) {
        return writeFile(filePath, content, false);
    }

    /**
     * @param filePath 文件路径
     * @param is       输入流
     * @return true 或 false
     */
    public static boolean writeFile(String filePath, InputStream is) {
        if (filePath == null) {
            return false;
        }
        return writeFile(filePath, is, false);
    }

    /**
     * 写文件
     *
     * @param filePath 路径
     * @param is       输入流
     * @param append   追加
     * @return true 或 false
     */
    public static boolean writeFile(String filePath, InputStream is, boolean append) {

        return writeFile(filePath != null ? new File(filePath) : null, is, append);
    }

    /**
     * Write file
     *
     * @param file 文件
     * @param is   流
     * @return true 或 false
     */
    public static boolean writeFile(File file, InputStream is) {
        return writeFile(file, is, false);
    }


    /**
     * Write file
     *
     * @param file   .
     * @param is     输入流     .
     * @param append .
     * @return .
     */
    public static boolean writeFile(File file, InputStream is, boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte[] data = new byte[1024];
            int length = -1;
            while ((length = is.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException", e);
        } finally {
            close(o);
            close(is);
        }
    }

    /**
     * 读取源文件内容
     *
     * @param filename String 文件路径
     * @return byte[] 文件内容
     */
    public static byte[] readFile2Byte(String filename) {

        File file = new File(filename);
        if (isEmpty(filename)) {
            throw new NullPointerException("无效的文件路径");
        }
        long len = file.length();
        byte[] bytes = new byte[(int) len];

        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    new FileInputStream(file));
            int r = bufferedInputStream.read(bytes);
            if (r != len) {
                throw new IOException("读取文件不正确");
            }
            bufferedInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;

    }

    /**
     * Move file
     *
     * @param srcFilePath  路径
     * @param destFilePath 目标
     * @throws FileNotFoundException 异常
     */
    public static void moveFile(String srcFilePath, String destFilePath)
            throws FileNotFoundException {
        if (isEmpty(srcFilePath) || isEmpty(destFilePath)) {
            throw new RuntimeException("Both srcFilePath and destFilePath cannot be null.");
        }
        moveFile(new File(srcFilePath), new File(destFilePath));
    }

    /**
     * Move file
     *
     * @param srcFile  路径
     * @param destFile 目标路径
     * @throws FileNotFoundException .
     */
    public static void moveFile(File srcFile, File destFile) throws FileNotFoundException {
        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
            deleteFile(srcFile.getAbsolutePath());
        }
    }

    /**
     * Copy file
     *
     * @param srcFilePath  srcFilePath
     * @param destFilePath destFilePath
     * @return t f
     * @throws FileNotFoundException .
     */
    public static boolean copyFile(String srcFilePath, String destFilePath)
            throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(srcFilePath);
        return writeFile(destFilePath, inputStream);
    }

    /**
     * rename file
     *
     * @param filePath    源文件路径
     * @param newFileName 新文件名(不需要加路径和后缀)
     * @return true 或 false
     */
    public static boolean renameFile(String filePath, String newFileName) {
        return renameFile(new File(filePath), newFileName);
    }

    /**
     * rename file
     *
     * @param file                源文件
     * @param newFileNameNoSuffix 新文件名(不需要加路径和后缀)
     * @return true 或 false
     */
    public static boolean renameFile(File file, String newFileNameNoSuffix) {
        if (!file.exists()) {
            return false;
        }
        if (!file.getName().contains(".")) {
            return file.renameTo(new File(newFileNameNoSuffix));
        }
        if (newFileNameNoSuffix.contains(".")) {
//            newFileNameNoSuffix = getFileName(newFileNameNoSuffix);
//            newFileNameNoSuffix = newFileNameNoSuffix.substring(0, newFileNameNoSuffix.lastIndexOf("."));
            newFileNameNoSuffix = getFileNameWithoutSuffix(newFileNameNoSuffix);
        }
        File newFile = null;
        if (file.isDirectory()) {
            newFile = new File(file.getParentFile(), newFileNameNoSuffix);
        } else {
            String temp = newFileNameNoSuffix + file.getName().substring(file.getName().lastIndexOf('.'));
            newFile = new File(file.getParentFile(), temp);
        }
        boolean renameSuccess = file.renameTo(newFile);
        System.out.println("重命名" + (renameSuccess ? "成功" : "失败"));
        return renameSuccess;
    }

    /**
     * 获取没有后缀的文件名 Get file name without suffix
     *
     * @param filePath .
     * @return 获取没有后缀的文件名
     */
    public static String getFileNameWithoutSuffix(String filePath) {
        filePath = replaceSeparator(filePath);//解决跟路径下返回带路径问题
        if (isEmpty(filePath)) {
            return filePath;
        }
        int suffix = filePath.lastIndexOf(FILE_SUFFIX_SEPARATOR);
        int fp = filePath.lastIndexOf(File.separator);
        if (fp == -1) {
            return (suffix == -1 ? filePath : filePath.substring(0, suffix));
        }
        if (suffix == -1) {
            return filePath.substring(fp + 1);
        }
        return (fp < suffix ? filePath.substring(fp + 1, suffix) : filePath.substring(fp + 1));
    }

    /**
     * 获取文件名(包含后缀) Get file name
     *
     * @param filePath 路径
     * @return 获取文件名(包含后缀)
     */
    public static String getFileName(String filePath) {
        if (isEmpty(filePath)) {
            return filePath;
        }
        int fp = filePath.lastIndexOf(File.separator);
        if (fp == -1) {
            fp = filePath.lastIndexOf("/");
        }
        return (fp == -1) ? filePath : filePath.substring(fp + 1);
    }

    /**
     * Get folder name
     *
     * @param filePath 文件路径
     * @return 文件夹名
     */
    public static String getFolderName(String filePath) {
        if (isEmpty(filePath)) {
            return filePath;
        }
        int fp = filePath.lastIndexOf(File.separator);
        return (fp == -1) ? "" : filePath.substring(0, fp);
    }

    /**
     * 获取文件后缀
     *
     * @param filePath 文件路径
     * @return 文件后缀(不包含 " . ")
     */
    public static String getFileSuffix(String filePath) {
        try {
            if (isEmpty(filePath)) {
                return filePath;
            }
            if (!filePath.contains(FILE_SUFFIX_SEPARATOR)) {
                String[] path = filePath.split(File.separator);
                return path[path.length - 1];
            }
            int suffix = filePath.lastIndexOf(FILE_SUFFIX_SEPARATOR);
            int fp = filePath.lastIndexOf(File.separator);
            if (suffix == -1) {
                return "";
            }
            return (fp >= suffix) ? "" : filePath.substring(suffix + 1);
        } catch (Exception e) {
            System.out.println("没有后缀名: " + filePath);
            return null;
        }
    }

    /**
     * 创建目录
     *
     * @param filePath 路径
     * @return true 或 false
     */
    public static boolean makeDirs(String filePath) {
        if (filePath == null) {
            return false;
        }
        filePath = filePath.replace("\\", File.separator).replace("/", File.separator);
        String folderName = getFolderName(filePath);
        if (isEmpty(folderName)) {
            return false;
        }
        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }

    /**
     * 判断一个文件是否存在
     *
     * @param filePath 路径
     * @return true 或 false
     */
    public static boolean isFileExist(String filePath) {
        if (isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * Judge whether a Directory is exist
     *
     * @param filePath 文件路径
     * @return true 或 false
     */
    public static boolean isFileDirExist(String filePath) {
        if (isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return (file.exists() && file.isDirectory());
    }


    /**
     * 判断文件夹是否存在
     *
     * @param directoryPath 路径
     * @return .
     */
    public static boolean isFolderExist(String directoryPath) {
        if (isEmpty(directoryPath)) {
            return false;
        }
        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    /**
     * @param directoryPath 路径
     * @return true 或 false
     */
    public static boolean isDirExist(String directoryPath) {
        return isFolderExist(directoryPath);
    }

    /**
     * Delete file or folder
     *
     * @param path 路径
     * @return true 或 false
     */
    public static boolean deleteFile(String path) {
        if (isEmpty(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f.isFile()) {
                    f.delete();
                } else if (f.isDirectory()) {
                    deleteFile(f.getAbsolutePath());
                }
            }
        }
        return file.delete();
    }

    /**
     * Delete file or folder
     *
     * @param file 文件
     * @return true 或 false
     */
    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                return file.delete();
            }
            for (File f : childFile) {
                deleteFile(f);
            }
        }
        return file.delete();
    }

    /**
     * Get file size
     *
     * @param path 路径
     * @return true 或 false
     */
    public static long getFileSize(String path) {

        if (isEmpty(path)) {
            return -1;
        }
        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    /**
     * @param path 路径
     *             获取文件大小 带后缀
     * @return true 或 false
     */
    public static String getFileSizeUnit(String path) {
        long size = getFileSize(path);
        double s = (double) size;
        String unit;
        if (size != -1L) {
            int l;
            if (size < 1024L) {
                l = 0;
            } else if (size < 1024L * 1024L) {
                l = 1;
                s = (double) size / 1024L;
            } else {
                for (l = 2; size >= 1024L * 1024L; l++) {
                    size = size / 1024L;
                    if ((size / 1024L) < 1024L) {
                        s = (double) size / 1024L;
                        break;
                    }
                }
            }
            switch (l) {
                case 0:
                    unit = "Byte";
                    break;
                case 1:
                    unit = "KB";
                    break;
                case 2:
                    unit = "MB";
                    break;
                case 3:
                    unit = "GB";
                    break;
                case 4:
//不可能也不该达到的值
                    unit = "TB";
                    break;
                default:
//ER代表错误
                    unit = "ER";
            }
            String format = String.format("%.3f", s);
            return format + unit;
        }
        return null;
    }

    private static boolean isEmpty(CharSequence s) {
        if (s == null) {
            return true;
        } else {
            return s.length() == 0;
        }
    }

    /**
     * 获取文件夹大小
     *
     * @param file .
     * @return .
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
     *
     * @param bytes 字节
     * @return 返回长度信息信息
     */
    public static String bytes2mb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal kilobyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
        return (returnValue + "MB");
    }

    /**
     * @param filePath 文件路径
     * @return 获取到的文件md5值
     */
    public static String getFileMD5(String filePath) {
        return getFileMD5(new File(filePath));
    }

    /**
     * @param file 文件路径
     * @return 获取到的文件md5值
     */
    public static String getFileMD5(File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[8192];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            close(in);
        }
    }
//==================================================================================================

    /**
     * 递归读取文件路径
     *
     * @param path      路径
     * @param readLines 读取
     */
    public static void readDirFiles(String path, ReadFileNames readLines) {
        try {
            readDirFilesExc(path, readLines);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 递归读取文件路径
     *
     * @param path      文件路径
     * @param readLines 读取回调
     * @throws Exception 异常
     */
    public static void readDirFilesExc(String path, ReadFileNames readLines) throws Exception {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            readLines.onReadLine(file.getPath(), file);
        } else if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                readDirFilesExc(files[i].getPath(), readLines);
            }
        }
    }


    /**
     * 获取文件的Sha1值
     *
     * @param file 文件
     * @return 返回sha1值
     */
    public static String getFileSha1(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[1024 * 1024 * 10];

            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                digest.update(buffer, 0, len);
            }
            String sha1 = new BigInteger(1, digest.digest()).toString(16);
            int length = 40 - sha1.length();
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    sha1 = "0" + sha1;
                }
            }
            return sha1;
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 遍历读取文件获取文件内容(主要用于索引文件内的文件是否包含某些关键词)
     *
     * @param path      文件或文件夹路径
     * @param readLines 读取到的行回调
     */
    public static void indexKeyForFile(String path, final ReadLines readLines) {
        readDirFiles(path, new ReadFileNames() {
            @Override
            public void onReadLine(String linStr, File file) {
                readTextFileLines(linStr, new ReadLines() {
                    @Override
                    public boolean onReadLine(int lineIndex, String linStr) {
                        readLines.onReadLine(lineIndex, linStr);
                        return false;
                    }
                });
            }
        });
    }

    /**
     * 逐行读取
     *
     * @param path      路径
     * @param readLines 读取行接口
     */
    public static void readTextFileLines(String path, ReadLines readLines) {
        try {
            File file = new File(path);
            //BufferedReader是可以按行读取文件
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String str = null;
            int lineIndex = 0;
            while ((str = bufferedReader.readLine()) != null) {
                if (readLines != null) {
                    lineIndex++;
                    if (readLines.onReadLine(lineIndex, str)) {
                        break;
                    }
                }
            }
            inputStream.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取文件总行数
     *
     * @param filePath 文件路径
     * @return 文件总行数
     */
    public static int getFlieTotalLines(String filePath) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            LineNumberReader reader = new LineNumberReader(in);
            String s = reader.readLine();
            int lines = 0;
            while (s != null) {
                lines++;
                s = reader.readLine();
            }
            reader.close();
            in.close();
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 方法:按行获取指定内容 使用的时候注意测试,该方法未试验过
     *
     * @param startLine 起始行
     * @param filePath  文件路径
     * @param endLine   结束行
     * @return 指定内容内容
     */
    public static String getSpecifyLinesContent(String filePath, int startLine, int endLine) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(filePath)));
            StringBuilder sb = new StringBuilder();
            String temp = null;
            int count = 0;
            while ((temp = br.readLine()) != null) {
                count++;
                if (count >= startLine && count <= endLine) {
                    sb.append(temp).append("\n");
                }
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "获取失败";
    }

    /**
     * 方法一:按行获取指定内容(至末尾)
     *
     * @param startLine 起始行
     * @param filePath  文件路径
     * @return 指定内容内容
     * @throws Exception .
     */
    public static String getSpecifyContent1(String filePath, int startLine) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
        StringBuilder sb = new StringBuilder();
        String temp = null;
        int count = 0;
        while ((temp = br.readLine()) != null) {
            count++;
            if (count >= startLine) {
                sb.append(temp).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * 替换文件内的文本信息[因为是一行一行读的,所以只能替换单词一类的字符串]
     *
     * @param path     文件路径
     * @param olderArg 旧参数
     * @param newArg   新参数
     * @throws IOException .
     */
    public static void replacetext(String path, String olderArg, String newArg) throws IOException {
        // 读
        File file = new File(path);
        FileReader in = new FileReader(file);
        BufferedReader bufIn = new BufferedReader(in);
        // 内存流, 作为临时流
        CharArrayWriter tempStream = new CharArrayWriter();
        // 替换
        String line = null;
        while ((line = bufIn.readLine()) != null) {
            // 替换每行中, 符合条件的字符串
            line = line.replaceAll(olderArg, newArg);
            // 将该行写入内存
            tempStream.write(line);
            // 添加换行符
            tempStream.append(System.getProperty("line.separator"));
        }
        // 关闭 输入流A
        bufIn.close();
        // 将内存中的流 写入 文件
        FileWriter out = new FileWriter(file);
        tempStream.writeTo(out);
        out.close();
    }

    /**
     * 在文件里面的指定行插入数据[如果插入后要换行的话在插入内容的结尾加  /n ]
     *
     * @param filePath         文件路径
     * @param insertLineNumber 要插入的行号
     * @param lineToBeInserted 要插入的数据
     * @throws Exception IO操作引发的异常
     */
    public static void insertContentToFile(String filePath, int insertLineNumber,
                                           String lineToBeInserted) throws Exception {
        File inFile = new File(filePath);
        // 临时文件
        File outFile = File.createTempFile("name", ".tmp");
        // 输入
        FileInputStream fis = new FileInputStream(inFile);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
        // 输出
        FileOutputStream fos = new FileOutputStream(outFile);
        PrintWriter out = new PrintWriter(fos);
        // 保存一行数据
        String thisLine;
        // 行号从1开始
        int i = 1;
        while ((thisLine = in.readLine()) != null) {
            // 如果行号等于目标行，则输出要插入的数据
            if (i == insertLineNumber) {
                out.println(lineToBeInserted);
            }
            // 输出读取到的数据
            out.println(thisLine);
            // 行号增加
            i++;
        }
        out.flush();
        out.close();
        in.close();
        // 删除原始文件
        inFile.delete();
        // 把临时文件改名为原文件名
        outFile.renameTo(inFile);
    }

    /**
     * Close closeable object 关闭可以关闭的对象
     */
    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
//                LogUtils.d("IOUtils",e.toString());
            }
        }
    }

    /**
     * 扫描路径下的文件
     *
     * @param file 文件路径
     */
    public static void scanningFilesName(File file) {
        if (file == null) {
            return;
        }
        File[] fs = file.listFiles();
        if (fs != null) {
            for (File f : fs) {
                if (f.isDirectory()) {    //若是目录，则递归打印该目录下的文件
                    System.out.println("文件夹:" + f.getPath());
                    scanningFilesName(f);
                }
                if (f.isFile())        //若是文件，直接打印
                {
                    System.out.println("文件:" + f.getPath());
                }
            }
        }
    }

    /**
     * 解决:文件名、目录名或卷标语法不正确。
     *
     * @param dirPath 净化文件名
     * @return 净化后的文件名
     */
    public static String replaceIllegalStr(String dirPath) {
        dirPath = dirPath.replaceAll("[/\\\\:*?|]", ".");
        dirPath = dirPath.replaceAll("[\"<>]", "'");
        return dirPath;
    }

    /**
     * @param dirPath 净化文件名
     * @return 路径是否有非法字符串
     */
    public static String clearPath(String dirPath) {
        return replaceIllegalStr(dirPath);
    }

    /**
     * @param dirPath 路径是否有非法字符串
     * @return 路径是否有非法字符串
     */
    public static boolean isContainIllegalStr(String dirPath) {
        return Pattern.matches("[/\\\\:*?|]", dirPath) || Pattern.matches("[\"<>]", dirPath);
    }

    /**
     * @return 获取桌面路径
     */
    public static String getDesktopPath() {
        return FileSystemView.getFileSystemView().getHomeDirectory().getPath() + File.separator;
    }

    /**
     * java中如何获得操作系统下用户目录
     * Key                     Meaning
     * -------------------     ------------------------------
     * "file.separator"        File separator (e.g., "/")
     * "java.class.path"       Java classpath
     * "java.class.version"    Java class version number
     * "java.home"             Java installation directory
     * "java.vendor"           Java vendor-specific string
     * <p>
     * "java.vendor.url"       Java vendor URL
     * "java.version"          Java version number
     * "line.separator"        Line separator
     * "os.arch"               Operating system architecture
     * "os.name"               Operating system name
     * <p>
     * "path.separator"        Path separator (e.g., ":")
     * "user.dir"              User's current working directory
     * "user.home"             User home directory
     * "user.name"             User account name
     *
     * @param property 参数如上
     * @return 路径或其他
     */
    public static String getSysPath(String property) {
        return System.getProperty(property) + File.separator;
    }

    /**
     * @param filePath 文件路径
     * @param pattern  日期格式
     * @return 创建日期
     */
    public static String getFileCreatDate(String filePath, String pattern) {
        BasicFileAttributes attrs;
        try {
            attrs = Files.readAttributes(Paths.get(filePath), BasicFileAttributes.class);
            FileTime time = attrs.creationTime();
            if (pattern == null) {
                pattern = "yyyy-MM-dd HH:mm:ss";
            }
//            System.out.println("文件创建日期和时间是: " + new SimpleDateFormat(pattern).format(new Date(time.toMillis())));
            return new SimpleDateFormat(pattern).format(new Date(time.toMillis()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param filePath 文件路径
     * @param pattern  日期格式
     * @return 文件修改日期
     */
    public static String getFileModifyDate(String filePath, String pattern) {
        File f = new File(filePath);
        long time = f.lastModified();
        if (pattern == null) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(pattern).format(new Date(time));
    }

    /**
     * @param path path
     * @return 替换路径分隔符后返回的路径
     */
    public static String replaceSeparator(String path) {
        return path.replace("//", File.separator).replace("\\", File.separator).replace("/", File.separator);
    }

    /**
     * 打开文件夹
     *
     * @param path path
     */
    public static void openDir(String path) {

        WinUtils.exeCmd("start " + path);
    }

    /**
     * 移动文件到指定文件夹
     * 如果目标文件夹不存在，程序将自动创建它。如果目标文件夹中已经存在同名文件，程序将覆盖它。
     *
     * @param path      源文件路径
     * @param targetDir 目标文件夹路径
     */
    public static void moveFile2Dir(String path, String targetDir) {
        moveFile2Dir(path, targetDir, false);
    }

    /**
     * 移动文件到指定文件夹
     * 如果目标文件夹不存在，程序将自动创建它。如果目标文件夹中已经存在同名文件，程序将覆盖它。
     *
     * @param path            源文件路径
     * @param targetDir       目标文件夹路径
     * @param replaceExisting 替换
     */
    public static void moveFile2Dir(String path, String targetDir, boolean replaceExisting) {
// 源文件路径
        Path sourcePath = Paths.get(path);
// 目标文件夹路径
        Path targetDirectory = Paths.get(targetDir);
        try {
// 如果目标文件夹不存在，则创建
            if (!Files.exists(targetDirectory)) {
                Files.createDirectories(targetDirectory);
            }
// 将文件移动到目标文件夹
            Path targetPath = targetDirectory.resolve(sourcePath.getFileName());
            if (replaceExisting) {
                // 如果需要替换，使用REPLACE_EXISTING选项
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                // 不替换，如果目标文件已存在，则抛出异常
                Files.move(sourcePath, targetPath);
            }
            System.out.println("文件已成功移动到文件夹");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("移动文件时发生错误");
        }
    }

    /**
     * @param filePath                  文件路径
     * @param iCaliFileReadCallBackBack 回调
     */
    public static void readFileBit(String filePath, IFileReadCallBack iCaliFileReadCallBackBack) {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            // 创建FileInputStream对象并指定要读取的MP4文件路径
            fis = new FileInputStream(filePath);

            // 创建ByteArrayOutputStream对象
            baos = new ByteArrayOutputStream();

            // 读取文件内容到ByteArrayOutputStream中
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                if (iCaliFileReadCallBackBack != null) {
                    iCaliFileReadCallBackBack.read(buffer);
                }
                baos.write(buffer, 0, bytesRead);
            }

            // 将MP4文件内容保存在字节数组中
            byte[] mp4Data = baos.toByteArray();

            // 打印字节数组长度
            System.out.println("MP4文件长度：" + mp4Data.length + " 字节");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解决:保存文件文件名不正确问题
     * 解决:文件名、目录名或卷标语法不正确。
     * <p>
     * \/:?| 用.替换
     * <p>
     * "尖括号 用'替换
     *
     * @param dirPath 文件路径
     * @return 文件名
     */
    public static String clearFileName(String dirPath) {
        return clearFileName(dirPath, false);
    }

    /**
     * windows下文件名中不能含有：\ / : * ? " 尖括号 | 英文的这些字符 ，这里使用"."、"'"进行替换。
     * 解决:保存文件文件名不正确问题
     * 解决:文件名、目录名或卷标语法不正确。
     * <p>
     * \/:?| 用.替换
     * <p>
     * "尖括号用'替换
     *
     * @param dirPath  文件路径
     * @param replHttp 是否替换http链接
     * @return 文件名
     */
    public static String clearFileName(String dirPath, boolean replHttp) {
        dirPath = dirPath.replace("\n", "");
        if (replHttp && dirPath.contains("(http")) {
            dirPath = dirPath.substring(0, dirPath.indexOf("(http")) + dirPath.substring(dirPath.indexOf(")") + 1);
        }
        dirPath = dirPath.replaceAll("[/\\\\:*?|]", " ");
        dirPath = dirPath.replaceAll("[\"<>]", " ");
        return dirPath;
    }

    /**
     * 获取文件编码格式
     *
     * @param filePath 文件路径
     * @return 文件编码格式
     */
    public static String getEncoding(String filePath) {
        return getEncoding(new File(filePath));
    }

    /**
     * 获取文件编码格式
     *
     * @param file 文件
     * @return 编码格式
     */
    public static String getEncoding(File file) {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] b = new byte[3];
            in.read(b);
            in.close();
            if (b[0] == -17 && b[1] == -69 && b[2] == -65)
                System.out.println(file.getName() + "：编码为UTF-8");
            else
                System.out.println(file.getName() + "：可能是GBK，也可能是其他编码");
            return ENCODING_GBK;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ENCODING_UTF_8;
    }

//try {
//            String srcDirPath = "L:\\Users\\19500\\Downloads\\test";
//            // 转为UTF-8编码格式源码路径
//            String utf8DirPath = "L:\\Users\\19500\\Downloads\\test1";
//            // 获取所有java文件
//            Collection<File> javaGbkFileCol = FileUtils.listFiles(new File(srcDirPath), new String[]{"java"}, true);
//
//            for (File javaGbkFile : javaGbkFileCol) {
//                System.out.println(javaGbkFile);
//                // UTF8格式文件路径
//                String utf8FilePath = utf8DirPath + javaGbkFile.getAbsolutePath().substring(srcDirPath.length());
//                // 使用GBK读取数据，然后用UTF-8写入数据
//                FileUtils.writeLines(new File(utf8FilePath), "GBK", FileUtils.readLines(javaGbkFile, "UTF-8"));
//            }
//            com.utils.FileUtils.openDir("path");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    /**
     * 方法二:按行获取指定内容(至末尾)
     *
     * @param filePath  文件路径
     * @param startLine 起始行
     * @return 指定内容内容
     * @throws Exception .
     */
    public String getSpecifyContent2(String filePath, int startLine) throws Exception {
        StringBuilder sb = new StringBuilder();
        LineNumberReader lnr = new LineNumberReader(new FileReader(filePath));
        String buff = lnr.readLine();
        while (buff != null) {
            if (lnr.getLineNumber() >= startLine) {
                sb.append(buff).append("\n");
            }
            buff = lnr.readLine();
        }
        return sb.toString();
    }

    /**
     * 读取文件名接口
     * 该接口用于定义读取文件过程中对每一行文件名的处理逻辑
     */
    public interface ReadFileNames {

        /**
         * 处理单个文件名的回调方法
         *
         * @param filePath 文件路径，指示当前正在读取的文件名
         * @param file     文件对象，提供对文件的直接访问
         * @throws Exception 如果在处理文件名时发生错误，允许抛出异常
         */
        void onReadLine(String filePath, File file) throws Exception;
    }

    /**
     * txt小说按章节分割打印
     * 读取文本文件并使用回调方式处理章节标题和内容
     *
     * @param filePath 文件路径
     * @param encode   编码  StandardCharsets.UTF_8  FileUtils.ENCODING_GBK FileUtils.ENCODING_UTF_8
     * @param callback 回调
     */
    public static void readNovelTextFile(String filePath, String encode, ChapterCallback callback) {

        try {
            readNovelTextFileEx(filePath, encode, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通用回调方法
     */
    public interface IFileReadCallBack {
        /**
         * 读
         *
         * @param buffer .
         */
        void read(byte[] buffer);
    }

    /**
     * @param filePath    java读取文件前五行并保存原文件
     * @param readLineNum 行数
     */
    public static void readFileTop5LineAndSave(String filePath, int readLineNum) {
        try {
            // 读取文件内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
            StringBuilder fileContent = new StringBuilder();
            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append(System.lineSeparator());
                lineCount++;
                if (lineCount == readLineNum) {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(fileContent.toString().trim()), null);// 复制前五行到剪贴板
                    fileContent.setLength(0); // 清空内容
                }
            }
            reader.close();
            // 如果剩余行数小于5，则不保存到源文件
            if (lineCount < readLineNum) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(fileContent.toString().trim()), null);// 复制前五行到剪贴板
                fileContent.setLength(0); // 清空内容
            }
            // 剩余内容保存到源文件
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8));
            writer.write(fileContent.toString().trim());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建副本文件 像windows 一样相同文件名存在时文件名自动+1
     *
     * @param filePath String filePath = "D:\\tmp01\\service/file.txt"; // 文件路径
     * @return 副本文件路径
     */
    private static String creatCopyFile(String filePath, boolean isCreatFile) {
        File file = new File(filePath);

        String baseName = file.getName(); // 获取文件名（包含扩展名）
        String extension = ""; // 扩展名
        int dotIndex = baseName.lastIndexOf(".");
        if (dotIndex != -1) {
            extension = baseName.substring(dotIndex); // 获取扩展名
            baseName = baseName.substring(0, dotIndex);
        }

        int count = 1; // 副本计数

        while (file.exists()) {
            String newFileName = baseName + "_" + count + extension; // 构造新的文件名
            file = new File(file.getParent(), newFileName); // 重新生成文件对象
            count++;
        }
        if (isCreatFile) {
            try {
                // 创建新文件
                if (file.createNewFile()) {
                    System.out.println("文件创建成功：" + file.getAbsolutePath());
                } else {
                    System.out.println("文件创建失败");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getPath();
    }

    /**
     * txt小说按章节分割打印
     * 读取文本文件并使用回调方式处理章节标题和内容
     *
     * @param filePath 文件连接
     * @param encode   编码  StandardCharsets.UTF_8  FileUtils.ENCODING_GBK FileUtils.ENCODING_UTF_8
     * @param callback 回调
     * @throws Exception 如果文件读取失败
     */
    public static void readNovelTextFileEx(String filePath, String encode, ChapterCallback callback) throws Exception {
        if (encode == null || encode.isEmpty()) {
            encode = "UTF-8";
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filePath)), encode));
        String line;
        StringBuilder chapterContent = new StringBuilder();
        String currentTitle = null;

        int number = 0;
        int start = 0;
        int end = 0;

        while ((line = reader.readLine()) != null) {
            if (isChapterTitle(line.trim())) {
                // 如果遇到新的章节标题，则调用回调方法处理上一章节的标题和内容  chapterContent.length()=>防止内容为空
                if (currentTitle != null && chapterContent.length() > 0) {
                    end = start + chapterContent.toString().length();
                    callback.onChapterFound(currentTitle, chapterContent.toString(), number, start, end);
                    number++;
                    start = end;
                    chapterContent.setLength(0); // 清空 chapterContent
                }
                currentTitle = line; // 保存新的章节标题
            } else {
                // 将行内容添加到章节内容中
                chapterContent.append("\t").append(line).append("<br/>");
            }
        }

        // 处理最后一个章节的内容
        if (currentTitle != null) {
            number++;
            end = start + chapterContent.toString().length();
            callback.onChapterFound(currentTitle, chapterContent.toString(), number, start, end);
            start = end + start;
        }
        reader.close();
    }


    /**
     * 提取java文件变量
     *
     * @param path java文件路径
     * @return java文件查找到的所有变量
     */
    public static ArrayList<String> javaExtractVariable(String path) {
        ArrayList<String> fields = new ArrayList<>();
        String[] types = {" string ", " byte ", " short ", " int ", " long ", " float ", " double ", " char ", " boolean ", "list", "[]"};

        FileUtils.readTextFileLines(path, (index, linStr) -> {
            String trim = linStr.trim();
            if (trim.startsWith("package") || trim.startsWith("import") || (trim.contains("this.") && trim.contains("=")) || !trim.endsWith(";")) {
                return false;
            }
            linStr = linStr.toLowerCase();

            for (String type : types) {
                if (linStr.contains(type)) {
                    if ("list".equals(type)) {
                        linStr = linStr.split(">")[1];
                    } else if ("[]".equals(type)) {
                        linStr = linStr.split("]")[1];
                    } else {
                        linStr = linStr.replace(type, "");
                    }
                }
            }
            linStr = linStr.replace(" public ", "")
                    .replace("private", "")
                    .replace(";", "")
                    .toLowerCase();
            if (linStr.contains("=")) {
                linStr = linStr.split("=")[0].trim();
            }
            fields.add(linStr);
            System.out.println("linStr = " + linStr);
            return false;
        });
        return fields;
    }

    /**
     * txt小说按章节分割打印
     * 读取文本文件并使用回调方式处理章节标题和内容
     *
     * @param filePath 文件路径
     * @param callback 回调
     */
    public static void readNovelTextFile(String filePath, ChapterCallback callback) {
        readNovelTextFile(filePath, ENCODING_UTF_8, callback);
    }

    /**
     * 判断是否为章节标题
     *
     * @param line 行
     * @return boolean 是否为章节标题
     */
    public static boolean isChapterTitle(String line) {
//        Pattern pattern = Pattern.compile("第\\d+[章节回]");
//        Pattern pattern = Pattern.compile("第.{1,8}[章节回][\\s\\n]");
//        Pattern pattern = Pattern.compile("第[一二三四五六七八九十百千万零]+[章节回]");
        Pattern pattern = Pattern.compile("第[一二三四五六七八九十百千万零\\d]+[章节回]");
        Matcher matcher = pattern.matcher(line);
        return matcher.find();
    }

    /**
     * 定义回调接口
     */
    public interface ChapterCallback {
        /**
         * 当找到一个章节时调用此方法
         *
         * @param title   title
         * @param content content
         * @param number  number
         * @param start   start
         * @param end     end
         * @throws SQLException .
         */
        void onChapterFound(String title, String content, int number, int start, int end) throws SQLException;
    }

    /**
     * 遍历文件夹
     *
     * @param folder   folder
     * @param callback callback
     */
    public static void traverseFolders(String folder, FolderCallback callback) {
        traverseFolders(new File(folder), callback);
    }

    /**
     * 遍历文件夹
     *
     * @param folder   folder
     * @param callback callback
     */
    private static void traverseFolders(File folder, FolderCallback callback) {
        if (folder == null || !folder.isDirectory()) {
            return;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    callback.onFolderFound(file, file.getAbsolutePath());
                    traverseFolders(file, callback); // 递归调用，遍历子文件夹
                }
            }
        }
    }

    /**
     * 读取指定位置和指定长度的内容，并通过回调函数处理读取到的数据
     *
     * @param filePath 文件路径
     * @param position 读取的起始位置
     * @param length   读取的长度
     * @param callback 数据回调接口
     */
    public static void readBjgFile(String filePath, long position, int length, DataCallback callback) {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            file.seek(position);
            byte[] data = new byte[length];
            int bytesRead = file.read(data);
            if (bytesRead != -1) {
                callback.onDataRead(data, new String(data, 0, bytesRead), bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用于在读取大文件过程中发现文件夹时进行回调的接口
     */
    public interface FolderCallback {
        /**
         * 当发现文件夹时调用的方法
         *
         * @param file       文件对象，代表当前发现的文件夹
         * @param folderPath 文件夹的路径字符串
         */
        void onFolderFound(File file, String folderPath);
    }

    /**
     * 读取大文件的方法，使用缓冲区大小和默认字符编码
     * 此方法重载了另一个具有相同名称的方法，提供了使用默认字符编码的便利
     *
     * @param filePath 文件路径字符串，指定要读取的文件
     * @param bufSize  缓冲区大小，用于指定读取操作的缓冲区大小
     * @param callback 回调接口对象，用于处理读取过程中发现的文件夹
     * @throws IOException 如果读取文件过程中发生I/O错误
     */
    public static void readBigFile(String filePath, int bufSize, Callback callback) throws IOException {
        // 调用重载的方法，指定文件路径、字符编码、缓冲区大小和回调接口
        readBigFile(filePath, "UTF-8", bufSize, callback);
    }

    /**
     * 读取大文件并回调处理函数
     *
     * @param filePath    文件路径
     * @param charsetName 字符编码
     * @param bufSize     缓冲区大小，单位为字节
     * @param callback    回调函数，每次读取到数据后会调用该函数进行处理
     * @throws IOException 如果读取文件失败，则抛出IOException异常
     */
    public static void readBigFile(String filePath, String charsetName, int bufSize, Callback callback) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            byte[] buffer = new byte[bufSize];
            int len;
            while ((len = raf.read(buffer)) != -1) {
                String data = new String(buffer, 0, len, Charset.forName(charsetName));
                callback.onDataRead(data);
            }
        }
    }

    /**
     * Callback interface for reading lines from a file.
     */
    public interface ReadLines {

        /**
         * @param lineIndex 当前行索引
         * @param linStr    行字符串
         * @return 返回false继续读取, 返回true结束读取
         */
        boolean onReadLine(int lineIndex, String linStr);
    }

    /**
     * Callback interface for string data reading.
     * Used to notify the caller of the read string data.
     */
    public interface Callback {
        /**
         * Called when string data is read.
         *
         * @param data The read string data.
         * @throws IOException If an I/O error occurs during data reading.
         */
        void onDataRead(String data) throws IOException;
    }

    /**
     * Callback interface for byte array data reading.
     * In addition to byte array data, it also provides additional string representation and the number of bytes read.
     * Used to notify the caller of the read byte array data and related information.
     */
    public interface DataCallback {
        /**
         * Called when byte array data is read.
         *
         * @param data      The read byte array data.
         * @param str       The string representation of the read data.
         * @param bytesRead The number of bytes read.
         */
        void onDataRead(byte[] data, String str, int bytesRead);
    }

    /**
     * Reader instance for reading data.
     * This BufferedReader instance is used to read data, which is a common resource in the class and can be shared by multiple methods.
     */
    private static BufferedReader reader;

    /**
     * 读取文件指定行的内容
     *
     * @param filePath    文件路径
     * @param lineNumber  需要读取的行号，从1开始
     * @param charsetName "UTF-8", "GBK", "GB2312"
     * @return 指定行的内容，如果行号超出范围返回null
     * @throws IOException 如果文件读取失败抛出异常
     */
    public static String readLineByLineNum(String filePath, int lineNumber, String charsetName) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charsetName))) {
            String line;
            int currentLine = 1;
            while ((line = bufferedReader.readLine()) != null) {
                if (currentLine == lineNumber) {
                    return line;
                }
                currentLine++;
            }
        }
        return null;
    }

    /**
     * 按行读文件:初始化文件路径
     *
     * @param path 文件路径
     */
    public static void readNextLineInit(String path) {
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + path, e);
        }
    }

    /**
     * @return 按行读文件, 读下一行, 掉一次读一行
     */
    public static String readNextLineStart() {
        try {
            if (reader != null) {
                String line = reader.readLine();
                if (line == null) {
                    //读完了
                    close(reader);
                    reader = null;
                }
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
            close(reader);
        }
        return null;
    }

    /**
     * 移动并重命名文件到目标路径
     *
     * @param sourceFilePath 源文件路径
     * @param targetDirPath  目标文件夹路径
     * @param newFileName    新文件名
     * @return true 如果移动成功，false 如果移动失败或目标文件夹不存在
     */
    public static boolean moveAndRenameFile(String sourceFilePath, String targetDirPath, String newFileName) {
        File sourceFile = new File(sourceFilePath);
        File targetDir = new File(targetDirPath);

        // 检查源文件是否存在
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            System.out.println("源文件不存在或不是一个普通文件");
            return false;
        }

        // 检查目标文件夹是否存在
        if (!targetDir.exists() || !targetDir.isDirectory()) {
            System.out.println("目标文件夹不存在");
            return false;
        }

        // 构造目标文件的路径
        String targetFilePath = targetDirPath + File.separator + newFileName;
        File targetFile = new File(targetFilePath);

        // 尝试重命名文件来移动
        if (sourceFile.renameTo(targetFile)) {
            System.out.println("文件移动成功");
            return true;
        } else {
            System.out.println("文件移动失败");
            return false;
        }
    }


    /**
     * 将文件从原始编码转换为目标编码，并保存到新文件夹
     *
     * @param filePath         文件路径
     * @param originalEncoding 原始编码
     * @param targetEncoding   目标编码
     * @throws IOException 如果文件读写过程中发生IO异常
     */
    public static void convertFileEncoding(String filePath, String originalEncoding, String targetEncoding) throws IOException {
        File originalFile = new File(filePath);
        String parentDir = originalFile.getParent();
        File newDir = new File(parentDir + File.separator + "new");
        newDir.mkdirs(); // 创建存放新文件的目录

        String originalFileName = originalFile.getName();
        String newFilePath = newDir.getAbsolutePath() + File.separator + originalFileName;

        // 读取原始文件内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), originalEncoding));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n"); // 保留换行符
        }
        reader.close();

        // 将内容按目标编码转换为字节数组
        byte[] targetBytes = content.toString().getBytes(targetEncoding);

        // 写入到新文件
        FileOutputStream fos = new FileOutputStream(newFilePath);
        fos.write(targetBytes);
        fos.close();

        System.out.println("文件编码转换完成，转换后文件路径：" + newFilePath);
    }


    /**
     * String str1 = "你好，世界！"; // 最后一个字符是汉字但是是符号
     * String str2 = "你好，世界"; // 最后一个字符是汉字且不是符号
     * String str3 = "Hello World!"; // 最后一个字符不是汉字
     * String str4 = "Hello 你好"; // 最后一个字符是汉字且不是符号
     * System.out.println("最后一个字符是否是汉字且不是符号：" + isLastCharacterChineseAndNotSymbol(str1)); // false
     * System.out.println("最后一个字符是否是汉字且不是符号：" + isLastCharacterChineseAndNotSymbol(str2)); // true
     * System.out.println("最后一个字符是否是汉字且不是符号：" + isLastCharacterChineseAndNotSymbol(str3)); // false
     * System.out.println("最后一个字符是否是汉字且不是符号：" + isLastCharacterChineseAndNotSymbol(str4)); // true
     * <p>
     * 判断字符串最后一个字符是否为汉字且不是符号
     *
     * @param str 要判断的字符串
     * @return true 如果最后一个字符是汉字且不是符号，否则返回 false
     */
    public static boolean isLastCharacterChineseAndNotSymbol(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        // 获取最后一个字符
        char lastChar = str.charAt(str.length() - 1);

        // 检查是否是汉字且不是符号
        return isChineseCharacter(lastChar) && !isChineseSymbol(lastChar);
    }

    /**
     * 判断一个字符是否是汉字
     *
     * @param c 要判断的字符
     * @return true 如果是汉字，否则返回 false
     */
    private static boolean isChineseCharacter(char c) {
        // 汉字的 Unicode 范围：[\u4e00-\u9fa5]
        return c >= '\u4e00' && c <= '\u9fa5';
    }

    /**
     * 判断一个字符是否是汉字的符号（标点符号）
     *
     * @param c 要判断的字符
     * @return true 如果是汉字的符号（标点符号），否则返回 false
     */
    private static boolean isChineseSymbol(char c) {
        // 判断是否是汉字的标点符号，参考汉字标点符号的 Unicode 范围
        // 3000-303F 是 CJK 标点符号，FF00-FFEF 是 全角 ASCII 及全角标点符号
        return (c >= '\u3000' && c <= '\u303F') || (c >= '\uFF00' && c <= '\uFFEF');
    }


    /**
     * url文件名编码
     *
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    public static String URLEncoder(String fileName) {
        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
        }
        return fileName;
    }

    /**
     * url文件名编码
     *
     * @param file 文件
     * @return url文件名编码
     */
    public static String URLEncoder(File file) {
        try {
            return URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
        }
        return file.getName();
    }

    /**
     * 移动文件到目标目录
     *
     * @param sourceFilePath     源文件路径
     * @param destinationDirPath 目标目录路径
     * @throws IOException 如果发生 IO 异常
     */
    public static void moveFileToDir(String sourceFilePath, String destinationDirPath) throws IOException {
        File sourceFile = new File(sourceFilePath);
        File destinationDir = new File(destinationDirPath);

        // 检查源文件是否存在
        if (!sourceFile.exists()) {
            throw new IOException("源文件不存在: " + sourceFilePath);
        }

        // 检查目标目录是否存在，如果不存在则创建
        if (!destinationDir.exists()) {
            boolean created = destinationDir.mkdirs();
            if (!created) {
                throw new IOException("无法创建目标目录: " + destinationDirPath);
            }
        }

        // 构建目标文件路径
        Path targetPath = destinationDir.toPath().resolve(sourceFile.getName());

        // 移动文件
        Files.move(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("文件移动成功: " + sourceFilePath + " -> " + targetPath);
    }

    /**
     * 响应内容类型
     *
     * @param fileName 文件名
     * @return 响应内容类型
     */
    public static String getContentType(String fileName) {
        if (fileName.endsWith(".txt")) {
            return "text/plain";
        } else if (fileName.endsWith(".mp4")) {
            return "video/mp4";
        } else if (fileName.endsWith(".mp3") || fileName.endsWith(".m4a")) {
            return "audio/mpeg";
        } else if (fileName.endsWith(".wav")) {
            return "audio/wav";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
            return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        } else if (fileName.endsWith(".zip")) {
            return "application/zip";
        } else if (fileName.endsWith(".rar")) {
            return "application/x-rar-compressed";
        }

        // 添加其他类型的处理
        return null;
    }

    /**
     * 转换文件编码格式
     *
     * @param inputFile  inputFile
     * @param outputFile outputFile
     * @throws IOException ioexception
     */
    private static void convertFileEncoding(File inputFile, File outputFile) throws IOException {
        Charset sourceCharset = Charset.forName("GB2312");
        Charset targetCharset = StandardCharsets.UTF_8;

        // 读取文件内容
        StringBuilder content = new StringBuilder();
        try (RandomAccessFile raf = new RandomAccessFile(inputFile, "r")) {
            byte[] bytes = new byte[(int) raf.length()];
            raf.readFully(bytes);
            content.append(new String(bytes, sourceCharset));
        }

        // 写入到新的文件中
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), targetCharset))) {
            writer.write(content.toString());
        }
    }

    /**
     * 读取文件的前特定行内容 读取前多少行内容
     *
     * @param filePath 文件路径
     * @param numLines 要读取的行数
     * @param charset  文件编码
     * @return 随机读取的行
     * @throws IOException 文件读取异常
     */
    public static String readTopLinesContent(String filePath, int numLines, String charset) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charset))) {
            String line;
            int count = 0;

            // 读取前100行
            while ((line = br.readLine()) != null && count < numLines) {
                sb.append(line);
                count++;
            }
        }


        return sb.toString();
    }

    /**
     * 读取特定字符数内容
     *
     * @param filePath 文件路径
     * @param position 文件中开始读取的位置
     * @param length   读取的字节数
     * @param charset  StandardCharsets.UTF_8; // 字符编码格式  UTF-8
     * @return 读取的字符串
     */
    public static String readTopContent(String filePath, long position, int length, Charset charset) {

        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            file.seek(position); // 移动到指定位置

            byte[] buffer = new byte[length];
            int bytesRead = file.read(buffer, 0, length); // 读取指定长度的字节

            if (bytesRead != -1) {
                String data = new String(buffer, 0, bytesRead, charset); // 根据编码格式解码
                System.out.println("读取的数据: " + data);
                return data;
            } else {
                System.out.println("未能读取数据。");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 线程数
     */
    private static final int THREAD_COUNT = 4;
    /**
     * 缓冲区大小
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     *
     * 多线程读取文件，转换文件编码格式 4线程 1Mb缓存
     *
     * @param inputFile     输入文件 String input = "E:/02code/web/test.txt";
     * @param sourceCharset 源文件编码 String output = "E:/02code/web/tes1t.txt";
     * @param outputFile    输出文件 String sourceCharset = "GBK";
     * @param targetCharset 目标文件编码 String targetCharset = "UTF-8";
     * @throws IOException io
     */
    public static void conversionFileEncodingFormat(File inputFile, String sourceCharset, File outputFile, String targetCharset) throws Exception {
        long fileSize = inputFile.length();
        long chunkSize = fileSize / THREAD_COUNT;

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<File>> futures = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            long start = i * chunkSize;
            long end = (i == THREAD_COUNT - 1) ? fileSize : (start + chunkSize);
            futures.add(executor.submit(new FileReadTask(inputFile, start, end, i, sourceCharset, targetCharset)));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            for (Future<File> future : futures) {
                File tempFile = future.get();
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(tempFile))) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }
                }
                tempFile.delete(); // 删除临时文件
            }
        }
    }

    /**
     * 文件读取任务
     */
    private static class FileReadTask implements Callable<File> {
        private final File inputFile;
        private final long start;
        private final long end;
        private final int index;
        /**
         * 源文件编码
         */
        private final String sourceCharset;
        /**
         * 目标文件编码
         */
        private final String targetCharset;

        public FileReadTask(File inputFile, long start, long end, int index, String sourceCharset, String targetCharset) {
            this.inputFile = inputFile;
            this.start = start;
            this.end = end;
            this.index = index;
            this.sourceCharset = sourceCharset;
            this.targetCharset = targetCharset;
        }

        @Override
        public File call() throws Exception {
            File tempFile = new File("temp_" + index + ".txt");
            try (RandomAccessFile raf = new RandomAccessFile(inputFile, "r");
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), targetCharset))) {

                raf.seek(start);
                byte[] buffer = new byte[BUFFER_SIZE];
                long bytesToRead = end - start;
                while (bytesToRead > 0) {
                    int bytesRead = raf.read(buffer, 0, (int) Math.min(buffer.length, bytesToRead));
                    if (bytesRead == -1) break;
                    String chunk = new String(buffer, 0, bytesRead, sourceCharset); // 假设原文件编码为 ISO_8859_1
                    writer.write(chunk);
                    bytesToRead -= bytesRead;
                }
            }
            return tempFile;
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
     * 获取用户的桌面路径
     *
     * @return 返回桌面的绝对路径，以斜杠结尾
     */
    public static String getDesktop() {
        return Paths.get(System.getProperty("user.home"), "Desktop").toString() + "/";
    }

    /**
     * 获取用户的下载路径
     *
     * @return 返回下载文件夹的绝对路径，以斜杠结尾
     */
    public static String getDownloads() {
        return Paths.get(System.getProperty("user.home"), "Downloads").toString() + "/";
    }

    /**
     * 获取用户的文档路径
     *
     * @return 返回文档文件夹的绝对路径，以斜杠结尾
     */
    public static String getDocuments() {
        return Paths.get(System.getProperty("user.home"), "Documents").toString() + "/";
    }

    /**
     * 获取用户的视频路径
     *
     * @return 返回视频文件夹的绝对路径，以斜杠结尾
     */
    public static String getVideos() {
        return Paths.get(System.getProperty("user.home"), "Videos").toString() + "/";
    }

    /**
     * 获取用户的音乐路径
     *
     * @return 返回音乐文件夹的绝对路径，以斜杠结尾
     */
    public static String getMusic() {
        return Paths.get(System.getProperty("user.home"), "Music").toString() + "/";
    }

    /**
     * 复制一个文件夹下的所有文件及文件夹到到目标位置(不包括根目录)。
     *
     * @param sourceDir 源文件夹路径
     * @param targetDir 目标文件夹路径
     * @throws IOException 如果复制过程中发生I/O错误
     */
    public static void copyDirChildFiles2Dir(String sourceDir, String targetDir) throws IOException {
        Path sourceFolder = Paths.get(sourceDir);
        Path targetFolder = Paths.get(targetDir);

        if (!Files.exists(sourceFolder) || !Files.isDirectory(sourceFolder)) {
            throw new IllegalArgumentException("Source directory does not exist or is not a directory: " + sourceDir);
        }

        // 确保目标文件夹存在
        Files.createDirectories(targetFolder);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceFolder)) {
            for (Path entry : stream) {
                Path targetPath = targetFolder.resolve(sourceFolder.relativize(entry));
                if (Files.isDirectory(entry)) {
                    // 递归复制子文件夹
                    copyDirChildFiles2Dir(entry.toString(), targetPath.toString());
                } else {
                    // 复制文件
                    Files.copy(entry, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to copy folder: " + e.getMessage());
            throw e; // 重新抛出异常以便上层捕获
        }
    }

    /**
     * 读取文件的特定行。
     *
     * @param filePath   文件的路径。
     * @param lineNumber 要读取的行号（从1开始计数）。
     * @return 特定行的内容，如果行号超出范围或发生错误，则返回null。
     */
    public static String readSpecificLine(String filePath, int lineNumber) {
        if (lineNumber < 1) {
            return null; // 行号必须是正数
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentLine = 1;
            while ((line = br.readLine()) != null) {
                if (currentLine == lineNumber) {
                    return line;
                }
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // 行号超出范围或发生错误
    }

    /**
     * 读取文件的所有行，并返回一个包含所有行的列表。
     *
     * @param filePath 文件的路径
     * @return 包含文件所有行的列表
     * @throws IOException 如果读取文件时发生错误
     */
    public static List<String> readAllLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    /**
     * 定义文件大小单位枚举
     */
    public enum SizeUnit {
        /**
         * 1KB等于1024字节
         */
        KB(1024),
        /**
         * 1MB等于1024*1024字节
         */
        MB(1024 * 1024),
        /**
         * 1GB等于1024*1024*1024字节
         */
        GB(1024 * 1024 * 1024);

        // 单位所对应的字节数
        private final long bytes;

        /**
         * 构造函数，初始化单位对应的字节数
         */
        SizeUnit(long bytes) {
            this.bytes = bytes;
        }

        /**
         * @return 获取单位对应的字节数
         */
        public long getBytes() {
            return bytes;
        }
    }

    /**
     * 获取文件大小并根据指定单位返回格式化的字符串。
     *
     * @param file 文件对象
     * @param unit 文件大小单位（KB, MB, GB）
     * @return 格式化的文件大小字符串
     */
    public static String getFileSizeUnit(File file, SizeUnit unit) {
        if (file == null || !file.exists()) {
            return "0 " + unit.name();
        }

        long fileSizeInBytes = file.length();
        double size = (double) fileSizeInBytes / unit.getBytes();

        return String.format("%.2f %s", size, unit.name());
    }

    /**
     * 统计文本文件的行数。
     *
     * @param filePath 文件路径
     * @return 文件行数
     */
    public static int getFileLineNum(String filePath) {
        try {
            return (int) Files.lines(Paths.get(filePath)).count();
        } catch (IOException e) {
            return -1;
        }
//        try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filePath))){
//            lineNumberReader.skip(Long.MAX_VALUE);
//            int lineNumber = lineNumberReader.getLineNumber();
//            return lineNumber + 1;//实际上是读取换行符数量 , 所以需要+1
//        } catch (IOException e) {
//            return -1;
//        }
    }


    /**
     * 文件回调接口
     */
    public interface FileCallback {
        /**
         * 文件回调接口
         *
         * @param file 文件对象
         */
        void onFileFound(File file);
    }

    /**
     * 遍历文件夹下的所有文件文件夹，包括子文件夹中的文件。
     *
     * @param directory 目标文件夹
     * @param callback  文件回调接口
     */
    public static void traverseFiles(String directory, FileCallback callback) {
        /**
         * 遍历文件夹下的所有文件文件夹，包括子文件夹中的文件。
         */
        traverseFiles(new File(directory), callback);
    }

    /**
     * 遍历文件夹下的所有文件文件夹，包括子文件夹中的文件。
     *
     * @param directory 目标文件夹
     * @param callback  文件回调接口
     */
    public static void traverseFiles(File directory, FileCallback callback) {
        if (directory == null || !directory.isDirectory()) {
            return;
        }
        Stack<File> stack = new Stack<>();
        stack.push(directory);
        while (!stack.isEmpty()) {
            File current = stack.pop();
            File[] files = current.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        stack.push(file);
                    }
                    callback.onFileFound(file);
                }
            }
        }
    }

    /**
     * 获取文件夹下的所有文件名，并返回一个排序后的数组。
     *
     * @param folderPath 文件夹路径
     * @return 文件名列表
     */
    public static String[] getSortedFilenames(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            return new String[0];
        }
        File[] files = folder.listFiles();
        if (files == null) {
            return new String[0];
        }
        String[] filenames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                filenames[i] = files[i].getName();
            }
        }
//        Arrays.sort(filenames);
        StringUtil.reverseArray(filenames);
        return filenames;
    }

    /**
     * 处理大文件，使用BufferedReader逐行读取文件内容。
     * @param filePath filePath
     * @param lineProcessor lineProcessor
     * @throws IOException IOException
     */
    public static void processLargeFile(String filePath, Consumer<String> lineProcessor) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineProcessor.accept(line);
            }
        }
    }

    /**
     * 使用NIO读取大文件
     * @param filePath filePath
     * @return 行数据
     * @throws IOException IOException
     */
    public static List<String> readAllLinesNIO(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    }

}