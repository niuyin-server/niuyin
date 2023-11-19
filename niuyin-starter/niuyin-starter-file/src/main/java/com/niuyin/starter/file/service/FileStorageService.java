package com.niuyin.starter.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FileStorageService {

    /**
     * 上传图片文件
     *
     * @param file
     * @param prefix
     * @param filePath
     * @return
     */
    String uploadImgFile(MultipartFile file, String prefix, String filePath);

    String uploadVideo(MultipartFile file);

//    String uploadTransForVideo(File file);
}
