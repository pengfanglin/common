package com.fanglin.common.util;

import java.util.regex.Pattern;

/**
 * 正则校验工具类
 *
 * @author 彭方林
 * @version 1.0
 * @date 2019/4/16 14:33
 **/
public class RegexUtils {
    /**
     * 邮箱
     */
    private static final Pattern EMAIL = Pattern.compile("\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?");
    /**
     * 身份证号
     */
    private static final Pattern ID_CARD = Pattern.compile("[1-9]\\d{13,16}[a-zA-Z0-9]");
    /**
     * 手机号
     */
    private static final Pattern MOBILE = Pattern.compile("(\\+\\d+)?1[34578]\\d{9}$");
    /**
     * 电话
     */
    private static final Pattern PHONE = Pattern.compile("(\\+\\d+)?(\\d{3,4}-?)?\\d{7,8}$");
    /**
     * 整数
     */
    private static final Pattern DIGIT = Pattern.compile("-?[1-9]\\d+");
    /**
     * 小数
     */
    private static final Pattern DECIMAL = Pattern.compile("-?[1-9]\\d+(\\.\\d+)?");
    /**
     * 中文
     */
    private static final Pattern CHINESE = Pattern.compile("^[u4e00-u9fa5]*$");
    /**
     * 生日
     */
    private static final Pattern BIRTHDAY = Pattern.compile("[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}");
    /**
     * 网址
     */
    private static final Pattern URL = Pattern.compile("(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?");
    /**
     * 邮编
     */
    private static final Pattern POST_CODE = Pattern.compile("[1-9]\\d{5}");
    /**
     * IP地址
     */
    private static final Pattern IP_ADDRESS = Pattern.compile("[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))");

    /**
     * 验证Email
     *
     * @param email email地址，格式：zhangsan@zuidaima.com，zhangsan@xxx.com.cn，xxx代表邮件服务商
     */
    public static boolean checkEmail(String email) {
        return EMAIL.matcher(email).matches();
    }

    /**
     * 验证身份证号码
     *
     * @param idCard 居民身份证号码15位或18位，最后一位可能是数字或字母
     */
    public static boolean checkIdCard(String idCard) {
        return ID_CARD.matcher(idCard).matches();
    }

    /**
     * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
     *
     * @param mobile 移动、联通、电信运营商的号码段
     */
    public static boolean checkMobile(String mobile) {
        return MOBILE.matcher(mobile).matches();
    }

    /**
     * 验证固定电话号码
     *
     * @param phone 电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
     */
    public static boolean checkPhone(String phone) {
        return PHONE.matcher(phone).matches();
    }

    /**
     * 验证整数（正整数和负整数）
     *
     * @param digit 一位或多位0-9之间的整数
     */
    public static boolean checkDigit(String digit) {
        return DIGIT.matcher(digit).matches();
    }

    /**
     * 验证整数和浮点数（正负整数和正负浮点数）
     *
     * @param decimals 一位或多位0-9之间的浮点数，如：1.23，233.30
     */
    public static boolean checkDecimals(String decimals) {
        return DECIMAL.matcher(decimals).matches();
    }

    /**
     * 验证中文
     *
     * @param chinese 中文字符
     */
    public static boolean checkChinese(String chinese) {
        return CHINESE.matcher(chinese).matches();
    }

    /**
     * 验证日期（年月日）
     *
     * @param birthday 日期，格式：1992-09-03，或1992.09.03
     */
    public static boolean checkBirthday(String birthday) {
        return BIRTHDAY.matcher(birthday).matches();
    }

    /**
     * 验证URL地址
     *
     * @param url 格式：http://blog.csdn.net:80/xyang81/article/details/7705960? 或 http://www.csdn.net:80
     */
    public static boolean checkUrl(String url) {
        return URL.matcher(url).matches();
    }

    /**
     * 匹配中国邮政编码
     *
     * @param postcode 邮政编码
     */
    public static boolean checkPostcode(String postcode) {
        return POST_CODE.matcher(postcode).matches();
    }

    /**
     * 匹配IP地址(简单匹配，格式，如：192.168.1.1，127.0.0.1，没有匹配IP段的大小)
     *
     * @param ipAddress IPv4标准地址
     */
    public static boolean checkIpAddress(String ipAddress) {
        return IP_ADDRESS.matcher(ipAddress).matches();
    }

}