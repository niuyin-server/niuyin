package com.niuyin.service.behave.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.behave.domain.UserFavoriteVideo;
import com.niuyin.model.behave.dto.UserFavoriteVideoDTO;

/**
 * (UserFavoriteVideo)表服务接口
 *
 * @author lzq
 * @since 2023-11-17 10:16:09
 */
public interface IUserFavoriteVideoService extends IService<UserFavoriteVideo> {

    /**
     * 收藏视频到收藏夹
     *
     * @param userFavoriteVideoDTO
     * @return
     */
    /**
     * 用户收藏视频到收藏夹功能
     * @param userFavoriteVideoDTO
     * @return
     */
    Boolean videoFavorites(UserFavoriteVideoDTO userFavoriteVideoDTO);

}
