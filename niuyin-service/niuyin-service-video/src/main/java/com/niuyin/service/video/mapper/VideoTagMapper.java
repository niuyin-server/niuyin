package com.niuyin.service.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.video.domain.VideoTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 视频标签表(VideoTag)表数据库访问层
 *
 * @author roydon
 * @since 2023-11-11 16:05:08
 */
@Mapper
public interface VideoTagMapper extends BaseMapper<VideoTag> {
}
