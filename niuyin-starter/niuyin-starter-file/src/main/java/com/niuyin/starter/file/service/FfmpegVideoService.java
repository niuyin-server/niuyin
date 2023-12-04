package com.niuyin.starter.file.service;

import ws.schild.jave.info.MultimediaInfo;

public interface FfmpegVideoService {

    /**
     * 获取视频详情
     * @param url
     * @return
     */
    MultimediaInfo getVideoInfo(String url);

    /**
     * 根据视频远程url生成首帧截图
     *
     * @param url
     * @param fileName
     * @return 截图生成地址
     */
    String getTargetThumbnail(String url, String fileName);

    /**
     * 为远程视频生成三张预览图
     *
     * @param url
     * @param fileName
     * @return
     */
    String[] generatePreviewCover(String url, String fileName);

}
