package com.niuyin.service.video.schedule;

import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.service.video.service.IVideoService;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.date.DateUtils;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.vo.HotVideoVO;
import com.niuyin.service.video.constants.VideoCacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.niuyin.service.video.constants.HotVideoConstants.VIDEO_BEFORE_DAT7;

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

    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void computeHotVideo() {
        log.info("==> 开始计算热门视频，首先查询最近7天的视频记录");
        List<Video> videoList = videoService.getVideoListLtCreateTime(DateUtils.getTodayMinusStartLocalDateTime(VIDEO_BEFORE_DAT7));
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
        // 缓存热门视频过期事件30天
        redisService.expire(VideoCacheConstants.VIDEO_HOT, 30, TimeUnit.DAYS);
        log.info("==> 热门视频缓存到redis完成");
    }

}
