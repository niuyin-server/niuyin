package com.niuyin.service.video.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.domain.VideoCategoryRelation;
import com.niuyin.model.video.domain.VideoImage;
import com.niuyin.model.video.enums.PublishType;
import com.niuyin.model.video.vo.VideoVO;
import com.niuyin.service.video.mapper.VideoMapper;
import com.niuyin.service.video.service.IVideoCategoryService;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoCategory;
import com.niuyin.model.video.dto.VideoCategoryPageDTO;
import com.niuyin.model.video.vo.VideoCategoryVo;
import com.niuyin.service.video.constants.VideoCacheConstants;
import com.niuyin.service.video.mapper.VideoCategoryMapper;
import com.niuyin.service.video.service.IVideoCategoryRelationService;
import com.niuyin.service.video.service.IVideoImageService;
import com.niuyin.service.video.service.IVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.niuyin.service.video.constants.VideoCacheConstants.VIDEO_IMAGES_PREFIX_KEY;

/**
 * (VideoCategory)表服务实现类
 *
 * @author lzq
 * @since 2023-10-30 19:41:14
 */
@Slf4j
@Service("videoCategoryService")
public class VideoCategoryServiceImpl extends ServiceImpl<VideoCategoryMapper, VideoCategory> implements IVideoCategoryService {
    @Resource
    private VideoCategoryMapper videoCategoryMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private IVideoCategoryRelationService videoCategoryRelationService;

    @Resource
    private IVideoService videoService;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private IVideoImageService videoImageService;

    @Override
    public List<VideoCategory> saveVideoCategoriesToRedis() {
        // 查询数据库获取视频分类列表
        List<VideoCategory> videoCategories = videoCategoryMapper.getAllVideoCategory();
        if (videoCategories.isEmpty()) {
            return new ArrayList<>();
        }
        redisService.setCacheList(VideoCacheConstants.VIDEO_CATEGORY_PREFIX, videoCategories);
        return videoCategories;
    }

    /**
     * 获取所有的分类列表
     */
    @Override
    public List<VideoCategoryVo> selectAllCategory() {

        List<VideoCategory> cacheList = redisService.getCacheList(VideoCacheConstants.VIDEO_CATEGORY_PREFIX);
        if (cacheList.isEmpty()) {
            cacheList = saveVideoCategoriesToRedis();
        }
        return BeanCopyUtils.copyBeanList(cacheList, VideoCategoryVo.class);
    }

    /**
     * 分页根据分类获取视频
     *
     * @param pageDTO
     * @return
     */
    @Override
    public PageDataInfo selectVideoByCategory(VideoCategoryPageDTO pageDTO) {
        if (StringUtils.isNull(pageDTO.getCategoryId())) {
            return PageDataInfo.emptyPage();
        }
        pageDTO.setPageNum((pageDTO.getPageNum() - 1) * pageDTO.getPageSize());
        List<Video> videoList = videoCategoryMapper.selectVideoByCategoryId(pageDTO);
        List<VideoVO> videoVOList = BeanCopyUtils.copyBeanList(videoList, VideoVO.class);
        videoVOList.forEach(v -> {
            System.out.println("v.getVideoId() = " + v.getVideoId());
        });
        Long videoCount = videoCategoryMapper.selectVideoCountByCategoryId(pageDTO.getCategoryId());
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(videoVOList
                .stream()
                .map(this::packageUserVideoVOAsync)
                .toArray(CompletableFuture[]::new));
        allFutures.join();
        return PageDataInfo.genPageData(videoList, videoCount);
    }

    @Async
    public CompletableFuture<Void> packageUserVideoVOAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageUserVideoVO(videoVO));
    }

    @Async
    public void packageUserVideoVO(VideoVO videoVO) {
        CompletableFuture<Void> behaveDataFuture = packageVideoBehaveDataAsync(videoVO);
        CompletableFuture<Void> memberDataFuture = packageMemberDataAsync(videoVO);
        CompletableFuture<Void> imageDataFuture = packageVideoImageDataAsync(videoVO);
//        CompletableFuture<Void> positionDataFuture = packageVideoPositionDataAsync(videoVO);
        CompletableFuture.allOf(
                behaveDataFuture,
                memberDataFuture,
                imageDataFuture
        ).join();
    }

    @Async
    public CompletableFuture<Void> packageVideoBehaveDataAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageVideoBehaveData(videoVO));
    }

    @Async
    public CompletableFuture<Void> packageMemberDataAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageMemberData(videoVO));
    }

    @Async
    public CompletableFuture<Void> packageVideoImageDataAsync(VideoVO videoVO) {
        return CompletableFuture.runAsync(() -> packageVideoImageData(videoVO));
    }

    /**
     * 封装视频行为数据
     *
     * @param videoVO
     */
    @Async
    public void packageVideoBehaveData(VideoVO videoVO) {
        log.debug("packageVideoBehaveData开始");
        // 封装观看量、点赞数、收藏量
        Integer cacheViewNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, videoVO.getVideoId());
        videoVO.setViewNum(StringUtils.isNull(cacheViewNum) ? 0L : cacheViewNum);
        videoVO.setLikeNum(videoMapper.selectLikeCountByVideoId(videoVO.getVideoId()));
        videoVO.setFavoritesNum(videoMapper.selectFavoriteCountByVideoId(videoVO.getVideoId()));
        // 评论数
        videoVO.setCommentNum(videoMapper.selectCommentCountByVideoId(videoVO.getVideoId()));
        log.debug("packageVideoBehaveData结束");
    }

    /**
     * 封装用户数据
     *
     * @param videoVO
     */
    @Async
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
     * 封装图文数据
     *
     * @param videoVO
     */
    @Async
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

}
