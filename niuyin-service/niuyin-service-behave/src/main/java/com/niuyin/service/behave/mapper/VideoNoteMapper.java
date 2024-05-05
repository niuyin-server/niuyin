package com.niuyin.service.behave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.behave.domain.VideoNote;
import org.apache.ibatis.annotations.Mapper;

/**
 * 视频笔记表(VideoNote)表数据库访问层
 *
 * @author roydon
 * @since 2024-05-05 18:51:04
 */
@Mapper
public interface VideoNoteMapper extends BaseMapper<VideoNote>{

}

