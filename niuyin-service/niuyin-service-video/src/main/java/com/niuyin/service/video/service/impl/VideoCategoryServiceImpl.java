package com.niuyin.service.video.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.common.core.service.RedisService;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoCategory;
import com.niuyin.model.video.domain.VideoImage;
import com.niuyin.model.video.dto.CategoryVideoPageDTO;
import com.niuyin.model.video.dto.VideoCategoryPageDTO;
import com.niuyin.model.video.enums.PublishType;
import com.niuyin.model.video.enums.VideoCategoryStatus;
import com.niuyin.model.video.vo.*;
import com.niuyin.model.video.vo.app.AppVideoCategoryVo;
import com.niuyin.model.video.vo.app.CategoryVideoVo;
import com.niuyin.model.constants.VideoCacheConstants;
import com.niuyin.service.video.mapper.VideoCategoryMapper;
import com.niuyin.service.video.mapper.VideoMapper;
import com.niuyin.service.video.service.IVideoCategoryRelationService;
import com.niuyin.service.video.service.IVideoCategoryService;
import com.niuyin.service.video.service.IVideoImageService;
import com.niuyin.service.video.service.IVideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.niuyin.service.video.constants.InterestPushConstant.VIDEO_CATEGORY_PUSHED_CACHE_KEY_PREFIX;
import static com.niuyin.service.video.constants.InterestPushConstant.VIDEO_CATEGORY_VIDEOS_CACHE_KEY_PREFIX;
import static com.niuyin.model.constants.VideoCacheConstants.VIDEO_IMAGES_PREFIX_KEY;

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

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @DubboReference(mock = "return null", interfaceClass = DubboMemberService.class, timeout = 2000)
    private DubboMemberService dubboMemberService;

    @Override
    public List<VideoCategory> saveVideoCategoriesToRedis() {
        // 查询数据库获取视频分类列表
        List<VideoCategory> videoCategories = videoCategoryMapper.getAllVideoParentCategory();
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

    @Override
    public List<VideoCategoryVo> selectAllParentCategory() {
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
        // 查询该分类id以及其子分类
        pageDTO.setPageNum((pageDTO.getPageNum() - 1) * pageDTO.getPageSize());
        List<Video> videoList = videoCategoryMapper.selectVideoByCategoryId(pageDTO);
        List<VideoVO> videoVOList = BeanCopyUtils.copyBeanList(videoList, VideoVO.class);
//        videoVOList.forEach(v -> {
//            System.out.println("v.getVideoId() = " + v.getVideoId());
//        });
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
        // 封装观看量、点赞数、收藏量 todo java.lang.IllegalArgumentException: non null hash key required
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

    @Override
    public List<VideoPushVO> pushVideoByCategory(Long categoryId) {
        // 先判断 categoryId 是否有效
        LambdaQueryWrapper<VideoCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoCategory::getId, categoryId);
        if (this.count(queryWrapper) < 1) {
            return new ArrayList<>();
        }
        String categoryKey = VIDEO_CATEGORY_VIDEOS_CACHE_KEY_PREFIX + categoryId;
        String pushedKey = VIDEO_CATEGORY_PUSHED_CACHE_KEY_PREFIX + UserContext.getUserId();
        Long totalCount = redisTemplate.opsForSet().size(categoryKey);
        Long pushedCount = redisTemplate.opsForSet().size(pushedKey);
        if (StringUtils.isNull(totalCount) || totalCount < 1) {
            log.debug("没有分类视频");
        }
        Long subCount = totalCount - pushedCount;
        if (subCount < 1) {
            return new ArrayList<>();
        }
        // 查询当前用户已推送历史
        Set<Object> cacheSet = redisService.getCacheSet(pushedKey);
        // 去重结果
        Set<String> results = new HashSet<>(10);
        // 随机获取10条记录
        while (results.size() < 10) {
            String item = (String) redisTemplate.opsForSet().randomMember(categoryKey);
            if (!cacheSet.contains(item)) {
                // 筛选出未被推送过的数据
                results.add(item);
                cacheSet.add(item);
                // 已推送记录存到redis，过期时间为1小时，可以封装为异步
                redisTemplate.opsForSet().add(pushedKey, item);
                redisService.expire(pushedKey, 1, TimeUnit.MINUTES);
            }
            if (results.size() >= subCount) {
                break;
            }
        }
        // 封装result
        List<Video> videoList = videoService.listByIds(results);
        List<VideoPushVO> videoPushVOList = BeanCopyUtils.copyBeanList(videoList, VideoPushVO.class);
        CompletableFuture.allOf(videoPushVOList.stream()
                .map(videoPushVO -> CompletableFuture.runAsync(() -> {
                    asyncPackageAuthor(videoPushVO);
                    asyncPackageVideoImage(videoPushVO);
                })).toArray(CompletableFuture[]::new)).join();
        return videoPushVOList;
    }

    /**
     * 封装视频作者
     */
    @Async
    public void asyncPackageAuthor(VideoPushVO videoPushVO) {
        Member member = dubboMemberService.apiGetById(videoPushVO.getUserId());
        Author author = BeanCopyUtils.copyBean(member, Author.class);
        videoPushVO.setAuthor(author);
    }

    /**
     * 封装视频图文
     */
    @Async
    public void asyncPackageVideoImage(VideoPushVO videoPushVO) {
        // 封装图文
        if (videoPushVO.getPublishType().equals(PublishType.IMAGE.getCode())) {
            Object imgsCacheObject = redisService.getCacheObject(VIDEO_IMAGES_PREFIX_KEY + videoPushVO.getVideoId());
            if (StringUtils.isNotNull(imgsCacheObject)) {
                if (imgsCacheObject instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) imgsCacheObject;
                    videoPushVO.setImageList(jsonArray.toArray(new String[0]));
                } else if (imgsCacheObject instanceof String) {
                    String jsonString = (String) imgsCacheObject;
                    videoPushVO.setImageList(JSON.parseObject(jsonString, String[].class));
                }
            } else {
                List<VideoImage> videoImageList = videoImageService.queryImagesByVideoId(videoPushVO.getVideoId());
                String[] imgs = videoImageList.stream().map(VideoImage::getImageUrl).toArray(String[]::new);
                videoPushVO.setImageList(imgs);
                // 重建缓存 todo 加分布式锁
                redisService.setCacheObject(VIDEO_IMAGES_PREFIX_KEY + videoPushVO.getVideoId(), imgs);
                redisService.expire(VIDEO_IMAGES_PREFIX_KEY + videoPushVO.getVideoId(), 1, TimeUnit.DAYS);
            }
        }
    }

    /**
     * 获取视频分类树
     */
    @Override
    public List<VideoCategoryTree> getCategoryTree() {
        LambdaQueryWrapper<VideoCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoCategory::getStatus, VideoCategoryStatus.NORMAL.getCode());
        queryWrapper.orderByAsc(VideoCategory::getOrderNum);
        List<VideoCategory> videoCategoryList = this.list(queryWrapper);
        List<VideoCategoryTree> videoCategoryTrees = BeanCopyUtils.copyBeanList(videoCategoryList, VideoCategoryTree.class);
        return buildVideoCategoryTree(videoCategoryTrees);
    }

    /**
     * 构造树
     *
     * @param videoCategoryTrees
     * @return
     */
    public List<VideoCategoryTree> buildVideoCategoryTree(List<VideoCategoryTree> videoCategoryTrees) {
        List<VideoCategoryTree> rootCategories = new ArrayList<>();

        // 使用 Map 存储分类 ID 和对应的分类树节点
        Map<Long, VideoCategoryTree> categoryMap = new HashMap<>();
        for (VideoCategoryTree categoryTree : videoCategoryTrees) {
            categoryMap.put(categoryTree.getId(), categoryTree);
        }

        for (VideoCategoryTree categoryTree : videoCategoryTrees) {
            Long parentId = categoryTree.getParentId();
            if (parentId == null || parentId == 0) {
                // 根节点
                rootCategories.add(categoryTree);
            } else {
                // 非根节点，将当前节点添加到父节点的 children 列表中
                VideoCategoryTree parentCategory = categoryMap.get(parentId);
                if (parentCategory != null) {
                    List<VideoCategory> children = parentCategory.getChildren();
                    if (children == null) {
                        children = new ArrayList<>();
                        parentCategory.setChildren(children);
                    }
                    children.add(categoryTree);
                }
            }
        }

        return rootCategories;
    }

    /**
     * 获取所有可用视频父分类
     */
    @Override
    public List<AppVideoCategoryVo> getNormalParentCategory() {
        LambdaQueryWrapper<VideoCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoCategory::getStatus, VideoCategoryStatus.NORMAL.getCode());
        queryWrapper.eq(VideoCategory::getParentId, 0L);
        queryWrapper.orderByAsc(VideoCategory::getOrderNum);
        List<VideoCategory> videoCategoryList = this.list(queryWrapper);
        return BeanCopyUtils.copyBeanList(videoCategoryList, AppVideoCategoryVo.class);
    }

    /**
     * 获取一级子分类
     *
     * @param id
     * @return
     */
    @Override
    public List<AppVideoCategoryVo> getNormalChildrenCategory(Long id) {
        VideoCategory byId = getById(id);
        if (byId.getStatus().equals(VideoCategoryStatus.DISABLE.getCode())) {
            throw new RuntimeException("已" + VideoCategoryStatus.DISABLE.getCode());
        }
        LambdaQueryWrapper<VideoCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoCategory::getStatus, VideoCategoryStatus.NORMAL.getCode());
        queryWrapper.eq(VideoCategory::getParentId, id);
        queryWrapper.orderByAsc(VideoCategory::getOrderNum);
        List<VideoCategory> videoCategoryList = this.list(queryWrapper);
        return BeanCopyUtils.copyBeanList(videoCategoryList, AppVideoCategoryVo.class);
    }

    /**
     * 根据分类id分页获取视频
     *
     * @param pageDTO
     * @return
     */
    @Override
    public PageDataInfo getVideoPageByCategoryId(CategoryVideoPageDTO pageDTO) {
        pageDTO.setPageNum((pageDTO.getPageNum() - 1) * pageDTO.getPageSize());
        List<Video> videoList = videoCategoryMapper.selectVideoPageByCategoryId(pageDTO);
        List<CategoryVideoVo> categoryVideoVoList = BeanCopyUtils.copyBeanList(videoList, CategoryVideoVo.class);
        // 设置属性
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(categoryVideoVoList.stream()
                .map(this::packageCategoryVideoVoAsync).toArray(CompletableFuture[]::new));
        allFutures.join();
        return PageDataInfo.genPageData(categoryVideoVoList, videoCategoryMapper.selectVideoCountByCategoryId(pageDTO.getId()));
    }

    @Async
    public CompletableFuture<Void> packageCategoryVideoVoAsync(CategoryVideoVo vo) {
        return CompletableFuture.runAsync(() -> packageCategoryVideoVo(vo));
    }

    @Async
    public void packageCategoryVideoVo(CategoryVideoVo vo) {
        // 点赞量
        CompletableFuture<Void> behaveDataFuture = packageCategoryVideoVoBehaveDataAsync(vo);
        // 作者
        CompletableFuture<Void> memberDataFuture = packageCategoryVideoVoMemberDataAsync(vo);
        CompletableFuture.allOf(
                behaveDataFuture,
                memberDataFuture
        ).join();
    }


    @Async
    public CompletableFuture<Void> packageCategoryVideoVoBehaveDataAsync(CategoryVideoVo vo) {
        return CompletableFuture.runAsync(() -> packageCategoryVideoVoBehaveData(vo));
    }

    @Async
    public CompletableFuture<Void> packageCategoryVideoVoMemberDataAsync(CategoryVideoVo vo) {
        return CompletableFuture.runAsync(() -> packageCategoryVideoVoMemberData(vo));
    }

    /**
     * 封装视频行为数据
     */
    @Async
    public void packageCategoryVideoVoBehaveData(CategoryVideoVo vo) {
        // 封装观看量、点赞数、收藏量 todo java.lang.IllegalArgumentException: non null hash key required
        Integer cacheViewNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, vo.getVideoId());
        vo.setViewNum(StringUtils.isNull(cacheViewNum) ? 0L : cacheViewNum);
        vo.setLikeNum(videoMapper.selectLikeCountByVideoId(vo.getVideoId()));
    }

    /**
     * 封装用户数据
     */
    @Async
    public void packageCategoryVideoVoMemberData(CategoryVideoVo vo) {
        // 封装用户信息
        Member member = dubboMemberService.apiGetById(vo.getUserId());
        Author author = BeanCopyUtils.copyBean(Objects.isNull(member) ? new Member() : member, Author.class);
        vo.setAuthor(author);
    }

    /**
     * 获取视频父分类集合
     *
     * @return
     */
    @Override
    public List<VideoCategory> getVideoParentCategoryList() {
        LambdaQueryWrapper<VideoCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoCategory::getParentId, 0L)
                .eq(VideoCategory::getStatus, VideoCategoryStatus.NORMAL.getCode());
        return this.list(queryWrapper);
    }
}
