package org.mmc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期格式解析工具类：支持解析多种输入格式（含不完整年月格式），转换为指定输出格式/Date对象
 *
 * @author 编程助手
 */
public class DateFormatUtil {

    // 定义常用的待兼容的日期输入格式（含完整日期+不完整年月格式）
    private static final List<String> COMMON_INPUT_FORMATS = new ArrayList<String>() {{
        // 完整日期格式
        add("yyyy-MM-dd");
        add("yyyy/MM/dd");
        add("yyyy.MM.dd");
        add("yyyy年MM月dd日");
        // 完整日期时间格式
        add("yyyy-MM-dd HH:mm:ss");
        add("yyyy/MM/dd HH:mm:ss");
        add("yyyy.MM.dd HH:mm:ss");
        add("yyyy年MM月dd日 HH时mm分ss秒");
        // 带毫秒的格式
        add("yyyy-MM-dd HH:mm:ss.SSS");
        // 英文格式（如：2025-12-04T10:30:00）
        add("yyyy-MM-dd'T'HH:mm:ss");
        // 简化格式（如：20251204）
        add("yyyyMMdd");
        // 不完整年月格式（基础格式，后续通过正则补全）
        add("yyyy年MM");
        add("yyyy.MM");
        add("yyyyMM");
        add("EEE MMM dd HH:mm:ss zzz yyyy");
        add("yyyy-MM-dd'T'HH:mm:ssXXX");//ISO格式：2026-10-29T15:30:00+08:00
        add("yyyy/MM/dd hh:mm:ss a");//2026/10/29 03:30:00 下午
    }};

    // 定义年月匹配正则（覆盖2025年12、2025年12月、2025.2、2025.02、2023年11月等格式）
    private static final Pattern YEAR_MONTH_PATTERN = Pattern.compile(
            "^(\\d{4})[年\\.](0?[1-9]|1[0-2])(月)?$"
    );

    /**
     * 预处理不完整日期字符串（补全为完整日期格式）
     * 如：2025年12 → 2025年12月01日；2025.2 → 2025.02.01；2023年11月 → 2023年11月01日
     *
     * @param dateStr 待预处理的日期字符串
     * @return 补全后的完整日期字符串，无法补全则返回原字符串
     */
    private static String preprocessIncompleteDate(String dateStr) {
        // 匹配年月格式（2025年12、2025年12月、2025.2、2025.02等）
        Matcher matcher = YEAR_MONTH_PATTERN.matcher(dateStr);
        if (matcher.matches()) {
            String year = matcher.group(1);
            String month = matcher.group(2);
            // 补全月份为两位（如2→02）
            month = month.length() == 1 ? "0" + month : month;

            // 根据原格式补全为完整日期
            if (dateStr.contains("年")) {
                return year + "年" + month + "月01日";
            } else if (dateStr.contains(".")) {
                return year + "." + month + ".01";
            } else {
                return year + month + "01";
            }
        }
        // 无匹配则返回原字符串
        return dateStr;
    }

    // ==================== 新增：返回Date对象的核心方法 ====================

    /**
     * 解析多种格式的日期字符串，返回Date对象（核心新增方法）
     *
     * @param dateStr 待解析的日期字符串（如：2025/12/04、2025年12、2025.2等）
     * @return 解析后的Date对象，解析失败返回null
     */
    public static Date parseDateToDateObject(String dateStr) {
        // 空值校验
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        dateStr = dateStr.trim();

        // 第一步：预处理不完整日期（补全为完整格式）
        String processedDateStr = preprocessIncompleteDate(dateStr);

        // 第二步：遍历所有支持的输入格式，尝试解析
        for (String inputFormat : COMMON_INPUT_FORMATS) {
            try {
                SimpleDateFormat inputSdf;
                if (inputFormat.equals("EEE MMM dd HH:mm:ss zzz yyyy")) {
                    inputSdf = new SimpleDateFormat(inputFormat, Locale.US);
                } else {
                    inputSdf = new SimpleDateFormat(inputFormat, Locale.CHINA);
                }
                // 设置严格解析模式，避免错误匹配（如2025/13/01也能解析的问题）
                inputSdf.setLenient(false);
                return inputSdf.parse(processedDateStr);
            } catch (ParseException e) {
                // 该格式解析失败，继续尝试下一个格式
                continue;
            }
        }

        // 所有格式都解析失败
        return null;
    }

