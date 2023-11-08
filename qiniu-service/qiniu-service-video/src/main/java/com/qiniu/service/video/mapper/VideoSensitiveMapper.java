package com.qiniu.service.video.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiniu.model.video.domain.VideoSensitive;
import org.apache.ibatis.annotations.Mapper;

/**
 * 敏感词信息表(VideoSensitive)表数据库访问层
 *
 * @author lzq
 * @since 2023-10-30 11:34:01
 */
@Mapper
public interface VideoSensitiveMapper extends BaseMapper<VideoSensitive> {


}

