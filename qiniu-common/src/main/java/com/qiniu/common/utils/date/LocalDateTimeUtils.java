package com.qiniu.common.utils.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * LocalDateTimeUtils
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/1
 * LocalDateTime工具类
 **/
public class LocalDateTimeUtils {

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取今日0时
     *
     * @return LocalDateTime
     */
    public static LocalDateTime getStartOfDay() {
        return LocalDate.now().atStartOfDay().with(LocalTime.MIN);
    }

    /**
     * 今日23：59：59
     *
     * @return LocalDateTime
     */
    public static LocalDateTime getEndOfDay() {
        return LocalDate.now().atStartOfDay().with(LocalTime.MAX);
    }

    /**
     * 获取明日开始0时
     *
     * @return LocalDateTime
     */
    public static LocalDateTime getStartOfNextDay() {
        return getStartOfDay().plusDays(1).with(LocalTime.MIN);
    }

    /**
     * 获取...天前的0时
     *
     * @return LocalDateTime
     */
    public static LocalDateTime getTimeBeforeDay(long days) {
        return LocalDateTime.now().minusDays(days).with(LocalTime.MIN);
    }

    /**
     * 获取...天前的0时
     *
     * @return LocalDateTime
     */
    public static LocalDateTime getTimeAfterDay(long days) {
        return LocalDateTime.now().plusDays(days).with(LocalTime.MIN);
    }

    /**
     * localDateTime转换为Date
     *
     * @param localDateTime LocalDateTime
     * @return Date
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
