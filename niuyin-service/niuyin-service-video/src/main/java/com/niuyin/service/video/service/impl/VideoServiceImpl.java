package com.niuyin.service.video.service.impl;

import com.niuyin.common.context.UserContext;
import com.niuyin.common.exception.CustomException;
import com.niuyin.common.utils.audit.SensitiveWordUtil;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.common.utils.file.PathUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.common.utils.uniqueid.IdGenerator;
import com.niuyin.feign.social.RemoteSocialService;
import com.niuyin.feign.member.RemoteMemberService;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoCategoryRelation;
import com.niuyin.model.video.domain.VideoSensitive;
import com.niuyin.model.behave.domain.VideoUserComment;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.service.video.constants.VideoConstants;
import com.niuyin.service.video.mapper.VideoMapper;
import com.niuyin.service.video.service.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.service.RedisService;
import com.niuyin.feign.behave.RemoteBehaveService;
import com.niuyin.model.search.vo.VideoSearchVO;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.dto.VideoFeedDTO;
import com.niuyin.model.video.dto.VideoPublishDto;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.model.video.vo.HotVideoVO;
import com.niuyin.model.video.vo.VideoUploadVO;
import com.niuyin.model.video.vo.VideoVO;
import com.niuyin.service.video.constants.HotVideoConstants;
import com.niuyin.service.video.constants.QiniuVideoOssConstants;
import com.niuyin.service.video.constants.VideoCacheConstants;
import com.niuyin.starter.file.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.niuyin.model.common.enums.HttpCodeEnum.*;
import static com.niuyin.model.video.mq.VideoDelayedQueueConstant.*;

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
    private RemoteMemberService remoteMemberService;

    @Resource
    private RedisService redisService;

    @Resource
    private IVideoSensitiveService videoSensitiveService;

    @Resource
    private RemoteBehaveService remoteBehaveService;

    @Resource
    private RemoteSocialService remoteSocialService;

    @Resource
    private IVideoTagService videoTagService;

    @Resource
    private IVideoTagRelationService videoTagRelationService;

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
        return videoMapper.selectById(id);
    }

    /**
     * 发布视频
     *
     * @param videoPublishDto
     * @return
     */
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
        video.setCoverImage(StringUtils.isNull(videoPublishDto.getCoverImage()) ? video.getVideoUrl() + VideoCacheConstants.VIDEO_VIEW_COVER_IMAGE_KEY : videoPublishDto.getCoverImage());
        //前端不传不用处理 将前端传递的分类拷贝到关联表对象
        if (StringUtils.isNotNull(videoPublishDto.getCategoryId())) {
            VideoCategoryRelation videoCategoryRelation = BeanCopyUtils.copyBean(videoPublishDto, VideoCategoryRelation.class);
            // video_id存入VideoCategoryRelation（视频分类关联表）
            videoCategoryRelation.setVideoId(video.getVideoId());
            // 再将videoCategoryRelation对象存入video_category_relation表中
            videoCategoryRelationService.saveVideoCategoryRelation(videoCategoryRelation);
        }
        // 视频标签处理
        // 视频标签限制个数，五个
        if (StringUtils.isNotNull(videoPublishDto.getVideoTags())) {
            if (videoPublishDto.getVideoTags().length > VideoConstants.VIDEO_TAG_LIMIT) {
                log.error("视频标签大于5个，不做处理");
            } else {
                videoTagRelationService.saveVideoTagRelationBatch(video.getVideoId(), videoPublishDto.getVideoTags());
            }
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
            Member userCache = redisService.getCacheObject("member:userinfo:" + userId);
            if (StringUtils.isNotNull(userCache)) {
                videoSearchVO.setUserNickName(userCache.getNickName());
                videoSearchVO.setUserAvatar(userCache.getAvatar());
            } else {
                Member remoteUser = remoteMemberService.userInfoById(userId).getData();
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
        queryWrapper.orderByDesc(Video::getCreateTime);
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
        queryWrapper.orderByDesc(Video::getCreateTime);
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
        queryWrapper.lt(Video::getCreateTime, StringUtils.isNull(createTime) ? LocalDateTime.now() : createTime).orderByDesc(Video::getCreateTime).last("limit 10");
        List<Video> videoList;
        try {
            videoList = this.list(queryWrapper);
            if (StringUtils.isNull(videoList) || videoList.isEmpty()) {
                LambdaQueryWrapper<Video> queryWrapper2 = new LambdaQueryWrapper<>();
                // 小于 LocalDateTime.now() 的5条数据
                queryWrapper2.select(Video::getVideoId);
                queryWrapper2.lt(Video::getCreateTime, LocalDateTime.now()).orderByDesc(Video::getCreateTime).last("limit 10");
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
                // 评论数
                videoVO.setCommentNum(videoMapper.selectCommentCountByVideoId(v.getVideoId()));
                Long loginUserId = null;
                try {
                    loginUserId = UserContext.getUserId();
                } catch (Exception ex) {
                    log.debug("未登录");
                }
                if (StringUtils.isNotNull(loginUserId)) {
                    // 是否关注、是否点赞、是否收藏
                    try {
                        videoVO.setWeatherLike(videoMapper.selectUserLikeVideo(v.getVideoId(), loginUserId) != 0);
                        videoVO.setWeatherFavorite(remoteBehaveService.weatherFavorite(v.getVideoId()).getData());
                        if (v.getUserId().equals(loginUserId)) {
                            videoVO.setWeatherFollow(true);
                        } else {
                            Boolean weatherFollow = remoteSocialService.weatherfollow(v.getUserId()).getData();
                            videoVO.setWeatherFollow(!StringUtils.isNull(weatherFollow) && weatherFollow);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 封装用户信息
                Member userCache = redisService.getCacheObject("member:userinfo:" + v.getUserId());
                if (StringUtils.isNotNull(userCache)) {
                    videoVO.setUserNickName(userCache.getNickName());
                    videoVO.setUserAvatar(userCache.getAvatar());
                } else {
                    Member publishUser = remoteMemberService.userInfoById(v.getUserId()).getData();
                    videoVO.setUserNickName(StringUtils.isNull(publishUser) ? "-" : publishUser.getNickName());
                    videoVO.setUserAvatar(StringUtils.isNull(publishUser) ? null : publishUser.getAvatar());
                }
                // 封装标签返回
                String[] tags = videoTagRelationService.queryVideoTags(videoVO.getVideoId());
                videoVO.setTags(tags);
                videoVOList.add(videoVO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return videoVOList;
    }

    @Async
    public void viewNumIncrement(String videoId) {
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
    public void deleteVideoByVideoId(String videoId) {
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
    private double computeVideoScore(Video video) {
        double score = 0;
        // 点赞
        if (video.getLikeNum() != null) {
            score += video.getLikeNum() * HotVideoConstants.WEIGHT_LIKE;
        }
        // 观看
        if (video.getViewNum() != null) {
            //获取redis中的浏览量
            score += video.getViewNum() * HotVideoConstants.WEIGHT_VIEW;
        }
        // 收藏
        if (video.getFavoritesNum() != null) {
            score += video.getFavoritesNum() * HotVideoConstants.WEIGHT_FAVORITE;
        }
        // 创建时间
        if (video.getCreateTime() != null) {
            LocalDateTime createTime = video.getCreateTime();
            Duration between = Duration.between(LocalDateTime.now(), createTime);
            long hours = between.toHours();
            // 计算的是五天的数据量，使用五天总的小时数减去这个差值
            long totalHour = 5 * 24;
            long realHour = totalHour - Math.abs(hours);
            score += realHour * HotVideoConstants.WEIGHT_CREATE_TIME;
        }
        return score / 100;
    }

    /**
     * 视频总获赞量
     *
     * @param userId
     * @return
     */
    @Override
    public Long getVideoLikeAllNumByUserId(Long userId) {
        return videoMapper.selectAllLikeNumForUser(userId);
    }

    /**
     * 查询用户作品数量
     *
     * @return
     */
    @Override
    public Long queryUserVideoCount() {
        return this.count(new LambdaQueryWrapper<Video>().eq(Video::getUserId, UserContext.getUserId()));
    }

    /**
     * 查询用户的作品
     *
     * @param pageDto
     * @return
     */
    @Override
    public IPage<Video> queryMemberVideoPage(VideoPageDto pageDto) {
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getUserId, pageDto.getUserId())
                .eq(Video::getDelFlag, 0);
        queryWrapper.like(StringUtils.isNotEmpty(pageDto.getVideoTitle()), Video::getVideoTitle, pageDto.getVideoTitle());
        queryWrapper.orderByDesc(Video::getCreateTime);
        return this.page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()), queryWrapper);
    }


}
