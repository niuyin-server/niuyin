package com.qiniu.service.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.bean.BeanCopyUtils;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.domain.VideoCategory;
import com.qiniu.model.video.domain.VideoCategoryRelation;
import com.qiniu.model.video.dto.VideoCategoryPageDTO;
import com.qiniu.model.video.vo.VideoCategoryVo;
import com.qiniu.service.video.constants.VideoCacheConstants;
import com.qiniu.service.video.mapper.VideoCategoryMapper;
import com.qiniu.service.video.service.IVideoCategoryRelationService;
import com.qiniu.service.video.service.IVideoCategoryService;
import com.qiniu.service.video.service.IVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    RedisService redisService;

    @Resource
    private IVideoCategoryRelationService videoCategoryRelationService;

    @Resource
    private IVideoService videoService;

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
        List<VideoCategoryVo> videoCategoryVos = BeanCopyUtils.copyBeanList(cacheList, VideoCategoryVo.class);
        return videoCategoryVos;
    }

    /**
     * 分页根据分类获取视频
     *
     * @param pageDTO
     * @return
     */
    @Override
    public IPage<Video> selectVideoByCategory(VideoCategoryPageDTO pageDTO) {
        if (StringUtils.isNull(pageDTO.getCategoryId())) {
            return new Page<>();
        }
        LambdaQueryWrapper<VideoCategoryRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(VideoCategoryRelation::getVideoId);
        queryWrapper.eq(VideoCategoryRelation::getCategoryId, pageDTO.getCategoryId());
        List<VideoCategoryRelation> list = videoCategoryRelationService.list(queryWrapper);
        if (StringUtils.isNull(list) || list.isEmpty()) {
            return new Page<>();
        }
        LambdaQueryWrapper<Video> videoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        videoLambdaQueryWrapper.in(Video::getVideoId, list.stream().map(VideoCategoryRelation::getVideoId).collect(Collectors.toList()));
        return videoService.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), videoLambdaQueryWrapper);
    }
}
