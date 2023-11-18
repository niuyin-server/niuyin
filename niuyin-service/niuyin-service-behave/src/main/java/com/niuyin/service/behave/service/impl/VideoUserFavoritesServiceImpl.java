package com.niuyin.service.behave.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.model.behave.domain.UserFavoriteVideo;
import com.niuyin.model.behave.domain.VideoUserFavorites;
import com.niuyin.model.behave.dto.UserFavoriteVideoDTO;
import com.niuyin.model.notice.domain.Notice;
import com.niuyin.model.notice.enums.NoticeType;
import com.niuyin.model.notice.enums.ReceiveFlag;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.service.behave.constants.VideoCacheConstants;
import com.niuyin.service.behave.mapper.UserFavoriteVideoMapper;
import com.niuyin.service.behave.mapper.VideoUserFavoritesMapper;
import com.niuyin.service.behave.mapper.VideoUserLikeMapper;
import com.niuyin.service.behave.service.IVideoUserFavoritesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static com.niuyin.model.notice.mq.NoticeDirectConstant.NOTICE_CREATE_ROUTING_KEY;
import static com.niuyin.model.notice.mq.NoticeDirectConstant.NOTICE_DIRECT_EXCHANGE;

/**
 * 视频收藏表(VideoUserFavorites)表服务实现类
 *
 * @author lzq
 * @since 2023-10-31 15:57:38
 */
@Slf4j
@Service("videoUserFavoritesService")
public class VideoUserFavoritesServiceImpl extends ServiceImpl<VideoUserFavoritesMapper, VideoUserFavorites> implements IVideoUserFavoritesService {
    @Resource
    private VideoUserFavoritesMapper videoUserFavoritesMapper;

    @Resource
    private UserFavoriteVideoMapper userFavoriteVideoMapper;
    @Resource
    private RedisService redisService;

    @Resource
    private VideoUserLikeMapper videoUserLikeMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 用户收藏
     *
     * @param videoId
     * @return
     */
    @Transactional
    @Override
    public boolean videoFavorites(String videoId) {

        //从token获取用户id
        Long userId = UserContext.getUser().getUserId();
        //构建查询条件
        LambdaQueryWrapper<VideoUserFavorites> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserFavorites::getVideoId, videoId).eq(VideoUserFavorites::getUserId, userId);
        //判断当前表中有没有记录
        List<VideoUserFavorites> list = this.list(queryWrapper);
        if (StringUtils.isNull(list) || list.isEmpty()) {
            //没有记录，则新建对象存入数据库
            VideoUserFavorites videoUserFavorites = new VideoUserFavorites();
            videoUserFavorites.setVideoId(videoId);
            videoUserFavorites.setUserId(userId);
            videoUserFavorites.setCreateTime(LocalDateTime.now());
            //将本条点赞信息存储到redis（key为videoId,value为videoUrl）
            favoriteNumIncrease(videoId);
            // 发送消息到通知
            sendNotice2MQ(videoId, userId);
            return this.save(videoUserFavorites);
        } else {
            //将本条点赞信息从redis移除
            favoriteNumDecrease(videoId);
            //如果收藏夹中有此视频，同时移除
            LambdaQueryWrapper<UserFavoriteVideo> qw = new LambdaQueryWrapper<>();
            qw.eq(UserFavoriteVideo::getUserId, userId)
                    .eq(UserFavoriteVideo::getVideoId, videoId);
            userFavoriteVideoMapper.delete(qw);
            return this.remove(queryWrapper);
        }
//        }


    }

    /**
     * 用户收藏视频，通知mq
     *
     * @param videoId
     * @param operateUserId
     */
    private void sendNotice2MQ(String videoId, Long operateUserId) {
        // 根据视频获取发布者id
        Video video = videoUserLikeMapper.selectVideoByVideoId(videoId);
        if (StringUtils.isNull(video)) {
            return;
        }
        if (operateUserId.equals(video.getUserId())) {
            return;
        }
        // 封装notice实体
        Notice notice = new Notice();
        notice.setOperateUserId(operateUserId);
        notice.setNoticeUserId(video.getUserId());
        notice.setVideoId(videoId);
        notice.setContent("视频被人收藏了0.o");
        notice.setNoticeType(NoticeType.FAVORITE.getCode());
        notice.setReceiveFlag(ReceiveFlag.WAIT.getCode());
        notice.setCreateTime(LocalDateTime.now());
        // notice消息转换为json
        String msg = JSON.toJSONString(notice);
        rabbitTemplate.convertAndSend(NOTICE_DIRECT_EXCHANGE, NOTICE_CREATE_ROUTING_KEY, msg);
        log.debug(" ==> {} 发送了一条消息 ==> {}", NOTICE_DIRECT_EXCHANGE, msg);
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
