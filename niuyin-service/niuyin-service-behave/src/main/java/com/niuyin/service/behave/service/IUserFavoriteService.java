package com.niuyin.service.behave.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.behave.domain.UserFavorite;

/**
 * (UserFavorite)表服务接口
 *
 * @author lzq
 * @since 2023-11-13 16:37:53
 */
public interface IUserFavoriteService extends IService<UserFavorite> {


    boolean saveFavorite(UserFavorite userFavorite);
}
