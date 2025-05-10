package com.niuyin.starter.file.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PathUtils {

    /**
     * 使用当前日期＋uuid生成文件相对地址
     *
     * @param fileName 原始文件名
     * @return 2023/10/24/9829cce9da304b66902fdd19c7cfbfc8.jpg
     */
    public static String generateFilePath(String fileName) {
        //根据日期生成路径   2023/10/24/
        String datePath = generateDataPath();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //后缀和文件后缀一致 test.jpg -> .jpg
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        return datePath + uuid + fileType;
    }

    /**
     * 使用当前日期＋uuid生成文件相对地址
     *
     * @param url 原始地址
     * @return 2025/05/10/52e8919527b24449a9db99f5364bc2d1.png
     */
    public static String generateFilePathForUrl(String url) {
        // 获取图片url文件格式，例如：https://sc-maas.oss-cn-shanghai.aliyuncs.com/outputs%2F20250510%2F12e5e77kvo.png?Expires=1746873178&OSSAccessKeyId=LTAI5tQnPSzwAnR8NmMzoQq4&Signature=gCBbhCzpNdx4ruZIxb2DVKsFIxg%3D 取 png
        String fileName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("?"));
        //根据日期生成路径   2023/10/24/
        String datePath = generateDataPath();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //后缀和文件后缀一致 test.jpg -> .jpg
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        return datePath + uuid + fileType;
    }

    /**
     * 根据当前时间生成文件夹格式
     *
     * @return 2023/10/24/
     */
    public static String generateDataPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        return sdf.format(new Date());
    }

    public static void main(String[] args) {
        System.out.println(generateFilePathForUrl("https://sc-maas.oss-cn-shanghai.aliyuncs.com/outputs%2F20250510%2F12e5e77kvo.png?Expires=1746873178&OSSAccessKeyId=LTAI5tQnPSzwAnR8NmMzoQq4&Signature=gCBbhCzpNdx4ruZIxb2DVKsFIxg%3D"));
    }

}
