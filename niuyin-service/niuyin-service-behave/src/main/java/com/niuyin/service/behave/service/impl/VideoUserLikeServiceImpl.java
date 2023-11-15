package com.niuyin.service.behave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.model.behave.domain.VideoUserLike;
import com.niuyin.model.member.domain.MemberInfo;
import com.niuyin.model.member.enums.ShowStatusEnum;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.service.behave.constants.VideoCacheConstants;
import com.niuyin.service.behave.mapper.VideoUserLikeMapper;
import com.niuyin.service.behave.service.IVideoUserLikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 点赞表(VideoUserLike)表服务实现类
 *
 * @author lzq
 * @since 2023-10-30 14:33:01
 */
@Slf4j
@Service("videoUserLikeService")
public class VideoUserLikeServiceImpl extends ServiceImpl<VideoUserLikeMapper, VideoUserLike> implements IVideoUserLikeService {

    @Resource
    private RedisService redisService;

    @Resource
    private VideoUserLikeMapper videoUserLikeMapper;

    /**
     * 向视频点赞表插入点赞信息
     *
     * @param videoId
     * @return
     */
    @Override
    public boolean videoLike(String videoId) {
        Long userId = UserContext.getUser().getUserId();
        LambdaQueryWrapper<VideoUserLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserLike::getVideoId, videoId).eq(VideoUserLike::getUserId, userId);
        List<VideoUserLike> list = this.list(queryWrapper);
        if (StringUtils.isNull(list) || list.isEmpty()) {
            VideoUserLike videoUserLike = new VideoUserLike();
            videoUserLike.setVideoId(videoId);
            videoUserLike.setUserId(userId);
            videoUserLike.setCreateTime(LocalDateTime.now());
            // 将本条点赞信息存储到redis
            likeNumIncrement(videoId);
            return this.save(videoUserLike);
        } else {
            //将本条点赞信息从redis
            likeNumDecrement(videoId);
            return this.remove(queryWrapper);
        }
    }

    /**
     * 缓存中点赞量自增一
     *
     * @param videoId
     */
    @Async
    protected void likeNumIncrement(String videoId) {
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_LIKE_NUM_MAP_KEY, videoId, 1);
    }

    /**
     * 缓存中点赞量自减一
     *
     * @param videoId
     */
    @Async
    protected void likeNumDecrement(String videoId) {
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_LIKE_NUM_MAP_KEY, videoId, -1);
    }

    /**
     * 分页查询我的视频
     *
     * @param pageDto
     * @return
     */
    @Override
    public IPage<VideoUserLike> queryMyLikeVideoPage(VideoPageDto pageDto) {
        LambdaQueryWrapper<VideoUserLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserLike::getUserId, UserContext.getUserId());
        return this.page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()), queryWrapper);
    }

    /**
     * 查询用户的点赞列表
     *
     * @param pageDto
     * @return
     */
    @Override
    public List<Video> queryPersonLikePage(VideoPageDto pageDto) {
        //判断当查询的时否为登录用户自己的主页
        List<Video> videos;
        if (pageDto.getUserId().equals(UserContext.getUserId())) {
            videos = videoUserLikeMapper.selectPersonLikePage(
                    pageDto.getUserId(), pageDto.getPageSize(), (pageDto.getPageNum() - 1) * pageDto.getPageNum());
        } else {
            //判断该用户的点赞列表是否对外展示
            MemberInfo memberInfo = videoUserLikeMapper.selectPersonLikeShowStatus(pageDto.getUserId());
            if (memberInfo.getLikeShowStatus().equals(ShowStatusEnum.HIDE.getCode())) {
                return new ArrayList<>();
            } else {
                videos = videoUserLikeMapper.selectPersonLikePage(
                        pageDto.getUserId(), pageDto.getPageSize(), (pageDto.getPageNum() - 1) * pageDto.getPageNum());
            }
        }
        return videos;
    }

}