    /**
     * 重载方法：解析日期字符串为Date对象，支持自定义输入格式列表
     *
     * @param dateStr      待解析的日期字符串
     * @param inputFormats 自定义输入格式列表
     * @return 解析后的Date对象，失败返回null
     */
    public static Date parseDateToDateObject(String dateStr, List<String> inputFormats) {
        if (dateStr == null || dateStr.trim().isEmpty() || inputFormats == null || inputFormats.isEmpty()) {
            return null;
        }
        dateStr = dateStr.trim();
        // 预处理不完整日期
        String processedDateStr = preprocessIncompleteDate(dateStr);

        for (String inputFormat : inputFormats) {
            try {
                SimpleDateFormat inputSdf = new SimpleDateFormat(inputFormat, Locale.CHINA);
                inputSdf.setLenient(false);
                return inputSdf.parse(processedDateStr);
            } catch (ParseException e) {
                continue;
            }
        }
        return null;
    }

    // ==================== 原有方法（保持不变） ====================

    /**
     * 核心方法：解析多种格式的日期字符串，转换为指定目标格式
     *
     * @param dateStr      待解析的日期字符串（如：2025/12/04、2025年12、2025.2等）
     * @param targetFormat 目标格式（如：yyyy-MM-dd、yyyy-MM-dd HH:mm:ss）
     * @return 转换后的标准格式日期字符串，解析失败返回null
     */
    public static String parseDateToTargetFormat(String dateStr, String targetFormat) {
        // 先解析为Date对象
        Date date = parseDateToDateObject(dateStr);
        if (date == null || targetFormat == null || targetFormat.trim().isEmpty()) {
            return null;
        }
        // 转换为指定格式字符串
        SimpleDateFormat targetSdf = new SimpleDateFormat(targetFormat, Locale.CHINA);
        return targetSdf.format(date);
    }

    /**
     * 重载方法：默认转换为 yyyy-MM-dd 格式（最常用场景）
     *
     * @param dateStr 待解析的日期字符串
     * @return 转换后的yyyy-MM-dd格式字符串，失败返回null
     */
    public static String parseDateToStandardFormat(String dateStr) {
        return parseDateToTargetFormat(dateStr, "yyyy-MM-dd");
    }

    /**
     * 扩展方法：自定义输入格式列表，解析日期字符串
     *
     * @param dateStr      待解析的日期字符串
     * @param inputFormats 自定义的输入格式列表
     * @param targetFormat 目标格式
     * @return 转换后的日期字符串，失败返回null
     */
    public static String parseDateWithCustomFormats(String dateStr, List<String> inputFormats, String targetFormat) {
        Date date = parseDateToDateObject(dateStr, inputFormats);
        if (date == null || targetFormat == null || targetFormat.trim().isEmpty()) {
            return null;
        }
        SimpleDateFormat targetSdf = new SimpleDateFormat(targetFormat, Locale.CHINA);
        return targetSdf.format(date);
    }

