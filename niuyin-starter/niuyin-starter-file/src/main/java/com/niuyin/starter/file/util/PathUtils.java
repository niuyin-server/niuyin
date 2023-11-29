package com.niuyin.starter.file.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PathUtils {

    public static String generateFilePath(String fileName) {
        //根据日期生成路径   2022/1/15/
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        String datePath = sdf.format(new Date());
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //后缀和文件后缀一致
        int index = fileName.lastIndexOf(".");
        // test.jpg -> .jpg
        String fileType = fileName.substring(index);
        return datePath + uuid + fileType;
    }

    public static void main(String[] args) {
//        System.out.println(generateFilePath("fileName.jpg"));
    }

}
