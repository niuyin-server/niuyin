package com.niuyin.starter.file.util.file;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传工具类
 *
 * @author roydon
 */
public class FileUploadUtils {
    /**
     * 图片默认最大值 5M
     */
    public static final long DEFAULT_IMAGE_MAX_SIZE = 5 * 1024 * 1024;
    /**
     * 视频默认最大值 100M
     */
    public static final long DEFAULT_VIDEO_MAX_SIZE = 100 * 1024 * 1024;

    // 用户头像大小限制
    public static final long MEMBER_AVATAR_MAX_SIZE = 1024 * 1024;

    /**
     * 文件大小校验
     *
     * @param file 上传的文件
     */
    public static final void assertImageAllowed(MultipartFile file, String[] allowedExtension) {
        long size = file.getSize();
        if (size > DEFAULT_IMAGE_MAX_SIZE) {
            throw new RuntimeException("图片大小超出限制：" + DEFAULT_IMAGE_MAX_SIZE / 1024 / 1024 + "M");
        }
    }

    /**
     * 文件大小校验
     *
     * @param file 上传的文件
     */
    public static final void assertVideoAllowed(MultipartFile file, String[] allowedExtension) {
        long size = file.getSize();
        if (size > DEFAULT_VIDEO_MAX_SIZE) {
            throw new RuntimeException("视频大小超出限制：" + DEFAULT_VIDEO_MAX_SIZE / 1024 / 1024 + "M");
        }
    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension
     * @param allowedExtension
     * @return
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

}
