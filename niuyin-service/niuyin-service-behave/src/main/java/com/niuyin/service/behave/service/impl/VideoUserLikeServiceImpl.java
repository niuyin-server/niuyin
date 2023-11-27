package com.niuyin.service.behave.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.model.behave.domain.VideoUserLike;
import com.niuyin.model.member.domain.MemberInfo;
import com.niuyin.model.member.enums.ShowStatusEnum;
import com.niuyin.model.notice.domain.Notice;
import com.niuyin.model.notice.enums.NoticeType;
import com.niuyin.model.notice.enums.ReceiveFlag;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoImage;
import com.niuyin.model.video.domain.VideoPosition;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.model.video.enums.PositionFlag;
import com.niuyin.model.video.enums.PublishType;
import com.niuyin.model.video.vo.VideoVO;
import com.niuyin.service.behave.constants.VideoCacheConstants;
import com.niuyin.service.behave.mapper.VideoUserLikeMapper;
import com.niuyin.service.behave.service.IVideoUserLikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.niuyin.model.notice.mq.NoticeDirectConstant.NOTICE_CREATE_ROUTING_KEY;
import static com.niuyin.model.notice.mq.NoticeDirectConstant.NOTICE_DIRECT_EXCHANGE;

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

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 向视频点赞表插入点赞信息
     *
     * @param videoId
     * @return
     */
    @Transactional
    @Override
    public boolean videoLike(String videoId) {
        Long userId = UserContext.getUserId();
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
            // 发送消息，创建通知
            sendNoticeWithLikeVideo(videoId, userId);
            return this.save(videoUserLike);
        } else {
            // 取消点赞
            //将本条点赞信息从redis
            likeNumDecrement(videoId);
            return this.remove(queryWrapper);
        }
    }

    /**
     * 发送用户点赞视频的消息
     *
     * @param videoId
     * @param operateUserId
     */
    private void sendNoticeWithLikeVideo(String videoId, Long operateUserId) {
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
        notice.setContent("视频被人点赞了o.0");
        notice.setNoticeType(NoticeType.LIKE.getCode());
        notice.setReceiveFlag(ReceiveFlag.WAIT.getCode());
        notice.setCreateTime(LocalDateTime.now());
        // notice消息转换为json
        String msg = JSON.toJSONString(notice);
        rabbitTemplate.convertAndSend(NOTICE_DIRECT_EXCHANGE, NOTICE_CREATE_ROUTING_KEY, msg);
        log.debug(" ==> {} 发送了一条消息 ==> {}", NOTICE_DIRECT_EXCHANGE, msg);
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
    public PageDataInfo queryMyLikeVideoPage(VideoPageDto pageDto) {
        pageDto.setPageNum((pageDto.getPageNum() - 1) * pageDto.getPageSize());
        pageDto.setUserId(UserContext.getUserId());
        List<Video> records = videoUserLikeMapper.selectPersonLikePage(pageDto);
//        List<VideoVO> videoVOList = new ArrayList<>();
//        records.forEach(r -> {
//            VideoVO videoVO = BeanCopyUtils.copyBean(r, VideoVO.class);
//            // 若是图文则封装图片集合
//            if (r.getPublishType().equals(PublishType.IMAGE.getCode())) {
//                List<VideoImage> videoImageList = videoUserLikeMapper.selectImagesByVideoId(videoVO.getVideoId());
//                String[] imgs = videoImageList.stream().map(VideoImage::getImageUrl).toArray(String[]::new);
//                videoVO.setImageList(imgs);
//            }
//            // 若是开启定位，封装定位
//            if (r.getPositionFlag().equals(PositionFlag.OPEN.getCode())) {
//                VideoPosition videoPosition = videoUserLikeMapper.selectPositionByVideoId(videoVO.getVideoId());
//                videoVO.setPosition(videoPosition);
//            }
//            videoVOList.add(videoVO);
//        });
        List<VideoVO> videoVOList = new ArrayList<>();
        List<CompletableFuture<Void>> futures = records.stream()
                .map(r -> CompletableFuture.runAsync(() -> {
                    VideoVO videoVO = BeanCopyUtils.copyBean(r, VideoVO.class);
                    // 若是图文则封装图片集合
                    if (r.getPublishType().equals(PublishType.IMAGE.getCode())) {
                        List<VideoImage> videoImageList = videoUserLikeMapper.selectImagesByVideoId(videoVO.getVideoId());
                        String[] imgs = videoImageList.stream().map(VideoImage::getImageUrl).toArray(String[]::new);
                        videoVO.setImageList(imgs);
                    }
                    // 若是开启定位，封装定位
                    if (r.getPositionFlag().equals(PositionFlag.OPEN.getCode())) {
                        VideoPosition videoPosition = videoUserLikeMapper.selectPositionByVideoId(videoVO.getVideoId());
                        videoVO.setPosition(videoPosition);
                    }
                    videoVOList.add(videoVO);
                })).collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return PageDataInfo.genPageData(videoVOList, videoUserLikeMapper.selectPersonLikeCount(pageDto));
    }

    /**
     * 查询用户的点赞列表
     *
     * @param pageDto
     * @return
     */
    @Override
    public PageDataInfo queryPersonLikePage(VideoPageDto pageDto) {
        //判断该用户的点赞列表是否对外展示
        MemberInfo memberInfo = videoUserLikeMapper.selectPersonLikeShowStatus(pageDto.getUserId());
        if (memberInfo.getLikeShowStatus().equals(ShowStatusEnum.HIDE.getCode())) {
            return PageDataInfo.emptyPage();
        }
        pageDto.setPageNum((pageDto.getPageNum() - 1) * pageDto.getPageSize());
        List<Video> records = videoUserLikeMapper.selectPersonLikePage(pageDto);
        ArrayList<VideoVO> videoVOList = new ArrayList<>();
        List<CompletableFuture<Void>> futures = records.stream()
                .map(r -> CompletableFuture.runAsync(() -> {
                    VideoVO videoVO = BeanCopyUtils.copyBean(r, VideoVO.class);
                    //若是图文，则封装图片集合
                    if (r.getPublishType().equals(PublishType.IMAGE.getCode())) {
                        List<VideoImage> videoImageList = videoUserLikeMapper.selectImagesByVideoId(videoVO.getVideoId());
                        String[] imgs = videoImageList.stream().map(VideoImage::getImageUrl).toArray(String[]::new);
                        videoVO.setImageList(imgs);
                    }
                    //若是开启定位，则封装定位
                    if (r.getPositionFlag().equals(PositionFlag.OPEN.getCode())) {
                        VideoPosition videoPosition = videoUserLikeMapper.selectPositionByVideoId(videoVO.getVideoId());
                        videoVO.setPosition(videoPosition);
                    }
                    videoVOList.add(videoVO);
                })).collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return PageDataInfo.genPageData(videoVOList, videoUserLikeMapper.selectPersonLikeCount(pageDto));
    }

}
