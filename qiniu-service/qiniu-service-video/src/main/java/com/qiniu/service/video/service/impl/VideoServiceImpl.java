package com.qiniu.service.video.service.impl;

import com.qiniu.common.utils.audit.SensitiveWordUtil;
import com.qiniu.common.utils.string.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.context.UserContext;
import com.qiniu.common.exception.CustomException;
import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.bean.BeanCopyUtils;
import com.qiniu.common.utils.file.PathUtils;
import com.qiniu.common.utils.uniqueid.IdGenerator;
import com.qiniu.feign.behave.RemoteBehaveService;
import com.qiniu.feign.social.RemoteSocialService;
import com.qiniu.feign.user.RemoteUserService;
import com.qiniu.model.common.dto.PageDTO;
import com.qiniu.model.search.vo.VideoSearchVO;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.video.domain.*;
import com.qiniu.model.video.dto.VideoFeedDTO;
import com.qiniu.model.video.dto.VideoPublishDto;
import com.qiniu.model.video.dto.VideoPageDto;
import com.qiniu.model.video.vo.HotVideoVO;
import com.qiniu.model.video.vo.VideoUploadVO;
import com.qiniu.model.video.vo.VideoVO;
import com.qiniu.service.video.constants.HotVideoConstants;
import com.qiniu.service.video.constants.QiniuVideoOssConstants;
import com.qiniu.service.video.constants.VideoCacheConstants;
import com.qiniu.service.video.mapper.VideoMapper;
import com.qiniu.service.video.mapper.VideoUserCommentMapper;
import com.qiniu.service.video.service.IVideoCategoryRelationService;
import com.qiniu.service.video.service.IVideoSensitiveService;
import com.qiniu.service.video.service.IVideoService;
import com.qiniu.starter.file.service.FileStorageService;
import jdk.nashorn.internal.ir.IfNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.qiniu.model.common.enums.HttpCodeEnum.*;
import static com.qiniu.model.video.mq.VideoDelayedQueueConstant.*;

/**
 * 视频表(Video)表服务实现类
 *
 * @author roydon
 * @since 2023-10-25 20:33:11
 */
