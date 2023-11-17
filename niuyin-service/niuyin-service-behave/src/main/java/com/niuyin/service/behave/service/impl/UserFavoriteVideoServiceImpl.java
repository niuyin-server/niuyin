package com.niuyin.service.behave.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.exception.CustomException;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.model.behave.domain.UserFavoriteVideo;
import com.niuyin.model.behave.dto.UserFavoriteVideoDTO;
import com.niuyin.model.notice.domain.Notice;
import com.niuyin.model.notice.enums.NoticeType;
import com.niuyin.model.notice.enums.ReceiveFlag;
import com.niuyin.model.video.domain.Video;
import com.niuyin.service.behave.constants.VideoCacheConstants;
import com.niuyin.service.behave.mapper.UserFavoriteVideoMapper;
import com.niuyin.service.behave.mapper.VideoUserLikeMapper;
import com.niuyin.service.behave.service.IUserFavoriteVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.niuyin.model.common.enums.HttpCodeEnum.*;
import static com.niuyin.model.notice.mq.NoticeDirectConstant.*;

/**
 * (UserFavoriteVideo)表服务实现类
 *
 * @author lzq
 * @since 2023-11-17 10:16:09
 */
@Slf4j
@Service("userFavoriteVideoService")
public class UserFavoriteVideoServiceImpl extends ServiceImpl<UserFavoriteVideoMapper, UserFavoriteVideo> implements IUserFavoriteVideoService {
    @Resource
    private UserFavoriteVideoMapper userFavoriteVideoMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private VideoUserLikeMapper videoUserLikeMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     * @param userFavoriteVideoDTO
     * @return
     */
    @Override
    public Boolean videoFavorites(UserFavoriteVideoDTO userFavoriteVideoDTO) {

        //从token中获取userid
        Long userId = UserContext.getUserId();
        //构建查询条件
        LambdaQueryWrapper<UserFavoriteVideo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFavoriteVideo::getVideoId, userFavoriteVideoDTO.getVideoId())
                .eq(UserFavoriteVideo::getUserId, userId);
        List<UserFavoriteVideo> list = this.list(queryWrapper);
        //判断收藏夹关联表中是否有记录
        if (list.isEmpty()) {
            //没有记录则加入收藏
            ArrayList<UserFavoriteVideo> userFavoriteVideos = new ArrayList<>();
            UserFavoriteVideo userFavoriteVideo = new UserFavoriteVideo();
            userFavoriteVideo.setUserId(userId);
            userFavoriteVideo.setVideoId(userFavoriteVideoDTO.getVideoId());
            for (int i = 0; i < userFavoriteVideoDTO.getFavorites().length; i++) {
                userFavoriteVideo.setFavoriteId(userFavoriteVideoDTO.getFavorites()[i]);
                userFavoriteVideos.add(userFavoriteVideo);
            }
            boolean b = this.saveBatch(userFavoriteVideos);
            if (b) {
                //将本条点赞信息存储到redis（key为videoId,value为videoUrl）
                favoriteNumIncrease(userFavoriteVideoDTO.getVideoId());
                // 发送消息到通知
                sendNotice2MQ(userFavoriteVideoDTO.getVideoId(), userId);
                return true;
            } else {
                throw new CustomException(FAVORITE_FAIL);
            }

        } else {
            //有记录则取消收藏
            //将本条点赞信息从redis移除
            favoriteNumDecrease(userFavoriteVideoDTO.getVideoId());
            return this.remove(queryWrapper);
        }
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

    @Async
    protected void favoriteNumIncrease(String videoId) {
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_FAVORITE_NUM_MAP_KEY, videoId, 1);
    }

    @Async
    protected void favoriteNumDecrease(String videoId) {
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_FAVORITE_NUM_MAP_KEY, videoId, -1);
    }
}