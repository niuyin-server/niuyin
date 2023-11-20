package com.niuyin.service.behave.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.behave.domain.UserFavorite;
import com.niuyin.model.behave.vo.UserFavoriteInfoVO;
import com.niuyin.model.common.dto.PageDTO;

import java.util.List;

/**
 * (UserFavorite)表服务接口
 *
 * @author lzq
 * @since 2023-11-13 16:37:53
 */
public interface IUserFavoriteService extends IService<UserFavorite> {

    /**
     * 用户新建收藏夹
     *
     * @param userFavorite
     * @return
     */
    boolean saveFavorite(UserFavorite userFavorite);

    /**
     * 查询收藏集详情
     *
     * @return
     */
    List<UserFavoriteInfoVO> queryCollectionInfoList();

    /**
     * 分页查询用户收藏夹
     *
     * @param pageDTO
     * @return
     */
    IPage<UserFavorite> queryCollectionPage(PageDTO pageDTO);
}
