package com.qiniu.service.video.schedule;

import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.bean.BeanCopyUtils;
import com.qiniu.common.utils.date.DateUtils;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.vo.HotVideoVO;
import com.qiniu.service.video.constants.VideoCacheConstants;
import com.qiniu.service.video.service.IVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * HotVideoTask
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/7
 **/
@Slf4j
@Component
public class HotVideoTask {

    @Resource
    private RedisService redisService;

    @Resource
    private IVideoService videoService;

    @Scheduled(fixedRate = 1000 * 60 * 5)
    public void computeHotVideo() {
        log.info("==> 开始计算热门视频，首先查询最近5天的视频记录");
        List<Video> videoList = videoService.getVideoListLtCreateTime(DateUtils.getTodayMinusStartLocalDateTime(5));
        log.info("==> 从redis获取视频点赞量，观看量，收藏量");
        List<HotVideoVO> hotVideoVOList = videoService.computeHotVideoScore(videoList);
        hotVideoVOList.forEach(h -> {
            if (h.getScore() == 0) {
                log.info("0.o");
            } else {
                Video video = BeanCopyUtils.copyBean(h, Video.class);
                redisService.setCacheZSet(VideoCacheConstants.VIDEO_HOT, video.getVideoId(), h.getScore());
            }
        });
        redisService.expire(VideoCacheConstants.VIDEO_HOT, 1, TimeUnit.HOURS);
        log.info("==> 热门视频缓存到redis完成");
    }

}
