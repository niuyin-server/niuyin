package com.niuyin.service.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.video.domain.Video;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 视频表(Video)表数据库访问层
 *
 * @author roydon
 * @since 2023-10-25 20:33:09
 */
@Mapper
public interface VideoMapper extends BaseMapper<Video> {

    String getVideoUrlByVideoId(String videoId);

    List<Video> getUserLikesVideos(Long userId, int pageNum, int pageSize);

    List<Video> getUserFavoritesVideos(Long userId, int pageNum, int pageSize);


    Long selectAllLikeNumForUser(Long userId);

}

