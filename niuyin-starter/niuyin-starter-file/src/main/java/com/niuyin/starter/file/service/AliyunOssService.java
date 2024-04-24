package com.niuyin.starter.file.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * AliyunOssService
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/29
 **/
public interface AliyunOssService {

    /**
     * 普通上传文件
     *
     * @param file 文件本体
     * @return url
     */
    String uploadFile(MultipartFile file, String folder);

    /**
     * 上传视频文件
     *
     * @param file 文件本体
     * @return url
     */
    String uploadVideoFile(MultipartFile file, String folder);

    /**
     * 分片上传视频文件
     *
     * @param file 文件本体
     * @param folder oss路径前缀
     * @return url
     */
    String multipartUploadVideoFile(MultipartFile file, String folder);

}
