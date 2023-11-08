package com.qiniu.service.social.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiniu.model.common.dto.PageDTO;
import com.qiniu.model.social.UserFollow;

/**
 * 用户关注表(UserFollow)表服务接口
 *
 * @author roydon
 * @since 2023-10-30 15:54:21
 */
public interface IUserFollowService extends IService<UserFollow> {

    /**
     * 关注用户
     *
     * @param userId 被关注用户id
     */
    boolean followUser(Long userId);

    /**
     * 取消关注
     *
     * @param userId 取消关注用户id
     */
    boolean unFollowUser(Long userId);

    /**
     * 分页查询用户关注列表
     *
     * @param pageDTO 分页对象
     * @return IPage<User>
     */
    IPage<UserFollow> followPage(PageDTO pageDTO);
}
