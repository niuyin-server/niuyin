package com.niuyin.dubbo.api;

import com.niuyin.model.video.domain.Video;

import java.util.List;

/**
 * 服务提供者：video
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/7
 **/
public interface DubboVideoService {

    /**
     * 同步视频标签库
     */
    void apiSyncVideoTagStack(String videoId, List<Long> tagsIds);

    /**
     * 通过id获取视频
     */
    Video apiGetVideoByVideoId(String videoId);

    /**
     * 通过id获取视频图文
     */
    String[] apiGetVideoImagesByVideoId(String videoId);

}
