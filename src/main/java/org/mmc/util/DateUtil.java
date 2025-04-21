package org.mmc.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * HH 表示使用的是24小时制的小时，范围是从 00 到 23。例如，15:30:45:123 表示下午3点30分45秒123毫秒。
 * kk 表示的是一种24小时制的小时，但其范围是从 01 到 24。这种格式允许将午夜的 00 点表示为 24。例如，24:30:45:123 表示的是午夜0点30分45秒123毫秒。
 * 使用 HH 表示的小时范围是从 00 到 23，适合大多数场景，包括将午夜表示为 00:00:00。
 * 使用 kk 表示的小时范围是从 01 到 24，允许将午夜表示为 24:00:00。
 * 小写hh标识12小时进制 大写HH标识24小时进制
 *
 *
 */
public class DateUtil {

    /**
     * 日期格式：年-月-日，例如：2024-01-01
     */
    public static final String REGEX_DATE = "yyyy-MM-dd";

    /**
     * 时间格式：时:分，例如：12:30
     */
    public static final String REGEX_TIME = "HH:mm";

    /**
     * 日期时间格式：年-月-日 时:分:秒，例如：2024-01-01 12:30:00
     */
    public static final String REGEX_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期时间格式：年月日_时分秒，例如：20240101_123000
     */
    public static final String REGEX_DATE_TIME_1 = "yyyyMMdd_HHmmss";

    /**
     * 日期时间毫秒格式：年-月-日 时:分:秒:毫秒，例如：2024-01-01 12:30:00:123
     */
    public static final String REGEX_DATE_TIME_MILL = "yyyy-MM-dd HH:mm:ss:SSS";

    /**
     * 中文日期格式：年 月 日，例如：2024年01月01日
     */
    public static final String REGEX_DATE_CHINESE = "yyyy年MM月dd日";

    /**
     * 中文日期时间格式：年 月 日 时:分，例如：2024年01月01日 12:30
     */
    public static final String REGEX_DATE_TIME_CHINESE = "yyyy年MM月dd日 HH:mm";

    // 使用ThreadLocal保证线程安全
    private static ThreadLocal<Map<String, SimpleDateFormat>> FORMATTER_CACHE;

    /**
     * 获取当前日期或时间的字符串
     *
     * @param regex 日期或时间的格式，例如：yyyy-MM-dd
     * @return 格式化后的当前日期或时间字符串
     */
    public static String formatDate(String regex) {
        return getFormatter(regex).format(new Date());
    }

    /**
     * 获取指定日期或时间的字符串
     *
     * @param regex 日期或时间的格式，例如：yyyy-MM-dd
     * @param date  指定的日期对象
     * @return 格式化后的指定日期或时间字符串
     */
    public static String formatDate(String regex, Date date) {
        return getFormatter(regex).format(date);
    }

    /**
     * 获取指定日期或时间的字符串
     *
     * @param regex 日期或时间的格式，例如：yyyy-MM-dd
     * @param year  年份
     * @param month 月份
     * @param day   日期
     * @return 格式化后的指定日期或时间字符串
     */
    public static String formatDate(String regex, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return getFormatter(regex).format(calendar.getTime());
    }

