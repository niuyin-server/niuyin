package com.niuyin.service.behave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.behave.domain.VideoUserFavorites;
import com.niuyin.model.behave.vo.UserFavoriteVideoVO;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.dto.VideoPageDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 视频收藏表(VideoUserFavorites)表数据库访问层
 *
 * @author lzq
 * @since 2023-10-31 15:57:37
 */
@Mapper
public interface VideoUserFavoritesMapper extends BaseMapper<VideoUserFavorites> {

    /**
     * 分页查询用户收藏视频集合
     *
     * @param videoPageDto
     * @return
     */
    List<UserFavoriteVideoVO> selectUserFavoriteVideos(VideoPageDto videoPageDto);
    Long selectUserFavoriteVideosCount(VideoPageDto videoPageDto);

    Long selectFavoriteCountByVideoId(String videoId);
}

