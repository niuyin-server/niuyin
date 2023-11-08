package com.qiniu.common.utils.file;

import com.qiniu.common.utils.Md5Util;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PathUtils {

    public static String generateFilePath(String fileName) {
        //根据日期生成路径   2022/1/15/
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        String datePath = sdf.format(new Date());
        //文件流的md5值作为文件名
//        String md5="";
//        try {
//            InputStream inputStream = file.getInputStream();
//            md5= DigestUtils.md5Hex(inputStream);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

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
