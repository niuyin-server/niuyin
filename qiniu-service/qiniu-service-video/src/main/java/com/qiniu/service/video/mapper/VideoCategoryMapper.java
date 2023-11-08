package com.qiniu.service.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiniu.model.video.domain.VideoCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

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

