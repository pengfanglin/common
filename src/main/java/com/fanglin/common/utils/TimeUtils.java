package com.fanglin.common.utils;

import com.fanglin.common.core.others.ValidateException;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * @author 彭方林
 * @date 2019/4/2 17:56
 * @version 1.0
 **/
@Slf4j
public class TimeUtils {

    /**
     * SimpleDateFormat对象不是线程安全的，需要放到ThreadLocal中
     */
    private static ThreadLocal<SimpleDateFormat> threadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    /**
     * 获取线程安全的SimpleDateFormat
     */
    public static SimpleDateFormat getSimpleDateFormat() {
        return threadLocal.get();
    }

    /**
     * 字符串转换为Date
     */
    public static Date parse(String dateStr) {
        try {
            return threadLocal.get().parse(dateStr);
        } catch (ParseException e) {
            log.warn(e.getMessage());
            throw new ValidateException(e.getMessage());
        }
    }

    /**
     * Date转化为字符串
     */
    public static String format(Date date) {
        return threadLocal.get().format(date);
    }

    /**
     * 由出生日期获得年龄
     */
    public static int getAge(Date date) {
        Calendar cal = Calendar.getInstance();
        if (cal.before(date)) {
            throw new ValidateException("生日大于当前时间，不合法!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(date);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }

    /**
     * 根据当前日期获得上周的开始时间
     */
    public static String getLastWeekBegin() {
        Calendar calendar1 = Calendar.getInstance();
        int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK) - 1;
        int offset1 = 1 - dayOfWeek;
        calendar1.add(Calendar.DATE, offset1 - 7);
        return threadLocal.get().format(calendar1.getTime());
    }

    /**
     * 根据当前日期获得上周的结束时间
     */
    public static String getLastWeekEnd() {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK) - 1;
        int offset2 = 7 - dayOfWeek;
        calendar2.add(Calendar.DATE, offset2 - 7);
        return threadLocal.get().format(calendar2.getTime());
    }

    /**
     * 今天在当前月中的第几天
     */
    public static int getDayWithMonth() {
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        return ca.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 今天在当前年中的第几天
     */
    public static int getDayWithYear() {
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        return ca.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 当前月最后一天
     */
    public static String getCurMonthLastDay() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        return threadLocal.get().format(ca.getTime());
    }

    /**
     * 2个时间比大小
     */
    public static int compareDate(Date date1, Date date2) {
        return Long.compare(date1.getTime(), date2.getTime());
    }

    /**
     * 2个时间相差天数
     */
    public static int getDayCompareDate(Date date1, Date date2) {
        return getHourCompareDate(date1, date2) / 24;
    }

    /**
     * 2个时间相差小时
     */
    public static int getHourCompareDate(Date date1, Date date2) {
        return getMinCompareDate(date1, date2) / 60;
    }

    /**
     * 2个时间相差分钟
     */
    public static int getMinCompareDate(Date date1, Date date2) {
        long mis = date1.getTime() - date2.getTime();
        return (int) (mis / (60 * 1000));
    }

    /**
     * 获取当前时间
     */
    public static String getCurrentTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    /**
     * 获取当前时间（无参数）
     */
    public static String getCurrentTime() {
        return threadLocal.get().format(new Date());
    }

    /**
     * 获得某个时间后几天的时间
     */
    public static Date getTimeDayAfter(Date date, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * 获得某个时间后几天的时间(毫秒数)
     */
    public static long getMsDayAfter(Date date, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime().getTime();
    }

    /**
     * 获得某个时间后几个小时的时间
     */
    public static Date geTimeHourAfter(Date date, int hour) {
        Calendar dar = Calendar.getInstance();
        dar.setTime(date);
        dar.add(Calendar.HOUR_OF_DAY, hour);
        return dar.getTime();
    }

    /**
     * 获得某个时间后几分钟的时间
     */
    public static Date getTimeMinuteAfter(Date date, int minute) {
        Calendar dar = Calendar.getInstance();
        dar.setTime(date);
        dar.add(Calendar.MINUTE, minute);
        return dar.getTime();
    }

    /**
     * 获取当前日期
     */
    public static String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
