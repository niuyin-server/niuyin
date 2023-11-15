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

    /**
     *
     * @param videoId
     * @return
     */
    String getVideoUrlByVideoId(String videoId);

    /**
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Video> getUserLikesVideos(Long userId, int pageNum, int pageSize);

    /**
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Video> getUserFavoritesVideos(Long userId, int pageNum, int pageSize);

    /**
     *
     * @param userId
     * @return
     */
    Long selectAllLikeNumForUser(Long userId);

}

