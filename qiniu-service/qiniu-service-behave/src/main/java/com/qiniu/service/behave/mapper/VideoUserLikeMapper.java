package com.qiniu.service.behave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiniu.model.video.domain.VideoUserLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 点赞表(VideoUserLike)表数据库访问层
 *
 * @author lzq
 * @since 2023-10-30 14:32:59
 */
@Mapper
public interface VideoUserLikeMapper extends BaseMapper<VideoUserLike>{

}

