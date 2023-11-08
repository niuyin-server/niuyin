package com.qiniu.common.utils.date;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 时间工具类
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算相差天数
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        return Math.abs((int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24)));
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 增加 LocalDateTime ==> Date
     */
    public static Date toDate(LocalDateTime temporalAccessor) {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 增加 LocalDate ==> Date
     */
    public static Date toDate(LocalDate temporalAccessor) {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 友好显示时间
     *
     * @param ms
     * @return
     */
    public static String friendlyDisplayTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分");
        }
        if (second > 0) {
            sb.append(second + "秒");
        }
        if (milliSecond > 0) {
            sb.append(milliSecond + "毫秒");
        }
        return sb.toString();
    }

    public static long todayHhMmSsToMillis(String hhmmss) {
        if (hhmmss == null) {
            throw new IllegalArgumentException("时间格式不能为null" + ",应该是hh:mm:ss的格式");
        }
        String[] strs = hhmmss.split(":");
        if (strs.length != 3) {
            throw new IllegalArgumentException("时间格式不正确：" + hhmmss + ",应该是hh:mm:ss的格式");
        } else {
            return LocalDateTime.of(LocalDate.now(), LocalTime.parse(hhmmss)).atZone(ZoneId.systemDefault()).toInstant()
                    .toEpochMilli();
        }

    }

    public static Long localDate2Long(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static Long localDateTime2Long(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    public static Long localTime2Long(LocalTime localTime) {
        return localTime.toSecondOfDay() * 1000L;
    }

    public static LocalDate long2LocalDate(long timestamp) {
        return new Date(timestamp).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime long2LocalDateTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    public static LocalTime long2LocalTime(long timestamp) {
        return LocalTime.ofSecondOfDay(timestamp / 1000);
    }

    // 一天内剩余的毫秒数
    public static long getLeftMillSecondsInDay() {
        LocalDateTime midnight = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long millSeconds = ChronoUnit.MILLIS.between(LocalDateTime.now(), midnight);
        return millSeconds;
    }

    public static LocalDateTime getTodayPlusStartLocalDateTime(int day) {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        return todayStart.plusDays(day);
    }

    public static LocalDateTime getTodayMinusStartLocalDateTime(int day) {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        return todayStart.minusDays(day);
    }

    public static LocalDateTime getTodayStartLocalDateTime() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        return todayStart;
    }

    public static LocalDateTime getTodayEndLocalDateTime() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return todayStart;
    }

    public static LocalDateTime getEndLocalDateTime(LocalDate localDate) {
        LocalDateTime todayStart = LocalDateTime.of(localDate, LocalTime.MAX);
        return todayStart;
    }

    public static LocalDateTime getEndLocalDateTime(LocalDateTime localDateTime) {
        LocalDateTime todayStart = LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX);
        return todayStart;
    }

    public static LocalDateTime getStartLocalDateTime(LocalDateTime localDateTime) {
        LocalDateTime todayStart = LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIN);
        return todayStart;
    }

    // 获取当天零点时间对象
    public static long getTodayStartLong() {
        return localDateTime2Long(getTodayStartLocalDateTime());
    }

    public static long getTodayEndLong() {
        return localDateTime2Long(getTodayEndLocalDateTime());
    }

    public static long getTodayPlusStartLocalLong(int day) {
        return localDateTime2Long(getTodayPlusStartLocalDateTime(day));
    }

    public static void main(String[] args) {

//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		System.out.println(sdf.format(new Date()));
//		Date date = new Date();
//		System.out.println(sdf.format(date));
//		System.out.println(sdf.format(new Date(date.getTime() + 1000 * 60)));
//
//		System.out.println(long2LocalDateTime(Long.MAX_VALUE));
//        System.out.println(friendlyDisplayTime(10L));
//        System.out.println(friendlyDisplayTime(1000L));
//        System.out.println(friendlyDisplayTime(100000L));
        LocalDateTime localDateTime = getTodayMinusStartLocalDateTime(2);
        System.out.println("localDateTime = " + localDateTime);
    }

}
