package com.niuyin.service.social.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.domain.vo.PageDataInfo;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.social.domain.UserFollow;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.vo.VideoVO;

import java.util.List;

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

    /**
     * 分页查询我的关注
     *
     * @param pageDTO
     * @return
     */
    PageDataInfo getFollowPage(PageDTO pageDTO);

    /**
     * 分页用户粉丝
     *
     * @param pageDTO
     * @return
     */
    PageDataInfo queryUserFansPage(PageDTO pageDTO);

    List<UserFollow> getFollowList(Long userId);

    void initFollowVideoFeed();

    /**
     * 关注流
     *
     * @param lastTime 滚动分页参数，首次为null，后续为上次的末尾视频时间
     * @return
     */
    List<Video> followVideoFeed(Long lastTime);

    /**
     * 获取社交动态分页
     *
     * @param pageDTO
     * @return
     */
    PageDataInfo<VideoVO> getSocialDynamicVideoPage(PageDTO pageDTO);

    /**
     * 是否关注用户
     *
     * @param userId
     * @param followUserId
     * @return
     */
    Boolean weatherFollow(Long userId, Long followUserId);

    Long getUserFollowCount(Long userId);

    Long getUserFansCount(Long userId);
}
