package com.qiniu.common.utils;

import cn.hutool.core.lang.UUID;

/**
 * ID生成器工具类
 *
 * @author roydon
 */
public class IdUtils {

    public static void main(String[] args) {
        System.out.println("simpleUUID() = " + simpleUUID());
    }

    /**
     * 获取随机UUID
     *
     * @return 随机UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取随机UUID，默认32位，只截取前八位
     *
     * @return 随机UUID
     */
    public static String shortUUID() {
        return UUID.randomUUID().toString().split("-")[0];
    }

    /**
     * 简化的UUID，去掉了横线
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 随机UUID
     */
    public static String fastUUID() {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID() {
        return UUID.fastUUID().toString(true);
    }
}
