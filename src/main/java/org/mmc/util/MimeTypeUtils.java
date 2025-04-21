package org.mmc.util;

/**
 * 媒体类型工具类
 * 该类提供了常见媒体类型的常量定义以及根据媒体类型获取文件扩展名的方法。
 *
 * @author 29975
 */
public class MimeTypeUtils {
    // 定义 PNG 图片的媒体类型
    public static final String IMAGE_PNG = "image/png";
    // 定义 JPG 图片的媒体类型
    public static final String IMAGE_JPG = "image/jpg";
    // 定义 JPEG 图片的媒体类型
    public static final String IMAGE_JPEG = "image/jpeg";
    // 定义 BMP 图片的媒体类型
    public static final String IMAGE_BMP = "image/bmp";
    // 定义 GIF 图片的媒体类型
    public static final String IMAGE_GIF = "image/gif";
    // 定义 PDF 文档的媒体类型
    public static final String DOCUMENT_PDF = "application/pdf";
    // 定义 DOC 文档的媒体类型
    public static final String DOCUMENT_DOC = "application/msword";
    // 定义 DOCX 文档的媒体类型
    public static final String DOCUMENT_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    // 定义 XLS 文档的媒体类型
    public static final String DOCUMENT_XLS = "application/vnd.ms-excel";
    // 定义 XLSX 文档的媒体类型
    public static final String DOCUMENT_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    // 定义 PPT 文档的媒体类型
    public static final String DOCUMENT_PPT = "application/vnd.ms-powerpoint";
    // 定义 PPTX 文档的媒体类型
    public static final String DOCUMENT_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    // 定义通用应用文件的媒体类型
    public static final String APPLICATION_FILE = "application/octet-stream";
    // 定义电子邮件的媒体类型
    public static final String EMAIL = "message/rfc822";

    // 定义图片文件扩展名数组
    public static final String[] IMAGE_EXTENSION = {"bmp", "gif", "jpg", "jpeg", "png"};
    // 定义 Flash 文件扩展名数组
    public static final String[] FLASH_EXTENSION = {"swf", "flv"};
    // 定义媒体文件扩展名数组
    public static final String[] MEDIA_EXTENSION = {"swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg",
            "asf", "rm", "rmvb"};
    // 定义视频文件扩展名数组
    public static final String[] VIDEO_EXTENSION = {"mp4", "avi", "rmvb"};
    // 定义默认允许的文件扩展名数组
    public static final String[] DEFAULT_ALLOWED_EXTENSION = {
            // 图片
            "bmp", "gif", "jpg", "jpeg", "png",
            // word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
            // 压缩文件
            "rar", "zip", "gz", "bz2",
            // 视频格式
            "mp4", "avi", "rmvb",
            // pdf
            "pdf",
            // ofd
            "ofd"};

    /**
     * 根据给定的媒体类型前缀获取对应的文件扩展名。
     *
     * @param prefix 媒体类型前缀，例如 "image/png"
     * @return 对应的文件扩展名，如 "png"；若未匹配到则返回空字符串
     */
    public static String getExtension(String prefix) {
        // 根据不同的媒体类型前缀返回对应的文件扩展名
        switch (prefix) {
            case IMAGE_PNG:
                return "png";
            case IMAGE_JPG:
                return "jpg";
            case IMAGE_JPEG:
                return "jpeg";
            case IMAGE_BMP:
                return "bmp";
            case IMAGE_GIF:
                return "gif";
            // 若未匹配到上述媒体类型，返回空字符串
            default:
                return "";
        }
    }
}