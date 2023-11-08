package com.qiniu.service.behave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.context.UserContext;
import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.model.video.domain.VideoUserFavorites;
import com.qiniu.model.video.dto.VideoPageDto;
import com.qiniu.service.behave.constants.VideoCacheConstants;
import com.qiniu.service.behave.mapper.VideoUserFavoritesMapper;
import com.qiniu.service.behave.service.IVideoUserFavoritesService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 视频收藏表(VideoUserFavorites)表服务实现类
 *
 * @author lzq
 * @since 2023-10-31 15:57:38
 */
@Service("videoUserFavoritesService")
public class VideoUserFavoritesServiceImpl extends ServiceImpl<VideoUserFavoritesMapper, VideoUserFavorites> implements IVideoUserFavoritesService {
    @Resource
    private VideoUserFavoritesMapper videoUserFavoritesMapper;

    @Resource
    private RedisService redisService;

    /**
     * 用户收藏
     *
     * @param videoId
     * @return
     */
    @Override
    public boolean videoFavorites(String videoId) {
        Long userId = UserContext.getUser().getUserId();
        LambdaQueryWrapper<VideoUserFavorites> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserFavorites::getVideoId, videoId).eq(VideoUserFavorites::getUserId, userId);
        List<VideoUserFavorites> list = this.list(queryWrapper);
        if (StringUtils.isNull(list) || list.isEmpty()) {
            VideoUserFavorites videoUserFavorites = new VideoUserFavorites();
            videoUserFavorites.setVideoId(videoId);
            videoUserFavorites.setUserId(userId);
            videoUserFavorites.setCreateTime(LocalDateTime.now());
            //将本条点赞信息存储到redis（key为videoId,value为videoUrl）
            favoriteNumIncrease(videoId);
            return this.save(videoUserFavorites);
        } else {
            //将本条点赞信息从redis
            favoriteNumDecrease(videoId);
            return this.remove(queryWrapper);
        }
    }

    /**
     * @param pageDto
     * @return
     */
    @Override
    public IPage<VideoUserFavorites> queryFavoritePage(VideoPageDto pageDto) {
        LambdaQueryWrapper<VideoUserFavorites> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserFavorites::getUserId, UserContext.getUserId());
        return this.page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()), queryWrapper);
    }

    @Async
    protected void favoriteNumIncrease(String videoId) {
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_FAVORITE_NUM_MAP_KEY, videoId, 1);
    }

    @Async
    protected void favoriteNumDecrease(String videoId) {
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_FAVORITE_NUM_MAP_KEY, videoId, -1);
    }
}
