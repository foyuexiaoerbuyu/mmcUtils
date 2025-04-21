package org.mmc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by ray on 2018/1/19.
 * String 字符串相关类方法
 */
public class StringUtil {


    public static String[] split(String str, String regx) {
        if (str == null || !str.contains(regx)) {
            return new String[0];
        }
        return str.split(regx);
    }

    // Equals
    //-----------------------------------------------------------------------

    /**
     * <p>Compares two CharSequences, returning {@code true} if they represent
     * equal sequences of characters.</p>
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null}
     * references are considered to be equal. The comparison is <strong>case sensitive</strong>.</p>
     *
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     *
     * @param cs1 the first CharSequence, may be {@code null}
     * @param cs2 the second CharSequence, may be {@code null}
     * @return {@code true} if the CharSequences are equal (case-sensitive), or both {@code null}
     * @see Object#equals(Object)
     * @since 3.0 Changed signature from equals(String, String) to equals(CharSequence, CharSequence)
     */
    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1.length() != cs2.length()) {
            return false;
        }
        if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
        }
        // Step-wise comparison
        final int length = cs1.length();
        for (int i = 0; i < length; i++) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    //把String转化为float
    public static float convertToFloat(String number, float defaultValue) {
        if (isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(number);
        } catch (Exception e) {
            return defaultValue;
        }

    }

    //把String转化为double
    public static double convertToDouble(String number, double defaultValue) {
        if (isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            return defaultValue;
        }

    }

    //把String转化为int
    public static int convertToInt(String number, int defaultValue) {
        if (isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 字符串是否包含中文
     *
     * @param str 待校验字符串
     * @return true 包含中文字符 false 不包含中文字符
     */
    public static boolean isContainChineseSimple(String str) throws CheckException {

        if (isEmpty(str)) {
            throw new CheckException("字符串不能为空");
        }
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 字符串是否包含中文及中文符号
     *
     * @param str 待校验字符串
     * @return true 包含中文字符 false 不包含中文字符
     */
    public static boolean isContainChinese2symbol(String str) throws CheckException {

        if (isEmpty(str)) {
            throw new CheckException("字符串不能为空");
        }
        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * @return 字符串或空字符串
     */
    public static String str(Object str) {
        return str == null ? "" : str.toString();
    }

    /**
     * @return 字符串或空字符串(返回第一个不为空的字符串)
     */
    public static String str(String... strs) {
        for (String str : strs) {
            if (str != null) {
                return str;
            }
        }
        return "";
    }

    /**
     * @return 忽略大小写比较
     */
    public static boolean equalsIgnoreCase(final String s1, final String s2) {
        return s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);
    }

    /**
     * @return 字符串或空字符串
     */
    public static String strNull(String value) {
        return checkStringIsNull(value) ? "" : value;
    }

    /**
     * 检查是不是null  是不是空串  是不是"null"
     *
     * @param checkStr
     * @return 非null "null" 值
     */
    public static boolean checkStringIsNull(String checkStr) {
        return checkStr == null || checkStr.isEmpty() || checkStr.equals("null");
    }

    /**
     * 去掉所有空格
     *
     * @param str
     * @return
     */
    public static String trim(String str) {
        if (str == null) {
            return "";
        }
        return str.replace(" ", "");
    }


    /**
     * 对数字开头进行补零操作
     *
     * @param number 数字
     * @param length 期望的字符串长度
     * @return 补零后的字符串
     */
    private static String padZero(int number, int length) {
        String numberStr = String.valueOf(number);
        if (numberStr.length() < length) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length - numberStr.length(); i++) {
                sb.append("0");
            }
            sb.append(numberStr);
            return sb.toString();
        } else {
            return numberStr;
        }
    }

    /**
     * 补零方法"两位"
     *
     * @param num 补零个数
     * @return 补零后的字符串
     */
    public static String zeroFill(int num) {
        return String.format("%02d", num);
    }

    public static String subStr(String str, int startIndex, int endIndex) {
        if (str == null || str.length() < endIndex || startIndex > endIndex) {
            return str;
        }
        return str.substring(startIndex, endIndex);
    }


    /**
     * 如果参数为null或者"null"字符串返回""空字符串
     * 注:org.apache.commons.lang.StringUtils#clean效果类似不同点在于"null"字符串不会返回""空字符串
     *
     * @param str 要判断的字符串
     * @return 字符串
     */
    public static String valueOf(Object str) {
        return isBlank(String.valueOf(str)) ? "" : String.valueOf(str);
    }

    /**
     * 如果参数为null或者"null"字符串返回""空字符串
     * 注:org.apache.commons.lang.StringUtils#clean效果类似不同点在于"null"字符串不会返回""空字符串
     *
     * @param str 要判断的字符串
     * @return 字符串
     */
    public static String valueOf(String str) {
        return isBlank(str) ? "" : str;
    }

    /**
     * 判断字符串是否有值，不为空
     */
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格，则返回true，否则则返回false
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格，则返回true，否则则返回false
     */
    public static boolean isEmpty(CharSequence... str) {
        if (str == null) {
            return true;
        }
        for (CharSequence charSequence : str) {
            if (charSequence == null) {
                return true;
            }
        }
        return str.length == 0;
    }

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isBlankNullStr(Object value) {
        return value == null || "".equalsIgnoreCase(value.toString().trim()) || "null".equalsIgnoreCase(value.toString().trim());
    }

    /**
     * <p>Checks if a CharSequence is not empty (""), not null and not whitespace only.</p>
     *
     * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is
     * not empty and not null and not whitespace only
     * @since 2.0
     * @since 3.0 Changed signature from isNotBlank(String) to isNotBlank(CharSequence)
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * <p>Checks if a CharSequence is empty (""), null or whitespace only.</p>
     *
     * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace only
     * @since 2.0
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    public static boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets a CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     *
     * @param cs a CharSequence or {@code null}
     * @return CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     * @since 2.4
     * @since 3.0 Changed signature from length(String) to length(CharSequence)
     */
    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * 一次只能替换一个属性
     *
     * @param resStr      要操作的网页style
     * @param oldStylePrt 样式属性
     * @return 操作后的网页代码
     */
    public static String optionHtmlStyle(String resStr, String oldStylePrt, String newStylePrt) {
        if (resStr.contains(oldStylePrt)) {
            String temp = resStr.substring(resStr.indexOf(oldStylePrt));
            resStr = resStr.replace(temp.substring(0, temp.indexOf(";") + 1), newStylePrt);
        }
        return resStr;
    }

    /**
     * @param str    源字符串("","null","  ",以上三种都为"")
     * @param defVal 默认字符串
     * @return 字符串为空时的默认值
     */
    public static String defaultIfBlank(String str, String defVal) {
        return isBlank(str) ? defVal : str;
    }

    public static String defVal(String val, String defVal) {
        if (val == null) return defVal;
        return val;
    }

    public static int parseInt(String str, int defVal) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    /**
     * @return 去除字符串所有中文
     */
    public static String replaceAllChinese(String str) {
        if (str == null || str.trim().length() == 0) {
            return "";
        }
        //// 中文正则
        return Pattern.compile("[\u4e00-\u9fa5]").matcher(str).replaceAll("");
    }

    /**
     * @return 统计出现次数
     */
    public static int countMatches(String str, String target) {
        int count = 0;
        int index = 0;
        while (index != -1) {
            index = str.indexOf(target, index);
            if (index != -1) {
                count++;
                index += target.length();
            }
        }
        return count;
    }

    /**
     * 统计出现次数
     */
    public static int countMatches(String str, char symbol) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == symbol) {
                count++;
            }
        }
        return count;
    }

    /**
     * 按行读取字符串
     */
    public static void readStrByLinsEx(String str, IReadLines iReadLin) throws IOException, CheckException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        String line;
        int linNum = 0;
        while ((line = br.readLine()) != null) {
            iReadLin.readLin(line, linNum++);
        }
    }

    /**
     * 按行读取字符串 iReadLin 返回true时跳出循环
     */
    public static void readStrByLins(String str, IReadLines iReadLin) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
            String line;
            int linNum = 0;
            while ((line = br.readLine()) != null) {
                iReadLin.readLin(line, linNum++);
            }
        } catch (Exception e) {
            iReadLin.readLinEx(e);
        }
    }

    public static void readStrByLine(String str, IReadLine iReadLine) {
        try (BufferedReader br = new BufferedReader(new StringReader(str))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                if (!iReadLine.readLine(line, ++lineNum)) {
                    break;
                }
            }
        } catch (Exception e) {
            iReadLine.readLineEx(e);
        }
    }

    /**
     * 首字母转大写
     *
     * @param s
     * @return
     */
    public static String firstOneToUpperCase(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    /**
     * 首字母转小写
     *
     * @param s
     * @return
     */
    public static String firstOneToLowerCase(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
        }
    }

    /*索引出现次数*/
    public static int countOccurrences(String text, String searchString) {
        if (text == null || text.isEmpty() || searchString == null || searchString.isEmpty()) {
            return 0;
        }

        int count = 0;
        int index = 0;

        while ((index = text.indexOf(searchString, index)) != -1) {
            count++;
            index += searchString.length();
        }

        return count;
    }

    /**
     * 作用:       去除开头结尾的逗号
     *
     * @param str 要操作的字符串
     * @return 截取后的字符串
     */
    private String subStrComma(String str) {
        if (!"".equals(str) && str != null) {
            if (str.indexOf(",") == 0) {
                String substring = str.substring(1);
                return subStrComma(substring);
            } else if (str.lastIndexOf(",") == str.length() - 1) {
                String substring = str.substring(0, str.length() - 1);
                return subStrComma(substring);
            }
        }
        return str;
    }


    /**
     * 使用正则表达式将多个连续空格替换为单个空格 下划线分割
     *
     * @param replacement 替换连续空格为单个的某个文本
     * @param isTrim      是否先去除开头结尾空格
     */
    public static String replaceContinuousSpaces(String input, String replacement, boolean isTrim) {
        if (isTrim) {
            input = input.trim();
        }
        // 使用正则表达式将多个连续空格替换为单个空格
        String replaced = input.replaceAll("\\s+", " ");
        // 将空格替换为下划线
        replaced = replaced.replaceAll(" ", replacement);
        return replaced;
    }

    public static String subStr(String str, int index) {
        if (str == null || str.length() < index) {
            return str;
        }
        return str.substring(0, index);
    }

    /**
     * 替换连续空格为单空格
     */
    public static String replaceContinuousSpaces_(String input) {
        return replaceContinuousSpaces(input, "_", true);
    }


    /**
     * 处理字段,替换连续空格或空格为下划线的同时大写字母转小写并在之前加下划线
     * 比如:"  wi n   Ut ils " => "wi_n_ut_ils"
     *
     * @param input 待处理字段
     * @return 处理后的字段
     */
    public static String processField(String input) {
        // 去除前后空格
        input = input.trim();

        // 替换连续或不连续空格为下划线
        input = input.replaceAll("\\s+", "_");

        // 在大写字母前加下划线
        StringBuilder output = new StringBuilder();
        boolean previousCharUnderscore = false;
        for (char c : input.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (!previousCharUnderscore) {
                    output.append("_");
                }
                output.append(Character.toLowerCase(c));
                previousCharUnderscore = false;
            } else if (c == '_') {
                if (!previousCharUnderscore) {
                    output.append(c);
                }
                previousCharUnderscore = true;
            } else {
                output.append(Character.toLowerCase(c));
                previousCharUnderscore = false;
            }
        }

        // 移除开头和结尾的下划线
        String result = output.toString();
        if (result.startsWith("_")) {
            result = result.substring(1);
        }
        if (result.endsWith("_")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    /**
     * 去除空行
     */
    public static String removalNullLine(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        StringUtil.readStrByLins(text, (lin, i) -> {
            if (!lin.trim().isEmpty()) {
                sb.append(lin).append("\n");
            }
        });
        return sb.toString();
    }

    /**
     * 下划线转驼峰字符串
     *
     * @param str 下划线字符串
     * @return 驼峰字符串
     */
    public static String underscoreToCamel(String str) {
        StringBuilder result = new StringBuilder();

        if (str != null && !str.isEmpty()) {
            boolean nextUpperCase = false;
            for (int i = 0; i < str.length(); i++) {
                char currentChar = str.charAt(i);

                if (currentChar == '_') {
                    nextUpperCase = true;
                } else {
                    if (nextUpperCase) {
                        result.append(Character.toUpperCase(currentChar));
                        nextUpperCase = false;
                    } else {
                        result.append(Character.toLowerCase(currentChar));
                    }
                }
            }
        }

        return result.toString();
    }

    /**
     * 驼峰转下划线字符串
     *
     * @param str 驼峰字符串
     * @return 下划线字符串
     */
    public static String camelToUnderscore(String str) {
        StringBuilder result = new StringBuilder();

        if (str != null && !str.isEmpty()) {
            for (int i = 0; i < str.length(); i++) {
                char currentChar = str.charAt(i);

                if (Character.isUpperCase(currentChar)) {
                    result.append("_").append(Character.toLowerCase(currentChar));
                } else {
                    result.append(currentChar);
                }
            }
        }

        return result.toString();
    }

    public interface IReadLine {
        /**
         * @return 返回false时跳出循环
         */
        boolean readLine(String line, int lineIndex) throws CheckException;

        default void readLineEx(Exception exception) {
            // 处理异常，例如打印错误信息
            exception.printStackTrace();
        }
    }

    public interface IReadLines {
        void readLin(String lin, int lineIndex) throws CheckException;

        default void readLinEx(Exception exception) {

        }

    }

    /**
     * 去除空格
     */
    public static String removalSpace(String text) {
        return text.replace(" ", "").replace("\t", "");
    }

    /**
     * @param text
     * @param isRemovalSpace 移除空格
     * @return
     */
    public static String mergeOneLine(String text, boolean isRemovalSpace) {
        text = text.replace("\n", "");
        if (isRemovalSpace) {
            text = text.replace(" ", "").replace("\t", "");
        }
        return text;
//        StringBuilder sb = new StringBuilder();
//        StringUtil.readStrByLins(text, lin -> {
//            if (!lin.trim().isEmpty()) {
//                lin.replace(" ", "");
//                lin.replace("   ", "");
//                sb.append(lin);
//            }
//        });
//        return sb.toString();
    }

    /**
     * 格式化json
     */
    public static String formatJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            json = WinUtils.getSysClipboardText();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Object jsonObject = gson.fromJson(json, Object.class);
        return gson.toJson(jsonObject);
    }

    /**
     * 区分大小写的替换方法
     */
    public static String replaceCaseSensitive(String original, String target, String replacement) {
        return original.replace(target, replacement);
    }

    /**
     * 不区分大小写的替换方法
     */
    public static String replaceIgnoreCase(String original, String target, String replacement) {
        return original.replaceAll("(?i)" + target, replacement);
    }

    private static final String[] CN_UPPER_NUMBER = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    private static final String[] CN_UPPER_MONETRAY_UNIT = {"分", "角", "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟"};

    /*
     *人民币金额大小写转换
     * 数字金额大写转换，思想先写个完整的然后将如零拾替换成零 要用到正则表达式
     * https://blog.csdn.net/eric_sunah/article/details/8831445
     * https://gist.github.com/binjoo/6028263
     */
    public static String convertRMB(double n) {

        String fraction[] = {"角", "分"};
        String digit[] = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String unit[][] = {{"元", "万", "亿"}, {"", "拾", "佰", "仟"}};

        String head = n < 0 ? "负" : "";
        n = Math.abs(n);

        String s = "";
        for (int i = 0; i < fraction.length; i++) {
            s += (digit[(int) (Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
        }
        if (s.length() < 1) {
            s = "整";
        }
        int integerPart = (int) Math.floor(n);

        for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
            String p = "";
            for (int j = 0; j < unit[1].length && n > 0; j++) {
                p = digit[integerPart % 10] + unit[1][j] + p;
                integerPart = integerPart / 10;
            }
            s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
        }
        return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
    }

    /**
     * 匹配字符串中的url
     */
    public static String matchLink(String str) {
        //"((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)"  也可以
        Pattern pattern = Pattern.compile("(?i)\\b((?:https?|ftp)://|www\\.)[-a-z0-9+&@#/%?=~_|!:,.;]*[-a-z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }
        return str;
    }

    /**
     * 批量 匹配字符串中的url
     *   allItems = shareService.getAllItems();
     * StringUtil.extractURLs(allItems, Share::getContent, url -> {
     * System.out.println("URL matched: " + url);
     * });
     *
     * @param dataList 数据
     * @param getContentFn 获取内容的方法
     * @param callback 回调
     * @param <T> 数据类型
     */
    public static <T> void matchLinks(List<T> dataList, Function<T, String> getContentFn, URLMatchCallback callback) {
        for (T data : dataList) {
            String content = getContentFn.apply(data);
            Pattern pattern = Pattern.compile("(?i)\\b((?:https?|ftp)://|www\\.)[-a-z0-9+&@#/%?=~_|!:,.;]*[-a-z0-9+&@#/%=~_|]");
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String url = matcher.group();
                callback.onURLMatched(url);
            }
        }
    }

    public interface URLMatchCallback {
        void onURLMatched(String url);
    }


    /**
     * 截取从目标字符串开始到最近的指定字符串前的子字符串
     *
     * @param source       源字符串
     * @param targetStart  目标开始字符串
     * @param endDelimiter 结束标识字符串
     * @return 截取后的子字符串，如果找不到目标开始字符串或结束标识字符串，返回空字符串
     */
    public static String substringBetween(String source, String targetStart, String endDelimiter) {
        if (source == null || targetStart == null || endDelimiter == null) {
            return "";
        }

        int startIndex = source.indexOf(targetStart);
        if (startIndex == -1) {
            return "";
        }

        startIndex += targetStart.length(); // 移动到目标开始字符串之后的位置

        int endIndex = source.indexOf(endDelimiter, startIndex);
        if (endIndex == -1) {
            return source.substring(startIndex);
        }

        return source.substring(startIndex, endIndex);
    }

    // 去除第一行的方法
    public static String removeFirstLine(String text) {
        int index = text.indexOf("\n");
        if (index != -1) {
            return text.substring(index + 1);
        }
        return text;
    }

    /**
     * 最后一个字符是否是汉字
     */
    public static boolean isLastCharChinese(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        // 获取最后一个字符
        char lastChar = str.charAt(str.length() - 1);

        // 判断字符是否在汉字范围内
        return Character.toString(lastChar).matches("[\u4e00-\u9fa5]");
    }

    /**
     * 删除空行
     */
    public static String delEmptyLines(String str) {
        if (str == null) {
            return "";
        }
        return str.replaceAll("(?m)^[ \t]*\r?\n", "");
    }

    /**
     * 包含中文
     */
    public static boolean isHasChinese(String str) {
        return str.matches(".*[\u4e00-\u9fa5]+.*");
    }


    /**
     * 查找并返回字符串中所有的中文字符。
     *
     * @param inputString 输入的字符串
     * @return 包含所有找到的中文字符的列表
     */
    public static List<String> findChineseCharacters(String inputString) {
        // 定义正则表达式，用于匹配中文字符
        String regex = "[\\u4e00-\\u9fa5]+";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 创建Matcher对象
        Matcher matcher = pattern.matcher(inputString);

        // 创建一个列表，用来存放匹配到的中文字符
        List<String> matches = new ArrayList<>();

        // 使用while循环遍历所有匹配项
        while (matcher.find()) {
            // 将每个匹配项添加到列表中
            matches.add(matcher.group());
        }

        // 返回包含所有中文字符的列表
        return matches;
    }

    /**
     * 截取添加省略符号
     *
     * @param length 截取长度
     * @param str    需要截取的字符串
     * @return 截取后的字符串
     */
    public static String omitStr(int length, String str) {
        if (str == null) {
            return "";
        }
        if (str.length() > length) {
            return str.substring(0, length) + "...";
        } else {
            return str;
        }
    }

    /**
     * 在字符串右侧填充指定字符，直到字符串达到指定的长度。
     *
     * @param str     要填充的字符串，可以为 null
     * @param size    字符串最终需要达到的最小长度
     * @param padChar 用来填充的字符
     * @return 填充后的字符串，如果原始字符串为 null，则返回基于 null 的新字符串
     */
    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return String.format("%" + size + "s", Objects.toString(str, "")).replace(' ', padChar);
        } else if (size <= str.length()) {
            return str;
        }
        return String.format("%-" + size + "s", str).replace(' ', padChar);
    }

    /**
     * 在字符串左侧填充指定字符，直到字符串达到指定的长度。
     *
     * @param str     要填充的字符串，可以为 null
     * @param size    字符串最终需要达到的最小长度
     * @param padChar 用来填充的字符
     * @return 填充后的字符串，如果原始字符串为 null，则返回基于 null 的新字符串
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return String.format("%" + size + "s", Objects.toString(str, "")).replace(' ', padChar);
        } else if (size <= str.length()) {
            return str;
        }
        return String.format("%" + size + "s", str).replace(' ', padChar);
    }

    /**
     * 将字符串转换为驼峰式命名
     *
     * @param str                   需要转换的字符串 下划线转驼峰式命名
     * @param capitalizeFirstLetter 首字母大写/小写  true: 首字母大写  false: 首字母小写
     * @return
     */
    public static String toCamelCase(String str, boolean capitalizeFirstLetter) {
        StringBuilder result = new StringBuilder();
        for (String s : str.split("_")) {
            if (s.length() > 0) {
                result.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase());
            }
        }
        if (!capitalizeFirstLetter && result.length() > 0) {
            result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
        }
        return result.toString();
    }

    /**
     * 转驼峰,首字母小写
     * <p>
     * 将连续单词转换为驼峰命名
     *
     * @param input 输入的字符串
     * @return 驼峰命名的字符串
     */
    public static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        input = input.trim();

        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (currentChar == ' ' || currentChar == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(currentChar));
                    nextUpper = false;
                } else {
                    result.append(Character.toLowerCase(currentChar));
                }
            }
        }

        return result.toString();
    }

    /**
     * 转下划线
     * <p>
     * 在单词间增加下划线
     *
     * @param input 输入的字符串
     * @return 带下划线的字符串
     */
    public static String toSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        input = input.trim();

        StringBuilder result = new StringBuilder();
        boolean previousCharWasLowerCase = false;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (Character.isUpperCase(currentChar)) {
                if (previousCharWasLowerCase) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(currentChar));
                previousCharWasLowerCase = false;
            } else if (currentChar == ' ') {
                result.append('_');
                previousCharWasLowerCase = false;
            } else {
                result.append(currentChar);
                previousCharWasLowerCase = Character.isLowerCase(currentChar);
            }
        }

        return result.toString();
    }

    /**
     * 自动转驼峰或下划线
     * <p>
     * 在单词间增加下划线
     *
     * @param input 输入的字符串
     * @return 带下划线的字符串
     */
    public static String autoToCamelSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        if (input.contains("_")) {
            result.append(toCamelCase(input));
        } else {
            result.append(toSnakeCase(input));
        }

        return result.toString();
    }

    /**
     * 移除空行
     *
     * @param input 字符串
     */
    public static String removeEmptyLines(String input) {
        if (input == null) {
            return null;
        }
        // 正则表达式匹配空行（包含仅含空白字符的行）
        return input.replaceAll("(?m)^\\s*$[\n\r]*", "");
    }


    /**
     * 峰命名法字符串转换为用空格分隔的字符串
     * 驼峰连续单子转空格单词
     *
     * @param camelCase 驼峰字符串
     * @return 替换驼峰为空格: AjaxResult => Ajax Result
     */
    public static String camelCaseToSpaced(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                result.append(" ");
            }
            result.append(c);
        }
        return result.toString();
    }

    /**
     * 多行字符串转 list，每行一个元素
     *
     * @param str 输入的多行字符串
     * @return 转换后的列表
     */
    public static List<String> linToList(String str) {
        return linToList(str, false);
    }

    /**
     * 多行字符串转 list，每行一个元素
     *
     * @param str         输入的多行字符串
     * @param hasEmptyLin 是否保留空行
     * @return 转换后的列表
     */
    public static List<String> linToList(String str, boolean hasEmptyLin) {
        if (str == null) {
            return null;
        }
        // 将字符串按换行符分割成数组
        String[] lines = str.split("\n");
        // 使用 ArrayList 存储结果
        List<String> result = new ArrayList<>(Arrays.asList(lines));

        if (!hasEmptyLin) {
            // 使用 Stream API 过滤掉空行
            result = result.stream()
                    .filter(line -> !isEmpty(line))
                    .collect(Collectors.toList());
        }
        return result;
    }

    /**
     * 连续空格转list
     */
    public static List<String> blankToList(String str) {
        // 使用正则表达式 \\s+ 匹配连续的空格
        String[] array = str.split("\\s+");
        return Arrays.asList(array);
    }

    /**
     * @param text        原始完整字符串
     * @param target      要替换的字符串
     * @param replacement 替换后的字符串
     * @return
     */
    public static String ignoreCaseReplace(String text, String target, String replacement) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("(?i)" + target, replacement);
    }

    /**
     * 美化SQL
     */
    public static String formatSQL(String sql) {
//        if (sql.trim().startsWith("stelect ")) {
//        }
        String trim = StringUtil.replaceContinuousSpaces(sql, " ", true).trim();
        trim = trim.replace(",", ",\n   ");
        trim = trim.replace(" from ", " \nFROM  \n  ");
        trim = ignoreCaseReplace(trim, "left join", "\n  LEFT JOIN");
        trim = ignoreCaseReplace(trim, "right join", "\n    RIGHT JOIN");
        trim = ignoreCaseReplace(trim, " where ", "\nWHERE\n    ");
        trim = ignoreCaseReplace(trim, " and ", "\n    AND ");
        // 使用正则表达式去除空行
//        trim = trim.replaceAll("(?m)^[ \t]*\r?\n", "");

        sql = removalNullLine(trim);
        System.out.println(sql);

        return sql;
    }


    /**
     * 根据数据库类型获取Java类型
     *
     * @param sqlType 数据库字段类型
     * @return
     */
    public static String getJavaType(String sqlType) {
        switch (sqlType.toUpperCase()) {
            case "VARCHAR":
            case "CHAR":
            case "TEXT":
            case "LONGTEXT":
                return "String";
            case "INT":
            case "INTEGER":
                return "Integer";
            case "BIGINT":
                return "Long";
            case "FLOAT":
                return "Float";
            case "DOUBLE":
                return "Double";
            case "DECIMAL":
            case "NUMERIC":
                return "java.math.BigDecimal";
            case "DATE":
            case "DATETIME":
            case "TIMESTAMP":
                return "java.util.Date";
            case "BOOLEAN":
            case "BIT":
                return "Boolean";
            case "BLOB":
            case "LONGBLOB":
                return "byte[]";
            default:
                return "Object";
        }
    }

    /**
     * 首字母缩写
     * 提取字符串中所有单词的首字母并转换为小写
     * 可以处理下划线分隔和驼峰式命名的混合字符串
     * 获取多个单词首字母并拼接(缩写多个单词)
     * 获取字符串的缩写（提取每个单词的首字母）
     *
     * @param input 输入字符串（如 "tbl_enterprise_basic TextsSc"）
     * @return 首字母小写组合（如 "tebts"）
     */
    public static String extractInitialsLowerCase(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // 标准化输入：替换所有非字母数字字符为空格，并处理驼峰式命名
        String normalized = input.replaceAll("[^a-zA-Z0-9]", " ")
                .replaceAll("([a-z])([A-Z])", "$1 $2")
                .toLowerCase();

        // 分割成单词
        String[] words = normalized.split("\\s+");

        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(word.charAt(0));
            }
        }

        return result.toString();
    }


    /**
     * 反转字符串数组
     *
     * @param array
     */
    public static void reverseArray(String[] array) {
        int left = 0;
        int right = array.length - 1;
        while (left < right) {
            String temp = array[left];
            array[left] = array[right];
            array[right] = temp;
            left++;
            right--;
        }
    }

    /**
     * 如果list为null，返回空List
     *
     * @param list 输入列表
     * @param <T>  泛型类型
     * @return 非null的List
     */
    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 如果set为null，返回空Set
     *
     * @param set 输入Set
     * @param <T> 泛型类型
     * @return 非null的Set
     */
    public static <T> Set<T> emptyIfNull(Set<T> set) {
        return set == null ? Collections.emptySet() : set;
    }

    /**
     * 如果map为null，返回空Map
     *
     * @param map 输入Map
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 非null的Map
     */
    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }

    /**
     * 如果collection为null，返回空List
     *
     * @param collection 输入集合
     * @param <T>        泛型类型
     * @return 非null的List
     */
    public static <T> List<T> toList(Collection<T> collection) {
        return collection == null ? Collections.emptyList() : new ArrayList<>(collection);
    }

    /**
     * 安全转换集合为指定类型的List
     *
     * @param collection 输入集合
     * @param mapper     类型转换函数
     * @param <T>        输入类型
     * @param <R>        输出类型
     * @return 非null的List
     */
    public static <T, R> List<R> toList(Collection<T> collection, Function<T, R> mapper) {
        if (collection == null) {
            return Collections.emptyList();
        }
        return collection.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * 如果数组为null，返回空数组
     *
     * @param array 输入数组
     * @param <T>   数组元素类型
     * @return 非null的数组
     */
    public static <T> T[] emptyIfNull(T[] array) {
        return array == null ? (T[]) new Object[0] : array;
    }

    /**
     * 将数组转换为List，如果数组为null返回空List
     *
     * @param array 输入数组
     * @param <T>   数组元素类型
     * @return 非null的List
     */
    public static <T> List<T> toList(T[] array) {
        return array == null ? Collections.emptyList() : Arrays.asList(array);
    }

}