    /**
     * 格式化毫秒时间戳 yyyy-MM-dd HH:mm:ss
     *
     * @return 时间字符串 yyyy-MM-dd HH:mm:ss
     */
    public static String getTimeStr() {
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
        LocalDateTime localDateTime = instant.atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 格式化毫秒时间戳
     *
     * @param timestampMs 13位毫秒
     * @param pattern     格式
     * @return 时间字符串
     */
    public static String formatTs(long timestampMs, String pattern) {
        Instant instant = Instant.ofEpochMilli(timestampMs);
        LocalDateTime localDateTime = instant.atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化毫秒时间戳
     *
     * @param pattern 格式
     * @return 时间字符串
     */
    public static String formatTs(String pattern) {
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
        LocalDateTime localDateTime = instant.atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化毫秒时间戳 yyMMddHHmmss
     *
     * @return 时间字符串 yyMMddHHmmss
     */
    public static String getShortTimestamp() {
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
        LocalDateTime localDateTime = instant.atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
    }

    /**
     * 格式化毫秒时间戳 yyyy-MM-dd
     *
     * @return 时间字符串 yyyy-MM-dd
     */
    public static String getShortDate() {
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
        LocalDateTime localDateTime = instant.atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 格式化毫秒时间戳 yyyyMMdd
     *
     * @return 时间字符串 yyyyMMdd
     */
    public static String getShortDate1() {
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
        LocalDateTime localDateTime = instant.atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    // 测试示例
    public static void main(String[] args) {
//        // 测试完整日期格式
//        String date1 = "2025/12/04";
//        String date7 = "2025.2";
//
//        // ==================== 测试新增的Date对象返回方法 ====================
//        Date dateObj1 = parseDateToDateObject(date1);
//        Date dateObj7 = parseDateToDateObject(date7);
//        System.out.println("解析为Date对象1：" + dateObj1); // 输出：Thu Dec 04 00:00:00 CST 2025
//        System.out.println("解析为Date对象7：" + dateObj7); // 输出：Sat Feb 01 00:00:00 CST 2025
//
//        // 测试自定义格式解析为Date对象
//        List<String> customFormats = new ArrayList<>();
//        customFormats.add("yyyy年MM月dd日");
//        Date dateObj3 = parseDateToDateObject("2025年12月04日", customFormats);
//        System.out.println("自定义格式解析Date对象：" + dateObj3); // 输出：Thu Dec 04 00:00:00 CST 2025
//
//        // ==================== 原有测试逻辑（验证兼容性） ====================
//        System.out.println("转换结果1：" + parseDateToStandardFormat(date1));      // 2025-12-04
//        System.out.println("转换结果7：" + parseDateToStandardFormat(date7));      // 2025-02-01
//
//        // 测试转换为yyyy-MM-dd HH:mm:ss格式
//        String result = parseDateToTargetFormat(date7, "yyyy-MM-dd HH:mm:ss");
//        System.out.println("自定义格式结果（2025.2）：" + result); // 2025-02-01 00:00:00

//
//        // 测试完整日期格式
//        String date1 = "2025/12/04";
//        String date2 = "2025.12.04 10:30:00";
//        String date3 = "2025年12月04日";
//        String date4 = "20251204";
//        String date5 = "2025-12-04T10:30:00";
//
//        // 测试不完整年月格式（核心新增兼容场景）
//        String date6 = "2025年12";
//        String date7 = "2025.2";
//        String date8 = "2025.02";
//        String date9 = "2023年11月";
//        String date10 = "2024年1月";
//
//        // 测试空/Null场景
//        String date11 = "";
//        String date12 = null;
//
//        // 测试无效日期
//        String invalidDate = "2025-13-01";
//        String invalidDate2 = "2025年13";
//
//        // 打印测试结果
//        System.out.println("转换结果1：" + parseDateToStandardFormat(date1));      // 2025-12-04
//        System.out.println("转换结果2：" + parseDateToStandardFormat(date2));      // 2025-12-04
//        System.out.println("转换结果3：" + parseDateToStandardFormat(date3));      // 2025-12-04
//        System.out.println("转换结果4：" + parseDateToStandardFormat(date4));      // 2025-12-04
//        System.out.println("转换结果5：" + parseDateToStandardFormat(date5));      // 2025-12-04
//        System.out.println("转换结果6：" + parseDateToStandardFormat(date6));      // 2025-12-01
//        System.out.println("转换结果7：" + parseDateToStandardFormat(date7));      // 2025-02-01
//        System.out.println("转换结果8：" + parseDateToStandardFormat(date8));    // 2025-02-01
//        System.out.println("转换结果9：" + parseDateToStandardFormat(date9));    // 2023-11-01
//        System.out.println("转换结果10：" + parseDateToStandardFormat(date10));    // 2024-01-01
//        System.out.println("转换结果11：" + parseDateToStandardFormat(date11));      // null
//        System.out.println("转换结果12：" + parseDateToStandardFormat(date12));      // null
//        System.out.println("无效日期1：" + parseDateToStandardFormat(invalidDate));// null
//        System.out.println("无效日期2：" + parseDateToStandardFormat(invalidDate2));// null

        String text = WinUtils.getSysClipboardText();
        String[] split = text.split("\n");
        StringBuffer sb = new StringBuffer();
        for (String s : split) {
            String s1 = parseDateToStandardFormat(s);
            sb.append(s1).append("\n");
        }
        System.out.println(sb);
        WinUtils.setSysClipboardText(sb.toString());

    }
}