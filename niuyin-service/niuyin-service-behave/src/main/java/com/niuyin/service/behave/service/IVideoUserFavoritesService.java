package com.niuyin.service.behave.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.behave.domain.UserFavoriteVideo;
import com.niuyin.model.behave.domain.VideoUserFavorites;
import com.niuyin.model.video.dto.VideoPageDto;

/**
 * 视频收藏表(VideoUserFavorites)表服务接口
 *
 * @author lzq
 * @since 2023-10-31 15:57:38
 */
public interface IVideoUserFavoritesService extends IService<VideoUserFavorites> {

    boolean videoFavorites(UserFavoriteVideo userFavoriteVideo);

    IPage<VideoUserFavorites> queryFavoritePage(VideoPageDto pageDto);
}
