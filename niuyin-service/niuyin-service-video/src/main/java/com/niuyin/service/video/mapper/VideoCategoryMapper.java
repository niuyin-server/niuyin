package com.niuyin.service.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.video.domain.VideoCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * (VideoCategory)表数据库访问层
 *
 * @author lzq
 * @since 2023-10-30 19:41:13
 */
@Mapper
public interface VideoCategoryMapper extends BaseMapper<VideoCategory> {
    List<VideoCategory> getAllVideoCategory();
}

