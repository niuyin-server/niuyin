package com.niuyin.service.behave.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.behave.domain.VideoUserFavorites;
import com.niuyin.model.video.dto.VideoPageDto;

import java.util.List;

/**
 * 视频收藏表(VideoUserFavorites)表服务接口
 *
 * @author lzq
 * @since 2023-10-31 15:57:38
 */
public interface IVideoUserFavoritesService extends IService<VideoUserFavorites> {

    /**
     * 收藏视频
     */
    boolean userOnlyFavoriteVideo(String videoId);

    /**
     * 取消收藏视频
     */
    boolean userUnFavoriteVideo(String videoId);

    IPage<VideoUserFavorites> queryFavoritePage(VideoPageDto pageDto);

    /**
     * 分页查询用户收藏的视频
     *
     * @param pageDto
     * @return
     */
    PageDataInfo queryUserFavoriteVideoPage(VideoPageDto pageDto);

    PageDataInfo queryMyFavoriteVideoPageForApp(VideoPageDto pageDto);
    PageDataInfo queryUserFavoriteVideoPageForApp(VideoPageDto pageDto);

    /**
     * 删除说有用户收藏此视频记录 ！！！
     *
     * @param videoId
     * @return
     */
    boolean removeFavoriteRecordByVideoId(String videoId);

    Long getFavoriteCountByVideoId(String videoId);

    /**
     * 获取用户收藏视频列表
     *
     * @param userId
     * @return
     */
    List<String> getFavoriteVideoIdListByUserId(Long userId);

}
