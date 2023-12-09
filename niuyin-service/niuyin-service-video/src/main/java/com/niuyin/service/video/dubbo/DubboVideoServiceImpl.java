package com.niuyin.service.video.dubbo;

import com.niuyin.dubbo.api.DubboVideoService;
import com.niuyin.model.video.domain.Video;
import com.niuyin.service.video.service.IVideoService;
import com.niuyin.service.video.service.InterestPushService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

/**
 * DubboVideoServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/7
 **/
@DubboService(timeout = 2000, retries = 3, weight = 1)
public class DubboVideoServiceImpl implements DubboVideoService {

    @Resource
    private InterestPushService interestPushService;

    @Resource
    private IVideoService videoService;

    /**
     * 同步视频标签库
     *
     * @param videoId
     * @param tagsIds
     */
    @Override
    public void apiSyncVideoTagStack(String videoId, List<Long> tagsIds) {
        interestPushService.cacheVideoToTagRedis(videoId, tagsIds);
    }

    /**
     * 通过id获取视频
     *
     * @param videoId
     */
    @Override
    public Video apiGetVideoByVideoId(String videoId) {
        return videoService.getById(videoId);
    }
}
