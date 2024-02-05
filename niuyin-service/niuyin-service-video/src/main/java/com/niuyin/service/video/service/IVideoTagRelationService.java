package com.niuyin.service.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.model.video.domain.VideoTagRelation;

import java.util.List;

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

    /**
     * 获取视频的标签集合
     *
     * @param videoId
     * @return
     */
    List<String> queryVideoTagsReturnList(String videoId);

    /**
     * 根据视频id获取标签集合
     *
     * @param videoId
     * @return
     */
    List<VideoTag> queryVideoTagsByVideoId(String videoId);

    /**
     * 根据视频id获取标签ids
     *
     * @param videoId
     * @return
     */
    List<Long> queryVideoTagIdsByVideoId(String videoId);

    /**
     * 删除视频关联标签
     */
    boolean deleteRecordByVideoId(String videoId);
}
