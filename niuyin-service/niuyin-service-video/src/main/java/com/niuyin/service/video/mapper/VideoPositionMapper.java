package com.niuyin.service.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.video.domain.VideoPosition;
import org.apache.ibatis.annotations.Mapper;

/**
 * 视频定位表(VideoPosition)表数据库访问层
 *
 * @author roydon
 * @since 2023-11-21 15:44:14
 */
@Mapper
public interface VideoPositionMapper extends BaseMapper<VideoPosition> {

}

