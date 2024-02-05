package com.niuyin.service.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoCategory;
import com.niuyin.model.video.dto.CategoryVideoPageDTO;
import com.niuyin.model.video.dto.VideoCategoryPageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 分页通过分类id查询视频集合
     */
    List<Video> selectVideoByCategoryId(VideoCategoryPageDTO pageDTO);

    Long selectVideoCountByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 分页通过分类id查询视频集合
     */
    List<Video> selectVideoPageByCategoryId(CategoryVideoPageDTO pageDTO);
}

