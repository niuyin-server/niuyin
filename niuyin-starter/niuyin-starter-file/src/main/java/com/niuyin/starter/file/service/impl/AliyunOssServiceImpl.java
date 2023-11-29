package com.niuyin.starter.file.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.niuyin.starter.file.config.QiniuOssConfig;
import com.niuyin.starter.file.config.QiniuOssConfigProperties;
import com.niuyin.starter.file.config.aliyun.AliyunOssConfig;
import com.niuyin.starter.file.config.aliyun.AliyunOssConfigProperties;
import com.niuyin.starter.file.service.AliyunOssService;
import com.niuyin.starter.file.util.PathUtils;
import com.niuyin.starter.file.util.file.FileUploadUtils;
import com.niuyin.starter.file.util.file.MimeTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.niuyin.starter.file.config.aliyun.AliyunOssConfigProperties.BUCKET_NAME;
import static com.niuyin.starter.file.config.aliyun.AliyunOssConfigProperties.END_POINT;

/**
 * OssServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/29
 **/
@Slf4j
@EnableConfigurationProperties(AliyunOssConfigProperties.class)
@Import(AliyunOssConfig.class)
public class AliyunOssServiceImpl implements AliyunOssService {

    @Autowired
    private OSSClient ossClient;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        return uploadCommonImage(file, folder);
    }

    /**
     * 上传图片方法
     *
     * @param file   限制大小100M
     * @param folder
     * @return
     */
    private String uploadCommonImage(MultipartFile file, String folder) {
        // 校验图片格式
        FileUploadUtils.assertAllowed(file, MimeTypeUtils.IMAGE_EXTENSION);
        return putObject(file, folder);
    }

    /**
     * 上传视频方法
     *
     * @param file   限制大小100M
     * @param folder
     * @return
     */
    private String uploadCommonVideo(MultipartFile file, String folder) {
        // 校验视频格式
        FileUploadUtils.assertAllowed(file, MimeTypeUtils.VIDEO_EXTENSION);
        return putObject(file, folder);
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return url
     */
    private String putObject(MultipartFile file, String folder) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String filePath = PathUtils.generateFilePath(file.getOriginalFilename());
        // 文件存储名称：服务名称/2023/11/11/uuid.jpg
        String ossFileName = folder + "/" + filePath;
        ossClient.putObject(BUCKET_NAME, ossFileName, inputStream);
        String url = "https://" + BUCKET_NAME + "." + END_POINT + "/" + ossFileName;
        //关闭 OSSClient
        ossClient.shutdown();
        return url;
    }

    /**
     * 删除文件
     *
     * @param url
     */
    private void deleteObject(String url) {
        String urlPrefix = "https://" + BUCKET_NAME + "." + END_POINT + "/";
        System.out.println("urlPrefix==================" + urlPrefix);
        System.out.println("url==================" + url);
        String replace = url.replace(urlPrefix, "");
        System.out.println("replace==================" + replace);
        ossClient.deleteObject(BUCKET_NAME, replace);
        ossClient.shutdown();
    }

}
