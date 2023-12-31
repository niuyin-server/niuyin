package com.niuyin.service.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.model.video.domain.VideoTagRelation;
import com.niuyin.service.video.mapper.VideoTagRelationMapper;
import com.niuyin.service.video.service.IVideoTagRelationService;
import com.niuyin.service.video.service.IVideoTagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 视频标签关联表(VideoTagRelation)表服务实现类
 *
 * @author roydon
 * @since 2023-11-11 17:19:10
 */
@Service("videoTagRelationService")
public class VideoTagRelationServiceImpl extends ServiceImpl<VideoTagRelationMapper, VideoTagRelation> implements IVideoTagRelationService {
    @Resource
    private VideoTagRelationMapper videoTagRelationMapper;

    @Resource
    private IVideoTagService videoTagService;

    /**
     * 根据视频id和标签id数组批量插入，标签最大不超过5个，不用考虑性能问题
     *
     * @param videoId
     * @param tagIds
     */
    @Transactional
    @Override
    public boolean saveVideoTagRelationBatch(String videoId, Long[] tagIds) {
        List<VideoTagRelation> list = new ArrayList<>();
        for (Long tagId : tagIds) {
            VideoTagRelation videoTagRelation = new VideoTagRelation();
            videoTagRelation.setVideoId(videoId);
            videoTagRelation.setTagId(tagId);
            list.add(videoTagRelation);
        }
        return this.saveBatch(list);
    }

    /**
     * 查询视频标签
     *
     * @param videoId 视频id
     * @return String[]
     */
    @Override
    public String[] queryVideoTags(String videoId) {
        LambdaQueryWrapper<VideoTagRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(VideoTagRelation::getTagId);
        queryWrapper.eq(VideoTagRelation::getVideoId, videoId);
        List<VideoTagRelation> list = this.list(queryWrapper);
        if (list.isEmpty()) {
            return new String[0];
        }
        List<Long> tagIds = list.stream().map(VideoTagRelation::getTagId).collect(Collectors.toList());
        LambdaQueryWrapper<VideoTag> vtQW = new LambdaQueryWrapper<>();
        vtQW.select(VideoTag::getTag);
        vtQW.in(VideoTag::getTagId, tagIds);
        List<VideoTag> tags = videoTagService.list(vtQW);
        return tags.stream().map(VideoTag::getTag).toArray(String[]::new);
    }
}
