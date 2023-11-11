package com.niuyin.service.behave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.behave.domain.VideoUserFavorites;
import org.apache.ibatis.annotations.Mapper;

/**
 * 视频收藏表(VideoUserFavorites)表数据库访问层
 *
 * @author lzq
 * @since 2023-10-31 15:57:37
 */
@Mapper
public interface VideoUserFavoritesMapper extends BaseMapper<VideoUserFavorites>{


}

