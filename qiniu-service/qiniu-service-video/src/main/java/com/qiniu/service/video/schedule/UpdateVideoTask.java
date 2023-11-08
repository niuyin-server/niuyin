package com.qiniu.service.video.schedule;

import com.qiniu.common.service.RedisService;
import com.qiniu.model.video.domain.Video;
import com.qiniu.service.video.constants.VideoCacheConstants;
import com.qiniu.service.video.service.IVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/10/31 10:58
 */
@Slf4j
@Component
public class UpdateVideoTask {

    @Resource
    private RedisService redisService;

    @Resource
    private IVideoService videoService;

    /**
     * 从redis更新视频点赞量
     */
    @Scheduled(fixedRate = 1000 * 60 * 5)
    public void updateLikeCount() {
        log.info("开始从redis更新视频点赞量==>");
        //获取redis中的浏览量
        Map<String, Integer> viewNumMap = redisService.getCacheMap(VideoCacheConstants.VIDEO_LIKE_NUM_MAP_KEY);
        List<Video> newsList = viewNumMap.entrySet().stream().map(entry -> {
            Video an = new Video();
            an.setVideoId(entry.getKey());
            an.setLikeNum(Long.valueOf(entry.getValue()));
            return an;
        }).collect(Collectors.toList());
        //更新数据库
        videoService.updateBatchById(newsList);
        log.info("<==视频点赞量数据库与redis同步成功");
    }

    /**
     * 从redis更新视频收藏量
     */
    @Scheduled(fixedRate = 1000 * 60 * 5)
    public void updateFavoriteCount() {
        log.info("开始从redis更新视频收藏量==>");
        //获取redis中的浏览量
        Map<String, Integer> viewNumMap = redisService.getCacheMap(VideoCacheConstants.VIDEO_FAVORITE_NUM_MAP_KEY);
        List<Video> newsList = viewNumMap.entrySet().stream().map(entry -> {
            Video an = new Video();
            an.setVideoId(entry.getKey());
            an.setFavoritesNum(Long.valueOf(entry.getValue()));
            return an;
        }).collect(Collectors.toList());
        //更新数据库
        videoService.updateBatchById(newsList);
        log.info("<==视频收藏量数据库与redis同步成功");
    }

    /**
     * 从redis更新视频浏览量
     */
    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void updateViewCount() {
        log.info("开始从redis更新视频浏览量==>");
        //获取redis中的浏览量
        Map<String, Integer> viewNumMap = redisService.getCacheMap(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY);
        List<Video> newsList = viewNumMap.entrySet().stream().map(entry -> {
            Video an = new Video();
            an.setVideoId(entry.getKey());
            an.setViewNum(Long.valueOf(entry.getValue()));
            return an;
        }).collect(Collectors.toList());
        //更新数据库
        videoService.updateBatchById(newsList);
        log.info("<==视频浏览量数据库与redis同步成功");
    }

}
