package com.qiniu.service.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiniu.model.video.domain.VideoUserComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * (VideoUserComment)表数据库访问层
 *
 * @author roydon
 * @since 2023-10-30 16:52:51
 */
@Mapper
public interface VideoUserCommentMapper extends BaseMapper<VideoUserComment> {

}

