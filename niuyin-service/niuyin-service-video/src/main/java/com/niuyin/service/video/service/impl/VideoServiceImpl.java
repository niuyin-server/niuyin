package com.niuyin.service.video.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.common.core.exception.CustomException;
import com.niuyin.common.core.service.RedisService;
import com.niuyin.common.core.utils.audit.SensitiveWordUtil;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.date.DateUtils;
import com.niuyin.common.core.utils.file.PathUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.common.core.utils.uniqueid.IdGenerator;
import com.niuyin.dubbo.api.DubboBehaveService;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.feign.behave.RemoteBehaveService;
import com.niuyin.feign.member.RemoteMemberService;
import com.niuyin.feign.social.RemoteSocialService;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.common.enums.DelFlagEnum;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.domain.*;
import com.niuyin.model.video.dto.UpdateVideoDTO;
import com.niuyin.model.video.dto.VideoFeedDTO;
import com.niuyin.model.video.dto.VideoPageDto;
import com.niuyin.model.video.dto.VideoPublishDto;
import com.niuyin.model.video.enums.PositionFlag;
import com.niuyin.model.video.enums.PublishType;
import com.niuyin.model.video.vo.*;
import com.niuyin.model.video.vo.app.MyVideoVO;
import com.niuyin.model.video.vo.app.VideoInfoVO;
import com.niuyin.model.video.vo.app.VideoRecommendVO;
import com.niuyin.service.video.constants.HotVideoConstants;
import com.niuyin.service.video.constants.QiniuVideoOssConstants;
import com.niuyin.model.constants.VideoCacheConstants;
import com.niuyin.service.video.constants.VideoConstants;
import com.niuyin.service.video.domain.MediaVideoInfo;
import com.niuyin.service.video.mapper.VideoMapper;
import com.niuyin.service.video.service.*;
import com.niuyin.service.video.service.cache.VideoRedisBatchCache;
import com.niuyin.starter.file.service.FileStorageService;
import com.niuyin.starter.video.service.FfmpegVideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.info.MultimediaInfo;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.niuyin.model.common.enums.HttpCodeEnum.SENSITIVEWORD_ERROR;
import static com.niuyin.model.video.mq.VideoDelayedQueueConstant.*;
import static com.niuyin.model.video.mq.VideoDirectExchangeConstant.DIRECT_KEY_INFO;
import static com.niuyin.model.video.mq.VideoDirectExchangeConstant.EXCHANGE_VIDEO_DIRECT;
import static com.niuyin.service.video.config.AsyncConfig.VIDEO_EXECUTOR;
import static com.niuyin.service.video.constants.HotVideoConstants.VIDEO_BEFORE_DAT30;
import static com.niuyin.service.video.constants.InterestPushConstant.VIDEO_CATEGORY_VIDEOS_CACHE_KEY_PREFIX;
import static com.niuyin.service.video.constants.InterestPushConstant.VIDEO_TAG_VIDEOS_CACHE_KEY_PREFIX;
import static com.niuyin.model.constants.VideoCacheConstants.*;

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
    private RedisTemplate<String, Object> redisTemplate;

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

    @Resource
    private IVideoImageService videoImageService;

    @Resource
    private IVideoPositionService videoPositionService;

    @Resource
    private InterestPushService interestPushService;

    @DubboReference(loadbalance = "random", mock = "return null")
    private DubboMemberService dubboMemberService;

    @Resource
    private IUserVideoCompilationRelationService userVideoCompilationRelationService;

    @DubboReference(mock = "return null")
    private DubboBehaveService dubboBehaveService;

    @Resource
    private FfmpegVideoService ffmpegVideoService;

    @Resource
    private UserFollowVideoPushService userFollowVideoPushService;

    @Resource
    private VideoRedisBatchCache videoRedisBatchCache;

    /**
     * 解决异步线程无法访问主线程的ThreadLocal
     */
    private static final ThreadLocal<Long> userIdThreadLocal = new InheritableThreadLocal<>();

    public static void setUserId(Long userId) {
        userIdThreadLocal.set(userId);
    }

    public static Long getUserId() {
        return userIdThreadLocal.get();
    }

    /**
     * 上传视频
     *
     * @param file
     * @return
     */
    @Override
    public VideoUploadVO uploadVideo(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        //对文件id进行判断，如果文件已经存在，则不上传，直接返回数据库中文件的存储路径
        String filePath = PathUtils.generateFilePath(originalFilename);
        String uploadVideo = fileStorageService.uploadVideo(file, filePath);
        VideoUploadVO videoUploadVO = new VideoUploadVO();
        videoUploadVO.setOriginUrl(QiniuVideoOssConstants.VIDEO_ORIGIN_PREFIX_URL + uploadVideo);
        videoUploadVO.setVideoUrl(QiniuVideoOssConstants.VIDEO_PREFIX_URL_2K + uploadVideo);
        videoUploadVO.setVframe(QiniuVideoOssConstants.VIDEO_FRAME_PREFIX_URL + uploadVideo + QiniuVideoOssConstants.VIDEO_FRAME_1_END);
        return videoUploadVO;
    }

    /**
     * 发布视频
     * todo 分布式锁解决幂等性
     * todo 优化使用异步完成各个步骤
     *
     * @param videoPublishDto
     * @return
     */
    @Transactional
    @Override
    public String videoPublish(VideoPublishDto videoPublishDto) {
        Long userId = UserContext.getUser().getUserId();
        // 数据库敏感词字典树过滤
        boolean b = sensitiveCheck(videoPublishDto.getVideoTitle() + videoPublishDto.getVideoDesc());
        if (b) {
            // 存在敏感词抛异常
            throw new CustomException(SENSITIVEWORD_ERROR);
        }
        // 判断发布类型 publicType 0视频1图文
        if (videoPublishDto.getPublishType().equals(PublishType.VIDEO.getCode())) {
            // 发布为视频
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
                // 关联视频合集
                if (StringUtils.isNotNull(videoPublishDto.getCompilationId())) {
                    userVideoCompilationRelationService.videoRelateCompilation(video.getVideoId(), videoPublishDto.getCompilationId());
                }
                // 发布成功添加缓存
                redisService.setCacheObject(VIDEO_INFO_PREFIX + video.getVideoId(), video);
                // 发送rabbit消息
                rabbitTemplate.convertAndSend(ESSYNC_DELAYED_EXCHANGE, ESSYNC_ROUTING_KEY, videoId, message -> {
                    // 添加延迟消息属性，设置1分钟
                    message.getMessageProperties().setDelay(ESSYNC_DELAYED_TIME);
                    return message;
                });
                log.debug(" ==> {} 发送了一条消息 ==> {}", ESSYNC_DELAYED_EXCHANGE, videoId);
                // 同步视频标签库
                interestPushService.cacheVideoToTagRedis(video.getVideoId(), Arrays.asList(videoPublishDto.getVideoTags()));
                // 同步视频详情
                rabbitTemplate.convertAndSend(EXCHANGE_VIDEO_DIRECT, DIRECT_KEY_INFO, videoId);
                log.debug(" ==> {} 发送了一条消息 ==> {}", EXCHANGE_VIDEO_DIRECT, videoId);
                // 同步用户发件箱
                userFollowVideoPushService.pusOutBoxFeed(video.getUserId(), videoId, DateUtils.toDate(video.getCreateTime()).getTime());
                return videoId;
            } else {
                throw new CustomException(null);
            }
        } else if (videoPublishDto.getPublishType().equals(PublishType.IMAGE.getCode())) {
            // 发布为图文
            Video video = BeanCopyUtils.copyBean(videoPublishDto, Video.class);
            video.setVideoId(IdGenerator.generatorShortId());
            video.setUserId(userId);
            video.setCreateTime(LocalDateTime.now());
            video.setCreateBy(userId.toString());
            // 设置图文封面，若为空则使用图片集合的第一条
            video.setCoverImage(StringUtils.isEmpty(videoPublishDto.getCoverImage()) ? videoPublishDto.getImageFileList()[0] : videoPublishDto.getCoverImage());
            // 前端不传不用处理 将前端传递的分类拷贝到关联表对象，图文类型暂不设置分类
//            if (StringUtils.isNotNull(videoPublishDto.getCategoryId())) {
//                VideoCategoryRelation videoCategoryRelation = BeanCopyUtils.copyBean(videoPublishDto, VideoCategoryRelation.class);
//                // video_id存入VideoCategoryRelation（视频分类关联表）
//                videoCategoryRelation.setVideoId(video.getVideoId());
//                // 再将videoCategoryRelation对象存入video_category_relation表中
//                videoCategoryRelationService.saveVideoCategoryRelation(videoCategoryRelation);
//            }
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
                // 关联视频合集
                if (StringUtils.isNotNull(videoPublishDto.getCompilationId())) {
                    userVideoCompilationRelationService.videoRelateCompilation(video.getVideoId(), videoPublishDto.getCompilationId());
                }
                // 发布成功添加缓存
                redisService.setCacheObject(VIDEO_INFO_PREFIX + video.getVideoId(), video);
                // 异步批量保存图片集合到mysql
                CompletableFuture<Void> allFutures = CompletableFuture.allOf(Arrays.stream(videoPublishDto.getImageFileList())
                        .map(url -> asyncSaveVideoImagesToDB(video.getVideoId(), url)).toArray(CompletableFuture[]::new));
                allFutures.join();
                // 开始存储视频发布位置
                if (videoPublishDto.getPositionFlag().equals(PositionFlag.OPEN.getCode())) {
                    VideoPosition videoPosition = BeanCopyUtils.copyBean(videoPublishDto.getPosition(), VideoPosition.class);
                    videoPosition.setVideoId(video.getVideoId());
                    boolean save1 = videoPositionService.save(videoPosition);
                }
                // 发送消息
                rabbitTemplate.convertAndSend(ESSYNC_DELAYED_EXCHANGE, ESSYNC_ROUTING_KEY, video.getVideoId(), message -> {
                    // 添加延迟消息属性，设置1分钟
                    message.getMessageProperties().setDelay(ESSYNC_DELAYED_TIME);
                    return message;
                });
                log.debug(" ==> {} 发送了一条消息 ==> {}", ESSYNC_DELAYED_EXCHANGE, video.getVideoId());
                // 同步视频标签库
                interestPushService.cacheVideoToTagRedis(video.getVideoId(), Arrays.asList(videoPublishDto.getVideoTags()));
                // 同步视频分类库

                // 同步视频观看量到redis

                // 同步用户发件箱
                userFollowVideoPushService.pusOutBoxFeed(video.getUserId(), video.getVideoId(), DateUtils.toDate(video.getCreateTime()).getTime());
                return video.getVideoId();
            }
        } else {
            return "";
        }
        return "";
    }

    /**
     * 异步执行同步数据操作
     *
     * @param videoId
     * @return
     */
    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> asyncSaveVideoImagesToDB(String videoId, String imageUrl) {
        VideoImage videoImage = new VideoImage();
        videoImage.setVideoId(videoId);
        videoImage.setImageUrl(imageUrl);
        return CompletableFuture.runAsync(() -> videoImageService.save(videoImage));
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
    public PageDataInfo queryMyVideoPage(VideoPageDto pageDto) {
        pageDto.setUserId(UserContext.getUserId());
        return getUserVideoPage(pageDto);
    }

    @Override
    public PageDataInfo queryMyVideoPageForApp(VideoPageDto pageDto) {
        pageDto.setUserId(UserContext.getUserId());
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getUserId, pageDto.getUserId());
        queryWrapper.eq(Video::getDelFlag, DelFlagEnum.EXIST.getCode());
        queryWrapper.like(StringUtils.isNotEmpty(pageDto.getVideoTitle()), Video::getVideoTitle, pageDto.getVideoTitle());
        queryWrapper.orderByDesc(Video::getCreateTime);
        IPage<Video> videoIPage = this.page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()), queryWrapper);
        List<Video> records = videoIPage.getRecords();
        if (StringUtils.isNull(records) || records.isEmpty()) {
            return PageDataInfo.emptyPage();
        }
        List<MyVideoVO> myVideoVOList = BeanCopyUtils.copyBeanList(records, MyVideoVO.class);
        // 设置点赞量
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(myVideoVOList.stream()
                .map(this::packageMyVideoVOAsync).toArray(CompletableFuture[]::new));
        allFutures.join();
        return PageDataInfo.genPageData(myVideoVOList, videoIPage.getTotal());
    }

    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> packageMyVideoVOAsync(MyVideoVO vo) {
        return CompletableFuture.runAsync(() -> packageMyUserVideoVO(vo));
    }

    @Async(VIDEO_EXECUTOR)
    public void packageMyUserVideoVO(MyVideoVO vo) {
        Long likeNum = dubboBehaveService.apiGetVideoLikeNum(vo.getVideoId());
        vo.setLikeNum(Objects.isNull(likeNum) ? 0L : likeNum);
    }

    @Override
    public PageDataInfo queryUserVideoPage(VideoPageDto pageDto) {
        if (StringUtils.isNull(pageDto.getUserId())) {
            return PageDataInfo.emptyPage();
        }
        return getUserVideoPage(pageDto);
    }

    @Async(VIDEO_EXECUTOR)
    public PageDataInfo getUserVideoPage(VideoPageDto pageDto) {
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getUserId, pageDto.getUserId());
        queryWrapper.eq(Video::getDelFlag, DelFlagEnum.EXIST.getCode());
        queryWrapper.like(StringUtils.isNotEmpty(pageDto.getVideoTitle()), Video::getVideoTitle, pageDto.getVideoTitle());
        queryWrapper.orderByDesc(Video::getCreateTime);
        IPage<Video> videoIPage = this.page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()), queryWrapper);
        List<Video> records = videoIPage.getRecords();
        if (StringUtils.isNull(records) || records.isEmpty()) {
            return PageDataInfo.emptyPage();
        }
        List<VideoVO> videoVOList = BeanCopyUtils.copyBeanList(records, VideoVO.class);
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(videoVOList.stream()
                .map(this::packageUserVideoVOAsync).toArray(CompletableFuture[]::new));
        allFutures.join();
        return PageDataInfo.genPageData(videoVOList, videoIPage.getTotal());
    }

    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> packageUserVideoVOAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageUserVideoVO(videoVO));
    }

    @Async(VIDEO_EXECUTOR)
    public void packageUserVideoVO(VideoVO videoVO) {
        CompletableFuture<Void> behaveDataFuture = packageVideoBehaveDataAsync(videoVO);
        CompletableFuture<Void> memberDataFuture = packageMemberDataAsync(videoVO);
        CompletableFuture<Void> imageDataFuture = packageVideoImageDataAsync(videoVO);
        CompletableFuture.allOf(
                behaveDataFuture,
                memberDataFuture,
                imageDataFuture
        ).join();
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
        log.debug("mp开始feed");
        // 小于 createTime 的数据
        queryWrapper.lt(Video::getCreateTime, StringUtils.isNull(createTime) ? LocalDateTime.now() : createTime)
                .eq(Video::getDelFlag, DelFlagEnum.EXIST.getCode())
                .orderByDesc(Video::getCreateTime)
                .last("limit 10");
        List<Video> videoList = this.list(queryWrapper);
        log.debug("mp结束feed");
        List<VideoVO> videoVOList = BeanCopyUtils.copyBeanList(videoList, VideoVO.class);
        // 封装VideoVO
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(videoVOList
                .stream()
                .map(vo -> packageVideoVOAsync(vo, UserContext.getUserId()))
                .toArray(CompletableFuture[]::new));
        allFutures.join();
        return videoVOList;
    }

    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> packageVideoVOAsync(VideoVO videoVO, Long loginUserId) {
        return CompletableFuture.runAsync(() -> packageVideoVO(videoVO, loginUserId));
    }

    @Async(VIDEO_EXECUTOR)
    public void packageVideoVO(VideoVO videoVO, Long loginUserId) {
        log.debug("packageVideoVO开始");
        CompletableFuture<Void> viewNumFuture = viewNumIncrementAsync(videoVO.getVideoId());
        CompletableFuture<Void> behaveDataFuture = packageVideoBehaveDataAsync(videoVO);
        CompletableFuture<Void> socialDataFuture = packageVideoSocialDataAsync(videoVO, loginUserId);
        CompletableFuture<Void> memberDataFuture = packageMemberDataAsync(videoVO);
        CompletableFuture<Void> tagDataFuture = packageVideoTagDataAsync(videoVO);
        CompletableFuture<Void> imageDataFuture = packageVideoImageDataAsync(videoVO);
        CompletableFuture<Void> positionDataFuture = packageVideoPositionDataAsync(videoVO);
        CompletableFuture.allOf(
                viewNumFuture,
                behaveDataFuture,
                socialDataFuture,
                memberDataFuture,
                tagDataFuture,
                imageDataFuture,
                positionDataFuture
        ).join();
        log.debug("packageVideoVO结束");
    }

    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> viewNumIncrementAsync(String videoId) {
        return CompletableFuture.runAsync(() -> viewNumIncrement(videoId));
    }

    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> packageVideoBehaveDataAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageVideoBehaveData(videoVO));
    }

    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> packageVideoSocialDataAsync(VideoVO videoVO, Long loginUserId) {
        return CompletableFuture.runAsync(() -> packageVideoSocialData(videoVO, loginUserId));
    }

    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> packageMemberDataAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageMemberData(videoVO));
    }

    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> packageVideoTagDataAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageVideoTagData(videoVO));
    }

    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> packageVideoImageDataAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageVideoImageData(videoVO));
    }

    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> packageVideoPositionDataAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageVideoPositionData(videoVO));
    }

    /**
     * 浏览量自增1存入redis
     *
     * @param videoId
     */
    @Async(VIDEO_EXECUTOR)
    public void viewNumIncrement(String videoId) {
        log.debug("viewNumIncrement开始");
        if (StringUtils.isNotEmpty(videoId)) {
            redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, videoId, 1);
        }
        log.debug("viewNumIncrement结束");
    }

    /**
     * 封装视频行为数据
     *
     * @param videoVO
     */
    @Async(VIDEO_EXECUTOR)
    public void packageVideoBehaveData(VideoVO videoVO) {
        log.debug("packageVideoBehaveData开始");
        // 封装观看量、点赞数、收藏量
        Integer cacheViewNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, videoVO.getVideoId());
        videoVO.setViewNum(StringUtils.isNull(cacheViewNum) ? 0L : cacheViewNum);
        Long likeNum = dubboBehaveService.apiGetVideoLikeNum(videoVO.getVideoId());
        videoVO.setLikeNum(likeNum == null ? 0L : likeNum);
        Long favoriteNum = dubboBehaveService.apiGetVideoFavoriteNum(videoVO.getVideoId());
        videoVO.setFavoritesNum(favoriteNum == null ? 0L : favoriteNum);
        // 评论数
        Long commentNum = dubboBehaveService.apiGetVideoCommentNum(videoVO.getVideoId());
        videoVO.setCommentNum(commentNum == null ? 0L : commentNum);
        log.debug("packageVideoBehaveData结束");
    }

    /**
     * 封装用户数据
     *
     * @param videoVO
     */
    @Async(VIDEO_EXECUTOR)
    public void packageMemberData(VideoVO videoVO) {
        log.debug("packageMemberData开始");
        // 封装用户信息
        Member userCache = redisService.getCacheObject("member:userinfo:" + videoVO.getUserId());
        if (StringUtils.isNotNull(userCache)) {
            videoVO.setUserNickName(userCache.getNickName());
            videoVO.setUserAvatar(userCache.getAvatar());
        } else {
            Member publishUser = videoMapper.selectVideoAuthor(videoVO.getUserId());
            videoVO.setUserNickName(StringUtils.isNull(publishUser) ? "-" : publishUser.getNickName());
            videoVO.setUserAvatar(StringUtils.isNull(publishUser) ? null : publishUser.getAvatar());
        }
        log.debug("packageMemberData结束");
    }

    /**
     * 封装视频社交数据
     *
     * @param videoVO
     */
    @Async(VIDEO_EXECUTOR)
    public void packageVideoSocialData(VideoVO videoVO, Long loginUserId) {
        log.debug("packageVideoSocialData开始" + getUserId());
        if (StringUtils.isNotNull(loginUserId)) {
            // 是否关注、是否点赞、是否收藏
            videoVO.setWeatherLike(videoMapper.selectUserLikeVideo(videoVO.getVideoId(), loginUserId) > 0);
            videoVO.setWeatherFavorite(videoMapper.userWeatherFavoriteVideo(videoVO.getVideoId(), loginUserId) > 0);
            if (videoVO.getUserId().equals(loginUserId)) {
                videoVO.setWeatherFollow(true);
            } else {
                videoVO.setWeatherFollow(videoMapper.userWeatherAuthor(loginUserId, videoVO.getUserId()) > 0);
            }
        }
        log.debug("packageVideoSocialData结束");
    }

    /**
     * 封装视频标签数据
     */
    @Async(VIDEO_EXECUTOR)
    public void packageVideoTagData(VideoVO videoVO) {
        log.debug("packageVideoTagData开始");
        // 封装标签返回
        String[] tags = videoTagRelationService.queryVideoTags(videoVO.getVideoId());
        videoVO.setTags(tags);
        log.debug("packageVideoTagData结束");
    }

    /**
     * 封装图文数据
     *
     * @param videoVO
     */
    @Async(VIDEO_EXECUTOR)
    public void packageVideoImageData(VideoVO videoVO) {
        log.debug("packageVideoImageData开始");
        // 若是图文则封装图片集合
        if (videoVO.getPublishType().equals(PublishType.IMAGE.getCode())) {
            Object imgsCacheObject = redisService.getCacheObject(VIDEO_IMAGES_PREFIX_KEY + videoVO.getVideoId());
            if (StringUtils.isNotNull(imgsCacheObject)) {
                if (imgsCacheObject instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) imgsCacheObject;
                    videoVO.setImageList(jsonArray.toArray(new String[0]));
                } else if (imgsCacheObject instanceof String) {
                    String jsonString = (String) imgsCacheObject;
                    videoVO.setImageList(JSON.parseObject(jsonString, String[].class));
                }
            } else {
                List<VideoImage> videoImageList = videoImageService.queryImagesByVideoId(videoVO.getVideoId());
                String[] imgs = videoImageList.stream().map(VideoImage::getImageUrl).toArray(String[]::new);
                videoVO.setImageList(imgs);
                // 重建缓存
                redisService.setCacheObject(VIDEO_IMAGES_PREFIX_KEY + videoVO.getVideoId(), imgs);
                redisService.expire(VIDEO_IMAGES_PREFIX_KEY + videoVO.getVideoId(), 1, TimeUnit.DAYS);
            }
        }
        log.debug("packageVideoImageData结束");
    }

    /**
     * 封装视频定位数据
     *
     * @param videoVO
     */
    @Async(VIDEO_EXECUTOR)
    public void packageVideoPositionData(VideoVO videoVO) {
        log.debug("packageVideoPositionData开始");
        // 若是开启定位，封装定位
        if (videoVO.getPositionFlag().equals(PositionFlag.OPEN.getCode())) {
            // 查询redis缓存
            VideoPosition videoPositionCache = redisService.getCacheObject(VIDEO_POSITION_PREFIX_KEY + videoVO.getVideoId());
            if (StringUtils.isNotNull(videoPositionCache)) {
                videoVO.setPosition(videoPositionCache);
            } else {
                VideoPosition videoPosition = videoPositionService.queryPositionByVideoId(videoVO.getVideoId());
                videoVO.setPosition(videoPosition);
                // 重建缓存
                redisService.setCacheObject(VIDEO_POSITION_PREFIX_KEY + videoVO.getVideoId(), videoPosition);
                redisService.expire(VIDEO_POSITION_PREFIX_KEY + videoVO.getVideoId(), 1, TimeUnit.DAYS);
            }
        }
        log.debug("packageVideoPositionData结束");
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
        queryWrapper.orderByDesc(Video::getCreateTime);
        return this.list(queryWrapper);
    }

    /**
     * 删除视频
     *
     * @param videoId
     */
    @Transactional
    @Override
    public boolean deleteVideoByVideoId(String videoId) {
        // 从视频表删除视频（单条） todo 还得验证当前登录用户
        boolean deleted = this.deleteVideoByUser(videoId);
        if (deleted) {
            // 删除相关redis缓存
            // 删除视频分类库记录
            List<Long> categoryIds = videoCategoryRelationService.queryVideoCategoryIdsByVideoId(videoId);
            if (!categoryIds.isEmpty()) {
                categoryIds.forEach(cid -> {
                    redisTemplate.opsForSet().remove(VIDEO_CATEGORY_VIDEOS_CACHE_KEY_PREFIX + cid, videoId);
                });
            }
            // 删除标签库该视频记录x
            List<Long> videoTagIds = videoTagRelationService.queryVideoTagIdsByVideoId(videoId);
            if (!videoTagIds.isEmpty()) {
                videoTagIds.forEach(tid -> {
                    redisTemplate.opsForSet().remove(VIDEO_TAG_VIDEOS_CACHE_KEY_PREFIX + tid, videoId);
                });
            }
            // 删除视频缓存
            redisService.deleteObject(VIDEO_INFO_PREFIX + videoId);
            // 删除热门视频记录缓存
            redisTemplate.opsForZSet().remove(VIDEO_HOT, videoId);
            // todo 删除视频观看量缓存

            // 删除图文视频图片数据
            videoImageService.deleteVideoImagesByVideoId(videoId);
            // 从视频分类表关联表删除信息
            videoCategoryRelationService.deleteRecordByVideoId(videoId);
            // 删除视频对应的es文档
//            remoteBehaveService.deleteVideoDocumentByVideoId(videoId);
            // 删除视频评论
            dubboBehaveService.removeVideoCommentByVideoId(videoId);
            // 删除视频标签关联表
            videoTagRelationService.deleteRecordByVideoId(videoId);
            // 删除视频位置信息表
            videoPositionService.deleteRecordByVideoId(videoId);
            // 删除视频合集关联表
            userVideoCompilationRelationService.deleteRecordByVideoId(videoId);
            // 删除别的用户对此视频点赞、收藏记录
            dubboBehaveService.removeOtherLikeVideoBehaveRecord(videoId);
            dubboBehaveService.removeOtherFavoriteVideoBehaveRecord(videoId);
            // todo 删除 es 文档
        }
        return deleted;
    }

    /**
     * 隐式删除视频
     *
     * @param videoId
     * @return
     */
    @Override
    public boolean deleteVideoByUser(String videoId) {
        Long userId = UserContext.getUserId();
        LambdaUpdateWrapper<Video> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(Video::getVideoId, videoId);
        queryWrapper.eq(Video::getUserId, userId);
        queryWrapper.set(Video::getDelFlag, DelFlagEnum.DELETED.getCode());
        return this.update(queryWrapper);
    }

    @Override
    public List<Video> getVideoListLtCreateTime(LocalDateTime ctime) {
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        // 视频发布时间大于ctime的数据
        queryWrapper.ge(Video::getCreateTime, ctime);
        queryWrapper.eq(Video::getDelFlag, DelFlagEnum.EXIST.getCode());
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
            return new ArrayList<>();
        }
        return hotVideoVOList;
    }

    // 视频算分
    private double computeVideoScore(Video video) {
        double score = 0;
        // 观看
        if (video.getViewNum() != null) {
            //获取redis中的浏览量
            score += video.getViewNum() * HotVideoConstants.WEIGHT_VIEW;
        }
        // 点赞
        Long likeCount = videoMapper.selectLikeCountByVideoId(video.getVideoId());
        score += likeCount * HotVideoConstants.WEIGHT_LIKE;
        // 收藏
        Long favoriteCount = videoMapper.selectFavoriteCountByVideoId(video.getVideoId());
        score += favoriteCount * HotVideoConstants.WEIGHT_FAVORITE;
        // 创建时间
        if (video.getCreateTime() != null) {
            LocalDateTime createTime = video.getCreateTime();
            Duration between = Duration.between(LocalDateTime.now(), createTime);
            long hours = between.toHours();
            // 计算的是7天的数据量，使用7天总的小时数减去这个差值
            long totalHour = VIDEO_BEFORE_DAT30 * 24;
            long realHour = totalHour - Math.abs(hours);
            score += Math.abs(realHour) * HotVideoConstants.WEIGHT_CREATE_TIME;
        }
        // 评论量
        Long commentCount = videoMapper.selectCommentCountByVideoId(video.getVideoId());
        score += commentCount * HotVideoConstants.WEIGHT_COMMENT;
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
        return this.count(new LambdaQueryWrapper<Video>().eq(Video::getUserId, UserContext.getUserId()).eq(Video::getDelFlag, DelFlagEnum.EXIST.getCode()));
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

    /**
     * @param pageDTO
     * @return
     */
    @Override
    public PageDataInfo getHotVideos(PageDTO pageDTO) {
//        int startIndex = (pageDTO.getPageNum() - 1) * pageDTO.getPageSize();
//        int endIndex = startIndex + pageDTO.getPageSize() - 1;
//        Set videoIds = redisService.getCacheZSetRange(VideoCacheConstants.VIDEO_HOT, startIndex, endIndex);
//        Long hotCount = redisService.getCacheZSetZCard(VideoCacheConstants.VIDEO_HOT);
//        List<VideoVO> videoVOList = new ArrayList<>();
//        // 使用 parallelStream 并行流相较于 stream 流，性能更高
//        List<CompletableFuture<Void>> futures = (List<CompletableFuture<Void>>) videoIds.parallelStream()
//                .map(vid -> CompletableFuture.supplyAsync(() -> {
//                    Video video = this.getById((String) vid);
//                    Member user = new Member();
//                    try {
//                        // 作者信息批量查询，相较于单条查询性能更高
//                        List<Member> authors = videoMapper.batchSelectVideoAuthor(Collections.singletonList(video.getUserId()));
//                        if (!authors.isEmpty()) {
//                            user = authors.get(0);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    VideoVO videoVO = BeanCopyUtils.copyBean(video, VideoVO.class);
//                    if (StringUtils.isNotNull(user)) {
//                        videoVO.setUserNickName(user.getNickName());
//                        videoVO.setUserAvatar(user.getAvatar());
//                    }
//                    // todo 是否关注
//                    videoVO.setHotScore(redisService.getZSetScore(VideoCacheConstants.VIDEO_HOT, (String) vid));
//                    videoVOList.add(videoVO);
//                    return videoVO;
//                })).collect(Collectors.toList());
//        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//        allFutures.join();
//        return PageDataInfo.genPageData(videoVOList, hotCount);
        int startIndex = (pageDTO.getPageNum() - 1) * pageDTO.getPageSize();
        int endIndex = startIndex + pageDTO.getPageSize() - 1;
        Set<Object> videoIds = redisService.getCacheZSetRange(VideoCacheConstants.VIDEO_HOT, startIndex, endIndex);
        Long hotCount = redisService.getCacheZSetZCard(VideoCacheConstants.VIDEO_HOT);

        List<CompletableFuture<VideoVO>> futures = videoIds.parallelStream()
                .map(vid -> CompletableFuture.supplyAsync(() -> {
                    Video video = this.getById((String) vid);
                    List<Member> authors = videoMapper.batchSelectVideoAuthor(Collections.singletonList(video.getUserId()));
                    Member user = authors.isEmpty() ? new Member() : authors.get(0);
                    VideoVO videoVO = BeanCopyUtils.copyBean(video, VideoVO.class);
                    videoVO.setUserNickName(user.getNickName());
                    videoVO.setUserAvatar(user.getAvatar());
                    // todo 是否关注
                    videoVO.setHotScore(redisService.getZSetScore(VideoCacheConstants.VIDEO_HOT, (String) vid));
                    return videoVO;
                })).collect(Collectors.toList());

        List<VideoVO> videoVOList = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return PageDataInfo.genPageData(videoVOList, hotCount);

    }

    @Override
    public List<VideoVO> pushVideoList() {
        Member member;
        if (!UserContext.hasLogin()) {
            // 游客登陆
            member = new Member();
            member.setUserId(2L);
        } else {
            member = dubboMemberService.apiGetById(UserContext.getUserId());
        }
        Collection<String> videoIdsByUserModel = interestPushService.getVideoIdsByUserModel(member);
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Video::getVideoId, videoIdsByUserModel);
        List<Video> videoList = this.list(queryWrapper);
        List<VideoVO> videoVOList = BeanCopyUtils.copyBeanList(videoList, VideoVO.class);
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(videoVOList
                .stream()
                .map(this::packageUserVideoVOAsync)
                .toArray(CompletableFuture[]::new));
        allFutures.join();
        return videoVOList;
    }

    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> packagePushVideoVOAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packagePushVideoVO(videoVO));
    }

    @Async(VIDEO_EXECUTOR)
    public void packagePushVideoVO(VideoVO videoVO) {
        log.debug("packagePushVideoVO");
        CompletableFuture<Void> memberDataFuture = packageMemberDataAsync(videoVO);
        CompletableFuture<Void> imageDataFuture = packageVideoImageDataAsync(videoVO);
        CompletableFuture.allOf(
                memberDataFuture,
                imageDataFuture
        ).join();
        log.debug("packagePushVideoVO");
    }

    /**
     * 更新视频
     *
     * @param updateVideoDTO
     */
    @Override
    public boolean updateVideo(UpdateVideoDTO updateVideoDTO) {
        if (StringUtils.isEmpty(updateVideoDTO.getVideoId())) {
            return false;
        }
        Video dbVideo = this.getById(updateVideoDTO.getVideoId());
        if (StringUtils.isNotNull(dbVideo) && UserContext.getUserId().equals(dbVideo.getUserId())) {
            LambdaUpdateWrapper<Video> queryWrapper = new LambdaUpdateWrapper<>();
            queryWrapper.eq(Video::getVideoId, updateVideoDTO.getVideoId());
            queryWrapper.set(StringUtils.isNotEmpty(updateVideoDTO.getVideoTitle()), Video::getVideoTitle, updateVideoDTO.getVideoTitle());
            queryWrapper.set(StringUtils.isNotEmpty(updateVideoDTO.getVideoDesc()), Video::getVideoDesc, updateVideoDTO.getVideoDesc());
            queryWrapper.set(StringUtils.isNotEmpty(updateVideoDTO.getCoverImage()), Video::getCoverImage, updateVideoDTO.getCoverImage());
            queryWrapper.set(StringUtils.isNotEmpty(updateVideoDTO.getShowType()), Video::getShowType, updateVideoDTO.getShowType());
            queryWrapper.set(StringUtils.isNotEmpty(updateVideoDTO.getPositionFlag()), Video::getPositionFlag, updateVideoDTO.getPositionFlag());
            boolean update = this.update(queryWrapper);
            if (update) {
                // 修改成功更新缓存
                if (StringUtils.isNotEmpty(updateVideoDTO.getVideoTitle())) {
                    dbVideo.setVideoTitle(updateVideoDTO.getVideoTitle());
                }
                if (StringUtils.isNotEmpty(updateVideoDTO.getVideoDesc())) {
                    dbVideo.setVideoDesc(updateVideoDTO.getVideoDesc());
                }
                if (StringUtils.isNotEmpty(updateVideoDTO.getCoverImage())) {
                    dbVideo.setCoverImage(updateVideoDTO.getCoverImage());
                }
                if (StringUtils.isNotEmpty(updateVideoDTO.getShowType())) {
                    dbVideo.setShowType(updateVideoDTO.getShowType());
                }
                if (StringUtils.isNotEmpty(updateVideoDTO.getPositionFlag())) {
                    dbVideo.setPositionFlag(updateVideoDTO.getPositionFlag());
                }
                redisService.setCacheObject(VIDEO_INFO_PREFIX + updateVideoDTO.getVideoId(), dbVideo);
            }
        }
        return false;
    }

    /**
     * 根据视频ids查询
     *
     * @param videoIds
     * @return
     */
    @Override
    public List<Video> listByVideoIds(List<String> videoIds) {
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Video::getVideoId, videoIds);
        return this.list(queryWrapper);
    }

    /**
     * 获取视频图文
     *
     * @param videoId
     * @return
     */
    @Override
    public String[] getVideoImages(String videoId) {
        List<VideoImage> videoImageList = videoImageService.queryImagesByVideoId(videoId);
        return videoImageList.stream().map(VideoImage::getImageUrl).toArray(String[]::new);
    }

    /**
     * 获取用户所有视频
     *
     * @param userId
     * @return
     */
    @Override
    public List<Video> getUserAllVideo(Long userId) {
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getUserId, userId);
        return this.list(queryWrapper);
    }

    /**
     * 获取所有未删除的视频
     *
     * @return
     */
    @Override
    public List<Video> getAllUnDeletedVideo() {
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getDelFlag, DelFlagEnum.EXIST.getCode());
        return this.list(queryWrapper);
    }

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    /**
     * 相关视频推荐 todo
     *
     * @param videoId
     * @return
     */
    @Override
    public List<RelateVideoVO> getRelateVideoList(String videoId) {
        Set<String> resultVideoIds = new HashSet<>(); // 推荐结果
        int videoCount = 10; // 需要推荐的视频个数
        // 查询视频的标签
        List<Long> videoTagIds = videoTagRelationService.queryVideoTagIdsByVideoId(videoId);
        // 每个标签需要推荐的视频数
        int ceil = (int) Math.ceil((double) videoCount / videoTagIds.size());

        List<Callable<Void>> tasks = new ArrayList<>();
        for (Long tagId : videoTagIds) {
            tasks.add(() -> {
                String tagSetKey = VIDEO_TAG_VIDEOS_CACHE_KEY_PREFIX + tagId;
                List<Object> vIds = redisTemplate.opsForSet().randomMembers(tagSetKey, ceil);
                if (vIds != null && !vIds.isEmpty()) {
                    synchronized (resultVideoIds) {
                        resultVideoIds.addAll(vIds.stream().map(Object::toString).collect(Collectors.toList()));
                    }
                }
                return null;
            });
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        resultVideoIds.forEach(System.out::println);

        Map<String, Video> batch = videoRedisBatchCache.getBatch(new ArrayList<>(resultVideoIds));
        List<Video> videoList = new ArrayList<>(batch.values());
        List<RelateVideoVO> relateVideoVOS = BeanCopyUtils.copyBeanList(videoList, RelateVideoVO.class);
        relateVideoVOS.forEach(v -> {
            //    private VideoAuthor videoAuthor;
            //    private VideoBehave videoBehave;
            //    private VideoSocial videoSocial;
            Member member = dubboMemberService.apiGetById(v.getUserId());
            if (!Objects.isNull(member)) {
                v.setVideoAuthor(new RelateVideoVO.VideoAuthor(v.getUserId(), member.getNickName(), member.getAvatar()));
            }
            // 封装观看量、点赞数、收藏量
            Integer cacheViewNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, v.getVideoId());
            Long likeNum = dubboBehaveService.apiGetVideoLikeNum(v.getVideoId());
            Long favoriteNum = dubboBehaveService.apiGetVideoFavoriteNum(v.getVideoId());
            // 评论数
            Long commentNum = dubboBehaveService.apiGetVideoCommentNum(v.getVideoId());
            v.setVideoBehave(new RelateVideoVO.VideoBehave(StringUtils.isNull(cacheViewNum) ? 0L : cacheViewNum, StringUtils.isNull(likeNum) ? 0L : likeNum, StringUtils.isNull(favoriteNum) ? 0L : favoriteNum, StringUtils.isNull(commentNum) ? 0L : commentNum));
            // 社交数据
            v.setVideoSocial(new RelateVideoVO.VideoSocial(false));
        });
        return relateVideoVOS;
    }

    @Override
    public List<VideoRecommendVO> pushAppVideoList() {
        Member member = dubboMemberService.apiGetById(UserContext.getUserId());
        if (!UserContext.hasLogin() || Objects.isNull(member)) {
            // 游客登陆
            member = new Member();
            member.setUserId(2L);
        }
        Collection<String> videoIdsByUserModel = interestPushService.getVideoIdsByUserModel(member);
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Video::getVideoId, videoIdsByUserModel);
        List<Video> videoList = this.list(queryWrapper);
        List<VideoRecommendVO> videoRecommendVOList = BeanCopyUtils.copyBeanList(videoList, VideoRecommendVO.class);
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(videoRecommendVOList
                .stream()
                .map(this::packageVideoRecommendVOAsync)
                .toArray(CompletableFuture[]::new));
        allFutures.join();
        return videoRecommendVOList;
    }

    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> packageVideoRecommendVOAsync(VideoRecommendVO vo) {
        return CompletableFuture.runAsync(() -> packageVideoRecommendVO(vo));
    }

    @Async(VIDEO_EXECUTOR)
    public void packageVideoRecommendVO(VideoRecommendVO vo) {
        CompletableFuture<Void> behaveDataFuture = packageVideoRecommendVOBehaveDataAsync(vo);
        CompletableFuture<Void> memberDataFuture = packageVideoRecommendVODataAsync(vo);
        CompletableFuture.allOf(
                behaveDataFuture,
                memberDataFuture
        ).join();
    }

    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> packageVideoRecommendVOBehaveDataAsync(VideoRecommendVO vo) {
        return CompletableFuture.runAsync(() -> packageVideoRecommendVOBehaveData(vo));
    }

    @Async(VIDEO_EXECUTOR)
    public CompletableFuture<Void> packageVideoRecommendVODataAsync(VideoRecommendVO vo) {
        return CompletableFuture.runAsync(() -> packageVideoRecommendVOMemberData(vo));
    }

    /**
     * 封装视频行为数据
     */
    @Async(VIDEO_EXECUTOR)
    public void packageVideoRecommendVOBehaveData(VideoRecommendVO vo) {
        // 封装观看量、点赞数、收藏量
        Integer cacheViewNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, vo.getVideoId());
        vo.setViewNum(StringUtils.isNull(cacheViewNum) ? 0L : cacheViewNum);
        Long likeNum = dubboBehaveService.apiGetVideoLikeNum(vo.getVideoId());
        vo.setLikeNum(Objects.isNull(likeNum) ? 0L : likeNum);
        Long favoriteNum = dubboBehaveService.apiGetVideoFavoriteNum(vo.getVideoId());
        vo.setFavoriteNum(Objects.isNull(favoriteNum) ? 0L : favoriteNum);
        // 评论数
        Long commentNum = dubboBehaveService.apiGetVideoCommentNum(vo.getVideoId());
        vo.setCommentNum(Objects.isNull(commentNum) ? 0L : commentNum);
    }

    /**
     * 封装作者数据
     */
    @Async(VIDEO_EXECUTOR)
    public void packageVideoRecommendVOMemberData(VideoRecommendVO vo) {
        // 封装用户信息
        Author author = new Author();
        Member userCache = redisService.getCacheObject("member:userinfo:" + vo.getUserId());
        if (StringUtils.isNotNull(userCache)) {
            author.setUserId(vo.getUserId());
            author.setUserName(userCache.getUserName());
            author.setNickName(userCache.getNickName());
            author.setAvatar(userCache.getAvatar());
        } else {
            Member publishUser = videoMapper.selectVideoAuthor(vo.getUserId());
            author.setUserId(vo.getUserId());
            author.setUserName(StringUtils.isNull(publishUser) ? "-" : publishUser.getUserName());
            author.setNickName(StringUtils.isNull(publishUser) ? "-" : publishUser.getNickName());
            author.setAvatar(StringUtils.isNull(publishUser) ? "" : publishUser.getAvatar());
        }
        vo.setAuthor(author);
    }

    @Override
    public VideoInfoVO getVideoInfoForApp(String videoId) {
        // 视频浏览量加一
        viewNumIncrement(videoId);
        Video video = this.getById(videoId);
        VideoInfoVO videoInfoVO = BeanCopyUtils.copyBean(video, VideoInfoVO.class);
        // 视频作者
        Member member = dubboMemberService.apiGetById(videoInfoVO.getUserId());
        Author author = BeanCopyUtils.copyBean(Objects.isNull(member) ? new Member() : member, Author.class);
        videoInfoVO.setAuthor(author);
        // 社交-是否关注
        if (UserContext.hasLogin()) {
            Long loginUserId = UserContext.getUserId();
            videoInfoVO.setWeatherFollow(videoMapper.userWeatherAuthor(loginUserId, videoInfoVO.getUserId()) > 0);
            videoInfoVO.setWeatherLike(videoMapper.selectUserLikeVideo(videoInfoVO.getVideoId(), loginUserId) > 0);
            videoInfoVO.setWeatherFavorite(videoMapper.userWeatherFavoriteVideo(videoInfoVO.getVideoId(), loginUserId) > 0);
        }
        // 行为-观看、点赞、收藏、评论量；是否点赞、收藏
        Integer cacheViewNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, videoInfoVO.getVideoId());
        videoInfoVO.setViewNum(StringUtils.isNull(cacheViewNum) ? 0L : cacheViewNum);
        Long likeNum = dubboBehaveService.apiGetVideoLikeNum(videoInfoVO.getVideoId());
        videoInfoVO.setLikeNum(Objects.isNull(likeNum) ? 0L : likeNum);
        Long favoriteNum = dubboBehaveService.apiGetVideoFavoriteNum(videoInfoVO.getVideoId());
        videoInfoVO.setFavoriteNum(Objects.isNull(favoriteNum) ? 0L : favoriteNum);
        Long commentNum = dubboBehaveService.apiGetVideoCommentNum(videoInfoVO.getVideoId());
        videoInfoVO.setCommentNum(Objects.isNull(commentNum) ? 0L : commentNum);
        // 视频标签
        String[] tags = videoTagRelationService.queryVideoTags(videoInfoVO.getVideoId());
        videoInfoVO.setTags(tags);
        // 图文视频
        if (videoInfoVO.getPublishType().equals(PublishType.IMAGE.getCode())) {
            List<VideoImage> videoImageList = videoImageService.queryImagesByVideoId(videoInfoVO.getVideoId());
            String[] imgs = videoImageList.stream().map(VideoImage::getImageUrl).toArray(String[]::new);
            videoInfoVO.setImageList(imgs);
        }
        // 位置信息
        if (videoInfoVO.getPositionFlag().equals(PositionFlag.OPEN.getCode())) {
            VideoPosition videoPosition = videoPositionService.queryPositionByVideoId(videoInfoVO.getVideoId());
            videoInfoVO.setPosition(videoPosition);
        }
        return videoInfoVO;
    }

    /**
     * 同步视频详情到db
     *
     * @param videoId
     */
    @Override
    public void updateVideoInfo(String videoId) {
        Video video = this.getById(videoId);
        if (Objects.isNull(video.getVideoUrl()) || video.getVideoUrl().isEmpty()) {
            log.debug("该视频url为空");
            return;
        }
        MultimediaInfo info = ffmpegVideoService.getVideoInfo(video.getVideoUrl());
        MediaVideoInfo mediaVideoInfo = new MediaVideoInfo(info);
        log.debug("视频详情：{}", mediaVideoInfo);
        Video videoUpdate = new Video();
        videoUpdate.setVideoId(videoId);
        videoUpdate.setVideoInfo(new Gson().toJson(mediaVideoInfo));
        this.updateById(videoUpdate);
    }

    /**
     * 封装videoVO
     *
     * @param videoIds
     * @return
     */
    @Override
    public List<VideoVO> packageVideoVOByVideoIds(Long loginUserId, List<String> videoIds) {
        Map<String, Video> batch = videoRedisBatchCache.getBatch(new ArrayList<>(videoIds));
        List<Video> videoList = new ArrayList<>(batch.values());
        List<VideoVO> videoVOList = BeanCopyUtils.copyBeanList(videoList, VideoVO.class);
        // 封装VideoVO
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(videoVOList.stream()
                .map(vo -> packageVideoVOAsync(vo, loginUserId)).toArray(CompletableFuture[]::new));
        allFutures.join();
        return videoVOList;
    }

    @Override
    public List<Video> exitsByVideoIds(List<String> videoIds) {
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Video::getVideoId, videoIds)
                .eq(Video::getDelFlag, DelFlagEnum.EXIST.getCode())
                .orderByDesc(Video::getCreateTime);
        return this.list(queryWrapper);
    }
}
