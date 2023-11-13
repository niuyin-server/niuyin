package com.niuyin.service.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.video.domain.VideoTagRelation;

/**
 * 视频标签关联表(VideoTagRelation)表服务接口
 *
 * @author roydon
 * @since 2023-11-11 17:19:10
 */
public interface IVideoTagRelationService extends IService<VideoTagRelation> {

    /**
     * 根据视频id和标签id数组批量插入
     */
    boolean saveVideoTagRelationBatch(String videoId, Long[] tagIds);

    /**
     * 查询视频标签
     *
     * @param videoId 视频id
     * @return String[]
     */
    String[] queryVideoTags(String videoId);

}
