package com.niuyin.tools.es.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.model.video.domain.VideoTagRelation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 视频标签关联表(VideoTagRelation)表数据库访问层
 *
 * @author roydon
 * @since 2023-11-11 17:19:09
 */
@Mapper
public interface VideoTagRelationMapper extends BaseMapper<VideoTagRelation> {

    List<VideoTag> selectTagNamesByVideoId(String videoId);
}