@Slf4j
@Service("videoService")
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IVideoService {

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private IVideoCategoryRelationService videoCategoryRelationService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private RedisService redisService;

    @Resource
    private IVideoSensitiveService videoSensitiveService;

    @Resource
    private RemoteBehaveService remoteBehaveService;

    @Resource
    private RemoteSocialService remoteSocialService;

    @Override
    public VideoUploadVO uploadVideo(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        //对文件id进行判断，如果文件已经存在，则不上传，直接返回数据库中文件的存储路径
        String filePath = PathUtils.generateFilePath(originalFilename);
        VideoUploadVO videoUploadVO = new VideoUploadVO();
        String uploadVideo = fileStorageService.uploadVideo(file);
        videoUploadVO.setVideoUrl(QiniuVideoOssConstants.PREFIX_URL + uploadVideo);
        String niuyin = uploadVideo.replace("niuyin", "");
        String video = niuyin.replace("video", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        String datePath = sdf.format(new Date());
        videoUploadVO.setVframe(QiniuVideoOssConstants.PREFIX_URL + datePath + video + "?vframe/jpg/offset/1");
        return videoUploadVO;
    }

    @Override
    public Video selectById(String id) {
        Video video = videoMapper.selectById(id);
        return video;
    }

    @Transactional
    @Override
    public String videoPublish(VideoPublishDto videoPublishDto) {
        Long userId = UserContext.getUser().getUserId();
        //从数据库获得敏感信息
        //判断传过来的数据是否符合数据库字段标准
        if (videoPublishDto.getVideoTitle().length() > 100) {
            throw new CustomException(BIND_CONTENT_TITLE_FAIL);
        }
        if (videoPublishDto.getVideoDesc().length() > 200) {
            throw new CustomException(BIND_CONTENT_DESC_FAIL);
        }
        // 查出video敏感词表所有敏感词集合
        boolean b = sensitiveCheck(videoPublishDto.getVideoTitle() + videoPublishDto.getVideoDesc());
        if (b) {
            // 存在敏感词抛异常
            throw new CustomException(SENSITIVEWORD_ERROR);
        }
        //将传过来的数据拷贝到要存储的对象中
        Video video = BeanCopyUtils.copyBean(videoPublishDto, Video.class);
        //生成id
        String videoId = IdGenerator.generatorShortId();
        //向新的对象中封装信息
        video.setVideoId(videoId);
        video.setUserId(userId);
        video.setCreateTime(LocalDateTime.now());
        video.setCreateBy(userId.toString());
        video.setCoverImage(StringUtils.isNull(videoPublishDto.getCoverImage()) ?
                video.getVideoUrl() + VideoCacheConstants.VIDEO_VIEW_COVER_IMAGE_KEY : videoPublishDto.getCoverImage());
        //前端不传不用处理 将前端传递的分类拷贝到关联表对象
        if (StringUtils.isNotNull(videoPublishDto.getCategoryId())) {
            VideoCategoryRelation videoCategoryRelation = BeanCopyUtils.copyBean(videoPublishDto, VideoCategoryRelation.class);
            // video_id存入VideoCategoryRelation（视频分类关联表）
            videoCategoryRelation.setVideoId(video.getVideoId());
            // 再将videoCategoryRelation对象存入video_category_relation表中
            videoCategoryRelationService.saveVideoCategoryRelation(videoCategoryRelation);
        }
        // 将video对象存入video表中
        boolean save = this.save(video);
        if (save) {
            // 发布成功添加缓存
            redisService.setCacheObject(VideoCacheConstants.VIDEO_INFO_PREFIX + video.getVideoId(), video);
            // 1.发送整个video对象发送消息，
            // 待添加视频封面
            VideoSearchVO videoSearchVO = new VideoSearchVO();
            videoSearchVO.setVideoId(video.getVideoId());
            videoSearchVO.setVideoTitle(video.getVideoTitle());
            // localdatetime转换为date
            videoSearchVO.setPublishTime(Date.from(video.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
            videoSearchVO.setCoverImage(video.getCoverImage());
            videoSearchVO.setVideoUrl(video.getVideoUrl());
            videoSearchVO.setUserId(userId);
            // 获取用户信息
            User userCache = redisService.getCacheObject("userinfo:" + userId);
            if (StringUtils.isNotNull(userCache)) {
                videoSearchVO.setUserNickName(userCache.getNickName());
                videoSearchVO.setUserAvatar(userCache.getAvatar());
            } else {
                User remoteUser = remoteUserService.userInfoById(userId).getData();
                videoSearchVO.setUserNickName(remoteUser.getNickName());
                videoSearchVO.setUserAvatar(remoteUser.getAvatar());
            }
            String msg = JSON.toJSONString(videoSearchVO);
            // 2.利用消息后置处理器添加消息头
            rabbitTemplate.convertAndSend(ESSYNC_DELAYED_EXCHANGE, ESSYNC_ROUTING_KEY, msg, message -> {
                // 3.添加延迟消息属性，设置1分钟
                message.getMessageProperties().setDelay(ESSYNC_DELAYED_TIME);
                return message;
            });
            log.debug(" ==> {} 发送了一条消息 ==> {}", ESSYNC_DELAYED_EXCHANGE, msg);
            return videoId;
        } else {
            throw new CustomException(null);
        }
    }

    /**
     * 敏感词检测
     */
    private boolean sensitiveCheck(String str) {
        LambdaQueryWrapper<VideoSensitive> userSensitiveLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userSensitiveLambdaQueryWrapper.select(VideoSensitive::getSensitives);
        List<String> videoSensitives = videoSensitiveService.list(userSensitiveLambdaQueryWrapper).stream().map(VideoSensitive::getSensitives).collect(Collectors.toList());
        SensitiveWordUtil.initMap(videoSensitives);
        //是否包含敏感词
        Map<String, Integer> map = SensitiveWordUtil.matchWords(str);
        // 存在敏感词
        return map.size() > 0;
    }

    @Override
    public IPage<Video> queryMyVideoPage(VideoPageDto pageDto) {
        Long userId = UserContext.getUser().getUserId();
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getUserId, userId);
        queryWrapper.like(StringUtils.isNotEmpty(pageDto.getVideoTitle()), Video::getVideoTitle, pageDto.getVideoTitle());
        return this.page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()), queryWrapper);
    }

    @Override
    public IPage<Video> queryUserVideoPage(VideoPageDto pageDto) {
        if (StringUtils.isNull(pageDto.getUserId())) {
            return new Page<>();
        }
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getUserId, pageDto.getUserId());
        queryWrapper.like(StringUtils.isNotEmpty(pageDto.getVideoTitle()), Video::getVideoTitle, pageDto.getVideoTitle());
        return this.page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()), queryWrapper);
    }

    /**
     * 视频feed接口
     *
     * @param videoFeedDTO createTime
     * @return video
     */
    @Override
    public List<VideoVO> feedVideo(VideoFeedDTO videoFeedDTO) {
        LocalDateTime createTime = videoFeedDTO.getCreateTime();
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        // 小于 createTime 的5条数据
        queryWrapper.select(Video::getVideoId);
        queryWrapper.lt(Video::getCreateTime, StringUtils.isNull(createTime) ? LocalDateTime.now() : createTime).orderByDesc(Video::getCreateTime).last("limit 3");
        List<Video> videoList;
        try {
            videoList = this.list(queryWrapper);
            if (StringUtils.isNull(videoList) || videoList.isEmpty()) {
                LambdaQueryWrapper<Video> queryWrapper2 = new LambdaQueryWrapper<>();
                // 小于 LocalDateTime.now() 的5条数据
                queryWrapper2.select(Video::getVideoId);
                queryWrapper2.lt(Video::getCreateTime, LocalDateTime.now()).orderByDesc(Video::getCreateTime).last("limit 3");
                videoList = this.list(queryWrapper2);
            }
            // 浏览自增1存入redis
            videoList.forEach(v -> {
                viewNumIncrement(v.getVideoId());
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        List<VideoVO> videoVOList = new ArrayList<>();
        // 封装VideoVO
        videoList.forEach(v -> {
            try {
                Video cacheObject = redisService.getCacheObject(VideoCacheConstants.VIDEO_INFO_PREFIX + v.getVideoId());
                if (StringUtils.isNull(cacheObject)) {
                    v = this.getById(v.getVideoId());
                } else {
                    v = cacheObject;
                }
                VideoVO videoVO = BeanCopyUtils.copyBean(v, VideoVO.class);
                // 封装点赞数，观看量，评论量
                Integer cacheLikeNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_LIKE_NUM_MAP_KEY, v.getVideoId());
                Integer cacheViewNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, v.getVideoId());
                Integer cacheFavoriteNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_FAVORITE_NUM_MAP_KEY, v.getVideoId());
                videoVO.setLikeNum(StringUtils.isNull(cacheLikeNum) ? 0L : cacheLikeNum);
                videoVO.setViewNum(StringUtils.isNull(cacheViewNum) ? 0L : cacheViewNum);
                videoVO.setFavoritesNum(StringUtils.isNull(cacheFavoriteNum) ? 0L : cacheFavoriteNum);
                LambdaQueryWrapper<VideoUserComment> commentQW = new LambdaQueryWrapper<>();
                commentQW.eq(VideoUserComment::getVideoId, v.getVideoId());
                videoVO.setCommentNum(remoteBehaveService.getCommentCountByVideoId(videoVO.getVideoId()).getData());
                // 封装用户信息
                User poublishUser = remoteUserService.userInfoById(v.getUserId()).getData();
                videoVO.setUserNickName(StringUtils.isNull(poublishUser) ? null : poublishUser.getNickName());
                videoVO.setUserAvatar(StringUtils.isNull(poublishUser) ? null : poublishUser.getAvatar());
                // 是否关注
                Long loginUserId = UserContext.getUserId();
                if (StringUtils.isNotNull(loginUserId) && v.getUserId().equals(loginUserId)) {
                    videoVO.setWeatherFollow(true);
                } else {
                    Boolean weatherFollow = remoteSocialService.weatherfollow(v.getUserId()).getData();
                    videoVO.setWeatherFollow(!StringUtils.isNull(weatherFollow) && weatherFollow);
                }
                videoVOList.add(videoVO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return videoVOList;
    }

    private void viewNumIncrement(String videoId) {
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, videoId, 1);
    }

    /**
     * 根据ids查询视频
     *
     * @param videoIds
     * @return
     */
    @Override
    public List<Video> queryVideoByVideoIds(List<String> videoIds) {
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Video::getVideoId, videoIds);
        return this.list(queryWrapper);
    }

    /**
     * @param videoId
     */
    @Transactional
    @Override
    public void deleteVideoByVideoIds(String videoId) {
        //从视频表删除视频（单条）
        videoMapper.deleteById(videoId);
        //从视频分类表关联表删除信息（单条）
        videoCategoryRelationService.removeById(videoId);
        //删除视频对应的es文档
        remoteBehaveService.deleteVideoDocumentByVideoId(videoId);
        //从视频收藏表删除该视频的所有记录
        remoteBehaveService.deleteVideoFavoriteRecordByVideoId(videoId);
        //从视频点赞表删除该视频的所有记录
        remoteBehaveService.deleteVideoLikeRecord(videoId);

    }

    @Override
    public List<Video> getVideoListLtCreateTime(LocalDateTime ctime) {
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        // 视频发布时间大于ctime的数据
        queryWrapper.ge(Video::getCreateTime, ctime);
        return this.list(queryWrapper);
    }

    /**
     * 视频算分
     *
     * @param videoList
     * @return
     */
    @Override
    public List<HotVideoVO> computeHotVideoScore(List<Video> videoList) {
        List<HotVideoVO> hotVideoVOList = new ArrayList<>();
        if (!videoList.isEmpty()) {
            videoList.forEach(v -> {
                HotVideoVO hotVideoVO = BeanCopyUtils.copyBean(v, HotVideoVO.class);
                hotVideoVO.setScore(computeVideoScore(v));
                hotVideoVOList.add(hotVideoVO);
            });
        } else {
            return null;
        }
        return hotVideoVOList;
    }

    // 视频算分
    private Long computeVideoScore(Video video) {
        Long score = 0L;
        if (video.getLikeNum() != null) {
            score += video.getLikeNum() * HotVideoConstants.WEIGHT_LIKE;
        }
        if (video.getViewNum() != null) {
            //获取redis中的浏览量
            score += video.getViewNum() * HotVideoConstants.WEIGHT_VIEW;
        }
        if (video.getFavoritesNum() != null) {
            score += video.getFavoritesNum() * HotVideoConstants.WEIGHT_FAVORITE;
        }
        return score;
    }

}
