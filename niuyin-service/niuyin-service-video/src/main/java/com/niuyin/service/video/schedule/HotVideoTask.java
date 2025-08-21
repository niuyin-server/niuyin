package com.niuyin.service.video.schedule;

import com.niuyin.common.cache.service.RedisService;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.date.DateUtils;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.vo.HotVideoVO;
import com.niuyin.model.constants.VideoCacheConstants;
import com.niuyin.service.video.service.IVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.niuyin.service.video.constants.HotVideoConstants.VIDEO_BEFORE_DAT30;

/**
 * HotVideoTask
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/7
 **/
@Slf4j
@Component
public class HotVideoTask {
    private static final Long CACHE_BUILD_LOCK_TTL = 5L; // 分布式锁过期时间
    private static final String HOT_VIDEO_CACHE_BUILD_LOCK_KEY = "video:hot:sync"; // 分布式锁过期时间

    @Resource
    private RedisService redisService;

    @Resource
    private IVideoService videoService;

    @Scheduled(fixedRate = 1000 * 60 * 30)
    public void computeHotVideo() {
        // 获取互斥锁
        try {
            boolean tryLock = redisService.tryLock(HOT_VIDEO_CACHE_BUILD_LOCK_KEY, CACHE_BUILD_LOCK_TTL, TimeUnit.SECONDS);
            if (tryLock) {
                log.info("==> 获取redis互斥锁 {} 成功！ 开始计算热门视频，首先查询最近30天的视频记录", HOT_VIDEO_CACHE_BUILD_LOCK_KEY);
                List<Video> videoList = videoService.getVideoListLtCreateTime(DateUtils.getTodayMinusStartLocalDateTime(VIDEO_BEFORE_DAT30));
                List<HotVideoVO> hotVideoVOList = videoService.computeHotVideoScore(videoList);
                if (hotVideoVOList.isEmpty()) {
                    return;
                }
                hotVideoVOList.forEach(h -> {
                    if (h.getScore() == 0) {
                        log.info("0.o");
                    } else {
                        Video video = BeanCopyUtils.copyBean(h, Video.class);
                        redisService.setCacheZSet(VideoCacheConstants.VIDEO_HOT, video.getVideoId(), h.getScore());
                    }
                });
                // 缓存热门视频过期时间7天
                redisService.expire(VideoCacheConstants.VIDEO_HOT, 30, TimeUnit.DAYS);
                log.info("==> 热门视频缓存到redis完成");
            }
        } finally {
            redisService.unLock(HOT_VIDEO_CACHE_BUILD_LOCK_KEY);
        }
    }

}