    /**
     * 将一种格式的日期字符串转换为另一种格式的日期字符串
     *
     * @param tagRegex 目标日期格式
     * @param srcRegex 源日期格式
     * @param dateStr  源日期字符串
     * @return 转换后的目标日期字符串，如果解析失败则返回空字符串
     */
    public static String formatDate(String tagRegex, String srcRegex, String dateStr) {
        try {
            Date date = getFormatter(srcRegex).parse(dateStr);
            return getFormatter(tagRegex).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取指定日期或时间的Calendar对象
     *
     * @param regex 日期或时间的格式，例如：yyyy-MM-dd
     * @param date  日期字符串
     * @return 对应的Calendar对象，如果解析失败则返回当前时间的Calendar对象
     */
    public static Calendar getCalendar(String regex, String date) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(getFormatter(regex).parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    /**
     * 将时间戳转换为指定格式的日期字符串
     *
     * @param seconds 时间戳字符串（秒级）
     * @param format  日期格式，默认为yyyy-MM-dd HH:mm:ss
     * @return 转换后的日期字符串，如果时间戳为空则返回空字符串
     */
    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    /**
     * 将毫秒时间格式化为易读的时间字符串
     *
     * @param time 毫秒时间
     * @return 格式化后的时间字符串，例如：1天2小时30分
     */
    public static String formatTime(long time) {
        time = time / 1000;

        //如果时间小于60秒，则返回秒数
        if (time / 60 == 0) {
            return time + "秒";
        }

        //如果时间小于1小时，则返回分钟数
        if (time / (60 * 60) == 0) {
            return (time / 60) + "分" + (time % 60) + "秒";
        }

        //如果时间小于1天，则返回小时数
        if (time / (60 * 60 * 24) == 0) {
            long hour = time / (60 * 60);
            time = time % (60 * 60);
            return hour + "小时" + (time / 60) + "分" + (time % 60) + "秒";
        }

        long day = time / (60 * 60 * 24);
        time = time % (60 * 60 * 24);

        long hour = time / (60 * 60);
        time = time % (60 * 60);

        return day + "天" + hour + "小时" + (time / 60) + "分";
    }

    /**
     * 将日期字符串解析为Date对象
     *
     * @param regex  日期格式，例如：yyyy-MM-dd
     * @param dateStr 日期字符串
     * @return 解析后的Date对象，如果解析失败则抛出异常
     * @throws IllegalArgumentException 如果日期解析失败
     */
    public static Date parseDate(String regex, String dateStr) {
        try {
            return getFormatter(regex).parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期解析失败: " + dateStr + ", 格式应为: " + regex, e);
        }
    }

    /**
     * 判断指定年份是否为闰年
     *
     * @param year 年份
     * @return 如果是闰年返回true，否则返回false
     */
    public static boolean isLeapYear(int year) {
        return Year.of(year).isLeap();
    }

    /**
     * 获取指定年份和月份的最后一天
     *
     * @param year  年份
     * @param month 月份
     * @return 该月的最后一天
     */
    public static int getLastDayOfMonth(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    /**
     * 根据邮件发送日期，返回易读的日期字符串
     *
     * @param regex 日期格式，例如：yyyy-MM-dd
     * @param date  日期字符串
     * @return 易读的日期字符串，如：今天 12:30，昨天 12:30 等
     */
    public static String formatSendDate(String regex, String date) {
        //首先获取到传入的日期和当前日期
        Calendar calendar = getCalendar(regex, date);
        Calendar current = Calendar.getInstance();

        //首先判断是否为当天或者3天以内的邮件
        int currentDay = current.get(Calendar.DAY_OF_YEAR);
        int calendarDay = calendar.get(Calendar.DAY_OF_YEAR);
        //如果是当天的邮件，则返回15:30这样格式的日期
        if (currentDay == calendarDay) {
            return "今天 " + getFormatter(REGEX_TIME).format(calendar.getTime());
        }

        //如果是昨天的邮件，则返回昨天 15:30这样格式的日期
        if (currentDay - calendarDay == 1) {
            return "昨天 " + getFormatter(REGEX_TIME).format(calendar.getTime());
        }

        //如果是前天的邮件，则返回前天 15:30这样格式的日期
        if (currentDay - calendarDay == 2) {
            return "前天 " + getFormatter(REGEX_TIME).format(calendar.getTime());
        }

        //然后判断是否为同一年
        int currentYear = current.get(Calendar.YEAR);
        int calendarYear = calendar.get(Calendar.YEAR);
        //如果不是同一年，则返回年-月-日 时:分:秒这样格式的日期
        if (currentYear != calendarYear) {
            return getFormatter("yyyy-MM-dd HH:mm").format(calendar.getTime());
        }

        //否则返回1月1日 15:02这样格式的日期
        return getFormatter("MM月dd日 HH:mm").format(calendar.getTime());
    }

    /**
     * 获取日期格式化器（线程安全）
     *
     * @param pattern 日期格式
     * @return 对应的SimpleDateFormat对象
     */
    private static SimpleDateFormat getFormatter(String pattern) {
        if (FORMATTER_CACHE == null) {
            FORMATTER_CACHE = ThreadLocal.withInitial(HashMap::new);
        }
        return FORMATTER_CACHE.get().computeIfAbsent(pattern, p -> new SimpleDateFormat(p, Locale.getDefault()));
    }

    /**
     * 计算两个日期之间的天数差
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 两个日期之间的天数差
     */
    public static long daysBetween(Date start, Date end) {
        return Duration.between(
                start.toInstant(),
                end.toInstant()
        ).toDays();
    }

    /**
     * 格式化当前时间为指定格式
     *
     * @param pattern 日期格式
     * @return 格式化后的当前时间字符串
     */
    public static String now(String pattern) {
        return getFormatter(pattern).format(new Date());
    }

    /**
     * 将时间字符串转换为小时数的字符串，保留一位小数
     *
     * @param timeStr 时间字符串，格式为：时:分:秒
     * @return 转换后的小时数字符串
     */
    public static String getHourStr(String timeStr) {
        String[] times = timeStr.split(":");

        int hours = Integer.parseInt(times[0]) * 3600;
        int minutes = Integer.parseInt(times[1]) * 60;
        int seconds = Integer.parseInt(times[2]);

        double time = (hours + minutes + seconds) / 3600.0;

        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(time);
    }

    /**
     * 获取指定日期是星期几的中文表示
     *
     * @param year  年份
     * @param month 月份
     * @param day   日期
     * @return 星期几的中文表示，如：星期日，星期一等
     */
    public static String getWeekStr(int year, int month, int day) {
        String[] arr = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return arr[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    /**
     * 格式化当前日期为默认格式（yyyy-MM-dd HH:mm:ss:SSS）
     *
     * @return 格式化后的当前日期字符串
     */
    public static String formatCurrentDate() {
        return formatCurrentDate(REGEX_DATE_TIME_MILL);
    }

    /**
     * 格式化当前日期为指定格式
     *
     * @param regex 日期格式
     * @return 格式化后的当前日期字符串
     */
    public static String formatCurrentDate(String regex) {
        return formatDate(regex, System.currentTimeMillis());
    }

    /**
     * 格式化指定时间戳为指定格式
     *
     * @param regex 日期格式
     * @param date  时间戳（毫秒或秒级）
     * @return 格式化后的日期字符串
     */
    public static String formatDate(String regex, long date) {
        if (date < 10000000000L) {
            date = date * 1000;
        }
        return getFormatter(regex).format(new Date(date));
    }

    /**
     * 获取当前时间的时间戳字符串
     *
     * @return 时间戳字符串，格式为：yyyyMMddHHmmssSS
     */
    public static String getTimeStamp() {
        return getFormatter("yyyyMMddHHmmssSS").format(new Date(System.currentTimeMillis()));
    }

    /**
     * 格式化LocalDateTime对象为指定格式
     *
     * @param offsetByMonths LocalDateTime对象
     * @param pattern        日期格式
     */
    private static void format(LocalDateTime offsetByMonths, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        // 使用格式化器对 LocalDateTime 对象进行格式化
        String formattedDateTime = offsetByMonths.format(formatter);
    }

    /**
     * 时间偏移单位枚举
     */
    /**
     * 时间偏移量单位枚举类
     * 用于定义时间偏移的单位，包括天、小时、月、分钟和秒
     * 这个枚举类可以帮助开发者以一种类型安全的方式来操作和管理时间偏移单位
     */
    public enum TimeOffsetUnit {
        /**表示天的时间偏移单位*/
        DAYS,
        /**表示小时的时间偏移单位*/
        HOURS,
        /**表示月的时间偏移单位*/
        MONTHS,
        /**表示分钟的时间偏移单位*/
        MINUTES,
        /**表示秒的时间偏移单位*/
        SECONDS
    }

    /**
     * 计算偏移后的时间
     *
     * @param baseTime 基础时间
     * @param offset   偏移量
     * @param unit     偏移单位
     * @return 偏移后的时间
     * @throws IllegalArgumentException 如果偏移单位不支持
     */
    public static LocalDateTime offsetDateTime(LocalDateTime baseTime, int offset, TimeOffsetUnit unit) {
        switch (unit) {
            case DAYS:
                return baseTime.plusDays(offset);
            case HOURS:
                return baseTime.plusHours(offset);
            case MONTHS:
                return baseTime.plusMonths(offset);
            case MINUTES:
                return baseTime.plusMinutes(offset);
            case SECONDS:
                return baseTime.plusSeconds(offset);
            default:
                throw new IllegalArgumentException("不支持的时间偏移单位: " + unit);
        }
    }

    /**
     * 将毫秒时间差转换为易读的时间差字符串
     *
     * @param between 毫秒时间差
     * @return 易读的时间差字符串，如：1天2小时30分40秒50毫秒
     */
    public static String getMsg(long between) {
        long day = between / (24 * 60 * 60 * 1000);
        long hour = (between / (60 * 60 * 1000) - day * 24);
        long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long ms = (between - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
                - min * 60 * 1000 - s * 1000);
        if (day != 0) {
            return (day + "天" + hour + "小时" + min + "分" + s + "秒" + ms + "毫秒");
        } else {
            if (hour != 0) {
                return (hour + "小时" + min + "分" + s + "秒" + ms + "毫秒");
            } else {
                if (min != 0) {
                    return (min + "分" + s + "秒" + ms + "毫秒");
                } else {
                    if (s != 0) {
                        return (s + "秒" + ms + "毫秒");
                    } else {
                        if (ms != 0) {
                            return (ms + "毫秒");
                        } else {
                            return (ms + "毫秒");
                        }
                    }
                }
            }
        }
    }

}
//G　　"公元"
//y　　四位数年份
//M　　月
//d　　日
//h　　时 在上午或下午 (1~12)
//H　　时 在一天中 (0~23)
//m　　分
//s　　秒
//S　　毫秒
//
//
//E　　一周中的周几
//D　　一年中的第几天
//w　　一年中第几个星期
//a　　上午 / 下午 标记符
//k 　　时(1~24)
//K 　   时 在上午或下午 (0~11)
//
//