package com.niuyin.service.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.video.domain.VideoTagRelation;
import com.niuyin.service.video.mapper.VideoTagRelationMapper;
import com.niuyin.service.video.service.IVideoTagRelationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

}
