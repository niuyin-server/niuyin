package com.niuyin.service.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.video.domain.VideoImage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 视频图片关联表(VideoImage)表数据库访问层
 *
 * @author roydon
 * @since 2023-11-20 21:18:59
 */
@Mapper
public interface VideoImageMapper extends BaseMapper<VideoImage>{

}

